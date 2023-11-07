package be.alexandre01.dnplugin.api.universal.player;

import be.alexandre01.dnplugin.api.objects.player.DNPlayer;
import be.alexandre01.dnplugin.api.objects.server.DNServer;
import lombok.Getter;

import java.util.*;

@Getter
public abstract class UniversalPlayer {
    public Map<Integer,String> idToOperation = new HashMap<>();
    private List<PlayerJoinListener> playerJoin = new ArrayList<>();
    private List<PlayerQuitListener> playerQuit = new ArrayList<>();
    private List<PlayerUpdateServer> playerUpdates = new ArrayList<>();
    public DNServer currentServer;
    public DNServer currentProxy;
    public DNPlayer dnPlayer;
    protected UniversalPlayer() {
        idToOperation.put(0,"sendMessage");
        idToOperation.put(1,"kickPlayer");
        idToOperation.put(2,"sendTitle");
    }

    public void setup(DNServer dnServer, DNPlayer dnPlayer){
        this.currentServer = dnServer;
        this.dnPlayer = dnPlayer;
    }

    public abstract void sendMessage(String message);
    public abstract String getName();

    public abstract UUID getUniqueId();
    public abstract void kickPlayer(String reason);

    public abstract void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut);


    public abstract void sendTo(String serverName);
    public abstract void sendTo(DNServer server);
    public abstract <T> T castTo(Class<T> clazz);

    public static interface PlayerJoinListener{
        void onPlayerJoin(UniversalPlayer player);
    }

    public static interface PlayerQuitListener{
        void onPlayerQuit(UniversalPlayer player);
    }

    public static interface PlayerUpdateServer{
        void onPlayerUpdateServer(UniversalPlayer player);
    }
}
