package be.alexandre01.dnplugin.plugins.bungeecord.objects;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.request.RequestType;
import be.alexandre01.dnplugin.api.utils.messages.Message;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerManagement {
    private final HashMap<ProxiedPlayer, Integer> proxiedPlayers = new HashMap<>();
    private final ArrayList<Integer> removedIds = new ArrayList<>();
    private int currentID = 0;

    public void updatePlayer(ProxiedPlayer proxiedPlayer){
        int id;
        if(proxiedPlayers.containsKey(proxiedPlayer)){
            id = proxiedPlayers.get(proxiedPlayer);
            NetworkBaseAPI.getInstance().getRequestManager().sendRequest(RequestType.CORE_UPDATE_PLAYER,id,proxiedPlayer.getServer().getInfo().getName());
        }else {
            id = createId();
            proxiedPlayers.put(proxiedPlayer,id);
            NetworkBaseAPI.getInstance().getRequestManager().sendRequest(RequestType.CORE_UPDATE_PLAYER,id,proxiedPlayer.getServer().getInfo().getName(),proxiedPlayer.getName());
        }
    }

    public int createId(){
        if(!removedIds.isEmpty()){
            int i = removedIds.get(0);
            removedIds.remove(0);
            return i;
        }
        int i = currentID;
        currentID++;
        return i;
    }

    public void removePlayer(ProxiedPlayer proxiedPlayer){
        if(!proxiedPlayers.containsKey(proxiedPlayer)){
              return;
        }
        int id = proxiedPlayers.get(proxiedPlayer);
        NetworkBaseAPI.getInstance().getRequestManager().sendRequest(RequestType.CORE_REMOVE_PLAYER,new Message(),future -> {
            removedIds.add(id);
        },id);
        proxiedPlayers.remove(proxiedPlayer);
    }
}
