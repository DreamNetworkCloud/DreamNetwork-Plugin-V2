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
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BasicClient extends Thread implements IBasicClient {
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    public int trying = 0;

    @Setter @Getter
    boolean isRunning = false;

    boolean isExternal = false;

    @Getter String fullName = null;
    @Getter String connectionID = null;

    @Getter BasicClientPipeline pipeline = new BasicClientPipeline(this);

    String host;
    int port;

    public BasicClient(){
        host = "localhost";
        port = 14520;
        //DNHost & DNPort
        System.out.println("Searching for -DNHost property...");
        String hProp = System.getProperty("NHost");
        if(hProp != null){
            //System.out.println(System.getProperty("NHost"));
            String[] hostProperty = hProp.split(":");
            System.out.println("NHost: "+ Arrays.toString(hostProperty));
            String remoteIP = hostProperty[0];
            if(!remoteIP.equals("this")){
                this.host = remoteIP;
                isExternal = true;
            }
            try {
                port = Integer.parseInt(hostProperty[1]);
            }catch (Exception e){
                System.out.println("Can't read -DNPort property or doesn't contain port numbers");
                System.out.println("Using default port 14520...");
            }
        }else {
            System.out.println("Can't read -DNHost property");
            System.out.println("Using default host localhost...");
        }

        String hInfo = System.getProperty("NInfo");
        if(hInfo != null){
            String[] split = hInfo.split("\\+");
            fullName = split[0];
            connectionID = split[1];
            NetworkBaseAPI.getInstance().setProcessName(fullName);
            NetworkBaseAPI.getInstance().setConnectionID(connectionID);
        }else {
            if(isExternal){
                System.out.println("Can't read -DNInfo property");
                System.out.println("The server will be shutdown");
                NetworkBaseAPI.getInstance().shutdownProcess();
            }
        }
    }

    @Override
    public void run(){
        connect();
    }

    @Override
    public void connect(){
        System.out.println("Attempt to connect to "+ host+":"+port +"#TRY_"+ trying);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(pipeline);

            // Start the client.
            ChannelFuture f = b.connect(host, port).sync(); // (5)

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if(isRunning){
                System.out.println("Connection lost");
                NetworkBaseAPI.getInstance().shutdownProcess();
            }else {
                System.out.println("Connection can't be established");
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
                }
            }
        }
    }

    @Override
    public boolean isExternal() {
        return isExternal;
    }
}