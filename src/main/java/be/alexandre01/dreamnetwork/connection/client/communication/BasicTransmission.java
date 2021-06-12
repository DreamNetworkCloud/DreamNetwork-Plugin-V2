package be.alexandre01.dreamnetwork.connection.client.communication;

import be.alexandre01.dreamnetwork.api.request.RequestType;
import be.alexandre01.dreamnetwork.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;

public class BasicTransmission extends ClientResponse {

    @Override
    public void onResponse(Message message, ChannelHandlerContext ctx) throws Exception {
        System.out.println(message);
        if(message.hasRequest()){
            RequestType requestType = RequestType.getByID(message.getRequest());
            switch (requestType){
                case SPIGOT_HANDSHAKE_SUCCESS:
                    System.out.println("The connection has been established on the remote address: "+ ctx.channel().remoteAddress());
                    break;
                case BUNGEECORD_HANDSHAKE_SUCCESS:
                    System.out.println("The connection has been established on the remote address: "+ ctx.channel().remoteAddress());
                    break;
            }
        }
    }
}
