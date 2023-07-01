package be.alexandre01.dnplugin.api.connection;

import be.alexandre01.dnplugin.api.request.communication.ClientResponse;
import be.alexandre01.dnplugin.utils.messages.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public interface IClientHandler extends ChannelHandler, ChannelInboundHandler {
    @Override
    void channelActive(ChannelHandlerContext ctx) throws Exception;

    @Override
    void channelRead(ChannelHandlerContext ctx, Object msg);

    @Override
    void channelInactive(ChannelHandlerContext ctx) throws Exception;

    @Override
    void channelUnregistered(ChannelHandlerContext ctx) throws Exception;

    @Override
    void exceptionCaught(ChannelHandlerContext ctx, Throwable cause);

    void writeAndFlush(Message msg);

    void writeAndFlush(Message msg, GenericFutureListener<? extends Future<? super Void>> listener);

    java.util.ArrayList<ClientResponse> getResponses();

    io.netty.channel.Channel getChannel();


    void setChannel(io.netty.channel.Channel channel);
}
