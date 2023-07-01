package be.alexandre01.dnplugin.plugins.spigot.api.events.server;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ServerAttachedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
