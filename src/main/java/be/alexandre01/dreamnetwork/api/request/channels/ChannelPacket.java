package be.alexandre01.dreamnetwork.api.request.channels;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.api.request.RequestBuilder;
import be.alexandre01.dreamnetwork.api.request.RequestFutureResponse;
import be.alexandre01.dreamnetwork.api.request.RequestType;
import be.alexandre01.dreamnetwork.connection.client.handler.BasicClientHandler;
import be.alexandre01.dreamnetwork.utils.messages.Message;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;
import org.spongepowered.api.util.Tuple;
import sun.nio.ch.Net;

@Getter
public class ChannelPacket {
    private static int currentId;

    private RequestType requestType;
    private final GenericFutureListener<? extends Future<? super Void>> listener;
    private Integer RID;
    private final Message message;
    private final String channel;
    private String provider;
    private RequestFutureResponse requestFutureResponse;

    private BasicClientHandler basicClientHandler;

    public ChannelPacket(Message message){
        this.message = message;
        if(message.hasRequest())
            this.requestType = message.getRequest();
        this.listener = null;
        if(message.containsKey("RID")){
            this.RID = message.getRequestID();
        }
        this.provider = message.getProvider();
        this.channel = message.getChannel();
        System.out.println(channel);
        this.basicClientHandler = NetworkBaseAPI.getInstance().getBasicClientHandler();
    }


    public void createResponse(Message message){
        this.createResponse(message,null);
    }

    public void createResponse(Message message,GenericFutureListener<? extends Future<? super Void>> listener){
        message.setProvider(provider);
        message.setSender(NetworkBaseAPI.getInstance().getInfo());
        message.setHeader("channel");
        message.setChannel(channel);
        if(requestType != null){
            RequestBuilder.RequestData requestData = NetworkBaseAPI.getInstance().getRequestManager().getRequestBuilder().getRequestData().get(requestType);
            if(requestData != null)
                message = requestData.write(message,this.provider);
        }

        if(RID != null){
            message.put("RID",RID);
        }
        System.out.println("HMM WRTITEANDFLUSH "+message);
        basicClientHandler.writeAndFlush(message,listener);
    }
}
