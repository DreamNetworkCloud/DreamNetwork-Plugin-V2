package be.alexandre01.dreamnetwork.connection.client.handler;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.api.request.RequestType;
import be.alexandre01.dreamnetwork.connection.client.BasicClient;
import be.alexandre01.dreamnetwork.api.request.communication.ClientResponse;
import be.alexandre01.dreamnetwork.connection.client.communication.BasicTransmission;
import be.alexandre01.dreamnetwork.utils.messages.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.PacketPlayOutResourcePackSend;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class BasicClientHandler extends ChannelInboundHandlerAdapter {
    @Getter private ArrayList<ClientResponse> responses = new ArrayList<>();
    private HashMap<Message, GenericFutureListener<? extends Future<? super Void>>> queue = new HashMap<>();
    private BasicClient basicClient;
    @Getter @Setter private Channel channel;

    public BasicClientHandler(BasicClient basicClient){
        this.basicClient = basicClient;
        responses.add(new BasicTransmission());

        NetworkBaseAPI.getInstance().setBasicClientHandler(this);

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.channel = ctx.channel();
        System.out.println("Channel active");

        if(!queue.isEmpty()){
            taskQueue();
        }
        basicClient.trying = 0;

        NetworkBaseAPI.getInstance().getRequestManager().sendRequest(RequestType.CORE_HANDSHAKE);
    }

    private void taskQueue(){
        Message msg = (Message) queue.keySet().toArray()[0];
        byte[] entry = msg.toString().getBytes(StandardCharsets.UTF_8);
        final ByteBuf buf = channel.alloc().buffer(entry.length);
        buf.writeBytes(entry);

        ChannelFuture future = channel.writeAndFlush(buf);
        future.addListener(f -> {
            queue.remove(msg);
            if(!queue.isEmpty()){
                taskQueue();
            }
        });
        if(queue.get(msg) != null){
            future.addListener(queue.get(msg));
        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf m = (ByteBuf) msg; // (1)
        String s_to_decode = m.toString(StandardCharsets.UTF_8);

        System.out.println(s_to_decode);

        //TO DECODE STRING IF ENCODED AS AES

        if(!Message.isJSONValid(s_to_decode))
            return;


        System.out.println("TO message");

        try {
            Message message = Message.createFromJsonString(s_to_decode);
            for(ClientResponse iBasicClientResponse : responses){
                try {
                    iBasicClientResponse.onResponse(message,ctx);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } finally {
            m.release();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Channel inactive try to reconnect...");


    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Channel unregister");
        NetworkBaseAPI.getInstance().shutdownProcess();
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public void writeAndFlush(Message msg){
        this.writeAndFlush(msg,null);
    }

    public void writeAndFlush(Message msg, GenericFutureListener<? extends Future<? super Void>> listener){
        if(channel == null || !channel.isActive() || !queue.isEmpty()){
            queue.put(msg,listener);
            return;
        }
        byte[] entry = msg.toString().getBytes(StandardCharsets.UTF_8);
        final ByteBuf buf = channel.alloc().buffer(entry.length);
        buf.writeBytes(entry);
        if(listener == null){
            channel.writeAndFlush(buf);
            return;
        }
        System.out.println(msg.toString());
        channel.writeAndFlush(buf).addListener(listener);
    }
}
