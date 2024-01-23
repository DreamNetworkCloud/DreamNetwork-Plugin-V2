package be.alexandre01.dnplugin.api.objects;

import be.alexandre01.dnplugin.api.objects.player.DNPlayer;
import be.alexandre01.dnplugin.api.objects.server.DNServer;
import be.alexandre01.dnplugin.api.objects.server.ExecutorCallbacks;
import be.alexandre01.dnplugin.api.universal.player.UniversalPlayer;
import be.alexandre01.dnplugin.api.utils.Mods;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Getter @Setter
public abstract class RemoteExecutor {

    protected List<UniversalPlayer> players = new ArrayList<>();
    protected HashMap<Integer, DNServer> servers = new HashMap<>();
    private String name;
    private Mods mods;

    protected boolean isStarted;
    protected RemoteBundle remoteBundle;

    public RemoteExecutor(String name, Mods mods, boolean isStarted, RemoteBundle remoteBundle){
        this.name = name;
        this.mods = mods;
        this.isStarted = isStarted;
        this.remoteBundle = remoteBundle;
    }

    public abstract Optional<DNServer> getServer(int id);

    public abstract ExecutorCallbacks start();

    public RemoteBundle getRemoteBundle(){
        return remoteBundle;
    }
}
