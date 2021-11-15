package be.alexandre01.dreamnetwork.api.objects.server;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.api.objects.player.DNPlayer;
import be.alexandre01.dreamnetwork.api.request.RequestType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DNServer {
    public Collection<DNPlayer> dnPlayers = new ArrayList<>();
    private String name;
    private int id;

    public DNServer(String name,int id){
        this.name = name;
        this.id = id;
    }

    public void stop(){
        NetworkBaseAPI.getInstance().getRequestManager().sendRequest(RequestType.CORE_STOP_SERVER,name+"-"+id);
    }
}
