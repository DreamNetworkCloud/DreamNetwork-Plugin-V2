package be.alexandre01.dreamnetwork.api.objects;

import be.alexandre01.dreamnetwork.api.objects.player.DNPlayer;
import be.alexandre01.dreamnetwork.api.objects.server.DNServer;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter @Setter
public abstract class RemoteService {
    protected List<DNPlayer> dnPlayers = new ArrayList<>();
    protected HashMap<Integer, DNServer> dnServers = new HashMap<>();
    private String name;

    public RemoteService(String name){
        this.name = name;
    }

    public abstract void start();
}
