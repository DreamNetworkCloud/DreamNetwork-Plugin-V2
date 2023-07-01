package be.alexandre01.dnplugin.plugins.bungeecord.api;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.connection.IClientHandler;
import be.alexandre01.dnplugin.api.request.RequestManager;
import be.alexandre01.dnplugin.api.request.channels.DNChannelManager;

import java.util.logging.Logger;

public interface DNBungeeAPI {
    static DNBungeeAPI getInstance() {
        return (DNBungeeAPI) NetworkBaseAPI.getInstance();
    }

    IClientHandler getClientHandler();

    String getInfo();

    String getProcessName();

    void setProcessName(String processName);

    String getServerName();

    void setServerName(String serverName);

    int getID();

    void setID(int id);

    int getPort();

    Logger getLogger();

    RequestManager getRequestManager();

    DNChannelManager getChannelManager();

    void setRequestManager(RequestManager requestManager);

    void setClientHandler(IClientHandler basicClientHandler);

    void shutdownProcess();

    boolean equals(Object o);

    int hashCode();

    DNBungeeServersManager getDnBungeeServersManager();
}
