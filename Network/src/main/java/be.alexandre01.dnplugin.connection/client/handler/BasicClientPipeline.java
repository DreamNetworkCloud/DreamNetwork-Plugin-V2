package be.alexandre01.dnplugin.connection.client.handler;

import be.alexandre01.dnplugin.connection.client.BasicClient;
import be.alexandre01.dnplugin.connection.client.communication.BasicDecoder;
import be.alexandre01.dnplugin.connection.client.communication.BasicEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class BasicClientPipeline extends ChannelInitializer<SocketChannel> {
    private final BasicClientHandler basicClientHandler;

    public BasicClientPipeline(BasicClient basicClient){
        basicClientHandler = new BasicClientHandler(basicClient);
    }
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast("Decoder",new BasicDecoder());
        ch.pipeline().addLast(basicClientHandler);
        ch.pipeline().addLast("Encoder",new BasicEncoder());
    }
}
