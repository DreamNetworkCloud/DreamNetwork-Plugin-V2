package be.alexandre01.dnplugin.api.request;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.connection.IClientHandler;
import be.alexandre01.dnplugin.api.request.exception.RequestNotFoundException;
import be.alexandre01.dnplugin.api.request.generated.DefaultGeneratedRequest;
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

    private HashMap<Integer, RequestPacket> requests = new HashMap<>();
    public RequestManager(){
        requestBuilder = new RequestBuilder();
        requestBuilder.addRequestBuilder(new DefaultGeneratedRequest());
        this.clientHandler = NetworkBaseAPI.getInstance().getClientHandler();
      
    }
    public RequestPacket sendRequest(RequestPacket request, Object... args){
        RequestBuilder.RequestData requestData = requestBuilder.requestData.get(request.getRequestInfo());
        request.setMessage(requestData.write(request.getMessage(),args));
        clientHandler.writeAndFlush(request.getMessage(),request.getListener());
        requests.put(request.getRequestID(),request);
        return request;
    }

    public RequestPacket sendRequest(RequestInfo requestInfo, Message message, GenericFutureListener<? extends Future<? super Void>> listener, Object... args){
         RequestPacket request = getRequestPacket(requestInfo,message,listener,args);

         clientHandler.writeAndFlush(request.getMessage(),listener);
         requests.put(request.getRequestID(),request);
         return request;
        // NetworkBaseAPI.getInstance().getBasicClientHandler().writeAndFlush(requestData.write(message,args),listener);
    }
    public RequestPacket getRequestPacket(RequestInfo requestType, Message message, GenericFutureListener<? extends Future<? super Void>> listener, Object... args){
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

    public RequestPacket getRequest(int MID){
        return requests.get(MID);
    }
}
