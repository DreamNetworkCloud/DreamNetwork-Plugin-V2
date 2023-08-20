package be.alexandre01.dnplugin.api.objects.server;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.objects.RemoteService;
import be.alexandre01.dnplugin.api.objects.player.DNPlayer;
import be.alexandre01.dnplugin.api.request.RequestInfo;
import be.alexandre01.dnplugin.api.request.RequestPacket;
import be.alexandre01.dnplugin.api.request.RequestType;
import be.alexandre01.dnplugin.api.utils.messages.Message;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;

@Getter
public class DNServer {
    private final Collection<DNPlayer> players = new ArrayList<>();
    private final RemoteService remoteService;
    private final String name;
    private final int id;
    private final NetworkBaseAPI networkBaseAPI = NetworkBaseAPI.getInstance();

    public DNServer(String name,int id,RemoteService remoteService){
        this.name = name;
        this.id = id;
        this.remoteService = remoteService;
    }

    public String getFullName(){
        return name + "-" + id;
    }

    public void sendMessage(Message message){
        networkBaseAPI.getRequestManager().sendRequest(RequestType.CORE_RETRANSMISSION,message,name+"-"+id);
    }
    public void sendRequest(RequestInfo requestInfo, Object... object){
        RequestPacket requestPacket = networkBaseAPI.getRequestManager().getRequestPacket(requestInfo, new Message(), null, object);
        sendMessage(requestPacket.getMessage());
    }
    public void stop(){
        networkBaseAPI.getRequestManager().sendRequest(RequestType.CORE_STOP_SERVER,name+"-"+id);
    }
}
