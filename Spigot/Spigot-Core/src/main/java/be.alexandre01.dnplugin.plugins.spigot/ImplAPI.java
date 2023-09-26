package be.alexandre01.dnplugin.plugins.spigot;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.connection.IClientHandler;
import be.alexandre01.dnplugin.api.connection.request.channels.DNChannelManager;
import be.alexandre01.dnplugin.api.objects.player.DNPlayerManager;
import be.alexandre01.dnplugin.api.objects.server.DNServer;
import be.alexandre01.dnplugin.api.connection.request.RequestManager;
import be.alexandre01.dnplugin.api.connection.request.RequestType;
import be.alexandre01.dnplugin.plugins.spigot.api.DNSpigotAPI;
import be.alexandre01.dnplugin.plugins.spigot.api.events.server.ServerAttachedEvent;
import be.alexandre01.dnplugin.plugins.spigot.communication.SpigotRequestResponse;
import be.alexandre01.dnplugin.plugins.spigot.communication.generated.SpigotGeneratedRequest;
import be.alexandre01.dnplugin.plugins.spigot.communication.objects.SpigotPlayer;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class ImplAPI extends NetworkBaseAPI implements DNSpigotAPI {
    IClientHandler iClientHandler;
    DNSpigot dnSpigot;
    boolean refreshedPlayers = false;
    String processName = null;

    String serverName = null;


    int id;
    @Getter
    private final DNPlayerManager dnPlayerManager = new DNPlayerManager();

    public ImplAPI(DNSpigot dnSpigot){
        this.dnSpigot = dnSpigot;
    }
    @Override
    public IClientHandler getClientHandler() {
        return iClientHandler;
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
    public SpigotPlayer getUniversalPlayer(String name) {
        if(dnPlayerManager.getDnPlayersByName().containsKey(name)){
            return (SpigotPlayer) dnPlayerManager.getDnPlayersByName().get(name).getUniversalPlayer();
        }
        return null;
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
    public void setClientHandler(IClientHandler basicClientHandler) {
        this.iClientHandler = basicClientHandler;
        getRequestManager().setClientHandler(basicClientHandler);
        basicClientHandler.getResponses().add(new SpigotRequestResponse());
        getRequestManager().getRequestBuilder().addRequestBuilder(new SpigotGeneratedRequest());
    }

    @Override
    public void callServerAttachedEvent() {
        System.out.println(DNSpigot.getInstance().getMessage("console.events.attached"));
        ServerAttachedEvent serverAttachedEvent = new ServerAttachedEvent();
        Bukkit.getScheduler().scheduleSyncDelayedTask(DNSpigot.getInstance(), () -> {
            DNSpigot.getInstance().getServer().getPluginManager().callEvent(serverAttachedEvent);
        });
    }


    @Override
    public void autoRefreshPlayers(){
        if(!refreshedPlayers){
            getRequestManager().sendRequest(RequestType.CORE_ASK_DATA,"PLAYERS","ALWAYS");
            refreshedPlayers = true;
        }else {
            System.out.println("[DreamNetwork] You already refreshed the players");
        }
    }
    @Override
    public void autoRefreshPlayers(boolean active){
        if(active && !refreshedPlayers || !active && refreshedPlayers){
            getRequestManager().sendRequest(RequestType.CORE_ASK_DATA,"PLAYERS","ALWAYS");
            refreshedPlayers = true;
        }else {
            System.out.println("[DreamNetwork] You already refreshed the players");
        }
    }

    @Override
    public void autoRefreshPlayers(boolean active, long time){
        if(active && !refreshedPlayers || !active && refreshedPlayers){
            getRequestManager().sendRequest(RequestType.CORE_ASK_DATA,"PLAYERS","TIME",time);
            refreshedPlayers = true;
        }else {
            System.out.println("[DreamNetwork] You already refreshed the players");
        }
    }

    @Override
    public void sendPlayerTo(Player player, String server){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(DNSpigot.getInstance(), "BungeeCord", out.toByteArray());
    }

    @Override
    public void sendPlayerTo(Player player, DNServer dnServer){
        sendPlayerTo(player,dnServer.getFullName());
    }


    @Override
    public boolean hasAlreadyPlayerRefreshed(){
        return refreshedPlayers;
    }

    @Override
    public DNServer getCurrentServer() {
        return DNSpigot.getInstance().getCurrentServer();
    }

    @Override
    public void shutdownProcess() {
        Bukkit.shutdown();
    }
}
