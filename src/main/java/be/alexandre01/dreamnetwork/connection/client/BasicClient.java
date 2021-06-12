package be.alexandre01.dreamnetwork.connection.client;

import be.alexandre01.dreamnetwork.connection.client.handler.BasicClientPipeline;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BasicClient extends Thread{
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    public int trying = 0;

    @Override
    public void run(){

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
            System.out.println("ouf");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

            System.out.println("Retrying to connect...");
            executorService.scheduleAtFixedRate(() -> {
                super.stop();
                super.start();
                executorService.shutdown();
            },5,5, TimeUnit.SECONDS);
            trying ++;

            workerGroup.shutdownGracefully();
            if(trying > 3){
                return;
            }

        }
    }
}