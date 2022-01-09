package be.alexandre01.dreamnetwork.plugins.spigot.components.items;

import be.alexandre01.dreamnetwork.plugins.spigot.components.GUIItem;
import be.alexandre01.dreamnetwork.plugins.spigot.components.GUIItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ServerItem extends GUIItem {
    public ServerItem(int slot) {
        super(new GUIItemStack(Material.GLASS), slot);

    }
}
