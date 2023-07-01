package be.alexandre01.dnplugin.plugins.velocity;

import be.alexandre01.dnplugin.api.connection.IBasicClient;
import be.alexandre01.dnplugin.api.connection.IClientHandler;
import be.alexandre01.dnplugin.api.request.RequestFile;
import be.alexandre01.dnplugin.api.request.RequestManager;
import be.alexandre01.dnplugin.api.request.channels.DNChannelManager;
import be.alexandre01.dnplugin.plugins.velocity.api.DNVelocityAPI;
import be.alexandre01.dnplugin.plugins.velocity.listeners.PlayerListener;
import be.alexandre01.dnplugin.utils.ASCII;
import be.alexandre01.dnplugin.utils.Config;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import lombok.Getter;
import lombok.Setter;
import org.bstats.velocity.Metrics;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
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
   // public Configuration configuration;
    public int slot = -2;
    public boolean isMaintenance;
    public boolean cancelKick;
    public String kickServerRedirection = null;
    public List<String> allowedPlayer;
    public String lobby;
    public boolean logoStatus;
    public boolean autoSendPlayer;
    public boolean connexionOnLobby;
    public int maxPerLobby;

    private final ProxyServer server;
    private final Logger logger;
    private final Metrics.Factory metricsFactory;
    //public TablistCustomizer tablistCustomizer;
    //@Getter private final PlayerManagement playerManagement = new PlayerManagement();
  //  private BungeeText bungeeText;
    @Inject
    public DNVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory, Metrics.Factory metricsFactory){
        this.server = server;
        this.logger = logger;
        instance = this;
        port = server.getBoundAddress().getPort();
        System.out.println(server.getConfiguration().getQueryMap());
        System.out.println(server.getBoundAddress().getPort());

        //port = server.getConfiguration().();
        this.metricsFactory = metricsFactory;
        int pluginId = 18387; // <-- Replace with the id of your plugin!
        System.setProperty("bstats.relocatecheck","false");

      //  Metrics metrics = metricsFactory.make(this,pluginId);

        //INIT defaultBungeeText

       // loadConfig();
        allowedPlayer = new ArrayList<>();
       /* if(!getProxy().getConfig().getListeners().isEmpty()){
            ListenerInfo listenerInfo = getProxy().getConfig().getListeners().stream().findFirst().get();
            port = listenerInfo.getHost().getPort();
        }*/



        //server.getEventManager().register(this, this);
        type = "VELOCITY";


        api = new ImplAPI(this);

        this.dnChannelManager = new DNChannelManager();
        this.requestManager = new RequestManager();

        RequestFile requestFile = new RequestFile();

        /*try {
            requestFile.loadFile(  Config.getPath(dataDirectory.toRealPath()+"/DreamNetwork/requests.dream"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/

        //  getLogger().log(Level.INFO,"Enabling the Network Connection on the port "+port+"...");
        if(!api.initConnection()){
            getLogger().severe("The connection to the server has failed.");
           // this.server.shutdown();
            return;
        }
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // Do some operation demanding access to the Velocity API here.
        // For instance, we could register an event:
        System.out.println("Salut");
        server.getServer("core").ifPresent(serverInfo -> {
            coreTemp = serverInfo.getServerInfo();
        });

        getServer().getEventManager().register(this, new PlayerListener());
    }
    @Subscribe
    public void onProxyShutDown(ProxyShutdownEvent event) {
        // Do some operation demanding access to the Velocity API here.
        // For instance, we could register an event:
        DNVelocityAPI.getInstance().getClientHandler().getChannel().close();
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
