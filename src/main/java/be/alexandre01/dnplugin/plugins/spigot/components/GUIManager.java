package be.alexandre01.dnplugin.plugins.spigot.components;

import org.bukkit.entity.Player;

public class GUIManager{
    private NetworkGUI networkGUI;

    public void setNetworkGUI(NetworkGUI networkGUI){
        this.networkGUI = networkGUI;
    }

    public NetworkGUI getNetworkGUI(){
        return networkGUI;
    }

    public void openNetworkGUI(Player player){
        player.openInventory(networkGUI.getInventory());
        networkGUI.open(player);
    }

    public interface InventoryInitializer{
        void init(NetworkGUI networkGUI);
    }

    public interface InventoryClickListener{
        void onClick(Player player, int slot, NetworkGUI networkGUI);
    }

}
