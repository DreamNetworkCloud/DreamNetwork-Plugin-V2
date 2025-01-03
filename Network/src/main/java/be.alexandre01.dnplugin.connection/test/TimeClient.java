package be.alexandre01.dnplugin.connection.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeClient {
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    int trying = 0;
    public static void main(String[] args) throws Exception {
        new TimeClient().init();
    }

    public void init(){

        String host = "localhost";
        int port = 8080;
        System.out.println("Attempt to connect to "+ host+":"+port +"#TRY_"+ trying);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new TimeDecoder(),new TimeClientHandler(TimeClient.this));
                }
            });


            // Start the client.
            ChannelFuture f = b.connect(host, port).sync(); // (5)

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

            System.out.println("Retrying to connect...");
            executorService.scheduleAtFixedRate(() -> {
                init();
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