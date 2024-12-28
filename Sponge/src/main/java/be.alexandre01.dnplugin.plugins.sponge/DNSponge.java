package be.alexandre01.dnplugin.plugins.sponge;

import be.alexandre01.dnplugin.api.request.RequestManager;
import be.alexandre01.dnplugin.api.request.channels.DNChannelManager;
import be.alexandre01.dnplugin.connection.client.BasicClient;
import be.alexandre01.dnplugin.connection.client.handler.BasicClientHandler;
import be.alexandre01.dnplugin.plugins.sponge.api.DNSpongeAPI;
import be.alexandre01.dnplugin.api.utils.ASCII;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;

@Plugin(id = "dreamnetwork",name = "DreamNetwork",version = "1.0",description = "Best Network Ever Made")
public class DNSponge {
    @Getter
    private static DNSponge instance;
    @Getter @Setter
    private BasicClientHandler basicClientHandler;
    @Getter
    private DNChannelManager dnChannelManager;
    @Getter private BasicClient basicClient;
    @Getter private String version;
    @Getter private String type;
    @Getter private int port;
    @Getter private RequestManager requestManager;
    public boolean isReloading = false;
    @Getter public Logger logger;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        instance = this;
        port = 25565;

        port = Sponge.getServer().getBoundAddress().get().getPort();

        type = "SPONGE";

        String a = "Test-SPONGE";
        version = a.substring(a.lastIndexOf('.') + 1);
        //version = "1.8";

        ASCII.sendDNText();

        System.out.println("\n");

        DNSpongeAPI dnSpongeAPI = new DNSpongeAPI(this);

        this.dnChannelManager = new DNChannelManager();
        this.requestManager = new RequestManager();
        //  getLogger().log(Level.INFO,"Enabling the Network Connection on the port "+port+"...");
        basicClient = new BasicClient();
        Thread thread = new Thread(basicClient);
        thread.start();

        //registerCommand("network",new NetworkCommand("network"));
        //getServer().getPluginManager().registerEvents(new ReloadListener(),this);
    }
}
