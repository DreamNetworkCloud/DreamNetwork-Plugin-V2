package be.alexandre01.dnplugin.plugins.spigot.communication;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.objects.RemoteService;
import be.alexandre01.dnplugin.api.objects.player.DNPlayer;
import be.alexandre01.dnplugin.api.objects.player.DNPlayerManager;
import be.alexandre01.dnplugin.api.objects.server.DNServer;
import be.alexandre01.dnplugin.api.request.RequestType;
import be.alexandre01.dnplugin.api.request.communication.ClientResponse;
import be.alexandre01.dnplugin.plugins.spigot.communication.objects.BaseService;
import be.alexandre01.dnplugin.plugins.spigot.DNSpigot;
import be.alexandre01.dnplugin.plugins.spigot.api.DNSpigotAPI;
import be.alexandre01.dnplugin.plugins.spigot.api.events.player.NetworkDisconnectEvent;
import be.alexandre01.dnplugin.plugins.spigot.api.events.player.NetworkJoinEvent;
import be.alexandre01.dnplugin.plugins.spigot.api.events.player.NetworkSwitchServerEvent;
import be.alexandre01.dnplugin.utils.Mods;
import com.google.gson.internal.LinkedTreeMap;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

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
        pluginManager = Bukkit.getPluginManager();

        addRequestInterceptor(RequestType.SPIGOT_EXECUTE_COMMAND,(message, ctx) -> {
            Bukkit.getScheduler().scheduleSyncDelayedTask(DNSpigot.getInstance(),() -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),message.getString("CMD"));
            });
        });

        addRequestInterceptor(RequestType.CORE_STOP_SERVER,(message, ctx) -> {
            Bukkit.shutdown();
        });
        addRequestInterceptor(RequestType.SPIGOT_NEW_SERVERS,(message, ctx) -> {
            List<String> nServers = (List<String>) message.getList("SERVERS");
            //networkBaseAPI.getServers().addAll(nServers);
            for(String servers : nServers){
                String[] nums = servers.split(";")[0].split("-");
                String[] data = servers.split(";");


                boolean b;
                if(data[2].equals("t")){
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
                    int i = Integer.parseInt(nums[1]);
                    BaseService baseService = (BaseService) networkBaseAPI.getServices().get(nums[0]);
                    baseService.createServer(nums[0],i);
                }
            }
        });

        addRequestInterceptor(RequestType.SPIGOT_REMOVE_SERVERS,(message, ctx) -> {
            System.out.println("Removing servers");
            System.out.println(message.toString());
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
        });

        addRequestInterceptor(RequestType.SPIGOT_UPDATE_PLAYERS,(message, ctx) ->  {
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
        });
        addRequestInterceptor(RequestType.SPIGOT_UNREGISTER_PLAYERS,(message, ctx) -> {
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
        });

        addRequestInterceptor(RequestType.CORE_REGISTER_CHANNEL,(message, ctx) -> {
            String channelName = message.getString("CHANNEL");
            LinkedTreeMap<String,Object> map = (LinkedTreeMap<String, java.lang.Object>) message.get("MAP");
            System.out.println("Données ! > "+map);
            System.out.println("Nom du channel ! > "+ channelName);
            networkBaseAPI.getChannelManager().getChannel(channelName).callRegisterEvent(map);
        });

    }
}
