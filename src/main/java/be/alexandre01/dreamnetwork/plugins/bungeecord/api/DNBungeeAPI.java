package be.alexandre01.dreamnetwork.plugins.bungeecord.api;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.api.request.RequestManager;
import be.alexandre01.dreamnetwork.connection.client.handler.BasicClientHandler;
import be.alexandre01.dreamnetwork.plugins.bungeecord.DNBungee;
import be.alexandre01.dreamnetwork.plugins.bungeecord.communication.BungeeRequestReponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.logging.Logger;

@EqualsAndHashCode(callSuper = true)
@Data
public class DNBungeeAPI extends NetworkBaseAPI{
    BasicClientHandler basicClientHandler;
    DNBungee dnBungee;
    DNBungeeServersManager dnBungeeServersManager;
    String processName = null;
    public DNBungeeAPI(DNBungee dnBungee){
        this.dnBungee = dnBungee;
        this.dnBungeeServersManager = new DNBungeeServersManager();
    }
    @Override
    public BasicClientHandler getBasicClientHandler() {
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
    public void setProcessName(String processName) {
       this.processName = processName;
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
    public void setRequestManager(RequestManager requestManager) {
        dnBungee.setRequestManager(requestManager);
    }

    @Override
    public void setBasicClientHandler(BasicClientHandler basicClientHandler) {

        System.out.println(basicClientHandler);
        this.basicClientHandler = basicClientHandler;
        getRequestManager().setBasicClientHandler(basicClientHandler);
        basicClientHandler.getResponses().add(new BungeeRequestReponse());
    }
}
