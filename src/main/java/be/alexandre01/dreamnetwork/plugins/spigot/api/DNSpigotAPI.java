package be.alexandre01.dreamnetwork.plugins.spigot.api;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.api.request.RequestManager;
import be.alexandre01.dreamnetwork.connection.client.handler.BasicClientHandler;
import be.alexandre01.dreamnetwork.plugins.spigot.DNSpigot;
import lombok.*;

import java.util.logging.Logger;

@EqualsAndHashCode(callSuper = true)
@Data
public class DNSpigotAPI extends NetworkBaseAPI{
    BasicClientHandler basicClientHandler;
    DNSpigot dnSpigot;

    public DNSpigotAPI(DNSpigot dnSpigot){
        this.dnSpigot = dnSpigot;
    }
    @Override
    public BasicClientHandler getBasicClientHandler() {
        return basicClientHandler;
    }

    @Override
    public String getInfo() {
        return "SPIGOT-"+dnSpigot.getVersion();
    }

    @Override
    public int getPort() {
        return dnSpigot.getPort();
    }

    @Override
    public Logger getLogger() {
        return Logger.getGlobal();//dnSpigot.getLogger();
    }

    @Override
    public RequestManager getRequestManager() {
        return dnSpigot.getRequestManager();
    }

    @Override
    public void setRequestManager(RequestManager requestManager) {

    }

    @Override
    public void setBasicClientHandler(BasicClientHandler basicClientHandler) {
        this.basicClientHandler = basicClientHandler;
    }
}
