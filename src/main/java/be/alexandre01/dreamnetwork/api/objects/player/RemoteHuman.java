package be.alexandre01.dreamnetwork.api.objects.player;

import be.alexandre01.dreamnetwork.api.objects.server.DNServer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

public abstract class RemoteHuman {
    @Getter private DNServer server;

    protected RemoteHuman(DNServer server) {
        this.server = server;
    }

    public void updateServer(DNServer dnServer){
        this.server = dnServer;
    }
}
