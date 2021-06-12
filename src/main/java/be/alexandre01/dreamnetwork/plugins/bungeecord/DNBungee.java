package be.alexandre01.dreamnetwork.plugins.bungeecord;

import be.alexandre01.dreamnetwork.api.request.RequestManager;
import be.alexandre01.dreamnetwork.connection.client.BasicClient;
import be.alexandre01.dreamnetwork.connection.client.handler.BasicClientHandler;
import be.alexandre01.dreamnetwork.plugins.bungeecord.api.DNBungeeAPI;
import be.alexandre01.dreamnetwork.plugins.spigot.api.DNSpigotAPI;
import be.alexandre01.dreamnetwork.utils.ASCII;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.plugin.Plugin;

public class DNBungee extends Plugin {
    @Getter
    private static DNBungee instance;
    @Getter @Setter
    private BasicClientHandler basicClientHandler;
    @Getter private BasicClient basicClient;
    @Getter private String version;
    @Getter private String type;
    @Getter private int port;
    @Getter private RequestManager requestManager;


    @Override
    public void onEnable(){
        instance = this;
        port = 25565;

        if(!getProxy().getConfig().getListeners().isEmpty()){
            ListenerInfo listenerInfo = getProxy().getConfig().getListeners().stream().findFirst().get();
            port = listenerInfo.getHost().getPort();
        }

        type = "BUNGEE";

        version = getProxy().getVersion();
        //version = "1.8";

        ASCII.sendDNText();

        System.out.println("\n");


        DNBungeeAPI dnBungeeAPI = new DNBungeeAPI(this);

        this.requestManager = new RequestManager();
        //  getLogger().log(Level.INFO,"Enabling the Network Connection on the port "+port+"...");
        basicClient = new BasicClient();
        Thread thread = new Thread(basicClient);
        basicClient.start();

    }
}
