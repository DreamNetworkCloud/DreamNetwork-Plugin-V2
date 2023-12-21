package be.alexandre01.dnplugin.plugins.spigot.communication;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.connection.request.channels.DNChannel;
import be.alexandre01.dnplugin.api.objects.RemoteBundle;
import be.alexandre01.dnplugin.api.objects.RemoteExecutor;
import be.alexandre01.dnplugin.api.objects.player.DNPlayer;
import be.alexandre01.dnplugin.api.objects.player.DNPlayerManager;
import be.alexandre01.dnplugin.api.objects.server.DNServer;
import be.alexandre01.dnplugin.api.connection.request.RequestType;
import be.alexandre01.dnplugin.api.connection.request.communication.ClientReceiver;
import be.alexandre01.dnplugin.api.utils.messages.Message;
import be.alexandre01.dnplugin.plugins.spigot.api.communication.MessageReceivedEvent;
import be.alexandre01.dnplugin.plugins.spigot.communication.objects.BaseExecutor;
import be.alexandre01.dnplugin.plugins.spigot.DNSpigot;
import be.alexandre01.dnplugin.plugins.spigot.api.events.player.NetworkDisconnectEvent;
import be.alexandre01.dnplugin.plugins.spigot.api.events.player.NetworkJoinEvent;
import be.alexandre01.dnplugin.plugins.spigot.api.events.player.NetworkSwitchServerEvent;
import be.alexandre01.dnplugin.plugins.spigot.communication.objects.BukkitPlayer;
import be.alexandre01.dnplugin.api.utils.Mods;
import io.netty.channel.ChannelHandlerContext;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class SpigotRequestReceiver extends ClientReceiver {
    final NetworkBaseAPI networkBaseAPI;
    final DNPlayerManager dnPlayerManager;
    final PluginManager pluginManager;
    final HashMap<String, RemoteExecutor> remoteServices;
    public SpigotRequestReceiver(){
        networkBaseAPI = NetworkBaseAPI.getInstance();
        dnPlayerManager =  DNSpigot.getAPI().getDnPlayerManager();
        remoteServices = NetworkBaseAPI.getInstance().getServices();
        pluginManager = Bukkit.getPluginManager();

        addRequestInterceptor(RequestType.SERVER_EXECUTE_COMMAND,(message, ctx) -> {
            Bukkit.getScheduler().scheduleSyncDelayedTask(DNSpigot.getInstance(),() -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),message.getString("CMD"));
            });
        });

        addRequestInterceptor(RequestType.SERVER_NEW_SERVERS,(message, ctx) -> {
            List<String> nServers = (List<String>) message.getList("SERVERS");
            //networkBaseAPI.getServers().addAll(nServers);
            for(String servers : nServers){
                String[] nums = servers.split(";")[0].split("-");
                String[] data = servers.split(";");


                // isStarted
                boolean isStarted;

                boolean proxy = false;
                if(data[3].equals("t")){
                    isStarted = true;
                }else {
                    isStarted = false;
                }

                if(data[4].equals("p")){
                    proxy = true;
                }

                if(!networkBaseAPI.getServices().containsKey(nums[0])){
                    Mods mods;

                    if(data[2].equals("S")){
                        mods = Mods.STATIC;
                    }else {
                        mods = Mods.DYNAMIC;
                    }
                    String[] splitPath = nums[0].split("/");
                    String serverName = splitPath[splitPath.length - 1];
                    String bundlePath = nums[0].substring(0,(nums[0].length()-serverName.length())-1);
                    //create Bundle
                    if(!networkBaseAPI.getBundles().containsKey(bundlePath)){
                        networkBaseAPI.getBundles().put(bundlePath,new RemoteBundle(bundlePath,proxy));
                    }
                    RemoteBundle remoteBundle = networkBaseAPI.getBundles().get(bundlePath);
                    networkBaseAPI.getBundles().put(bundlePath,remoteBundle);
                    BaseExecutor service = new BaseExecutor(nums[0],mods,isStarted,remoteBundle);
                    remoteBundle.getRemoteExecutors().put(serverName,service);
                    networkBaseAPI.getServices().put(nums[0],service);
                }
                if(isStarted){
                    int i = Integer.parseInt(nums[1]);
                    BaseExecutor baseService = (BaseExecutor) networkBaseAPI.getServices().get(nums[0]);
                    String customName = null;
                    if(!data[1].equals("n"))
                        customName = data[1];
                    baseService.createServer(nums[0],customName,i);
                }
            }
        });

        addRequestInterceptor(RequestType.SERVER_REMOVE_SERVERS,(message, ctx) -> {
            System.out.println(message.toString());
            List<String> rServers = (List<String>) message.getList("SERVERS");
            if(rServers.size() > 0){
                for(String servers : rServers){
                    String[] nums = servers.split("-");
                    int i = Integer.parseInt(nums[nums.length-1]);
                    BaseExecutor baseService = (BaseExecutor) networkBaseAPI.getServices().get(nums[0]);
                    baseService.removeServer(i);
                }
            }
            //networkBaseAPI.getServers().removeAll(rServers);
        });

        addRequestInterceptor(RequestType.SERVER_UPDATE_PLAYERS,(message, ctx) ->  {
            List<String> upPlayers =  (List<String>) message.getList("P");
            System.out.println(upPlayers);
            for(String p : upPlayers){
                DNPlayer dnPlayer = null;
                Integer id = null;
                DNServer dnServer = null;
                String playerName = null;
                UUID uuid = null;
                RemoteExecutor service = null;

                String[] split = p.split(";");

                for (int i = 0; i < split.length; i++) {
                    System.out.println(split[i]);
                    if(i == 0) {
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
                           // System.out.println("Retreaving server");
                            dnServer = service.getServers().get(serverId);
                        } /*else {
                           // System.out.println("Creating server");
                            dnServer = new DNServer(serverName, serverId, service);
                            service.getServers().put(serverId, dnServer);
                        }*/
                    }
                    if(i == 2) {
                       // System.out.println("Player name : "+split[i]);
                        playerName = split[i];
                    }
                    if(i == 3) {
                        uuid = UUID.fromString(split[i]);
                    }
                }

                if(dnPlayer == null){
                  //  System.out.println(playerName + " and "+ dnServer);
                    if(playerName == null || dnServer == null)
                        return;

                    dnPlayer = new BukkitPlayer(playerName, uuid, dnServer, id);
                    dnPlayerManager.addPlayer(dnPlayer);

                    NetworkJoinEvent event = new NetworkJoinEvent(dnPlayer.getServer(),dnPlayer);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(DNSpigot.getInstance(), () -> {
                        pluginManager.callEvent(event);
                    });
                }else {
                    if(dnServer != null){
                        System.out.println("Change Server "+dnPlayer.getServer().getName()+ " to "+ dnServer.getName());
                        dnPlayerManager.updatePlayer(dnPlayer,dnServer);

                        NetworkSwitchServerEvent event = new NetworkSwitchServerEvent(dnPlayer.getServer(),dnPlayer);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(DNSpigot.getInstance(), () -> {
                            pluginManager.callEvent(event);
                        });
                    }
                }

            }
        });
        addRequestInterceptor(RequestType.SERVER_UNREGISTER_PLAYERS,(message, ctx) -> {
            System.out.println("Removing players");
            List<Integer> unPlayers =  (List<Integer>) message.get("P");
            for(Integer id : unPlayers){
                DNPlayer dnPlayer = dnPlayerManager.getDnPlayers().get(id);
                dnPlayerManager.removePlayer(dnPlayer);
                Bukkit.getScheduler().scheduleSyncDelayedTask(DNSpigot.getInstance(),()->{
                    NetworkDisconnectEvent event = new NetworkDisconnectEvent(dnPlayer.getServer(),dnPlayer);
                    pluginManager.callEvent(event);
                });
            }
        });

        addRequestInterceptor(RequestType.CORE_REGISTER_CHANNEL,(message, ctx) -> {
            String channelName = message.getString("CHANNEL");
            LinkedHashMap<String,Object> map = (LinkedHashMap<String, Object>) message.get("MAP");
            System.out.println("Données ! > "+map);
            System.out.println("Nom du channel ! > "+ channelName);
            networkBaseAPI.getChannelManager().getChannel(channelName).callRegisterEvent(map);
        });


        addRequestInterceptor(RequestType.CORE_REGISTER_CHANNELS_INFOS,(message, ctx) -> {
            List<String> channels = message.getList("CHANNELS",String.class);
            for(String channel : channels){
                System.out.println("New Channel = "+channel);
                if(networkBaseAPI.getChannelManager().hasChannel(channel)){
                    continue;
                }
                networkBaseAPI.getChannelManager().getChannels().put(channel,new DNChannel(channel));
            }
        });

    }

    @Override
    public void onReceive(Message message, ChannelHandlerContext ctx) throws Exception {
        Bukkit.getScheduler().scheduleSyncDelayedTask(DNSpigot.getInstance(),()->{
            MessageReceivedEvent event = new MessageReceivedEvent(message);
            pluginManager.callEvent(event);
        });
    }
}
