package be.alexandre01.dnplugin.plugins.bungeecord;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.connection.IClientHandler;
import be.alexandre01.dnplugin.api.connection.request.RequestManager;
import be.alexandre01.dnplugin.api.connection.request.channels.DNChannelManager;
import be.alexandre01.dnplugin.api.universal.player.UniversalPlayer;
import be.alexandre01.dnplugin.plugins.bungeecord.api.DNBungeeAPI;
import be.alexandre01.dnplugin.plugins.bungeecord.api.DNBungeeServersManager;
import be.alexandre01.dnplugin.plugins.bungeecord.communication.BungeeRequestResponse;
import be.alexandre01.dnplugin.plugins.bungeecord.communication.generated.BungeeGeneratedRequest;
import be.alexandre01.dnplugin.plugins.bungeecord.utils.BungeeServersManager;
import lombok.Getter;

import java.util.logging.Logger;

public class ImplAPI extends NetworkBaseAPI implements DNBungeeAPI {
    IClientHandler basicClientHandler;
    DNBungee dnBungee;
    @Getter
    DNBungeeServersManager dnBungeeServersManager;

    boolean isManagingConnections = true;

    String serverName;
    int id;
    String processName = null;
    public ImplAPI(DNBungee dnBungee){
        this.dnBungee = dnBungee;
        this.dnBungeeServersManager = new BungeeServersManager(this);
    }
    @Override
    public IClientHandler getClientHandler() {
        return basicClientHandler;
    }

    @Override
    public String getInfo() {
        return "BUNGEE-"+dnBungee.getVersion();
    }

    @Override
    public String getProcessName() {
        return processName;
    }

    @Override
    public UniversalPlayer getUniversalPlayer(String name) {
        return null;
    }

    @Override
    public void setProcessName(String processName) {
        this.processName = processName;
    }

    @Override
    public String getServerName() {
        return serverName;
    }

    @Override
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void setID(int id) {
        this.id = id;
    }

    @Override
    public int getPort() {
        return dnBungee.getPort();
    }

    @Override
    public Logger getLogger() {
        return Logger.getGlobal();
    }

    @Override
    public RequestManager getRequestManager() {
        return dnBungee.getRequestManager();
    }

    @Override
    public DNChannelManager getChannelManager() {
        return dnBungee.getDnChannelManager();
    }

    @Override
    public void setRequestManager(RequestManager requestManager) {
        dnBungee.setRequestManager(requestManager);
    }
    @Override
    public void callServerAttachedEvent() {

    }

    @Override
    public void setClientHandler(IClientHandler basicClientHandler) {
        this.basicClientHandler = basicClientHandler;
        getRequestManager().setClientHandler(basicClientHandler);
        basicClientHandler.getResponses().add(new BungeeRequestResponse());
        getRequestManager().getRequestBuilder().addRequestBuilder(new BungeeGeneratedRequest());
    }

    @Override
    public void shutdownProcess() {
        dnBungee.getProxy().stop();
    }

    @Override
    public void isManagingConnections(boolean isManagingConnections) {
        this.isManagingConnections = isManagingConnections;
    }
    @Override
    public boolean isManagingConnections() {
        return isManagingConnections;
    }
}
