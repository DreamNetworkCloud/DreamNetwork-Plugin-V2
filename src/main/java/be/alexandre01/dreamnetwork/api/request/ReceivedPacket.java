package be.alexandre01.dreamnetwork.api.request;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.connection.client.handler.BasicClientHandler;
import be.alexandre01.dreamnetwork.utils.messages.Message;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;
import sun.nio.ch.Net;

@Getter
public class ReceivedPacket {
    private static int currentId;

    private RequestType requestType;
    private final GenericFutureListener<? extends Future<? super Void>> listener;
    private int RID;
    private final Message message;
    private final String channel;
    private String provider;
    private RequestFutureResponse requestFutureResponse;

    private BasicClientHandler basicClientHandler;

    public ReceivedPacket(Message message){
        this.message = message;
        if(message.hasRequest())
            this.requestType = message.getRequest();
        this.listener = null;
        if(message.containsKey("RID")){
            this.RID = Integer.parseInt((String) message.get("RID"));
        }
        this.provider = message.getProvider();
        this.channel = message.getChannel();
        this.basicClientHandler = NetworkBaseAPI.getInstance().getBasicClientHandler();
    }


    public void createResponse(Message message){
        this.createResponse(message,null);
    }

    public void createResponse(Message message,GenericFutureListener<? extends Future<? super Void>> listener){
        message.setProvider(provider);
        message.setSender(NetworkBaseAPI.getInstance().getInfo());
        message.setRequestType(RequestType.CORE_RETRANSMISSION);
        message.setChannel(channel);
        RequestBuilder.RequestData requestData = NetworkBaseAPI.getInstance().getRequestManager().requestBuilder.requestData.get(requestType);

        message = requestData.write(message,this.provider);

        message.put("RID",""+RID);
        basicClientHandler.writeAndFlush(message,listener);
    }
}
