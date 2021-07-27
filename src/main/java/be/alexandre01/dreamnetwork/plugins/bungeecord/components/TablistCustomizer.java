package be.alexandre01.dreamnetwork.plugins.bungeecord.components;

import be.alexandre01.dreamnetwork.plugins.bungeecord.DNBungee;
import be.alexandre01.dreamnetwork.plugins.bungeecord.utils.BaseComponentBuilder;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class TablistCustomizer {
    private File file;
    private Configuration configuration;
    private DNBungee dnBungee;
    private int header_lineToOverride;
    private int footer_lineToOverride;
    private final HashMap<Integer, String> header_default = new HashMap<>();
    private final HashMap<Integer, String> footer_default = new HashMap<>();
    private final HashMap<Integer, HashMap<Integer, String>> header_lines_by_tick = new HashMap<>();
    private final HashMap<Integer, HashMap<Integer, String>> footer_lines_by_tick = new HashMap<>();
    private final HashMap<Integer,Integer> header_repeat_by_tick = new HashMap<>();
    private final HashMap<Integer, Integer> footer_repeat_by_tick = new HashMap<>();

    private final HashMap<Integer,TextComponent> header_final = new HashMap<>();
    private final HashMap<Integer, TextComponent> footer_final = new HashMap<>();
    public TablistCustomizer() {
        loadConfig();
        this.dnBungee = DNBungee.getInstance();
        if(!configuration.getBoolean("tablist.activated")){
            return;
        }
        readTablist();
        dnBungee.getProxy().getScheduler().schedule(dnBungee, new Runnable() {
            int i = 0;
            @Override
            public void run() {

                for(ProxiedPlayer player: dnBungee.getProxy().getPlayers()){
                    updateTab(player,i);
                }

                if(i == 1){
                    i = 0;
                }else {
                    i++;
                }

            }
        },0,configuration.getInt("tablist.ticks")* 50L, TimeUnit.MILLISECONDS);
    }

    public void updateTab(ProxiedPlayer player, int actualTick){
        TextComponent header = new TextComponent();
        TextComponent footer = new TextComponent();

        if(actualTick == 0){
            header.addExtra("   §5▰▰▰▰▰▰▰▰▰▰▰ §9§lShirome §5▰▰▰▰▰▰▰▰▰▰▰");
        }else {
            header.addExtra("   §d▰▰▰▰▰▰▰▰▰▰▰ §3§lShirome §d▰▰▰▰▰▰▰▰▰▰▰");
        }

        header.addExtra("\n\n");
        header.addExtra("§7Joueurs en ligne: "+dnBungee.getProxy().getPlayers().size());
        header.addExtra("\n");
        footer.addExtra("\n");
        footer.addExtra("§2Ping : §a"+ player.getPing());
        footer.addExtra("\n\n");
        footer.addExtra("§7Site: §dwww§5.§dshirome§5.§deu\n");
        footer.addExtra("§7Discord: §ddiscord§5.§dshirome§5.§dfr");
        player.setTabHeader(header,footer);
    }



    public void readTablist(){
        for(String key : configuration.getSection("tablist.header-animation").getKeys()){
            String[] sections = key.split("\\.");
            if(sections[0].equalsIgnoreCase("default")){
                distributeText(header_default,sections,"header");
            }
            if(Character.isDigit(sections[0].charAt(0)) && sections[0].length() < 2){
                int tick = Character.getNumericValue(sections[1].charAt(0));
                HashMap<Integer, String> workingHM;
                header_lines_by_tick.put(tick,workingHM = new HashMap<>());
                distributeText(workingHM,sections,"header");
                if(sections[1].equalsIgnoreCase("repeat")) {
                    header_repeat_by_tick.put(tick,configuration.getInt("tablist.header-animation."+sections[0]+"."+sections[1]));
                }
            }
        }
        HashMap<Integer, BaseComponentBuilder> b = new HashMap<>();

        for (int i = 1; i < header_lines_by_tick.size()+1; i++) {
            b.put(i, new BaseComponentBuilder());
        }

        header_default.entrySet().forEach(entry -> {
            for (int i = 1; i < header_lines_by_tick.size()+1; i++) {
                BaseComponentBuilder baseComponentBuilder;
                baseComponentBuilder = b.get(i);
                baseComponentBuilder.setLine(entry.getKey(),entry.getValue());
            }
        });

        header_lines_by_tick.entrySet().forEach(entry -> {
            BaseComponentBuilder baseComponentBuilder;
            baseComponentBuilder = b.get(entry.getKey());
            for(Integer line : entry.getValue().keySet()){
                baseComponentBuilder.setLine(line,entry.getValue().get(line));
            }
            header_final.put(entry.getKey(),baseComponentBuilder.toTextComponent());
        });


        for(String key : configuration.getSection("tablist.footer-animation").getKeys()){

        }
    }

    public void distributeText(HashMap<Integer, String> h, String[] sections,String type){
        if(sections[1].toLowerCase().startsWith("line-") && Character.isDigit(sections[1].charAt(sections[1].length()-1))){
            int line = Character.getNumericValue(sections[1].charAt(sections[1].length()-1));
            h.put(line,configuration.getString("tablist."+type+"-animation."+sections[0]+"."+sections[1]));
        }
    }
    public void loadConfig(){
        File theDir = new File(ProxyServer.getInstance().getPluginsFolder(), "/DreamNetwork/");
        if(!theDir.exists()){
            theDir.mkdirs();
        }

        file = new File(ProxyServer.getInstance().getPluginsFolder(), "/DreamNetwork/tablist.yml");
        try{
            if(!file.exists()){
                file.createNewFile();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void saveConfig(){
        try{
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
        }catch (IOException e){
            e.printStackTrace();
        }
    }


}
