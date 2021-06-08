package be.alexandre01.dreamnetwork.api.request;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.api.request.exception.RequestNotFoundException;
import be.alexandre01.dreamnetwork.api.request.generated.DefaultGeneratedRequest;
import be.alexandre01.dreamnetwork.utils.messages.Message;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class RequestManager {
    RequestBuilder requestBuilder;
    public RequestManager(){
        requestBuilder = new RequestBuilder();
        requestBuilder.addRequestBuilder(new DefaultGeneratedRequest());
    }

    public void sendRequest(RequestType requestType, GenericFutureListener<? extends Future<? super Void>> listener, String... args){
         if(!requestBuilder.requestData.containsKey(requestType)){
             try {
                 throw new RequestNotFoundException();
             } catch (RequestNotFoundException e) {
                 e.printStackTrace();
             }
         }

         RequestBuilder.RequestData requestData = requestBuilder.requestData.get(requestType);

         NetworkBaseAPI.getInstance().getBasicClientHandler().writeAndFlush(requestData.write(new Message().setHeader("RequestType"),args),listener);
    }

    public void sendRequest(RequestType requestType, String... args){
        this.sendRequest(requestType,null,args);
    }
    public void sendRequest(RequestType requestType,boolean notifiedWhenSent, String... args){
        if(notifiedWhenSent){
            this.sendRequest(requestType,future -> {
                System.out.println("Request"+ requestType.name()+" sended with success!");
            },args);
            return;
        }
        this.sendRequest(requestType,null,args);
    }

}
