package be.alexandre01.dnplugin.plugins.bungeecord.components.listeners;

import be.alexandre01.dnplugin.plugins.bungeecord.DNBungee;
import be.alexandre01.dnplugin.plugins.bungeecord.utils.BaseComponentBuilder;
//import be.alexandre01.dnplugin.utils.Config;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MOTD implements Listener {/*
    DNBungee dnBungee;
    private File file;
    private Configuration configuration;
    private final HashMap<Integer,String> content_lines = new HashMap<>();
    private final HashMap<Integer, String> hover_lines = new HashMap<>();
    private ServerPing.PlayerInfo[] hover_final;
    private TextComponent content_final;

    private boolean slot_max_players_increment;
    private String version = "none";
    private boolean hasCustomVersionProtocol = false;
    @Getter
    private boolean activated = true;

    String slotNumber;

    public MOTD(){
        dnBungee = DNBungee.getInstance();
        loadConfig();
        readConfig();
        if(!configuration.getBoolean("motd.activated")){
            activated = false;
            return;
        }

        /*if(dnBungee.slot == -2){
            slot_max_players_increment = true;
        }*/
    //}

    /*
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPingTheProxy(final ProxyPingEvent e) {
        final ServerPing srvPing = e.getResponse();
        final ServerPing.Protocol version = srvPing.getVersion();
        final ServerPing.Players players = srvPing.getPlayers();


        players.setSample(hover_final);
        if(slot_max_players_increment){;
            if(dnBungee.slot != -2 && dnBungee.slot <= players.getOnline()){
                srvPing.setPlayers(new ServerPing.Players(dnBungee.slot,players.getOnline(),players.getSample()));
            }else {
                srvPing.setPlayers(new ServerPing.Players(players.getOnline()+1,players.getOnline(),players.getSample()));
            }
        }else{
            srvPing.setPlayers(new ServerPing.Players(dnBungee.slot,players.getOnline(),players.getSample()));
        }


        if(hasCustomVersionProtocol){
            version.setProtocol(999);
        }

        if(!this.version.equalsIgnoreCase("none")){
           // version.setName("§4[§e1.8§c+§4] §e("+ srvPing.getPlayers().getOnline()+"§c╱§e"+ srvPing.getPlayers().getMax()+")");
            version.setName(this.version
                    .replaceAll("&","§")
                    .replaceAll("%max%", srvPing.getPlayers().getMax()+"")
                    .replaceAll("%online%",srvPing.getPlayers().getOnline()+""));
        }


        srvPing.setVersion(version);
        /*TextComponent component = new TextComponent();
        component.addExtra("                     §e§l✯ §9§lInazumaUHC §e§l✯ §f§n§l\n");
       // component.addExtra("            §e▅▆▇ §6§lClassico §f|§9 Inazuma Eleven §f|§a Autres  §e▇▆▅");
        component.addExtra("            §e▅▆▇ §6§lDu lourd arrive ▇▆▅");*/
        //srvPing.setDescriptionComponent(this.content_final);

        //e.setResponse(srvPing);
    //}
    /*
    public boolean readConfig(){
        try {
            hasCustomVersionProtocol = configuration.getBoolean("motd.custom-version-protocol");
            slot_max_players_increment = configuration.getBoolean("motd.auto-slot-increment");
            version = configuration.getString("motd.custom-version-message");

            for(String key : configuration.getSection("motd.content").getKeys()){

                if(key.toLowerCase().startsWith("line-") && Character.isDigit(key.charAt(key.length()-1))){
                    int line = Character.getNumericValue(key.charAt(key.length()-1));
                    content_lines.put(line,configuration.getString("motd.content."+key));
                }
            }
            for(String key : configuration.getSection("motd.version-hover").getKeys()){
                if(key.toLowerCase().startsWith("line-") && Character.isDigit(key.charAt(key.length()-1))){
                    int line = Character.getNumericValue(key.charAt(key.length()-1));
                    hover_lines.put(line,configuration.getString("motd.version-hover."+key));
                }
            }

            BaseComponentBuilder baseComponentBuilder = new BaseComponentBuilder();

            for (Integer line : content_lines.keySet()) {
                baseComponentBuilder.setLine(line, content_lines.get(line));
            }
            content_final = baseComponentBuilder.toTextComponent();


            ArrayList<ServerPing.PlayerInfo> p = new ArrayList<>();

            for (Integer line : hover_lines.keySet()) {

                p.add(new ServerPing.PlayerInfo(hover_lines.get(line).replaceAll("&","§"),UUID.randomUUID()));
            }
            hover_final = new ServerPing.PlayerInfo[p.size()];
            hover_final = p.toArray(hover_final);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }
    */
    /*
    public void loadConfig(){
        File theDir = new File(ProxyServer.getInstance().getPluginsFolder(), "/DreamNetwork/");
        if(!theDir.exists()){
            theDir.mkdirs();
        }

        file = new File(ProxyServer.getInstance().getPluginsFolder(), "/DreamNetwork/motd.yml");
        try{
            if(!file.exists()){
                file.createNewFile();

                try (InputStream is = getClass().getClassLoader().getResourceAsStream("bungeecord/motd.yml")) {
                    if(is == null){
                        System.out.println("no configuration for motd.yml");
                        return;
                    }
                    Config.write(is, file);
                } catch (IOException e) {
                    System.out.println("no input configuration for motd.yml");
                    e.printStackTrace();
                    // An error occurred copying the resource
                }
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
    */
/*
    public void saveConfig(){
        try{
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
*/
}