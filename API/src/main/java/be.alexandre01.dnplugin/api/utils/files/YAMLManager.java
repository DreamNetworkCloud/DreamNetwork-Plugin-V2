package be.alexandre01.dnplugin.api.utils.files;

import be.alexandre01.dnplugin.api.utils.files.messages.MessagesManager;
import be.alexandre01.dnplugin.api.utils.files.motd.MOTDYAML;
import be.alexandre01.dnplugin.api.utils.files.network.NetworkYAML;
import be.alexandre01.dnplugin.api.utils.files.tablist.Animations;
import be.alexandre01.dnplugin.api.utils.files.tablist.LineState;
import be.alexandre01.dnplugin.api.utils.files.tablist.TabList;
import be.alexandre01.dnplugin.api.utils.files.tablist.TabListYAML;
import lombok.Getter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class YAMLManager {
    private final String pluginFolder;
    private final String type;

    public YAMLManager(String pluginFolder, String type){
        this.pluginFolder = pluginFolder;
        this.type = (!type.equalsIgnoreCase("SERVER") && !type.equalsIgnoreCase("PROXY")) ? null : type;
        if(this.type.equalsIgnoreCase("PROXY")){
            loadNetwork();
            loadMOTD();
            loadTabList();
        }
        loadMessages();
    }

    @Getter private NetworkYAML network;
    @Getter private MOTDYAML motd;
    @Getter private TabListYAML tabList;
    @Getter private MessagesManager messagesManager;

    private void loadNetwork(){
        File f = new File(pluginFolder + "/network.yml");
        if(!f.exists()){
            try {
                f.createNewFile();
            }catch (IOException e){e.printStackTrace();}
        }
        try {
            LinkedHashMap<String, Object> network = read(f);
            System.out.println(network);
            assert network != null;
            this.network = new NetworkYAML(
                    (String) network.getOrDefault("lobby", "main/lobby"),
                    (boolean) network.getOrDefault("connexionOnLobby", false),
                    (int) network.getOrDefault("maxPlayerPerLobby", 15),
                    (boolean) network.getOrDefault("maintenance", false),
                    (boolean) network.getOrDefault("enableRedirectionKick", true),
                    (String) network.getOrDefault("redirectionKickServer", "main/lobby"),
                    (boolean) network.getOrDefault("statusLogo", false),
                    (List<String>) network.getOrDefault("maintenanceAllowedPlayers", new ArrayList<String>()),
                    (boolean) network.getOrDefault("autoSendPlayers", true),
                    (int) network.getOrDefault("slots", 50)
            );
            saveNetwork();
        }catch (IOException e){
            e.printStackTrace();
            this.network = null;
        }
    }

    public void saveNetwork(){
        LinkedHashMap<String, Object> network = new LinkedHashMap<>();
        network.put("lobby", this.network.getLobby());
        network.put("connexionOnLobby", this.network.isConnexionOnLobby());
        network.put("maxPlayerPerLobby", this.network.getMaxPlayerPerLobby());
        network.put("maintenance", this.network.isMaintenance());
        network.put("enableRedirectionKick", this.network.isKickRedirectionEnabled());
        network.put("redirectionKickServer", this.network.getKickRedirectionServer());
        network.put("statusLogo", this.network.isStatusLogo());
        network.put("maintenanceAllowedPlayers", this.network.getMaintenanceAllowedPlayers());
        network.put("autoSendPlayers", this.network.isAutoSendPlayers());
        network.put("slots", this.network.getSlots());

        try {
            saveYAML(pluginFolder + "/network.yml", network);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void loadMOTD(){
        File f = new File(pluginFolder + "/motd.yml");
        if(!f.exists()){
            try{
                f.createNewFile();
            }catch (IOException e){e.printStackTrace();}
        }
        try {
            LinkedHashMap<String, Object> motd = read(f);
            List<String> defaultC = new ArrayList<>();
            defaultC.add("              &e&l✯ &9&lDreamNetwork &e&l✯ &f");
            defaultC.add("   &e▅▆▇ &6&lBest Network System &e▇▆▅ ");
            List<String> hm = (List<String>) motd.getOrDefault("content", defaultC);

            List<String> content = new ArrayList<>(hm);

            defaultC.clear();
            defaultC.add("&7&m--------------------");
            defaultC.add("      §9Dream§3Network");
            defaultC.add("&7 &4 &c &e");
            defaultC.add("&7» &eBEST NETWORK");
            defaultC.add("");
            defaultC.add("&7 &eSite&7:");
            defaultC.add("&7 &b§owww.dreamnetwork.cloud");
            defaultC.add("&7&m--------------------");
            hm = (List<String>) motd.getOrDefault("version-hover", defaultC);

            List<String> versionHover = new ArrayList<>(hm);

            this.motd = new MOTDYAML(
                    (boolean) motd.getOrDefault("activated", true),
                    (boolean) motd.getOrDefault("custom-version-protocol", true),
                    (String) motd.getOrDefault("custom-version-message", "&4[&e1.8&c+&4] &e(%online%&c/&e%max%)"),
                    (boolean) motd.getOrDefault("auto-slot-increment", false),
                    content,
                    versionHover
            );
            saveMOTD();
        }catch (IOException e){
            e.printStackTrace();
            this.motd = null;
        }
    }

    public void saveMOTD(){
        LinkedHashMap<String, Object> motd = new LinkedHashMap<>();
        motd.put("activated", this.motd.isActivated());
        motd.put("custom-version-protocol", this.motd.isCustomVersionProtocol());
        motd.put("custom-version-message", this.motd.getCustomVersionMessage());
        motd.put("auto-slot-increment", this.motd.isAutoSlotIncrement());
        motd.put("content", this.motd.getContent());
        motd.put("version-hover", this.motd.getVersionHover());

        try{
            saveYAML(pluginFolder + "/motd.yml", motd);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void loadTabList(){
        File f = new File(pluginFolder + "/tablist.yml");
        if(!f.exists()){
            try {
               //f.createNewFile();
                CopyFiles.copyRessource("proxy/tablist.yml", f);
            }catch (IOException e){
                e.printStackTrace();
                return;
            }
        }
        try {
            LinkedHashMap<String, Object> tabList = read(f);
            System.out.println("Tablist => "+tabList);
            int ticks = (int) tabList.getOrDefault("delay", 1);
            TabList header = readAnimation(Animations.HEADER, tabList);
            TabList footer = readAnimation(Animations.FOOTER, tabList);

            this.tabList = new TabListYAML(
                    (Boolean) tabList.getOrDefault("activated", true),
                    ticks,
                    header,
                    footer
            );
        }catch (IOException e){
            e.printStackTrace();
            this.tabList = null;
        }
    }

    public void reloadTabList(){
        loadTabList();
    }

    public void saveTabList(){
        LinkedHashMap<String, Object> tabList = new LinkedHashMap<>();
        tabList.put("activated", this.tabList.isActivate());
        tabList.put("delay", this.tabList.getDelay());
        tabList.put("header-animation", compressAnimation(this.getTabList().getHeader()));
        tabList.put("footer-animation", compressAnimation(this.getTabList().getFooter()));

        try{
            saveYAML(pluginFolder + "/tablist.yml", tabList);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public LinkedHashMap<String, Object> compressAnimation(TabList tl){
        LinkedHashMap<String, Object> compressed = new LinkedHashMap<>();
        compressed.put("override_lines", tl.getOverrideLines());
        compressed.put("default", tl.getDefaultLines());

        tl.getStates().forEach((n, state) -> {
            HashMap<String, Object> info = new HashMap<>();
            info.put("repeat", state.getRepeat());
            state.getLines().forEach((k,v)-> {info.put(k.toString(), v);});
            compressed.put(n.toString(), info);
        });

        return compressed;
    }

    private TabList readAnimation(Animations a, LinkedHashMap<String, Object> yml){
        String animation = a.toString().toLowerCase();
        LinkedHashMap<String, Object> tab = (LinkedHashMap<String, Object>) yml.getOrDefault(animation + "-animation", new LinkedHashMap<>());
        int lines = (int) tab.getOrDefault("override_lines", 4);
        HashMap<Integer, LineState> states = new HashMap<>();
        for(int i = 0 ; i != 10 ; i++){
            if(tab.containsKey(String.valueOf(i))){
                HashMap<String, Object> it = (HashMap<String, Object>) tab.get(String.valueOf(i));
                HashMap<Integer, String> linesContent = new HashMap<>();
                it.forEach((k, v) -> {
                    if(v instanceof String){
                        String l = k.replace("line-", "");
                        if(!l.equals(k)){
                            try {
                                int line = Integer.parseInt(l);
                                linesContent.put(line, (String) v);
                            }catch (NumberFormatException ignored){}
                        }
                    }
                });
                if(linesContent.size() != 0) {
                    LineState ls = new LineState(linesContent, (Integer) it.getOrDefault("repeat", 5));
                    states.put(i, ls);
                }
            }
        }

        HashMap<String, String> d = (HashMap<String, String>) tab.getOrDefault("default", null);
        HashMap<Integer, String> defaultLine = new HashMap<>();

        if(d != null){
            d.forEach((k, v) -> {
                String l = k.replace("line-", "");
                if(!l.equals(k)){
                    try {
                        int line = Integer.parseInt(l);
                        defaultLine.put(line, v);
                    }catch (NumberFormatException ignore){}
                }
            });
        }
        if(defaultLine.size() == 0) {
            defaultLine.put(3, "&7%online%/%max%");
        }

        return new TabList(
                lines,
                defaultLine,
                states
        );
    }

    private void loadMessages(){
        if(type == null){return;}
        File f = new File(pluginFolder + "/messages.yml");
        if(!f.exists()){
            try{
                f.createNewFile();
                CopyFiles.copyRessource(this.type.toLowerCase() + "/messages.yml", f);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        try {
            this.messagesManager = new MessagesManager(read(f));
        }catch (IOException e){
            e.printStackTrace();
            this.messagesManager = null;
        }
    }

    public static LinkedHashMap<String, Object> read(String file) throws IOException {
        return read(new File(file));
    }
    public static LinkedHashMap<String, Object> read(File file) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        if(file.length() == 0){
            return new LinkedHashMap<>();
        }
        BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);

        return (LinkedHashMap<String, Object>) new Yaml().load(reader);
    }

    public static void saveYAML(String file, LinkedHashMap<String, Object> yamlContent) throws IOException {
        saveYAML(new File(file), yamlContent);
    }
    public static void saveYAML(File file, LinkedHashMap<String, Object> yamlContent) throws IOException {
        if(!file.exists()){
            file.createNewFile();
        }
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        //options.setWidth(99999999);
        options.setAllowUnicode(true);
     //   BufferedWriter writer = new BufferedWriter(utf8Writer);
         BufferedWriter bw = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8);
     //   StringWriter sw = new StringWriter();
        // force utf-8 in yaml
       // new Yaml().dump(yamlContent, sw);
        System.out.println(yamlContent);
        bw.write(new Yaml(options).dump(yamlContent));
        bw.flush();
        bw.close();
    }
}
