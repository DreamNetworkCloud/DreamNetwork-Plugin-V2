package be.alexandre01.dnplugin.plugins.spigot.listeners;

import be.alexandre01.dnplugin.plugins.spigot.DNSpigot;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ReloadListener implements Listener {
    @EventHandler
    public void onReload(PlayerCommandPreprocessEvent event){
        String cmd = event.getMessage();
        if(cmd.equalsIgnoreCase("/reload") || cmd.equalsIgnoreCase("/rl") || cmd.equalsIgnoreCase("/reloads") ){
            if(!event.getPlayer().hasPermission("network.reload")){
                event.getPlayer().sendMessage("§cYou don't have the permission to execute the command.");
                event.setCancelled(true);
                return;
            }
            event.setCancelled(true);
            DNSpigot.getInstance().isReloading = true;
            event.getPlayer().sendMessage("§e>> DreamNetwork: §cThe /reload command is not recommended, it can conflict with other plugins and cause memory leaks! Use with caution");
            Bukkit.reload();
        }
    }
}
