package be.alexandre01.dnplugin.plugins.spigot;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.connection.IBasicClient;
import be.alexandre01.dnplugin.api.connection.IClientHandler;
import be.alexandre01.dnplugin.api.objects.server.DNServer;
import be.alexandre01.dnplugin.api.connection.request.RequestFile;
import be.alexandre01.dnplugin.api.connection.request.RequestManager;
import be.alexandre01.dnplugin.api.connection.request.channels.DNChannelManager;
import be.alexandre01.dnplugin.plugins.spigot.command.NetworkCommand;
import be.alexandre01.dnplugin.plugins.spigot.command.StatsCommand;
import be.alexandre01.dnplugin.plugins.spigot.listeners.ReloadListener;
import be.alexandre01.dnplugin.plugins.spigot.listeners.RestartListener;
import be.alexandre01.dnplugin.plugins.spigot.utils.SpigotText;
import be.alexandre01.dnplugin.api.utils.ASCII;
import be.alexandre01.dnplugin.api.utils.Config;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;


public class DNSpigot extends JavaPlugin{

    @Getter private static DNSpigot instance;
    @Getter @Setter private IClientHandler basicClientHandler;
    @Getter private IBasicClient basicClient;
    @Getter private String version;
    @Getter private String type;
    @Getter private int port;
    @Getter private RequestManager requestManager;
    @Getter private DNChannelManager dnChannelManager;
    @Getter @Setter private DNServer currentServer;
    private static ImplAPI api;
    private SpigotText spigotText;
    public boolean isReloading = false;
    @Override
    public void onEnable(){
        instance = this;
        new SpigotText(true).init();
        spigotText = new SpigotText(false);
        spigotText.init();


        port = 25565;



        port = getServer().getPort();


        type = "SPIGOT";

        String a = getServer().getClass().getPackage().getName();
        version = a.substring(a.lastIndexOf('.') + 1);
        //version = "1.8";

        ASCII.sendDNText();

        System.out.println("\n");

        api = new ImplAPI(this);


        this.requestManager = new RequestManager(api);
        this.dnChannelManager = new DNChannelManager();

        RequestFile requestFile = new RequestFile();
        requestFile.loadFile(Config.getPath(getServer().getWorldContainer() + "/plugins/DreamNetwork/requests.dream"));
      //  getLogger().log(Level.INFO,"Enabling the Network Connection on the port "+port+"...");
        if(!api.initConnection()){
            getLogger().severe("The connection to the server has failed.");
            Bukkit.getServer().shutdown();
            return;
        }

        registerCommand("network",new NetworkCommand("network"));
        registerCommand("dnstats",new StatsCommand("dnstats"));
        getServer().getPluginManager().registerEvents(new ReloadListener(),this);
        getServer().getPluginManager().registerEvents(new RestartListener(),this);
        //getServer().getPluginManager().registerEvents(new TestChannelListener(),this);

        //Register BungeeCord Channel
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }
    @Override
    public void onDisable(){
        if(isReloading){
            Bukkit.broadcastMessage("§cLe serveur est entrain de se réactualiser, des lags peuvent être ressentit.");
        }else {
            for(Player player : Bukkit.getOnlinePlayers()){
                getMessage("server.shutdown");
                player.kickPlayer("Le serveur est entrain de s'éteindre.");
            }
            getAPI().getClientHandler().getChannel().close();
        }

    }

    public void registerCommand(String commandName, Command commandClass){
        try{
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            commandMap.register(commandName, commandClass);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public String getMessage(String path,Object... objects) {
        return spigotText.getMessage(path,objects);
    }

    public static ImplAPI getAPI(){
        return api;
    }

}
