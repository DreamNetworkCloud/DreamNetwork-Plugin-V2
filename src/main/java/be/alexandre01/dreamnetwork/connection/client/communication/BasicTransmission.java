package be.alexandre01.dreamnetwork.connection.client.communication;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.api.request.channels.ChannelPacket;
import be.alexandre01.dreamnetwork.api.request.RequestPacket;
import be.alexandre01.dreamnetwork.api.request.RequestType;
import be.alexandre01.dreamnetwork.api.request.channels.DNChannel;
import be.alexandre01.dreamnetwork.api.request.channels.DNChannelInterceptor;
import be.alexandre01.dreamnetwork.api.request.communication.ClientResponse;
import be.alexandre01.dreamnetwork.plugins.bungeecord.api.DNBungeeAPI;
import be.alexandre01.dreamnetwork.plugins.spigot.DNSpigot;
import be.alexandre01.dreamnetwork.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;


public class BasicTransmission extends ClientResponse {

    @Override
    public void onResponse(Message message, ChannelHandlerContext ctx) throws Exception {
        ChannelPacket receivedPacket = new ChannelPacket(message);
        if(message.getHeader().equals("channel")) {
        DNChannel dnChannel = NetworkBaseAPI.getInstance().getChannelManager().getChannel(message.getChannel());
        if(dnChannel != null){
                if(!dnChannel.getDnChannelInterceptors().isEmpty()){
                    for (DNChannelInterceptor dnChannelInterceptor : dnChannel.getDnChannelInterceptors()){
                        dnChannelInterceptor.received(receivedPacket);
                    }
                }
                return;
            }
        }
        if(message.getHeader().equals("cData")) {
            DNChannel dnChannel = NetworkBaseAPI.getInstance().getChannelManager().getChannel(message.getChannel());
            if(dnChannel != null){
               String key = message.getString("key");
               Object value = message.get("value");
                if(dnChannel.getDataListener().containsKey(key)){
                    dnChannel.getDataListener().get(key).onUpdateData(value);
                }
                dnChannel.getObjects().put(key,value);
            }
            return;
        }
        if(message.getHeader().equals("cAsk")) {
            System.out.println("Est ce que je m'aime ?");
            DNChannel dnChannel = NetworkBaseAPI.getInstance().getChannelManager().getChannel(message.getChannel());
            System.out.println("Oui et toi ? "+ dnChannel);
            if(dnChannel != null){
                String key = message.getString("key");
                Object value = message.get("value");
                System.out.println(value + " " + key);
                System.out.println("class : " + value.getClass());


                if(dnChannel.getDataListener().containsKey(key)){
                    DNChannel.DataListener dataListener = dnChannel.getDataListener().get(key);
                    if(dataListener.getAClass() == Integer.class){

                    }
                    dnChannel.getDataListener().get(key).onUpdateData(value);
                }
                System.out.println("Complete ASK");

                dnChannel.getCompletables().get(key).supplyAsync(() -> value).complete(value);

                dnChannel.getObjects().put(key,value);
            }
            return;
        }
        if(message.hasProvider()){
            if(message.getProvider().equals(NetworkBaseAPI.getInstance().getProcessName()) && message.hasRequestID()){
                RequestPacket request = NetworkBaseAPI.getInstance().getRequestManager().getRequest(message.getRequestID());
                if(request != null)
                    request.getRequestFutureResponse().onReceived(receivedPacket);
            }
        }
        if(message.hasRequest()){
            RequestType requestType = message.getRequest();
            if(requestType.equals(RequestType.SPIGOT_HANDSHAKE_SUCCESS)) {
                NetworkBaseAPI.getInstance().setProcessName("s-" + message.getString("PROCESSNAME"));
                System.out.println("The connection has been established on the remote address: " + ctx.channel().remoteAddress());
                DNSpigot.getInstance().callServerAttachedEvent();
            }  else if(requestType.equals(RequestType.BUNGEECORD_HANDSHAKE_SUCCESS)){
                DNBungeeAPI.getInstance().setProcessName("p-"+message.getString("PROCESSNAME"));
                System.out.println("The connection has been established on the remote address: "+ ctx.channel().remoteAddress());
            }
        }
    }
}
