package be.alexandre01.dnplugin.connection.client;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.connection.IBasicClient;
import be.alexandre01.dnplugin.connection.client.handler.BasicClientPipeline;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BasicClient extends Thread implements IBasicClient {
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    public int trying = 0;

    @Override
    public void run(){
        connect();
    }

    @Override
    public void connect(){
        String host = "localhost";
        int port = 14520;
        System.out.println("Attempt to connect to "+ host+":"+port +"#TRY_"+ trying);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new BasicClientPipeline(this));

            // Start the client.
            ChannelFuture f = b.connect(host, port).sync(); // (5)

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

            System.out.println("Retrying to connect...");
            executorService.scheduleAtFixedRate(() -> {
                System.out.println("...");
                connect();
                executorService.shutdown();
            },5,5, TimeUnit.SECONDS);
            trying ++;

            workerGroup.shutdownGracefully();
            if(trying > 6){
                NetworkBaseAPI.getInstance().shutdownProcess();
                return;
            }

        }
    }
}