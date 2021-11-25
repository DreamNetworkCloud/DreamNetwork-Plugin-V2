package be.alexandre01.dreamnetwork.api.objects.server;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.api.objects.RemoteService;
import be.alexandre01.dreamnetwork.api.objects.player.DNPlayer;
import be.alexandre01.dreamnetwork.api.request.RequestType;
import be.alexandre01.dreamnetwork.utils.messages.Message;
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
    private final NetworkBaseAPI networkBaseAPI = NetworkBaseAPI.getInstance();

    public DNServer(String name,int id,RemoteService remoteService){
        this.name = name;
        this.id = id;
        this.remoteService = remoteService;
    }

    public void sendMessage(Message message){
        networkBaseAPI.getRequestManager().sendRequest(RequestType.CORE_RETRANSMISSION,message,name+"-"+id);
    }
    public void stop(){
        networkBaseAPI.getRequestManager().sendRequest(RequestType.CORE_STOP_SERVER,name+"-"+id);
    }
}
