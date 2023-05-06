package be.alexandre01.dnplugin.plugins.velocity.objects;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.request.RequestType;
import be.alexandre01.dnplugin.utils.messages.Message;
import com.velocitypowered.api.proxy.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerManagement {
    private final HashMap<Player, Integer> proxiedPlayers = new HashMap<>();
    private final ArrayList<Integer> removedIds = new ArrayList<>();
    private int currentID = 0;

    public void updatePlayer(Player proxiedPlayer){
        int id;
        if(proxiedPlayers.containsKey(proxiedPlayer)){
            id = proxiedPlayers.get(proxiedPlayer);
            NetworkBaseAPI.getInstance().getRequestManager().sendRequest(RequestType.CORE_UPDATE_PLAYER,id,proxiedPlayer.getCurrentServer().get().getServer().getServerInfo().getName());
        }else {
            id = createId();
            proxiedPlayers.put(proxiedPlayer,id);
            NetworkBaseAPI.getInstance().getRequestManager().sendRequest(RequestType.CORE_UPDATE_PLAYER,id,proxiedPlayer.getCurrentServer().get().getServer().getServerInfo().getName(),proxiedPlayer.getGameProfile().getName());
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

    public void removePlayer(Player proxiedPlayer){
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
