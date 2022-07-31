package be.alexandre01.dnplugin.api.request;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.connection.client.handler.BasicClientHandler;
import be.alexandre01.dnplugin.utils.messages.Message;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;

@Data
public class RequestPacket {
    private static int currentId;

    private final RequestInfo requestInfo;
    private final GenericFutureListener<? extends Future<? super Void>> listener;
    private final int RequestID;
    private Message message;
    private String provider;
    private RequestFutureResponse requestFutureResponse;

    private  BasicClientHandler basicClientHandler;

    public RequestPacket(RequestInfo requestInfo, Message message, GenericFutureListener<? extends Future<? super Void>> listener) {
        this.requestInfo = requestInfo;
        this.listener = listener;
        this.RequestID = currentId;
        this.message = message;
        this.provider = NetworkBaseAPI.getInstance().getProcessName();
        message.setProvider(provider);
        message.setInRoot("MID",RequestID);
        currentId++;
    }
    public RequestPacket(Message message, GenericFutureListener<? extends Future<? super Void>> listener) {
        this.requestInfo = RequestType.CUSTOM;
        this.listener = listener;
        this.RequestID = currentId;
        this.message = message;
        this.provider = NetworkBaseAPI.getInstance().getProcessName();
        message.setProvider(provider);
        message.setInRoot("MID",RequestID);
        currentId++;
    }
    public RequestPacket(Message message) {
        this.requestInfo = RequestType.CUSTOM;
        this.listener = null;
        this.RequestID = currentId;
        this.message = message;
        this.provider = NetworkBaseAPI.getInstance().getProcessName();
        message.setProvider(provider);
        message.setInRoot("MID",RequestID);
        currentId++;
    }

    public RequestPacket send(){
        return NetworkBaseAPI.getInstance().getRequestManager().sendRequest(this);
    }

}
