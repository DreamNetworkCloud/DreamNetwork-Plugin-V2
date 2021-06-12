package be.alexandre01.dreamnetwork.plugins.bungeecord.api;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.api.request.RequestManager;
import be.alexandre01.dreamnetwork.connection.client.handler.BasicClientHandler;
import be.alexandre01.dreamnetwork.plugins.bungeecord.DNBungee;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.logging.Logger;

@EqualsAndHashCode(callSuper = true)
@Data
public class DNBungeeAPI extends NetworkBaseAPI{
    BasicClientHandler basicClientHandler;
    DNBungee dnBungee;

    public DNBungeeAPI(DNBungee dnBungee){
        this.dnBungee = dnBungee;
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

    }

    @Override
    public void setBasicClientHandler(BasicClientHandler basicClientHandler) {
        this.basicClientHandler = basicClientHandler;
    }
}
