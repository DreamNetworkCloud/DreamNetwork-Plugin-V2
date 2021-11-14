package be.alexandre01.dreamnetwork.api.objects;

import be.alexandre01.dreamnetwork.api.objects.player.DNPlayer;
import lombok.Data;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Data
public abstract class RemoteService {
    public List<DNPlayer> dnPlayers = new ArrayList<>();

    public abstract void start();
}
