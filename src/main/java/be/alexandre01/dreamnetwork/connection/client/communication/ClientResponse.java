package be.alexandre01.dreamnetwork.connection.client.communication;

import be.alexandre01.dreamnetwork.utils.messages.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

public abstract class ClientResponse {
    public abstract void onResponse(Message message, ChannelHandlerContext ctx) throws Exception;
}
