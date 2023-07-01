package be.alexandre01.dnplugin.plugins.spigot.components.items;

import be.alexandre01.dnplugin.plugins.spigot.components.GUIItem;
import be.alexandre01.dnplugin.plugins.spigot.components.GUIItemStack;
import org.bukkit.Material;

public class ServerItem extends GUIItem {
    public ServerItem(int slot) {
        super(new GUIItemStack(Material.GLASS), slot);

    }
}
