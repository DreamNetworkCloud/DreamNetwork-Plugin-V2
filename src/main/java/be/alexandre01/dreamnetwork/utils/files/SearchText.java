package be.alexandre01.dreamnetwork.utils.files;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.bukkit.configuration.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public abstract class SearchText {
    public HashMap<String, String> messages = new HashMap<>();
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private Configuration configuration;
    private final String path1 = "messages";
    @Setter private distributeText distributeText;
    @Getter private static SearchText old;
    @Getter private static SearchText instance;
    private final boolean autoDelete;


    public static String get(String path){
        return instance.getMessage(path);
    }
    public static String get(String path,Object... objects){
        return instance.getMessage(path,objects);
    }
    public SearchText(boolean autoDelete){
        this.autoDelete = autoDelete;
        if(instance != null) old = instance;
        instance = this;
    }
    public void init(){
        reloadConfig();
        recursiveSearch("");
        if(autoDelete){
          tryToDelete();
        }
        if(old != null){
            old.messages.entrySet().forEach(e -> {
                if(!messages.containsKey(e.getKey())){
                    messages.put(e.getKey(),e.getValue());
                    setString(path1+"."+e.getKey(),e.getValue());
                }
            });
            saveFile();
        }
    }



    public void tryToDelete(){
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    Files.delete(getFile().toPath());
                } catch (IOException e) {
                    executorService.shutdown();
                    tryToDelete();
                    System.out.println("Retrying to delete SRC Messages file");
                    return;
                }
                executorService.shutdown();
            }
        },5, 5,TimeUnit.SECONDS);

    }
    public void reset(){
        messages.clear();
    }
    private boolean recursiveSearch(String currentPath){
        System.out.println(currentPath);
        System.out.println(path1+"."+currentPath);
        String path = path1;
        /*if(!currentPath.equals("")){
            path = path+".";
        }*/
        if(!hasSubSection(path+currentPath)) return false;


        for (String s : getKeys(path+currentPath,false)){
            System.out.println(currentPath);
            String totalPath =path+currentPath+"."+s;
            System.out.println(totalPath);
            if(!recursiveSearch(currentPath+"."+s)){
                messages.put(currentPath.substring(1)+"."+s,getString(totalPath));
            }
        }
        return true;
    }



    protected abstract Set<String> getKeys(String path, boolean b);
    protected abstract boolean hasSubSection(String path);
    protected abstract String getString(String path);

    public String getMessage(String path){
        String message;
        if(messages.containsKey(path)){
            message = messages.get(path);
        }else {
            message = old.messages.get(path);
        }
        return message;
    }

    @SneakyThrows
    public String getMessage(String path, Object... objects){
        String message;
        if(messages.containsKey(path)){
            message = messages.get(path);
        }else {
            message = old.messages.get(path);
        }

        if(message == null) return "null";

        message = message.replaceAll("&","§");
        distributeText.exec(message,objects);


        if(path.contains("%time%"))
         distribute(returnObjects(Date.class,objects),"%time%","getTime",message);


        return message;
    }

    protected  <T> List<T> returnObjects(Class<T> c, Object... objects){
        return  Arrays.stream(objects).filter(c::isInstance)
                .map(o -> (T) o)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    protected  <T> String distribute(List<T> types,String regex, String method, String s){
        int i = 0;
        boolean hasStringFormat = false;
        if(types.isEmpty()) return s;
        for (int j = 0; j < types.size(); j++) {
            Object o =   types.get(j);
            Object v = o.getClass().getDeclaredMethod(method).invoke(o);
            if(v instanceof String){
                hasStringFormat = true;
                s.replace(regex, (CharSequence) v);
                i++;
            }else {
                types.remove(i);
            }
        }
        if(hasStringFormat){
            while (s.contains(regex)){
                if(i > types.size()){
                    i = 0;
                }else {
                    i++;
                }
                Object o =   types.get(i);
                Object v = o.getClass().getDeclaredMethod(method).invoke(o);
                if(v instanceof String) {
                    s.replace(regex, (CharSequence) v);
                }
            }
        }

        return s;
    }

    public abstract void reloadConfig();
    public abstract void saveFile();
    public abstract void setString(String path, String value);
    public abstract File getFile();
    protected interface distributeText{
        void exec(String message,Object... objects);
    }
}
