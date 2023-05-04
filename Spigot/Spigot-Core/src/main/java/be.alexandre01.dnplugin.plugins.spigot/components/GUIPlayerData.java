package be.alexandre01.dnplugin.plugins.spigot.components;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class GUIPlayerData {
    private Player player;
    private NetworkGUI gui;
    private NetworkGUI lastGUI;
    private Inventory inventory;


    private int page;

    public GUIPlayerData(Player player, NetworkGUI gui) {
        this.player = player;
        this.gui = gui;
    }
    //copy inventory
    public void copyData(){
        Inventory inventory = Bukkit.createInventory(null, gui.getInventory().getSize(),gui.getInventory().getTitle());
        inventory.setContents(gui.getInventory().getContents());
        player.openInventory(inventory);
    }


    public Player getPlayer() {
        return player;
    }

    public NetworkGUI getGui() {
        return gui;
    }
}
