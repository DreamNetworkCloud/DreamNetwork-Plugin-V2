package be.alexandre01.dreamnetwork.plugins.bungeecord.communication;

import be.alexandre01.dreamnetwork.connection.client.communication.ClientResponse;
import be.alexandre01.dreamnetwork.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;

public class BungeeRequestReponse extends ClientResponse {
    @Override
    public void onResponse(Message message, ChannelHandlerContext ctx) throws Exception {
        if (message.getHeader().contains("RequestType")) {

        }
    }
}
