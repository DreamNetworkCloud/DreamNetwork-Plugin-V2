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
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void onResponse(Message message, ChannelHandlerContext ctx) throws Exception {
        if(message.contains("Hello")){
                Message msg = new Message();
                System.out.println("Send");
                msg.set("Bonjour","Boule de gomme");
                msg.set("ms",new Date().getTime());
                NetworkBaseAPI.getInstance().getBasicClientHandler().writeAndFlush(msg);
        }

        if(message.hasRequest()){
            RequestType requestType = RequestType.getByID(message.getRequest());
            switch (requestType){
                case SPIGOT_HANDSHAKE_SUCCESS:
                    System.out.println("YES JE ME SUIS CONNECTE");
                    break;
            }
        }
    }
}
