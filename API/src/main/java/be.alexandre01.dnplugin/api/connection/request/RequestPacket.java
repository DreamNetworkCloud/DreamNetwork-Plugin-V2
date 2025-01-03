package be.alexandre01.dnplugin.api.connection.request;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.connection.IClientHandler;
import be.alexandre01.dnplugin.api.objects.server.NetEntity;
import be.alexandre01.dnplugin.api.utils.messages.Message;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;

@Data
public class RequestPacket implements Packet {
    private static int currentId;

    private final RequestInfo requestInfo;
    private final GenericFutureListener<? extends Future<? super Void>> listener;
    private Message message;
    private String provider;
    private RequestFutureResponse requestFutureResponse;

    private IClientHandler clientHandler;

    public RequestPacket(RequestInfo requestInfo, Message message, GenericFutureListener<? extends Future<? super Void>> listener) {
        this.requestInfo = requestInfo;
        this.listener = listener;
        this.message = message;
        this.provider = NetworkBaseAPI.getInstance().getProcessName();
        message.setProvider(provider);
       // message.setInRoot("MID",RequestID);
    }
    public RequestPacket(Message message, GenericFutureListener<? extends Future<? super Void>> listener) {
        this.requestInfo = RequestType.CUSTOM;
        this.listener = listener;
        this.message = message;
        this.provider = NetworkBaseAPI.getInstance().getProcessName();
        message.setProvider(provider);
        //message.setInRoot("MID",RequestID);
        currentId++;
    }
    public RequestPacket(Message message) {
        this.requestInfo = RequestType.CUSTOM;
        this.listener = null;
        this.message = message;
        this.provider = NetworkBaseAPI.getInstance().getProcessName();
        message.setProvider(provider);
        //message.setInRoot("MID",RequestID);
        currentId++;
    }


    @Override
    public NetEntity getReceiver() {
        return NetworkBaseAPI.getInstance();
    }
}
