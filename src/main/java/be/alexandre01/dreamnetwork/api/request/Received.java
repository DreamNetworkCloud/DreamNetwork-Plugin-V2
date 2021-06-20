package be.alexandre01.dreamnetwork.api.request;

import be.alexandre01.dreamnetwork.connection.client.handler.BasicClientHandler;
import be.alexandre01.dreamnetwork.utils.messages.Message;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class Received {
    private static int currentId;

    private final RequestType requestType;
    private final GenericFutureListener<? extends Future<? super Void>> listener;
    private final int RID;
    private final Message message;
    private String provider;
    private RequestFutureResponse requestFutureResponse;

    private BasicClientHandler basicClientHandler;

    public Received(Message message){
        this.message = message;
        this.requestType = message.getRequest();
        this.listener = null;
        this.RID = Integer.parseInt((String) message.get("RID"));
        this.provider = message.getProvider();
    }


    public void createResponse(Message message, BasicClientHandler basicClientHandler){
        message.setProvider(provider);
        basicClientHandler.writeAndFlush(message);
    }

    public void createResponse(Message message, BasicClientHandler basicClientHandler,GenericFutureListener<? extends Future<? super Void>> listener){
        message.setProvider(provider);
        basicClientHandler.writeAndFlush(message,listener);
    }
}
