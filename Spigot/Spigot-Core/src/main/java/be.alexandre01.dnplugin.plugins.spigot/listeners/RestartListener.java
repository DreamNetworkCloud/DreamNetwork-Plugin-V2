package be.alexandre01.dnplugin.plugins.spigot.listeners;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.connection.request.RequestType;
import be.alexandre01.dnplugin.plugins.spigot.DNSpigot;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class RestartListener implements Listener {

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Bukkit.broadcastMessage("§e>> DreamNetwork: §c The server is restarting...");
            NetworkBaseAPI.getInstance().restartCurrentServer();
        }
    };
    @EventHandler
    public void onRestart(PlayerCommandPreprocessEvent event){
        String cmd = event.getMessage();

        if(cmd.equalsIgnoreCase("/restart")){
            if(!event.getPlayer().hasPermission("network.restart")){
                event.getPlayer().sendMessage("§cYou don't have the permission to execute the command.");
                event.setCancelled(true);
                return;
            }
            event.setCancelled(true);
            event.getPlayer().sendMessage("§e>> DreamNetwork: §c You are about to restart the server, please wait a few seconds...");
            Bukkit.getScheduler().runTaskLater(DNSpigot.getInstance(), runnable, 60L);
        }
    }

    @EventHandler
    public void onRestartFromConsole(ServerCommandEvent event){
        String cmd = event.getCommand();
        if(cmd.equalsIgnoreCase("restart")){
            if(!event.getSender().hasPermission("network.restart")){
                event.getSender().sendMessage("§cYou don't have the permission to execute the command.");
                event.setCancelled(true);
                return;
            }
            Bukkit.getConsoleSender().sendMessage("§e>> DreamNetwork: §c You are about to restart the server, please wait a few seconds...");
            NetworkBaseAPI.getInstance().restartCurrentServer();
        }
    }
}
