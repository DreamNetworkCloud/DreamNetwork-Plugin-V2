package be.alexandre01.dreamnetwork.plugins.spigot.communication;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.api.request.communication.ClientResponse;
import be.alexandre01.dreamnetwork.connection.client.objects.BaseService;
import be.alexandre01.dreamnetwork.plugins.spigot.DNSpigot;
import be.alexandre01.dreamnetwork.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;
import org.bukkit.Bukkit;

import java.util.List;

public class SpigotRequestReponse extends ClientResponse {
    NetworkBaseAPI networkBaseAPI;
    public SpigotRequestReponse(){
        networkBaseAPI = NetworkBaseAPI.getInstance();
    }
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
                    List<String> nServers = (List<String>) message.getList("SERVERS");
                    networkBaseAPI.getServers().addAll(nServers);
                    for(String servers : nServers){
                        String[] nums = servers.split("-");
                        int i = Integer.parseInt(nums[nums.length-1]);

                        if(!networkBaseAPI.getServices().containsKey(nums[0])){
                            networkBaseAPI.getServices().put(nums[0],new BaseService(nums[0]));
                        }

                        BaseService baseService = (BaseService) networkBaseAPI.getServices().get(nums[0]);

                        baseService.createServer(nums[0],i);
                    }

                    System.out.println("New servers : "+ nServers);
                    break;
                case SPIGOT_REMOVE_SERVERS:
                    List<String> rServers = (List<String>) message.getList("SERVERS");
                    for(String servers : rServers){
                        String[] nums = servers.split("-");
                        int i = Integer.parseInt(nums[nums.length-1]);
                        BaseService baseService = (BaseService) networkBaseAPI.getServices().get(nums[0]);
                        baseService.removeServer(i);
                    }
                    networkBaseAPI.getServers().removeAll(rServers);
                    System.out.println("Remove servers : "+ rServers);
            }
        }
    }
}
