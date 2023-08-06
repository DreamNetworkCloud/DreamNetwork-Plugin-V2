package be.alexandre01.dnplugin.plugins.velocity.objects;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.request.RequestType;
import be.alexandre01.dnplugin.plugins.velocity.DNVelocity;
import be.alexandre01.dnplugin.api.utils.messages.Message;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.ServerInfo;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerManagement {
    private final HashMap<Player, Integer> players = new HashMap<>();
    private final ArrayList<Integer> removedIds = new ArrayList<>();
    private int currentID = 0;

    public void updatePlayer(Player player){
        int id;
        if(players.containsKey(player)){
            id = players.get(player);
            NetworkBaseAPI.getInstance().getRequestManager().sendRequest(
                    RequestType.CORE_UPDATE_PLAYER,
                    id,
                    player.getCurrentServer().get().getServer().getServerInfo().getName()
            );
        }else {
            id = createId();
            players.put(player,id);

            NetworkBaseAPI.getInstance().getRequestManager().sendRequest(
                    RequestType.CORE_UPDATE_PLAYER,
                    id,
                    player.getCurrentServer().get().getServer().getServerInfo().getName(),
                    player.getGameProfile().getName()
            );
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
        if(!players.containsKey(proxiedPlayer)){
              return;
        }
        int id = players.get(proxiedPlayer);
        NetworkBaseAPI.getInstance().getRequestManager().sendRequest(RequestType.CORE_REMOVE_PLAYER,new Message(),future -> {
            removedIds.add(id);
        },id);
        players.remove(proxiedPlayer);
    }
}
