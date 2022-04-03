package be.alexandre01.dreamnetwork.plugins.spigot.communication;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.api.objects.RemoteService;
import be.alexandre01.dreamnetwork.api.objects.player.DNPlayer;
import be.alexandre01.dreamnetwork.api.objects.player.DNPlayerManager;
import be.alexandre01.dreamnetwork.api.objects.player.RemoteHuman;
import be.alexandre01.dreamnetwork.api.objects.server.DNServer;
import be.alexandre01.dreamnetwork.api.request.communication.ClientResponse;
import be.alexandre01.dreamnetwork.connection.client.objects.BaseService;
import be.alexandre01.dreamnetwork.plugins.spigot.DNSpigot;
import be.alexandre01.dreamnetwork.plugins.spigot.api.DNSpigotAPI;
import be.alexandre01.dreamnetwork.plugins.spigot.api.events.player.NetworkDisconnectEvent;
import be.alexandre01.dreamnetwork.plugins.spigot.api.events.player.NetworkJoinEvent;
import be.alexandre01.dreamnetwork.plugins.spigot.api.events.player.NetworkSwitchServerEvent;
import be.alexandre01.dreamnetwork.plugins.spigot.api.events.server.ServerStartedEvent;
import be.alexandre01.dreamnetwork.utils.Mods;
import be.alexandre01.dreamnetwork.utils.messages.Message;
import com.google.gson.internal.LinkedTreeMap;
import io.netty.channel.ChannelHandlerContext;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SpigotRequestResponse extends ClientResponse {
    final NetworkBaseAPI networkBaseAPI;
    final DNPlayerManager dnPlayerManager;
    final PluginManager pluginManager;
    final HashMap<String, RemoteService> remoteServices;
    public SpigotRequestResponse(){
        networkBaseAPI = NetworkBaseAPI.getInstance();
        dnPlayerManager = ((DNSpigotAPI) DNSpigotAPI.getInstance()).getDnPlayerManager();
        remoteServices = NetworkBaseAPI.getInstance().getServices();
        pluginManager =     Bukkit.getPluginManager();
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
                    //networkBaseAPI.getServers().addAll(nServers);
                    for(String servers : nServers){
                        String[] nums = servers.split(";")[0].split("-");
                        String[] data = servers.split(";");


                        boolean b;
                        if(data[2].equals("true")){
                            b = true;
                        }else {
                            b = false;
                        }
                        if(!networkBaseAPI.getServices().containsKey(nums[0])){
                            Mods mods;

                            if(data[1].equals("S")){
                                mods = Mods.STATIC;
                            }else {
                                mods = Mods.DYNAMIC;
                            }
                            networkBaseAPI.getServices().put(nums[0],new BaseService(nums[0],mods,b));
                        }



                        if(b){
                            int i = Integer.parseInt(nums[nums.length-1]);
                            BaseService baseService = (BaseService) networkBaseAPI.getServices().get(nums[0]);
                            baseService.createServer(nums[0],i);
                        }
                    }

               //     System.out.println("New servers : "+ nServers);
                    break;
                case SPIGOT_REMOVE_SERVERS:
                    List<String> rServers = (List<String>) message.getList("SERVERS");
                    if(rServers.size() > 0){
                    for(String servers : rServers){
                        String[] nums = servers.split("-");
                        int i = Integer.parseInt(nums[nums.length-1]);
                        BaseService baseService = (BaseService) networkBaseAPI.getServices().get(nums[0]);
                        baseService.removeServer(i);
                    }
                    }
                    //networkBaseAPI.getServers().removeAll(rServers);
                    System.out.println("Remove servers : "+ rServers);
                    break;
                case SPIGOT_UPDATE_PLAYERS:
                    List<String> upPlayers =  (List<String>) message.getList("P");
                    System.out.println(upPlayers);
                    for(String p : upPlayers){
                        DNPlayer dnPlayer = null;
                        Integer id = null;
                        DNServer dnServer = null;
                        String playerName = null;
                        UUID uuid = null;
                        RemoteService service = null;

                        String[] split = p.split(";");

                        for (int i = 0; i < split.length; i++) {
                            System.out.println(split[i]);
                              if(i== 0) {
                                  id = Integer.parseInt(split[i]);
                                  if (dnPlayerManager.getDnPlayers().containsKey(id)) {
                                      dnPlayer = dnPlayerManager.getDnPlayers().get(id);
                                  }
                              }
                              if(i == 1) {
                                  String[] server = split[i].split("-");
                                  String serverName = server[0];
                                  int serverId = Integer.parseInt(server[1]);
                                  if (!remoteServices.containsKey(serverName)) {
                                      continue;
                                  }
                                  service = remoteServices.get(serverName);
                                  if (service.getServers().containsKey(serverId)) {
                                      dnServer = service.getServers().get(serverId);
                                  } else {
                                      dnServer = new DNServer(serverName, serverId, service);
                                      service.getServers().put(serverId, dnServer);
                                  }
                              }
                              if(i == 2) {
                                  playerName = split[i];
                              }
                              if(i == 3) {
                                  uuid = UUID.fromString(split[i]);
                              }
                          }

                          if(dnPlayer == null){
                              if(playerName == null || dnServer == null)
                                  return;


                              dnPlayer = new DNPlayer(playerName,uuid,dnServer,id);
                              dnServer.getPlayers().add(dnPlayer);
                              dnPlayerManager.getDnPlayers().put(id, dnPlayer);

                              NetworkJoinEvent event = new NetworkJoinEvent(dnPlayer.getServer(),dnPlayer);
                              Bukkit.getScheduler().scheduleSyncDelayedTask(DNSpigot.getInstance(), () -> {
                                  pluginManager.callEvent(event);
                              });
                          }else {
                              if(dnServer != null){
                                  System.out.println("Change Server Ouwa "+ dnServer.getName()+" to "+ dnPlayer.getServer().getName());
                                  dnPlayer.getServer().getRemoteService().getPlayers().remove(dnPlayer);
                                  dnPlayer.updateServer(dnServer);
                                  dnServer.getPlayers().add(dnPlayer);
                                  NetworkSwitchServerEvent event = new NetworkSwitchServerEvent(dnPlayer.getServer(),dnPlayer);
                                  Bukkit.getScheduler().scheduleSyncDelayedTask(DNSpigot.getInstance(), () -> {
                                      pluginManager.callEvent(event);
                                  });
                              }
                          }

                        }
                    break;
                case SPIGOT_UNREGISTER_PLAYERS:
                    List<Integer> unPlayers =  (List<Integer>) message.getIntegersList("P");
                    for(Integer id : unPlayers){
                        DNPlayer dnPlayer = dnPlayerManager.getDnPlayers().get(id);
                        dnPlayerManager.getDnPlayers().remove(id);
                        dnPlayer.getServer().getRemoteService().getPlayers().remove(dnPlayer);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(DNSpigot.getInstance(),()->{
                            NetworkDisconnectEvent event = new NetworkDisconnectEvent(dnPlayer.getServer(),dnPlayer);
                            pluginManager.callEvent(event);
                        });
                    }
                    break;
                case CORE_REGISTER_CHANNEL:
                    String channelName = message.getString("CHANNEL");

                    LinkedTreeMap<String,Object> map = (LinkedTreeMap<String, java.lang.Object>) message.get("MAP");
                    networkBaseAPI.getChannelManager().getChannel(channelName).callRegisterEvent(map);
                }

        }
    }
}
