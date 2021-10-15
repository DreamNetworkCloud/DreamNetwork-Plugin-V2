package be.alexandre01.dreamnetwork.plugins.spigot.communication;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.api.request.communication.ClientResponse;
import be.alexandre01.dreamnetwork.plugins.spigot.DNSpigot;
import be.alexandre01.dreamnetwork.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;
import org.bukkit.Bukkit;

import java.util.List;

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
                case CORE_STOP_SERVER:
                    Bukkit.shutdown();
                    break;
                case SPIGOT_NEW_SERVERS:
                    List<String> servers = (List<String>) message.getList("SERVERS");
                    NetworkBaseAPI.getInstance().getServers().addAll(servers);
                    System.out.println("New servers : "+ servers);
            }
        }
    }
}
