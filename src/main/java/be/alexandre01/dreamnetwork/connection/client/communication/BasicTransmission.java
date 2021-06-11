package be.alexandre01.dreamnetwork.connection.client.communication;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.api.request.RequestType;
import be.alexandre01.dreamnetwork.spigot.DNSpigot;
import be.alexandre01.dreamnetwork.utils.messages.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
            }
        }
    }
}
