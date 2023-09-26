package be.alexandre01.dnplugin.api.connection.request.communication;

import be.alexandre01.dnplugin.api.connection.request.RequestInfo;
import be.alexandre01.dnplugin.api.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;

import java.util.LinkedHashMap;

public abstract class ClientResponse {
    private final LinkedHashMap<Integer,RequestInterceptor> map = new LinkedHashMap<>();

    public void onAutoResponse(Message message, ChannelHandlerContext ctx){
        if(!preReader(message,ctx)){
            return;
        }
        try {
            if(message.hasRequest()){
                final RequestInterceptor interceptor = map.get(message.getRequestID());
                if (interceptor != null) {
                    interceptor.onRequest(message, ctx);
                }
            }
            onResponse(message,ctx);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void onResponse(Message message, ChannelHandlerContext ctx) throws Exception {
        // override this
    }

    public void addRequestInterceptor(RequestInfo requestInfo, RequestInterceptor requestInterceptor){
        map.put(requestInfo.id,requestInterceptor);
    }

    private RequestInterceptor getRequestInterceptor(Message message){
        return map.get(message.getRequestID());
    }
    public interface RequestInterceptor {
        public void onRequest(Message message, ChannelHandlerContext ctx) throws Exception;
    }

    protected boolean preReader(Message message, ChannelHandlerContext ctx){
        // do nothing
        return true;
    }
}
