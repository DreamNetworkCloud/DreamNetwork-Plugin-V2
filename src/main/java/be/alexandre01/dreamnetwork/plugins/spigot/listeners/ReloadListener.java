package be.alexandre01.dreamnetwork.plugins.spigot.listeners;

import be.alexandre01.dreamnetwork.plugins.spigot.DNSpigot;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ReloadListener implements Listener {
    @EventHandler
    public void onReload(PlayerCommandPreprocessEvent event){
        String cmd = event.getMessage();

        if(cmd.equalsIgnoreCase("/plugin") || cmd.equalsIgnoreCase("/pl") || cmd.equalsIgnoreCase("/plugins") ){
            event.setCancelled(true);
            DNSpigot.getInstance().isReloading = true;
            event.getPlayer().sendMessage("§e>> DreamNetwork: §cLa commande /reload n'est pas conseillé, elle peut rentrer en conflit avec d'autres plugins et causer des fuites de mémoire! A utiliser avec précaution");
            Bukkit.reload();
        }
    }
}
