package be.alexandre01.dnplugin.plugins.spigot.components.overlays;

import be.alexandre01.dnplugin.plugins.spigot.components.GUIItemStack;
import be.alexandre01.dnplugin.plugins.spigot.components.GUIOverlay;
import be.alexandre01.dnplugin.plugins.spigot.components.NetworkGUI;
import org.bukkit.Material;

public class DreamOverlay extends GUIOverlay  {

    public DreamOverlay(NetworkGUI networkGUI) {
        super(networkGUI);

        insertItem(new GUIItemStack(Material.GLASS), 0);
        insertItem(new GUIItemStack(Material.GLASS), 1);
    }
}
