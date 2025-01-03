package be.alexandre01.dnplugin.plugins.spigot.api.events.player;

import be.alexandre01.dnplugin.api.objects.player.DNPlayer;
import be.alexandre01.dnplugin.api.objects.server.DNServer;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NetworkSwitchServerEvent extends Event {
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

    public NetworkSwitchServerEvent(DNServer dnServer, DNPlayer dnPlayer){
        this.server = dnServer;
        this.player = dnPlayer;
    }
}
