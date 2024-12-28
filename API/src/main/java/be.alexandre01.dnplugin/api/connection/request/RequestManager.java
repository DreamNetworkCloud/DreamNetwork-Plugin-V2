package be.alexandre01.dnplugin.api.connection.request;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.connection.IClientHandler;
import be.alexandre01.dnplugin.api.connection.request.exception.RequestNotFoundException;
import be.alexandre01.dnplugin.api.connection.request.generated.DefaultGeneratedRequest;
import be.alexandre01.dnplugin.api.objects.server.NetEntity;
import be.alexandre01.dnplugin.api.utils.messages.Message;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

public class RequestManager {
    @Getter
    RequestBuilder requestBuilder;
    @Getter @Setter
    IClientHandler clientHandler;

    private NetEntity netEntity;


    public RequestManager(NetEntity netEntity){
        requestBuilder = new RequestBuilder();
        requestBuilder.addRequestBuilder(new DefaultGeneratedRequest());
        this.clientHandler = NetworkBaseAPI.getInstance().getClientHandler();
        this.netEntity = netEntity;
    }
    public RequestPacket sendRequest(RequestPacket request, Object... args){
        RequestBuilder.RequestData requestData = requestBuilder.requestData.get(request.getRequestInfo());
        request.setMessage(requestData.write(request.getMessage(),args));
        netEntity.writeAndFlush(request.getMessage(),request.getListener());
        return request;
    }

    public RequestPacket sendRequest(RequestInfo requestInfo, Message message, GenericFutureListener<? extends Future<? super Void>> listener, Object... args){
         RequestPacket request = getRequest(requestInfo,message,listener,args);
         netEntity.writeAndFlush(request.getMessage(),listener);
         return request;
        // NetworkBaseAPI.getInstance().getBasicClientHandler().writeAndFlush(requestData.write(message,args),listener);
    }
    public RequestPacket getRequest(RequestInfo requestType, Message message, GenericFutureListener<? extends Future<? super Void>> listener, Object... args){
        if(!requestBuilder.requestData.containsKey(requestType)){
            try {
                throw new RequestNotFoundException();
            } catch (RequestNotFoundException e) {
                e.printStackTrace();
            }
        }

        RequestBuilder.RequestData requestData = requestBuilder.requestData.get(requestType);
        message.setHeader("RI");
        message.setRequestInfo(requestType);

        RequestPacket request = new RequestPacket(requestType,requestData.write(message,args),listener);
        request.setClientHandler(clientHandler);
        return request;
    }

    public RequestPacket getRequest(RequestInfo requestType,Object... args){
        return getRequest(requestType,new Message(),null,args);
    }
    public RequestPacket getRequest(RequestInfo requestType,GenericFutureListener<? extends Future<? super Void>> listener,Object... args){
        return getRequest(requestType,new Message(),listener,args);
    }

    public RequestPacket sendRequest(RequestInfo requestType, Object... args){
        return this.sendRequest(requestType,new Message(),null,args);
    }
    public RequestPacket sendRequest(RequestInfo requestType, Message message, Object... args){
        return this.sendRequest(requestType,message,null,args);
    }
    public RequestPacket sendRequest(RequestInfo requestType, boolean notifiedWhenSent, Object... args){
        if(notifiedWhenSent){
           return this.sendRequest(requestType,new Message(),future -> {
                System.out.println("Request "+ requestType.name+" sended with success!");
            },args);
        }
        return this.sendRequest(requestType,new Message(),null,args);
    }

    public RequestPacket sendRequest(RequestInfo requestType, Message message, boolean notifiedWhenSent, Object... args){
        if(notifiedWhenSent){
           return this.sendRequest(requestType,message,future -> {
                System.out.println("Request"+ requestType.name+" sended with success!");
            },args);

        }
        return this.sendRequest(requestType,message,null,args);
    }
}
