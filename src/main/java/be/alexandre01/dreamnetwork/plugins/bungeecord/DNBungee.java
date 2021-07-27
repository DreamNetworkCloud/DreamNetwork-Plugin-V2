package be.alexandre01.dreamnetwork.plugins.bungeecord;

import be.alexandre01.dreamnetwork.api.request.RequestManager;
import be.alexandre01.dreamnetwork.connection.client.BasicClient;
import be.alexandre01.dreamnetwork.connection.client.handler.BasicClientHandler;
import be.alexandre01.dreamnetwork.plugins.bungeecord.api.DNBungeeAPI;
import be.alexandre01.dreamnetwork.plugins.bungeecord.components.commands.Maintenance;
import be.alexandre01.dreamnetwork.plugins.bungeecord.components.TablistCustomizer;
import be.alexandre01.dreamnetwork.plugins.bungeecord.components.commands.Slot;
import be.alexandre01.dreamnetwork.plugins.bungeecord.components.listeners.MOTD;
import be.alexandre01.dreamnetwork.plugins.bungeecord.listeners.RedirectConnection;
import be.alexandre01.dreamnetwork.plugins.spigot.api.DNSpigotAPI;
import be.alexandre01.dreamnetwork.utils.ASCII;
import lombok.Data;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
public class DNBungee extends Plugin {
    @Getter private static DNBungee instance;
    private BasicClientHandler basicClientHandler;
    private BasicClient basicClient;
    private String version;
    private String type;
    private int port;
    private RequestManager requestManager;
    public File file;
    public Configuration configuration;
    public int slot = -2;
    public boolean isMaintenance;
    public boolean cancelKick;
    public String kickServerRedirection = null;
    public List<String> allowedPlayer;
    public String lobby;
    public boolean logoStatus;
    public boolean connexionOnLobby;
    public TablistCustomizer tablistCustomizer;

    @Override
    public void onEnable(){
        instance = this;
        port = 25565;
        loadConfig();
        allowedPlayer = new ArrayList<>();
        if(!getProxy().getConfig().getListeners().isEmpty()){
            ListenerInfo listenerInfo = getProxy().getConfig().getListeners().stream().findFirst().get();
            port = listenerInfo.getHost().getPort();
        }


        type = "BUNGEE";

         tablistCustomizer = new TablistCustomizer();


        if(!configuration.contains("network.lobby")){
            configuration.set("network.lobby","lobby");
            saveConfig();
        }
        lobby = configuration.getString("network.lobby");

        if(!configuration.contains("network.connexionOnLobby")){
            configuration.set("network.connexionOnLobby",true);
            saveConfig();
        }
        connexionOnLobby = configuration.getBoolean("network.connexionOnLobby");


        if(!configuration.contains("network.maintenance")){
            configuration.set("network.maintenance",false);
            saveConfig();
        }
        isMaintenance = configuration.getBoolean("network.maintenance");

        if(!configuration.contains("network.kickRedirection")){
            configuration.set("network.kickRedirection.enabled",true);
            configuration.set("network.kickRedirection.server",lobby);
            saveConfig();
        }
        cancelKick = configuration.getBoolean("network.kickRedirection.enabled");
        if(cancelKick){
            kickServerRedirection = configuration.getString("network.kickRedirection.enabled");
        }
        if(!configuration.contains("network.status")){
            configuration.set("network.status.logo",true);
            saveConfig();
        }
        logoStatus = configuration.getBoolean("network.status.logo");

        if(!configuration.contains("network.allowed-players-maintenance")){
            configuration.set("network.allowed-players-maintenance",new ArrayList<>());
            saveConfig();
        }
        for(String string : configuration.getStringList("network.allowed-players-maintenance")){
            allowedPlayer.add(string.toLowerCase());
        }



        version = getProxy().getVersion();


        ASCII.sendDNText();

        System.out.println("\n");

        if(configuration.contains("network.slot")){
            slot = configuration.getInt("network.slot");
        }

        DNBungeeAPI dnBungeeAPI = new DNBungeeAPI(this);


        getProxy().getPluginManager().registerListener(this,new RedirectConnection());
        getProxy().getPluginManager().registerListener(this,new MOTD());

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new Maintenance("maintenance"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new Slot("slot"));

        this.requestManager = new RequestManager();

        //  getLogger().log(Level.INFO,"Enabling the Network Connection on the port "+port+"...");
        basicClient = new BasicClient();


        Thread thread = new Thread(basicClient);
        thread.start();

        System.out.println(getBasicClientHandler());

    }

    @Override
    public void onDisable(){
        DNSpigotAPI.getInstance().getBasicClientHandler().getChannel().close();
    }

    public void loadConfig(){
        File theDir = new File(ProxyServer.getInstance().getPluginsFolder(), "/DreamNetwork/");
        if(!theDir.exists()){
            theDir.mkdirs();
        }

        file = new File(ProxyServer.getInstance().getPluginsFolder(), "/DreamNetwork/network.yml");
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
