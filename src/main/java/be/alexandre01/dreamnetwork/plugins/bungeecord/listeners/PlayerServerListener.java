package be.alexandre01.dreamnetwork.plugins.bungeecord.listeners;

import be.alexandre01.dreamnetwork.plugins.bungeecord.DNBungee;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerServerListener implements Listener {
    DNBungee dnBungee;
    public PlayerServerListener(){
        dnBungee = DNBungee.getInstance();
    }
    @EventHandler
    public void onSwitch(ServerSwitchEvent event){
        dnBungee.getPlayerManagement().updatePlayer(event.getPlayer());
    }

    @EventHandler
    public void onLeave(ServerDisconnectEvent event){
        dnBungee.getPlayerManagement().removePlayer(event.getPlayer());
    }
}