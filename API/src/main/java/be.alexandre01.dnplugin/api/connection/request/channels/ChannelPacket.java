package be.alexandre01.dnplugin.api.connection.request.channels;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.connection.IClientHandler;
import be.alexandre01.dnplugin.api.connection.request.Packet;
import be.alexandre01.dnplugin.api.connection.request.RequestBuilder;
import be.alexandre01.dnplugin.api.connection.request.RequestFutureResponse;
import be.alexandre01.dnplugin.api.connection.request.RequestInfo;
import be.alexandre01.dnplugin.api.objects.server.NetEntity;
import be.alexandre01.dnplugin.api.utils.messages.Message;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;

@Getter
public class ChannelPacket implements Packet {
    private static int currentId;

    private RequestInfo requestInfo;
    private final GenericFutureListener<? extends Future<? super Void>> listener;
    private Integer MID;
    private final Message message;
    private final String channel;
    private String provider;
    private RequestFutureResponse requestFutureResponse;

    private IClientHandler basicClientHandler;

    public ChannelPacket(Message message){
        this.message = message;
        if(message.hasRequest())
            this.requestInfo = message.getRequest();
        this.listener = null;
        if(message.containsKeyInRoot("MID")){
            this.MID = message.getMessageID();
        }
        this.provider = (String) message.getInRoot("provider");
        this.channel = message.getChannel();
        this.basicClientHandler = NetworkBaseAPI.getInstance().getClientHandler();
    }
    public ChannelPacket(String channel,String provider){
        this.message = null;
        this.listener = null;
        this.channel = channel;
        this.provider = provider;
        this.basicClientHandler = NetworkBaseAPI.getInstance().getClientHandler();
    }

    public void createRequest(Message message){
        this.createRequest(message,null,null);
    }
    public void createRequest(Message message, String header){
        this.createRequest(message,null,header);
    }

    public void createRequest(Message message, GenericFutureListener<? extends Future<? super Void>> listener){
        this.createRequest(message,listener,"channel");
    }

    public void createRequest(Message message, GenericFutureListener<? extends Future<? super Void>> listener, String header){
        message.setProvider(provider);
       // message.setHeader(header);
        if(header != null)
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

    @Override
    public NetEntity getReceiver() {
        return NetworkBaseAPI.getInstance();
    }
}
