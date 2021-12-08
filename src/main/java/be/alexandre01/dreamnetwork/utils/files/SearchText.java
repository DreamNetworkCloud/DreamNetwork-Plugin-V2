package be.alexandre01.dreamnetwork.utils.files;

import lombok.SneakyThrows;
import org.bukkit.configuration.Configuration;

import java.util.*;
import java.util.stream.Collectors;

public abstract class SearchText {
    private HashMap<String, String> messages = new HashMap<>();
    private boolean isProxy;
    private Configuration configuration;
    private final String path1 = "messages.";

    public SearchText(boolean isProxy){
        this.isProxy = isProxy;
    }
    public void init(){
        recursiveSearch("");
    }
    public void reset(){
        messages.clear();
    }
    private boolean recursiveSearch(String currentPath){

        if(!hasSubSection(path1+currentPath)) return false;

        for (String s : getKeys(path1+currentPath,false)){
            String totalPath = path1+currentPath+"."+s;

            if(!recursiveSearch(totalPath)){
                messages.put(currentPath,getString(currentPath+"."+s));
            }
        }
        return true;
    }



    protected abstract Set<String> getKeys(String path, boolean b);
    protected abstract boolean hasSubSection(String path);
    protected abstract String getString(String path);

    public String getMessage(String path){
        return messages.get(path);
    }

    @SneakyThrows
    public String getMessage(String path, Object... objects){
        String message = messages.get(path);
        if(path.contains("%player%")){
            if(isProxy){
                distribute(returnObjects(Class.forName("net.md_5.bungee.api.connection.ProxiedPlayer"),objects),"%player%","getName",message);
            }else {
                distribute(returnObjects(Class.forName("org.bukkit.entity.Player"),objects),"%player%","getName",message);
            }
        }

        if(path.contains("%time%"))
         distribute(returnObjects(Date.class,objects),"%time%","getTime",message);


        return message;
    }

    private <T> List<T> returnObjects(Class<T> c, Object... objects){
        return  Arrays.stream(objects).filter(c::isInstance)
                .map(o -> (T) o)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private <T> String distribute(List<T> types,String regex, String method, String s){
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

}
