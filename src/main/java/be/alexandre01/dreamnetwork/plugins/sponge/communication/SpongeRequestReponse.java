package be.alexandre01.dreamnetwork.plugins.sponge.communication;

import be.alexandre01.dreamnetwork.connection.client.communication.ClientResponse;
import be.alexandre01.dreamnetwork.plugins.spigot.DNSpigot;
import be.alexandre01.dreamnetwork.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;
import org.spongepowered.api.Sponge;

public class SpongeRequestReponse extends ClientResponse {
    @Override
    public void onResponse(Message message, ChannelHandlerContext ctx) throws Exception {
        if(message.hasRequest()){
            switch (message.getRequest()){
                case SPIGOT_EXECUTE_COMMAND:
                        Sponge.getCommandManager().process(Sponge.getServer().getConsole(), message.getString("CMD"));
                    break;
            }
        }
    }
}
