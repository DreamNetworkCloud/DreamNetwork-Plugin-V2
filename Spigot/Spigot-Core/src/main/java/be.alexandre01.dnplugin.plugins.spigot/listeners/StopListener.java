package be.alexandre01.dnplugin.plugins.spigot.listeners;

import be.alexandre01.dnplugin.api.objects.server.DNServer;
import be.alexandre01.dnplugin.plugins.spigot.DNSpigot;
import be.alexandre01.dnplugin.api.utils.Mods;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class StopListener implements Listener {
  @EventHandler
  public void onConsole(ServerCommandEvent event) {
    boolean b = doAction(event.getCommand());
    if (b)
      event.setCancelled(true); 
  }
  
  @EventHandler
  public void onChat(PlayerCommandPreprocessEvent event) {
    if (!event.getPlayer().hasPermission("network.stop"))
      return; 
    boolean b = doAction(event.getMessage());
    if (b)
      event.setCancelled(true); 
  }
  
  private boolean doAction(String cmd) {
    if (cmd.equalsIgnoreCase("stop")) {
      System.out.println("Stopping server...");
      DNServer server = DNSpigot.getAPI().getCurrentServer();
      if (server != null) {
        if (server.getRemoteExecutor().getMods() == Mods.DYNAMIC) {
          for (World world : Bukkit.getWorlds())
            world.setAutoSave(false); 
          Runtime.getRuntime().halt(0);
          return true;
        } 
        return false;
      } 
      Runtime.getRuntime().halt(0);
      for (World world : Bukkit.getWorlds())
        world.setAutoSave(false); 
      return true;
    } 
    return false;
  }
}
