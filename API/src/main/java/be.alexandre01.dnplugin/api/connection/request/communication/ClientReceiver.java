package be.alexandre01.dnplugin.api.connection.request.communication;

import be.alexandre01.dnplugin.api.connection.request.RequestInfo;
import be.alexandre01.dnplugin.api.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;

import java.util.LinkedHashMap;

public abstract class ClientReceiver {
    private final LinkedHashMap<Integer,RequestInterceptor> map = new LinkedHashMap<>();

    public void onAutoReceive(Message message, ChannelHandlerContext ctx){
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
            onReceive(message,ctx);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void onReceive(Message message, ChannelHandlerContext ctx) throws Exception {
        System.out.println("onResponse" + getClass().getSimpleName());
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
