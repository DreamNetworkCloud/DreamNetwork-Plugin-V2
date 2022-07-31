package be.alexandre01.dnplugin.api.request.channels;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.request.RequestBuilder;
import be.alexandre01.dnplugin.api.request.RequestFutureResponse;
import be.alexandre01.dnplugin.api.request.RequestInfo;
import be.alexandre01.dnplugin.connection.client.handler.BasicClientHandler;
import be.alexandre01.dnplugin.utils.messages.Message;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;

@Getter
public class ChannelPacket {
    private static int currentId;

    private RequestInfo requestInfo;
    private final GenericFutureListener<? extends Future<? super Void>> listener;
    private Integer MID;
    private final Message message;
    private final String channel;
    private String provider;
    private RequestFutureResponse requestFutureResponse;

    private BasicClientHandler basicClientHandler;

    public ChannelPacket(Message message){
        this.message = message;
        if(message.hasRequest())
            this.requestInfo = message.getRequest();
        this.listener = null;
        if(message.containsKey("MID")){
            this.MID = message.getMessageID();
        }
        this.provider = message.getProvider();
        this.channel = message.getChannel();
        this.basicClientHandler = NetworkBaseAPI.getInstance().getBasicClientHandler();
    }
    public ChannelPacket(String channel,String provider){
        this.message = null;
        this.listener = null;
        this.channel = channel;
        this.provider = provider;
        this.basicClientHandler = NetworkBaseAPI.getInstance().getBasicClientHandler();
    }

    public void createResponse(Message message){
        this.createResponse(message,null,"channel");
    }
    public void createResponse(Message message,String header){
        this.createResponse(message,null,header);
    }

    public void createResponse(Message message,GenericFutureListener<? extends Future<? super Void>> listener){
        this.createResponse(message,listener,"channel");
    }

    public void createResponse(Message message,GenericFutureListener<? extends Future<? super Void>> listener,String header){
        message.setProvider(provider);
        message.setSender(NetworkBaseAPI.getInstance().getInfo());
        message.setHeader(header);
        message.setChannel(channel);
        if(requestInfo != null){
            RequestBuilder.RequestData requestData = NetworkBaseAPI.getInstance().getRequestManager().getRequestBuilder().getRequestData().get(requestInfo);
            if(requestData != null)
                message = requestData.write(message,this.provider);
        }

        if(MID != null){
            message.setInRoot("MID",MID);
        }

        basicClientHandler.writeAndFlush(message,listener);
    }
}
