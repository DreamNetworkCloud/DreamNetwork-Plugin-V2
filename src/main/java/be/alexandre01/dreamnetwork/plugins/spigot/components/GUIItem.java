package be.alexandre01.dreamnetwork.plugins.spigot.components;

import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
public class GUIItem {
    private GUIItemStack itemStack;
    private NetworkGUI gui;
    private int slot;
    private GUIAction guiAction;

    public GUIItem(ItemStack item, int slot) {
        this.itemStack = new GUIItemStack(item);
        this.slot = slot;
    }
    public GUIItem(GUIItemStack item, int slot) {
        this.itemStack = item;
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }



}
