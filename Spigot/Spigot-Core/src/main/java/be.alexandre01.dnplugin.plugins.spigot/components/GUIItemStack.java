package be.alexandre01.dnplugin.plugins.spigot.components;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class GUIItemStack {
    private ItemStack itemStack;
    private ItemMeta itemMeta;

    public GUIItemStack(Material material) {
        itemStack = new ItemStack(material);
    }


    public GUIItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }
    //set amount
    public void setAmount(int amount) {
        itemStack.setAmount(amount);
    }

    //set display name
    public void setName(String displayName) {
        itemMeta.setDisplayName(displayName);
    }

    //set lore
    public void setLore(String... lore) {
        itemMeta.setLore(Arrays.asList(lore));
    }

    //set color with data
    public void setColor(ColorID colorID) {
        itemStack.getData().setData((byte) colorID.getID());
    }


    public ItemStack build(){
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }


}
