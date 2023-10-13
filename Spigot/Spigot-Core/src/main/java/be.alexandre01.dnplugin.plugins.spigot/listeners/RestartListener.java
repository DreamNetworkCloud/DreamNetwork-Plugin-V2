package be.alexandre01.dnplugin.plugins.spigot.listeners;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.connection.request.RequestType;
import be.alexandre01.dnplugin.plugins.spigot.DNSpigot;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class RestartListener implements Listener {
    @EventHandler
    public void onRestart(PlayerCommandPreprocessEvent event){
        String cmd = event.getMessage();

        if(cmd.equalsIgnoreCase("/restart")){
            event.getPlayer().sendMessage("§e>> DreamNetwork: §c You are about to restart the server, please wait a few seconds...");
            if(!event.getPlayer().hasPermission("network.restart")){
                event.getPlayer().sendMessage("§cYou don't have the permission to execute the command.");
                event.setCancelled(true);
                return;
            }
            event.setCancelled(true);
            NetworkBaseAPI.getInstance().restartCurrentServer();
        }
    }
}
