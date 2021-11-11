package be.alexandre01.dreamnetwork.plugins.spigot.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ServerStoppedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public ServerStoppedEvent(){

    }

}
