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
        DNChannel dnChannel = NetworkBaseAPI.getInstance().getChannelManager().getChannel(message.getChannel());
        if(dnChannel != null){
            if(!dnChannel.getDnChannelInterceptors().isEmpty()){
                for (DNChannelInterceptor dnChannelInterceptor : dnChannel.getDnChannelInterceptors()){
                    dnChannelInterceptor.received(receivedPacket);
                }
            }
        }
        if(message.hasProvider()){
            if(message.getProvider().equals(NetworkBaseAPI.getInstance().getProcessName())){
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
            }  else if(requestType.equals(RequestType.BUNGEECORD_HANDSHAKE_SUCCESS))
                DNBungeeAPI.getInstance().setProcessName("p-"+message.getString("PROCESSNAME"));
                System.out.println("The connection has been established on the remote address: "+ ctx.channel().remoteAddress());
            }
    }
}
