package be.alexandre01.dreamnetwork.api.objects.server;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.api.objects.RemoteService;
import be.alexandre01.dreamnetwork.api.objects.player.DNPlayer;
import be.alexandre01.dreamnetwork.api.request.RequestType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class DNServer {
    private final Collection<DNPlayer> dnPlayers = new ArrayList<>();
    private final RemoteService remoteService;
    private final String name;
    private final int id;

    public DNServer(String name,int id,RemoteService remoteService){
        this.name = name;
        this.id = id;
        this.remoteService = remoteService;
    }

    public void stop(){
        NetworkBaseAPI.getInstance().getRequestManager().sendRequest(RequestType.CORE_STOP_SERVER,name+"-"+id);
    }
}
