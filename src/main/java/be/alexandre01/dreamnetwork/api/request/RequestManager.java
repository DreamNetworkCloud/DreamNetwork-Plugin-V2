package be.alexandre01.dreamnetwork.api.request;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.api.request.exception.RequestNotFoundException;
import be.alexandre01.dreamnetwork.api.request.generated.DefaultGeneratedRequest;
import be.alexandre01.dreamnetwork.connection.client.handler.BasicClientHandler;
import be.alexandre01.dreamnetwork.utils.messages.Message;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

public class RequestManager {
    @Getter RequestBuilder requestBuilder;
    @Getter @Setter
    BasicClientHandler basicClientHandler;

    private HashMap<Integer, Request> requests = new HashMap<>();
    public RequestManager(){
        requestBuilder = new RequestBuilder();
        requestBuilder.addRequestBuilder(new DefaultGeneratedRequest());
        this.basicClientHandler = NetworkBaseAPI.getInstance().getBasicClientHandler();
        System.out.println(basicClientHandler);
      
    }

    public Request sendRequest(Request request){
        request.setBasicClientHandler(basicClientHandler);
        request.getBasicClientHandler().writeAndFlush(request.getMessage(),request.getListener());
        requests.put(request.getRID(),request);
        return request;
    }

    public Request sendRequest(RequestType requestType,Message message, GenericFutureListener<? extends Future<? super Void>> listener, String... args){
         if(!requestBuilder.requestData.containsKey(requestType)){
             try {
                 throw new RequestNotFoundException();
             } catch (RequestNotFoundException e) {
                 e.printStackTrace();
             }
         }

         RequestBuilder.RequestData requestData = requestBuilder.requestData.get(requestType);
         message.setHeader("RequestType");
         message.setRequestType(requestType);

         Request request = new Request(requestType,requestData.write(message,args),listener);
         request.setBasicClientHandler(basicClientHandler);
        System.out.println(basicClientHandler);
         basicClientHandler.writeAndFlush(request.getMessage(),listener);
        requests.put(request.getRID(),request);
         return request;
        // NetworkBaseAPI.getInstance().getBasicClientHandler().writeAndFlush(requestData.write(message,args),listener);
    }

    public Request sendRequest(RequestType requestType, String... args){
        return this.sendRequest(requestType,new Message(),null,args);
    }
    public Request sendRequest(RequestType requestType,Message message, String... args){
        return this.sendRequest(requestType,message,null,args);
    }
    public Request sendRequest(RequestType requestType,boolean notifiedWhenSent, String... args){
        if(notifiedWhenSent){
           return this.sendRequest(requestType,new Message(),future -> {
                System.out.println("Request "+ requestType.name()+" sended with success!");
            },args);
        }
        return this.sendRequest(requestType,new Message(),null,args);
    }

    public Request sendRequest(RequestType requestType,Message message,boolean notifiedWhenSent, String... args){
        if(notifiedWhenSent){
           return this.sendRequest(requestType,message,future -> {
                System.out.println("Request"+ requestType.name()+" sended with success!");
            },args);

        }
        return this.sendRequest(requestType,message,null,args);
    }

    public Request getRequest(int RID){
        return requests.get(RID);
    }
}
