package be.alexandre01.dnplugin.plugins.velocity;

import be.alexandre01.dnplugin.api.connection.IBasicClient;
import be.alexandre01.dnplugin.api.connection.IClientHandler;
import be.alexandre01.dnplugin.api.connection.request.RequestFile;
import be.alexandre01.dnplugin.api.connection.request.RequestManager;
import be.alexandre01.dnplugin.api.connection.request.channels.DNChannelManager;
import be.alexandre01.dnplugin.plugins.velocity.api.DNVelocityAPI;
import be.alexandre01.dnplugin.plugins.velocity.components.commands.Maintenance;
import be.alexandre01.dnplugin.plugins.velocity.components.commands.Slot;
import be.alexandre01.dnplugin.plugins.velocity.components.commands.TabList;
import be.alexandre01.dnplugin.plugins.velocity.listeners.PlayerServerListener;
import be.alexandre01.dnplugin.plugins.velocity.components.listeners.ServerPingListener;
import be.alexandre01.dnplugin.plugins.velocity.listeners.RedirectConnection;
import be.alexandre01.dnplugin.plugins.velocity.objects.PlayerManagement;
import be.alexandre01.dnplugin.api.utils.files.YAMLManager;
import be.alexandre01.dnplugin.api.utils.files.messages.MessagesManager;
import be.alexandre01.dnplugin.api.utils.files.network.NetworkYAML;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import lombok.Getter;
import lombok.Setter;
import org.bstats.velocity.Metrics;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

@Getter @Setter @Plugin(id = "dreamnetwork-plugin", name = "DreamNetwork Plugin for Velocity", version = "1.0.0-SNAPSHOT",
        url = "https://dreamnetwork.cloud", description = "We did it!", authors = {"Alexandre01"})
public class DNVelocity {
    @Getter private static DNVelocity instance;
    @Getter private static ImplAPI api;
    private IClientHandler clientHandler;
    private DNChannelManager dnChannelManager;
    private IBasicClient basicClient;
    private String version;
    private ServerInfo coreTemp;
    private String type;
    private int port;
    private RequestManager requestManager;
    public File file;
    @Getter private YAMLManager yamlManager;
    @Getter private NetworkYAML configuration;
    @Getter private MessagesManager messagesManager;
    @Getter private PlayerTabList playerTabList;
   // public Configuration configuration;
    /*public int slot = -2;
    public boolean isMaintenance;
    public boolean cancelKick;
    public String kickServerRedirection = null;
    public List<String> allowedPlayer;
    public String lobby;
    public boolean logoStatus;
    public boolean autoSendPlayer;
    public boolean connexionOnLobby;
    public int maxPerLobby;*/

    @Getter private final ProxyServer server;
    @Getter private final Logger logger;
    private final Metrics.Factory metricsFactory;
    //public TablistCustomizer tablistCustomizer;
    @Getter private final PlayerManagement playerManagement = new PlayerManagement();
    //@Getter private final PlayerManagement playerManagement = new PlayerManagement();
  //  private BungeeText bungeeText;
    @Inject
    public DNVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory, Metrics.Factory metricsFactory){
        this.server = server;
        this.logger = logger;
        instance = this;
        port = server.getBoundAddress().getPort();
        getLogger().info(server.getConfiguration().getQueryMap());
        getLogger().info(String.valueOf(server.getBoundAddress().getPort()));
        this.metricsFactory = metricsFactory;
        int pluginId = 18387; // <-- Replace with the id of your plugin!
        System.setProperty("bstats.relocatecheck","false");

        loadConfig(dataDirectory);
        type = "VELOCITY";
        api = new ImplAPI(this);
        this.dnChannelManager = new DNChannelManager();
        this.requestManager = new RequestManager(api);

        RequestFile requestFile = new RequestFile();

        /*try {
            requestFile.loadFile(  Config.getPath(dataDirectory.toRealPath()+"/DreamNetwork/requests.dream"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/

        //  getLogger().log(Level.INFO,"Enabling the Network Connection on the port "+port+"...");

    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // Do some operation demanding access to the Velocity API here.
        // For instance, we could register an event:

        // JAMAIS PRESENT

        System.out.println("Enabling the Network Connection on the port "+port+"...");

        System.out.println("Servers => "+server.getAllServers());
        server.getServer("core").ifPresent(serverInfo -> {
            coreTemp = serverInfo.getServerInfo();
        });
        if(!api.initConnection()){
            getLogger().severe("The connection to the server has failed.");
            // this.server.shutdown();
            return;
        }
        playerTabList = new PlayerTabList(server);
        playerTabList.start();

        EventManager eventManager = getServer().getEventManager();
        CommandManager commandManager = getServer().getCommandManager();

        eventManager.register(this, new PlayerServerListener());
        eventManager.register(this,new ServerPingListener());
        eventManager.register(this,new RedirectConnection());

        CommandMeta slotCommandMeta = commandManager.metaBuilder("slot").aliases("slots").plugin(this).build();
        commandManager.register(slotCommandMeta, new Slot());
        CommandMeta maintenanceCommandMeta = commandManager.metaBuilder("maintenance").plugin(this).build();
        commandManager.register(maintenanceCommandMeta, new Maintenance());
        CommandMeta tablistCommandMeta = commandManager.metaBuilder("tablist").plugin(this).build();
        commandManager.register(tablistCommandMeta, new TabList());
    }
    @Subscribe
    public void onProxyShutDown(ProxyShutdownEvent event) {
        // Do some operation demanding access to the Velocity API here.
        // For instance, we could register an event:
        DNVelocityAPI.getInstance().getClientHandler().getChannel().close();
    }

    public void loadConfig(Path dataDirectory){
        File f = new File(String.valueOf(dataDirectory));
        if(!f.exists()){
            f.mkdirs();
        }
        yamlManager = new YAMLManager(String.valueOf(dataDirectory), "PROXY");
        configuration = yamlManager.getNetwork();
        messagesManager = yamlManager.getMessagesManager();
    }

    public void saveConfig(){
        yamlManager.saveNetwork();
    }

    public String getMessage(String path, Player player){
        String msg = messagesManager.getString(path);
        if(msg == null){
            return "";
        }

        msg = msg.replace("%player%", player.getGameProfile().getName())
                .replace("%ping%", String.valueOf(player.getPing()))
                .replace("%max%", String.valueOf(getConfiguration().getSlots()))
                .replace("%online%", String.valueOf(getServer().getPlayerCount()));

        List<String> generals = messagesManager.getPaths("general");

        if(generals != null){
            for(String p : generals){
                String[] cut = p.split("\\.");
                msg = msg.replace("%" + cut[cut.length - 1] + "%", messagesManager.getString(p));
            }
        }

        return msg.replace("&", "§");
    }

  /*  public void loadConfig(){
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


    public String getMessage(String path) {
        return bungeeText.getMessage(path);
    }

    public String getMessage(String path,Object... objects) {
        return bungeeText.getMessage(path,objects);
    }*/
}
