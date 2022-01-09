package be.alexandre01.dreamnetwork.plugins.spigot.components;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class GUIOverlay {

    private NetworkGUI networkGUI;
    private Inventory inventory;
    private HashMap<Integer, ItemStack> itemStacks;
    private boolean enabled;
    private Type type = Type.SQUARE;
    private int startSlot;
    private int minSlot = 0;
    private int maxSlot;
    private boolean isSetup;
    private int[] free;
    private final ArrayList<NetworkGUI> networkGUIs = new ArrayList<>();
    private final HashMap<Integer,ItemStack> places = new HashMap<>();
    private final ArrayList<Integer> slots = new ArrayList<>();
    private final HashMap<Integer,Integer> converterSlot = new HashMap<>();

    public static GUIOverlay EMPTY(NetworkGUI networkGUI){
        return new GUIOverlay(networkGUI);
    }

    public GUIOverlay(NetworkGUI networkGUI) {
         this.networkGUI = networkGUI;
    }

    private void regenerateSlot(){
        for (int i = 0; i < slots.size(); i++) {
            converterSlot.put(i,slots.get(i));
        }
    }
    public void setup(boolean recursive){
        maxSlot = networkGUI.getInventory().getSize();
        regenerateSlot();
        networkGUI.setOverlay(this);
        networkGUIs.add(networkGUI);
        if(recursive){
            for(NetworkGUI n : networkGUI.getRecursiveSubGUIs(new ArrayList<>())){
                networkGUIs.add(n);
                n.setOverlay(this);
            }
        }
        update();
        isSetup = true;
    }
    public void update() {
        for(NetworkGUI n : networkGUIs){
            for (int i = 0; i < n.getInventory().getSize(); i++) {
                if(itemStacks.containsKey(i)){
                    n.getInventory().setItem(i,itemStacks.get(i));
                }
            }
        }
    }

    public void insertItem(GUIItem item,int slot) {
        itemStacks.put(slot,item.getItemStack().build());
        if(minSlot >= slot || minSlot == 0){
            minSlot = slot;
        }
        if(maxSlot <= slot){
            maxSlot = slot;
        }
        slots.remove(slot);
    }

    public GUIItem insertItem(ItemStack item,int slot) {
        itemStacks.put(slot,item);


        if(minSlot >= slot || minSlot == 0){
            minSlot = slot;
        }
        if(maxSlot <= slot){
            maxSlot = slot;
        }
        slots.remove(slot);
        return new GUIItem(item,slot);
    }
    public GUIItem insertItem(GUIItemStack item,int slot) {
        itemStacks.put(slot,item.build());


        if(minSlot >= slot || minSlot == 0){
            minSlot = slot;
        }
        if(maxSlot <= slot){
            maxSlot = slot;
        }
        slots.remove(slot);
        return new GUIItem(item,slot);
    }
    public enum Type {
        SQUARE,
        FREE;
    }
}