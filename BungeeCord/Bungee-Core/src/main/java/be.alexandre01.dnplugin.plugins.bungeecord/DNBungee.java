package be.alexandre01.dnplugin.plugins.bungeecord;

import be.alexandre01.dnplugin.api.connection.IBasicClient;
import be.alexandre01.dnplugin.api.connection.IClientHandler;
import be.alexandre01.dnplugin.api.request.RequestFile;
import be.alexandre01.dnplugin.api.request.RequestManager;
import be.alexandre01.dnplugin.api.request.channels.DNChannelManager;
import be.alexandre01.dnplugin.plugins.bungeecord.api.DNBungeeAPI;
import be.alexandre01.dnplugin.plugins.bungeecord.components.TablistCustomizer;
import be.alexandre01.dnplugin.plugins.bungeecord.components.commands.Maintenance;
import be.alexandre01.dnplugin.plugins.bungeecord.components.commands.Slot;
import be.alexandre01.dnplugin.plugins.bungeecord.components.commands.TabList;
import be.alexandre01.dnplugin.plugins.bungeecord.listeners.PlayerServerListener;
import be.alexandre01.dnplugin.plugins.bungeecord.listeners.RedirectConnection;
import be.alexandre01.dnplugin.plugins.bungeecord.components.listeners.ServerPingListener;
import be.alexandre01.dnplugin.plugins.bungeecord.objects.PlayerManagement;
import be.alexandre01.dnplugin.plugins.bungeecord.utils.BungeeText;
import be.alexandre01.dnplugin.utils.ASCII;
import be.alexandre01.dnplugin.utils.Config;
import be.alexandre01.dnplugin.utils.files.YAMLManager;
import be.alexandre01.dnplugin.utils.files.messages.MessagesManager;
import be.alexandre01.dnplugin.utils.files.network.NetworkYAML;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;

import java.io.File;
import java.util.Date;
import java.util.List;

@Getter @Setter
public class DNBungee extends Plugin {
    @Getter private static DNBungee instance;
    @Getter private static ImplAPI api;
    private IClientHandler clientHandler;
    private DNChannelManager dnChannelManager;
    private IBasicClient basicClient;
    private String version;
    private String type;
    private int port;
    private RequestManager requestManager;
    public File file;
    //public Configuration configuration;
    public YAMLManager yamlManager;
    public NetworkYAML configuration;
    public MessagesManager messagesManager;
    public PlayerTabList playerTabList;
    //public int slot = -2;
    //public boolean isMaintenance;
    public boolean cancelKick;
    //public String kickServerRedirection = null;
    //public List<String> allowedPlayer;
    //public String lobby;
    //public boolean logoStatus;
    //public boolean autoSendPlayer;
    //public boolean connexionOnLobby;
    //public int maxPerLobby;

    public TablistCustomizer tablistCustomizer;
    @Getter private final PlayerManagement playerManagement = new PlayerManagement();
    private BungeeText bungeeText;

    @Override
    public void onEnable(){
        instance = this;
        //INIT defaultBungeeText
        /*new BungeeText(true).init();
        bungeeText = new BungeeText(false);
        bungeeText.init();
        System.out.println(bungeeText.messages.values());
        System.out.println(bungeeText.messages.keySet());
        System.out.println(getMessage("connect.noLobby"));
        System.out.println(getMessage("connect.test"));*/
        //port = 25565;
        port = getProxy().getConfig().getListeners().stream().findFirst().get().getHost().getPort();
        loadConfig();
        //allowedPlayer = new ArrayList<>();
        if(!getProxy().getConfig().getListeners().isEmpty()){
            ListenerInfo listenerInfo = getProxy().getConfig().getListeners().stream().findFirst().get();
            port = listenerInfo.getHost().getPort();
        }

        int pluginId = 15799; // <-- Replace with the id of your plugin!
        System.setProperty("bstats.relocatecheck","false");
        Metrics metrics = new Metrics(this, pluginId);

        type = "BUNGEE";

        //tablistCustomizer = new TablistCustomizer();
        playerTabList = new PlayerTabList();
        playerTabList.start();


        /*if(!configuration.contains("network.lobby")){
            configuration.set("network.lobby","main/lobby");
            saveConfig();
        }

        lobby = configuration.getString("network.lobby");



        if(!configuration.contains("network.connexionOnLobby")){
            configuration.set("network.connexionOnLobby",false);
            saveConfig();
        }
        if(!configuration.contains("network.maxPerLobby")){
            configuration.set("network.maxPerLobby",15);
            saveConfig();
        }
        maxPerLobby =  configuration.getInt("network.maxPerLobby");
        connexionOnLobby = configuration.getBoolean("network.connexionOnLobby");


        if(!configuration.contains("network.maintenance")){
            configuration.set("network.maintenance",false);
            saveConfig();
        }
        isMaintenance = configuration.getBoolean("network.maintenance");

        if(!configuration.contains("network.kickRedirection")){
            configuration.set("network.kickRedirection.enabled",false);
            configuration.set("network.kickRedirection.server",lobby);
            saveConfig();
        }
        cancelKick = configuration.getBoolean("network.kickRedirection.enabled");
        if(cancelKick){
            kickServerRedirection = configuration.getString("network.kickRedirection.enabled");
        }
        if(!configuration.contains("network.status")){
            configuration.set("network.status.logo",false);
            saveConfig();
        }
        logoStatus = configuration.getBoolean("network.status.logo");

        if(!configuration.contains("network.allowed-players-maintenance")){
            configuration.set("network.allowed-players-maintenance",new ArrayList<>());
            saveConfig();
        }
        if(!configuration.contains("network.players.autoSend")){
            configuration.set("network.players.autoSend",false);
            saveConfig();
        }
        if(!configuration.contains("network.slots.autoSend")){
            configuration.set("network.slots.autoSend",false);
            saveConfig();
        }

        if(!configuration.contains("network.slot")){
            configuration.set("network.slot",1000);
            saveConfig();
        }

        autoSendPlayer = configuration.getBoolean("network.players.autoSend");
        for(String string : configuration.getStringList("network.allowed-players-maintenance")){
            allowedPlayer.add(string.toLowerCase());
        }*/



        version = getProxy().getVersion();


        ASCII.sendDNText();

        System.out.println("\n");

        /*if(configuration.contains("network.slot")){
            slot = configuration.getInt("network.slot");
        }*/

        api = new ImplAPI(this);



        getProxy().getPluginManager().registerListener(this,new RedirectConnection());
        if(configuration.isAutoSendPlayers())
            getProxy().getPluginManager().registerListener(this,new PlayerServerListener());
        /*MOTD motd = new MOTD();
        if(motd.isActivated())
            getProxy().getPluginManager().registerListener(this,motd);*/
        getProxy().getPluginManager().registerListener(this, new ServerPingListener());

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new Maintenance("maintenance"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new Slot("slot"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TabList("tablist"));
        this.dnChannelManager = new DNChannelManager();
        this.requestManager = new RequestManager();

        RequestFile requestFile = new RequestFile();
        requestFile.loadFile(  Config.getPath(ProxyServer.getInstance().getPluginsFolder() + "/DreamNetwork/requests.dream"));

        //  getLogger().log(Level.INFO,"Enabling the Network Connection on the port "+port+"...");
        if(!api.initConnection()){
            getLogger().severe("The connection to the server has failed.");
            super.getProxy().stop();
            return;
        }

    }

    @Override
    public void onDisable(){
        DNBungeeAPI.getInstance().getClientHandler().getChannel().close();
    }

    public void loadConfig(){
        File theDir = new File(ProxyServer.getInstance().getPluginsFolder(), "/DreamNetwork/");
        if(!theDir.exists()){
            theDir.mkdirs();
        }

        /*file = new File(ProxyServer.getInstance().getPluginsFolder(), "/DreamNetwork/network.yml");
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
        }*/

        yamlManager = new YAMLManager(getProxy().getPluginsFolder().getPath() + "/DreamNetwork", "PROXY");
        configuration = yamlManager.getNetwork();
        messagesManager = yamlManager.getMessagesManager();
    }

    public void saveConfig(){
        /*try{
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
        }catch (IOException e){
            e.printStackTrace();
        }*/
        yamlManager.saveNetwork();
    }


    public String getMessage(String path, ProxiedPlayer player) {
        String msg = messagesManager.getString(path);
        if(msg == null){
            return "";
        }
        msg = msg
                .replace("%player%", player.getName())
                .replace("%ping%", String.valueOf(player.getPing()))
                .replace("%max%", String.valueOf(getConfiguration().getSlots()))
                .replace("%online%", String.valueOf(getProxy().getPlayers().size()));
        List<String> generals = messagesManager.getPaths("general");

        for(String p : generals){
            String[] cut = p.split("\\.");
            msg = msg.replace("%" + cut[cut.length-1] + "%", messagesManager.getString(p));
        }

       return msg.replace("&", "§");
        //return bungeeText.getMessage(path);
    }

    private String completeMessagePart(String part, ProxiedPlayer player){
        switch (part){
            case "player":
                return player.getName();
            case "ping":
                return String.valueOf(player.getPing());
            default:
                return (messagesManager.getString(part) != null ? messagesManager.getString(part) : part);
        }
    }

    /*public String getMessage(String path,Object... objects) {
        return bungeeText.getMessage(path,objects);
    }*/
}
