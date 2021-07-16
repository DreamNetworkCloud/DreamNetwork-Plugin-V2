package be.alexandre01.dreamnetwork.plugins.spigot.communication;

import be.alexandre01.dreamnetwork.connection.client.communication.ClientResponse;
import be.alexandre01.dreamnetwork.plugins.spigot.DNSpigot;
import be.alexandre01.dreamnetwork.plugins.spigot.api.DNSpigotAPI;
import be.alexandre01.dreamnetwork.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;
import org.bukkit.Bukkit;

public class SpigotRequestReponse extends ClientResponse {
    @Override
    public void onResponse(Message message, ChannelHandlerContext ctx) throws Exception {
        if(message.hasRequest()){
            switch (message.getRequest()){
                case SPIGOT_EXECUTE_COMMAND:
                    Bukkit.getScheduler().scheduleSyncDelayedTask(DNSpigot.getInstance(),() -> {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),message.getString("CMD"));
                    });
                    break;
            }
        }
    }
}
