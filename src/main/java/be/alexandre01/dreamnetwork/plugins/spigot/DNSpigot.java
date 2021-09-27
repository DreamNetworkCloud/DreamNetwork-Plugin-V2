package be.alexandre01.dreamnetwork.plugins.spigot;

import be.alexandre01.dreamnetwork.api.request.RequestManager;
import be.alexandre01.dreamnetwork.api.request.channels.DNChannelManager;
import be.alexandre01.dreamnetwork.connection.client.BasicClient;
import be.alexandre01.dreamnetwork.connection.client.handler.BasicClientHandler;
import be.alexandre01.dreamnetwork.plugins.spigot.api.DNSpigotAPI;
import be.alexandre01.dreamnetwork.plugins.spigot.command.NetworkCommand;
import be.alexandre01.dreamnetwork.plugins.spigot.listeners.ReloadListener;
import be.alexandre01.dreamnetwork.utils.ASCII;
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
    @Getter @Setter private BasicClientHandler basicClientHandler;
    @Getter private BasicClient basicClient;
    @Getter private String version;
    @Getter private String type;
    @Getter private int port;
    @Getter private RequestManager requestManager;
    @Getter private DNChannelManager dnChannelManager;
    public boolean isReloading = false;
    @Override
    public void onEnable(){
        instance = this;
        port = 25565;

        port = getServer().getPort();

        type = "SPIGOT";

        String a = getServer().getClass().getPackage().getName();
        version = a.substring(a.lastIndexOf('.') + 1);
        //version = "1.8";

        ASCII.sendDNText();

        System.out.println("\n");

        DNSpigotAPI dnSpigotAPI = new DNSpigotAPI(this);


        this.requestManager = new RequestManager();
        this.dnChannelManager = new DNChannelManager();
      //  getLogger().log(Level.INFO,"Enabling the Network Connection on the port "+port+"...");
        basicClient = new BasicClient();
        Thread thread = new Thread(basicClient);
        thread.start();

        registerCommand("network",new NetworkCommand("network"));
        getServer().getPluginManager().registerEvents(new ReloadListener(),this);
    }

    @Override
    public void onDisable(){
        if(isReloading){
            Bukkit.broadcastMessage("§cLe serveur est entrain de se réactualiser, des lags peuvent être ressentit.");
        }else {
            for(Player player : Bukkit.getOnlinePlayers()){
                player.kickPlayer("Le serveur est entrain de s'éteindre.");
            }
            DNSpigotAPI.getInstance().getBasicClientHandler().getChannel().close();
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


}
