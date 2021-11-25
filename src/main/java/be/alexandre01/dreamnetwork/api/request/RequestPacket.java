package be.alexandre01.dreamnetwork.api.request;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.connection.client.handler.BasicClientHandler;
import be.alexandre01.dreamnetwork.utils.messages.Message;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;

@Data
public class RequestPacket {
    private static int currentId;

    private final RequestType requestType;
    private final GenericFutureListener<? extends Future<? super Void>> listener;
    private final int RequestID;
    private Message message;
    private String provider;
    private RequestFutureResponse requestFutureResponse;

    private  BasicClientHandler basicClientHandler;

    public RequestPacket(RequestType requestType, Message message, GenericFutureListener<? extends Future<? super Void>> listener) {
        this.requestType = requestType;
        this.listener = listener;
        this.RequestID = currentId;
        this.message = message;
        this.provider = NetworkBaseAPI.getInstance().getProcessName();
        message.setProvider(provider);
        message.setInRoot("RID",RequestID);
        currentId++;
    }
    public RequestPacket(Message message, GenericFutureListener<? extends Future<? super Void>> listener) {
        this.requestType = RequestType.CUSTOM;
        this.listener = listener;
        this.RequestID = currentId;
        this.message = message;
        this.provider = NetworkBaseAPI.getInstance().getProcessName();
        message.setProvider(provider);
        message.setInRoot("RID",RequestID);
        currentId++;
    }
    public RequestPacket(Message message) {
        this.requestType = RequestType.CUSTOM;
        this.listener = null;
        this.RequestID = currentId;
        this.message = message;
        this.provider = NetworkBaseAPI.getInstance().getProcessName();
        message.setProvider(provider);
        message.setInRoot("RID",RequestID);
        currentId++;
    }

    public RequestPacket send(){
        return NetworkBaseAPI.getInstance().getRequestManager().sendRequest(this);
    }

}
