package be.alexandre01.dnplugin.api.utils.files.yaml;

import be.alexandre01.dnplugin.api.utils.colors.Colors;
import lombok.Getter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class YamlFileUtils<T> {

    transient private List<String> annotations = new ArrayList<>();
    transient private Class<T> clazz;
    transient @Getter File file;
    transient boolean skipNull;

    public transient Constructor constructor = null;

    public transient Representer representer = null;

    HashMap<Class<?>,Tag> tags = new HashMap<>();
     List<String> settedFields = new ArrayList<>();
    public void addTag(Class<?> clazz, Tag tag) {
        tags.put(clazz, tag);
    }

    public boolean config(File file,Class<T> clazz,boolean skipNull){
        this.skipNull = skipNull;
        this.clazz = clazz;
        this.file = file;
        if(!file.exists()){
            try {
                file.createNewFile();
                return false;
            } catch (IOException e) {
                System.out.println(Colors.RED + "Error while creating file " + file.getName());
                e.printStackTrace();
            }
        }
        return true;
    }

    public T read(){
        return (T) loadFile(file, clazz);
    }

    public void saveFile(T obj){
        saveFile(file, obj,clazz,skipNull);
    }
    public void saveFile(){
        saveFile(file,this,clazz,skipNull);
    }
    public T loadFile(File file, Class<T> clazz){
      /* Representer representer = new Representer() {
            @Override
            protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {


                if (this.getClass().equals(property.getType())) {
                    return null;
                }
                else {
                    settedFields.add(property.getName());
                    return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
                }
            }
        };*/
        Yaml yaml;
        if(constructor == null){
            yaml = new Yaml(new Constructor(),new CustomRepresenter(skipNull,null,clazz)/*,representer*/);
        }else {
            yaml = new Yaml(constructor,this.representer);
        }


        try {
           /* LinkedHashMap<String,Object> map = yaml.load(new FileInputStream(file));
            if(map == null){
                return null;
            }
            if(map.isEmpty()){
                return null;
            }*/
            T t = null;
            try {
               // System.out.println("Load yml file: "+file.getName());
                t = yaml.loadAs(new FileInputStream(file),clazz);
            }catch (Exception e){
                System.out.println("Error while loading file: "+file.getName());
                //e.printStackTrace();
            }



           // Gson gson = new Gson();
            //T t = gson.fromJson(gson.toJsonTree(map), clazz);
          //  YamlFileUtils.this.readFile();
            return t;
        } catch (Exception e) {
            System.out.println(Colors.RED + "Error while loading file " + file.getName());
            e.printStackTrace();
            return null;
        }
    }

    public void readAndReplace(Object o){
        Object config = read();

        // Copy all data from config to this class
        // get declaredfields and fields
        if(config == null){
            return;
        }
        Field[] fields = config.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Field field1 = o.getClass().getDeclaredField(field.getName());
                field1.setAccessible(true);
                if(field1.getAnnotation(Ignore.class) != null) continue;
                field1.set(this,field.get(config));
                //Console.printLang("service.startupConfig.settingField", field.getName(), field.get(config));
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }
    public void saveFile(File file, Object obj, Class<?> clazz, boolean skipNull){
        try {

            if(!file.exists())
                file.createNewFile();
            Representer representer;
            if(this.representer == null){
                representer = new CustomRepresenter(skipNull,obj,clazz);
            }else {
                representer = this.representer;
                if(representer instanceof CustomRepresenter){
                    ((CustomRepresenter) representer).setObj(obj);
                }
            }

            for (Class<?> aClass : tags.keySet()) {
                representer.addClassTag(aClass, tags.get(aClass));
            }
            Yaml yaml;
            DumperOptions dumperOptions = new DumperOptions();
            if(constructor == null){
                yaml = new Yaml(representer,new DumperOptions());
            }else {
                yaml = new Yaml(representer,new DumperOptions());
                //yaml = new Yaml(constructor,representer,new DumperOptions());
            }



            FileWriter fileWriter = new FileWriter(file);
            if(!annotations.isEmpty()){
                for (String annotation : annotations) {
                    fileWriter.write(annotation);
                }
            }
            fileWriter.write(yaml.dumpAsMap(obj));
            fileWriter.flush();
            fileWriter.close();


        } catch (IOException e) {
            System.out.println(Colors.RED + "Error during the writing of the file " + file.getName());
            e.printStackTrace();
        }
    }

    public void addAnnotation(String annotation){
        annotations.add("# "+annotation+"\n");
    }

}
