package be.alexandre01.dreamnetwork.plugins.spigot.api;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.api.objects.player.DNPlayerManager;
import be.alexandre01.dreamnetwork.api.request.RequestManager;
import be.alexandre01.dreamnetwork.api.request.RequestType;
import be.alexandre01.dreamnetwork.api.request.channels.DNChannelManager;
import be.alexandre01.dreamnetwork.connection.client.handler.BasicClientHandler;
import be.alexandre01.dreamnetwork.plugins.spigot.DNSpigot;
import be.alexandre01.dreamnetwork.plugins.spigot.communication.SpigotRequestResponse;
import be.alexandre01.dreamnetwork.plugins.spigot.communication.generated.SpigotGeneratedRequest;
import lombok.*;
import org.bukkit.Bukkit;

import java.util.logging.Logger;

@EqualsAndHashCode(callSuper = true)
public class DNSpigotAPI extends NetworkBaseAPI{
    BasicClientHandler basicClientHandler;
    DNSpigot dnSpigot;
    boolean refreshedPlayers = false;
    String processName = null;
    @Getter private final DNPlayerManager dnPlayerManager = new DNPlayerManager();

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
    public DNChannelManager getChannelManager() {
        return dnSpigot.getDnChannelManager();
    }

    @Override
    public void setRequestManager(RequestManager requestManager) {

    }

    @Override
    public void setBasicClientHandler(BasicClientHandler basicClientHandler) {
        this.basicClientHandler = basicClientHandler;
        getRequestManager().setBasicClientHandler(basicClientHandler);
        basicClientHandler.getResponses().add(new SpigotRequestResponse());
        getRequestManager().getRequestBuilder().addRequestBuilder(new SpigotGeneratedRequest());
    }


    public void autoRefreshPlayers(){
        if(!refreshedPlayers){
            getRequestManager().sendRequest(RequestType.CORE_ASK_DATA,"PLAYERS","ALWAYS");
            refreshedPlayers = true;
        }else {
            System.out.println("[DreamNetwork] You already refreshed the players");
        }
    }
    public void autoRefreshPlayers(boolean active){
        if(active && !refreshedPlayers || !active && refreshedPlayers){
            getRequestManager().sendRequest(RequestType.CORE_ASK_DATA,"PLAYERS","ALWAYS");
            refreshedPlayers = true;
        }else {
            System.out.println("[DreamNetwork] You already refreshed the players");
        }
    }

    public void autoRefreshPlayers(boolean active,long time){
        if(active && !refreshedPlayers || !active && refreshedPlayers){
            getRequestManager().sendRequest(RequestType.CORE_ASK_DATA,"PLAYERS","TIME",time);
            refreshedPlayers = true;
        }else {
            System.out.println("[DreamNetwork] You already refreshed the players");
        }
    }


    public static DNSpigotAPI getInstance() {
        return (DNSpigotAPI) NetworkBaseAPI.getInstance();
    }

    public boolean hasAlreadyPlayerRefreshed(){
        return refreshedPlayers;
    }

    @Override
    public void shutdownProcess() {
        Bukkit.shutdown();
    }
}
