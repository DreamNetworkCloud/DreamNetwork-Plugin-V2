package be.alexandre01.dreamnetwork.plugins.spigot.api;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.api.request.RequestManager;
import be.alexandre01.dreamnetwork.connection.client.handler.BasicClientHandler;
import be.alexandre01.dreamnetwork.plugins.bungeecord.communication.BungeeRequestReponse;
import be.alexandre01.dreamnetwork.plugins.spigot.DNSpigot;
import be.alexandre01.dreamnetwork.plugins.spigot.communication.SpigotRequestReponse;
import lombok.*;
import org.bukkit.Bukkit;

import java.util.logging.Logger;

@EqualsAndHashCode(callSuper = true)
@Data
public class DNSpigotAPI extends NetworkBaseAPI{
    BasicClientHandler basicClientHandler;
    DNSpigot dnSpigot;
    String processName = null;

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
    public String getProcessName() {
        return processName;
    }

    @Override
    public void setProcessName(String processName) {
        this.processName = processName;
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
        getRequestManager().setBasicClientHandler(basicClientHandler);
        basicClientHandler.getResponses().add(new SpigotRequestReponse());
    }

    @Override
    public void shutdownProcess() {
        Bukkit.shutdown();
    }
}
