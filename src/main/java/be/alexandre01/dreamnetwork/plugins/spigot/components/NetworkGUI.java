package be.alexandre01.dreamnetwork.plugins.spigot.components;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.api.objects.RemoteService;
import be.alexandre01.dreamnetwork.connection.client.objects.BaseService;
import be.alexandre01.dreamnetwork.plugins.spigot.api.DNSpigotAPI;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public abstract class NetworkGUI {
    @Getter private final Inventory inventory;
    private HashMap<Integer, NetworkGUI> pages = new HashMap<>();
    private boolean hasMultiplePages = false;
    private final String title;
    @Setter @Getter private GUIOverlay overlay;
    private GUIManager.InventoryInitializer inventoryInitializer;
    @Getter private final HashMap<Integer,NetworkGUI> subGUIs = new HashMap<>();

    public NetworkGUI(String title) {
        this.title = title;
        this.inventory = Bukkit.createInventory(null, 54, title);
        overlay = GUIOverlay.EMPTY(this);
    }

    public List<NetworkGUI> getRecursiveSubGUIs(List<NetworkGUI> list){
        for(NetworkGUI gui : subGUIs.values()){
            list.add(gui);
            gui.getRecursiveSubGUIs(list);
        }
        return list;
    }


    public void setInitializer(GUIManager.InventoryInitializer inventoryInitializer){
        this.inventoryInitializer = inventoryInitializer;
    }
    public void setInitialPage(NetworkGUI page) {
        this.pages.put(0, page);
        this.hasMultiplePages = true;
    }
    public void open(Player player){
        new GUIPlayerData(player, this);
    }
    private static ItemStack generateItemFromServers(RemoteService remoteService){
        ItemStack it = new ItemStack(Material.GLASS);
        ItemMeta im = it.getItemMeta();

        im.setDisplayName("§6§l" + remoteService.getName());
        //set lore status of the removeservice
        String online = "§aOnline";
        List<String> lore = new ArrayList<>();

        if(remoteService.isStarted()){
            //GREEN DATA ITEMSTASK
            it.getData().setData((byte) 5);
            if(remoteService.getServers().size() > 1){
                online += " (" + remoteService.getServers().size() + ")";
            }
        }else {
            online = "§cOffline";
            //RED DATA ITEMSTASK
            it.getData().setData((byte) 14);
        }
        lore.add("§7Status: "+ online);

        lore.add("§7Type: " + remoteService.getMods().name());
        if(DNSpigotAPI.getInstance().hasAlreadyPlayerRefreshed()) {
            lore.add("§7Players: " + remoteService.getPlayers().size());
        }

        it.setItemMeta(im);
        return it;
    }
}