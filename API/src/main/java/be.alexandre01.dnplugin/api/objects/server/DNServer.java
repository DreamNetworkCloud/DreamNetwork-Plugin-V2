package be.alexandre01.dnplugin.api.objects.server;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.connection.request.*;
import be.alexandre01.dnplugin.api.objects.RemoteService;
import be.alexandre01.dnplugin.api.objects.player.DNPlayer;
import be.alexandre01.dnplugin.api.utils.messages.Message;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Getter
public class DNServer extends RemoteClient {
    private final Collection<DNPlayer> players = new ArrayList<>();
    private final RemoteService remoteService;
    private final int id;
    private final NetworkBaseAPI networkBaseAPI = NetworkBaseAPI.getInstance();
    private final RequestManager requestManager = new RequestManager(this);

    public DNServer(String name,int id,RemoteService remoteService){
        super(name);
        this.id = id;
        this.remoteService = remoteService;
    }

    public String getFullName(){
        return name + "-" + id;
    }


    @Deprecated
    public void sendMessage(Message message){
        networkBaseAPI.getRequestManager().sendRequest(RequestType.CORE_RETRANSMISSION,message,name+"-"+id);
    }



    public void sendRequest(RequestInfo requestInfo, Object... object){
        RequestPacket requestPacket = networkBaseAPI.getRequestManager().getRequest(requestInfo, new Message(), null, object);
        sendMessage(requestPacket.getMessage());
    }
    public CompletableFuture<Boolean> stop(){
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        DNCallback.single(networkBaseAPI.getRequestManager().getRequest(RequestType.CORE_STOP_SERVER, name + "-" + id), new TaskHandler() {
            @Override
            public void onAccepted() {
                completableFuture.complete(true);
            }

            @Override
            public void onFailed() {
                completableFuture.complete(false);
            }
        }).send();
        return completableFuture;
    }

    public void restart(){
        networkBaseAPI.getRequestManager().sendRequest(RequestType.CORE_RESTART_SERVER,name+"-"+id);
    }

    @Override
    public Packet writeAndFlush(Message message) {
        return writeAndFlush(message,null);
    }

    @Override
    public Packet writeAndFlush(Message message, GenericFutureListener<? extends Future<? super Void>> listener) {
        message.setReceiver(getFullName());
        networkBaseAPI.getClientHandler().writeAndFlush(message);
        return new Packet() {
            @Override
            public Message getMessage() {
                return message;
            }

            @Override
            public String getProvider() {
                return getFullName();
            }

            @Override
            public NetEntity getReceiver() {
                return DNServer.this;
            }
        };
    }

    @Override
    public Packet dispatch(Packet packet) {
        networkBaseAPI.getClientHandler().writeAndFlush(packet.getMessage());
        return packet;
    }

    @Override
    public Packet dispatch(Packet packet, GenericFutureListener<? extends Future<? super Void>> future) {
        networkBaseAPI.getClientHandler().writeAndFlush(packet.getMessage(),future);
        return packet;
    }

}
