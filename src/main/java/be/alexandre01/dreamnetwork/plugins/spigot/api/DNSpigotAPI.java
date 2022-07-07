package be.alexandre01.dreamnetwork.plugins.spigot.api;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.api.objects.player.DNPlayerManager;
import be.alexandre01.dreamnetwork.api.objects.server.DNServer;
import be.alexandre01.dreamnetwork.api.request.RequestManager;
import be.alexandre01.dreamnetwork.api.request.RequestType;
import be.alexandre01.dreamnetwork.api.request.channels.DNChannelManager;
import be.alexandre01.dreamnetwork.connection.client.handler.BasicClientHandler;
import be.alexandre01.dreamnetwork.plugins.spigot.DNSpigot;
import be.alexandre01.dreamnetwork.plugins.spigot.communication.SpigotRequestResponse;
import be.alexandre01.dreamnetwork.plugins.spigot.communication.generated.SpigotGeneratedRequest;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

@EqualsAndHashCode(callSuper = true)
public class DNSpigotAPI extends NetworkBaseAPI{
    BasicClientHandler basicClientHandler;
    DNSpigot dnSpigot;
    boolean refreshedPlayers = false;
    String processName = null;

    String serverName = null;


    int id;
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

    public void sendPlayerTo(Player player,String server){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(DNSpigot.getInstance(), "BungeeCord", out.toByteArray());
    }

    public void sendPlayerTo(Player player,DNServer dnServer){
        sendPlayerTo(player,dnServer.getFullName());
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
