package be.alexandre01.dnplugin.plugins.spigot.api;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.connection.IClientHandler;
import be.alexandre01.dnplugin.api.objects.player.DNPlayerManager;
import be.alexandre01.dnplugin.api.objects.server.DNServer;
import be.alexandre01.dnplugin.api.request.RequestManager;
import be.alexandre01.dnplugin.api.request.channels.DNChannelManager;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public interface DNSpigotAPI {
    static DNSpigotAPI getInstance()                                                      {
        return (DNSpigotAPI) NetworkBaseAPI.getInstance();
    }

    static NetworkBaseAPI getCommon() {
        return NetworkBaseAPI.getInstance();
    }

    IClientHandler getClientHandler();

    String getInfo();

    String getProcessName();

    String getServerName();

    void setServerName(String serverName);

    int getID();

    void setID(int id);

    void setProcessName(String processName);

    int getPort();

    Logger getLogger();

    RequestManager getRequestManager();

    DNChannelManager getChannelManager();

    void setRequestManager(RequestManager requestManager);

    void setClientHandler(IClientHandler basicClientHandler);

    void callServerAttachedEvent();

    void autoRefreshPlayers();

    void autoRefreshPlayers(boolean active);

    void autoRefreshPlayers(boolean active, long time);

    void sendPlayerTo(Player player, String server);

    void sendPlayerTo(Player player, DNServer dnServer);

    boolean hasAlreadyPlayerRefreshed();

    DNServer getCurrentServer();

    void shutdownProcess();

    boolean equals(Object o);

    int hashCode();

    DNPlayerManager getDnPlayerManager();
}
