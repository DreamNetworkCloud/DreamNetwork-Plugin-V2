package be.alexandre01.dnplugin.plugins.velocity;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.connection.IClientHandler;
import be.alexandre01.dnplugin.api.request.RequestManager;

import be.alexandre01.dnplugin.api.request.channels.DNChannelManager;
import be.alexandre01.dnplugin.plugins.velocity.api.DNVelocityAPI;
import be.alexandre01.dnplugin.plugins.velocity.api.DNVelocityServersManager;
import be.alexandre01.dnplugin.plugins.velocity.communication.VelocityRequestResponse;
import be.alexandre01.dnplugin.plugins.velocity.communication.generated.VelocityGeneratedRequest;
import be.alexandre01.dnplugin.plugins.velocity.utils.VelocityServersManager;
import lombok.Getter;

import java.util.logging.Logger;

public class ImplAPI extends NetworkBaseAPI implements DNVelocityAPI {
    IClientHandler basicClientHandler;
    DNVelocity dnVelocity;
    @Getter
    DNVelocityServersManager dnVelocityServersManager;

    String serverName;
    int id;
    String processName = null;
    public ImplAPI(DNVelocity dnVelocity){
        this.dnVelocity = dnVelocity;
        this.dnVelocityServersManager = new VelocityServersManager(this);
    }
    @Override
    public IClientHandler getClientHandler() {
        return basicClientHandler;
    }

    @Override
    public String getInfo() {
        return "VELOCITY-"+dnVelocity.getVersion();
    }

    @Override
    public String getProcessName() {
        return processName;
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
        return dnVelocity.getPort();
    }

    @Override
    public Logger getLogger() {
        return Logger.getGlobal();
    }

    @Override
    public RequestManager getRequestManager() {
        return dnVelocity.getRequestManager();
    }

    @Override
    public DNChannelManager getChannelManager() {
        return dnVelocity.getDnChannelManager();
    }

    @Override
    public void setRequestManager(RequestManager requestManager) {
        dnVelocity.setRequestManager(requestManager);
    }



    @Override
    public void callServerAttachedEvent() {

    }

    @Override
    public void setClientHandler(IClientHandler basicClientHandler) {
        this.basicClientHandler = basicClientHandler;
        getRequestManager().setClientHandler(basicClientHandler);
        basicClientHandler.getResponses().add(new VelocityRequestResponse());
        getRequestManager().getRequestBuilder().addRequestBuilder(new VelocityGeneratedRequest());
    }

    @Override
    public void shutdownProcess() {
        dnVelocity.getServer().shutdown();
    }
}