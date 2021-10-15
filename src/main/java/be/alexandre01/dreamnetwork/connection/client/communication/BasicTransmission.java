package be.alexandre01.dreamnetwork.connection.client.communication;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.api.request.ReceivedPacket;
import be.alexandre01.dreamnetwork.api.request.RequestPacket;
import be.alexandre01.dreamnetwork.api.request.RequestType;
import be.alexandre01.dreamnetwork.api.request.channels.DNChannel;
import be.alexandre01.dreamnetwork.api.request.communication.ClientResponse;
import be.alexandre01.dreamnetwork.plugins.bungeecord.api.DNBungeeAPI;
import be.alexandre01.dreamnetwork.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;

public class BasicTransmission extends ClientResponse {

    @Override
    public void onResponse(Message message, ChannelHandlerContext ctx) throws Exception {
        System.out.println(message);
        ReceivedPacket receivedPacket = new ReceivedPacket(message);
        DNChannel dnChannel = NetworkBaseAPI.getInstance().getChannelManager().getChannel(message.getChannel());
        if(dnChannel != null){
            dnChannel.received(receivedPacket);
        }
        if(message.hasProvider()){
            if(message.getProvider().equals(NetworkBaseAPI.getInstance().getProcessName())){
                RequestPacket request = NetworkBaseAPI.getInstance().getRequestManager().getRequest(Integer.parseInt((String) message.get("RID")));
                if(request != null)
                    request.getRequestFutureResponse().onReceived(receivedPacket);
            }
        }
        if(message.hasRequest()){
            RequestType requestType = message.getRequest();
            switch (requestType){
                case SPIGOT_HANDSHAKE_SUCCESS:
                    NetworkBaseAPI.getInstance().setProcessName("s-"+message.getString("PROCESSNAME"));
                    System.out.println("The connection has been established on the remote address: "+ ctx.channel().remoteAddress());
                    break;
                case BUNGEECORD_HANDSHAKE_SUCCESS:
                    DNBungeeAPI.getInstance().setProcessName("p-"+message.getString("PROCESSNAME"));
                    System.out.println("The connection has been established on the remote address: "+ ctx.channel().remoteAddress());
                    break;
            }
        }
    }
}
