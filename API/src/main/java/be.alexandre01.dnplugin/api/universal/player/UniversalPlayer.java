package be.alexandre01.dnplugin.api.universal.player;

import be.alexandre01.dnplugin.api.objects.player.DNPlayer;
import be.alexandre01.dnplugin.api.objects.server.DNServer;
import lombok.Getter;

import java.util.*;

@Getter
public abstract class UniversalPlayer extends DNPlayer {
    private final Map<Integer,String> idToOperation = new HashMap<>();
    private final List<PlayerJoinListener> playerJoin = new ArrayList<>();
    private final List<PlayerQuitListener> playerQuit = new ArrayList<>();
    private final List<PlayerUpdateServer> playerUpdates = new ArrayList<>();
    protected DNServer currentServer;
    protected DNServer currentProxy;
    protected UniversalPlayer(String name, UUID uuid, DNServer dnServer, int id, boolean isProxy) {
        super(name, uuid, dnServer, id);
        this.currentServer = dnServer;
        idToOperation.put(0,"sendMessage");
        idToOperation.put(1,"kickPlayer");
        idToOperation.put(2,"sendTitle");
    }


    public abstract void sendMessage(String message);

    public abstract void kickPlayer(String reason);

    public abstract void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut);


    public abstract void sendTo(String serverName);
    public abstract void sendTo(DNServer server);
    public abstract <T> T castTo(Class<T> clazz);

    public static interface PlayerJoinListener{
        void onPlayerJoin();
    }

    public static interface PlayerQuitListener{
        void onPlayerQuit();
    }

    public static interface PlayerUpdateServer{
        void onPlayerUpdateServer(DNServer newServer);
    }
}
