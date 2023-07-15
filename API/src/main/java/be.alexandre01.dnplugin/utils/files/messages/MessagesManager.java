package be.alexandre01.dnplugin.utils.files.messages;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class MessagesManager {
    private final LinkedHashMap<String, Object> messages;

    public MessagesManager(LinkedHashMap<String, Object> messages){
        this.messages = messages;
    }

    public String getString(String path){
        String[] p = path.split("\\.");
        LinkedHashMap<String, Object> m = messages;
        for(int i = 0 ; i != p.length-1 ; i++){
            m = (LinkedHashMap<String, Object>) m.get(p[i]);
        }
        return (String) m.get(p[p.length-1]);
    }

    public List<String> getPaths(String path){
        String[] p = path.split("\\.");
        LinkedHashMap<String, Object> m = messages;
        for(int i = 0 ; i != p.length ; i++){
            if(m == null){return null;}
            m = (LinkedHashMap<String, Object>) m.getOrDefault(p[i], null);
        }
        List<String> paths = new ArrayList<>();
        m.keySet().forEach(k -> {
            paths.add(path + "." + k);
        });
        return paths;
    }
}
