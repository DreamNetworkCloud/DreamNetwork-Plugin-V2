package be.alexandre01.dreamnetwork.connection.client.handler;

import be.alexandre01.dreamnetwork.connection.client.BasicClient;
import be.alexandre01.dreamnetwork.connection.client.BasicDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class BasicClientPipeline extends ChannelInitializer<SocketChannel> {
    private BasicClient basicClient;

    public BasicClientPipeline(BasicClient basicClient){

        this.basicClient = basicClient;
    }
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new BasicDecoder(),new BasicClientHandler(basicClient));
    }
}
