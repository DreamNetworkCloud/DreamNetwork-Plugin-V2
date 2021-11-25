package be.alexandre01.dreamnetwork.plugins.spigot.api.events.server;

import be.alexandre01.dreamnetwork.api.objects.server.DNServer;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ServerStoppedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    @Getter private final DNServer server;

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public ServerStoppedEvent(DNServer dnServer){
        this.server = dnServer;
    }

}
