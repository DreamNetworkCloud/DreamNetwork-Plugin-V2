package be.alexandre01.dreamnetwork.plugins.spigot.api.events.player;

import be.alexandre01.dreamnetwork.api.objects.player.DNPlayer;
import be.alexandre01.dreamnetwork.api.objects.server.DNServer;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NetworkDisconnectEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    @Getter private final DNServer server;
    @Getter private final DNPlayer player;

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public NetworkDisconnectEvent(DNServer dnServer, DNPlayer dnPlayer){
        this.server = dnServer;
        this.player = dnPlayer;
    }
}
