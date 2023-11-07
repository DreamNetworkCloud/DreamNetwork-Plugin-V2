package be.alexandre01.dnplugin.plugins.spigot.communication.objects;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.objects.player.DNPlayer;
import be.alexandre01.dnplugin.api.objects.server.DNServer;
import be.alexandre01.dnplugin.api.connection.request.RequestType;
import be.alexandre01.dnplugin.api.universal.player.UniversalPlayer;
import be.alexandre01.dnplugin.api.universal.player.server.DNServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BukkitPlayer extends DNServerPlayer {

    Player player;

    @Override
    public void setup(DNServer dnServer, DNPlayer dnPlayer) {
        this.currentServer = dnServer;
        updateServer(dnServer);
    }

    public BukkitPlayer() {
        playerUpdates.add(new PlayerUpdateServer() {
            @Override
            public void onPlayerUpdateServer(UniversalPlayer player) {
                updateServer(currentServer);
            }
        });
    }


    public void updateServer(DNServer dnServer) {
        player = null;
        if(dnServer.getName().equals(NetworkBaseAPI.getInstance().getProcessName())){
            try {
                player = Bukkit.getPlayer(getName());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public void sendMessage(String message) {
        if(player != null){
            player.sendMessage(message);
            return;
        }
        NetworkBaseAPI.getInstance().getRequestManager().sendRequest(RequestType.PROXY_PLAYER_INSTRUCTION,0,message,getName());
    }

    @Override
    public String getName() {
        return dnPlayer.getName();
    }

    @Override
    public UUID getUniqueId() {
        return dnPlayer.getUniqueId();
    }

    @Override
    public void kickPlayer(String reason) {
        NetworkBaseAPI.getInstance().getRequestManager().sendRequest(RequestType.PROXY_PLAYER_INSTRUCTION,getName(),1,reason);
    }

    @Override
    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        NetworkBaseAPI.getInstance().getRequestManager().sendRequest(RequestType.PROXY_PLAYER_INSTRUCTION,getName(),2,title,subtitle,fadeIn,stay,fadeOut);
    }

    @Override
    public void sendTo(String serverName) {
        if(NetworkBaseAPI.getInstance().getByName(serverName) == null){
            throw new NullPointerException("Server "+serverName+" not found");
        }
        NetworkBaseAPI.getInstance().getRequestManager().sendRequest(RequestType.PROXY_PLAYER_INSTRUCTION,getName(),3,serverName);
    }
    @Override
    public void sendTo(DNServer server) {
        NetworkBaseAPI.getInstance().getRequestManager().sendRequest(RequestType.PROXY_PLAYER_INSTRUCTION,getName(),3,server.getName());
    }

    @Override
    public <T> T castTo(Class<T> clazz) {
        if(clazz.isAssignableFrom(OfflinePlayer.class)){
            return (T) Bukkit.getOfflinePlayer(getName());
        }
        if(clazz.isAssignableFrom(Player.class)){
            if(player != null){
                return (T) player;
            }
        }
        return null;
    }
}
