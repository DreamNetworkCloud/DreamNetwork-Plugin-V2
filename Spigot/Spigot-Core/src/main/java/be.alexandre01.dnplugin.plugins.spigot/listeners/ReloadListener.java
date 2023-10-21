package be.alexandre01.dnplugin.plugins.spigot.listeners;

import be.alexandre01.dnplugin.plugins.spigot.DNSpigot;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class ReloadListener implements Listener {
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Bukkit.broadcastMessage("§e>> DreamNetwork: §c The server is reloading...");
            DNSpigot.getInstance().isReloading = true;
            Bukkit.reload();
        }
    };
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

            event.getPlayer().sendMessage("§e>> DreamNetwork: §cThe /reload command is not recommended, it can conflict with other plugins and cause memory leaks! Use with caution");
            runnable.run();
        }
    }

    @EventHandler
    public void onReloadFromConsole(ServerCommandEvent event){
        String cmd = event.getCommand();

        if(cmd.equalsIgnoreCase("reload") || cmd.equalsIgnoreCase("rl") || cmd.equalsIgnoreCase("reloads")){
            if(!event.getSender().hasPermission("network.reload")){
                event.getSender().sendMessage("§cYou don't have the permission to execute the command.");
                event.setCancelled(true);
                return;
            }
            Bukkit.getConsoleSender().sendMessage("§e>> DreamNetwork: §cThe /reload command is not recommended, it can conflict with other plugins and cause memory leaks! Use with caution");
            DNSpigot.getInstance().isReloading = true;
            runnable.run();
        }
    }
}
