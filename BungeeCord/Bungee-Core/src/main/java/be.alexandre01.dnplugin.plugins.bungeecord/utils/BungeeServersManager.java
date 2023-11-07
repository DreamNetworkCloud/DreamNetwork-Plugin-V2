package be.alexandre01.dnplugin.plugins.bungeecord.utils;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.objects.RemoteBundle;
import be.alexandre01.dnplugin.plugins.bungeecord.DNBungee;
import be.alexandre01.dnplugin.plugins.bungeecord.api.DNBungeeServersManager;
import be.alexandre01.dnplugin.plugins.bungeecord.communication.objects.ProxyExecutor;
import be.alexandre01.dnplugin.api.utils.Mods;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;
import java.util.ArrayList;

public class BungeeServersManager implements DNBungeeServersManager {
    @Getter
    public ArrayList<String> servers = new ArrayList<>();
    NetworkBaseAPI networkBaseAPI;
    ServerInfo core = null;

    boolean isCoreRegistered = true;

    public BungeeServersManager(NetworkBaseAPI networkBaseAPI) {
        this.networkBaseAPI = networkBaseAPI;
        ProxyServer.getInstance().getServers().values().stream().findAny().ifPresent(serverInfo -> core = serverInfo);
    }

    @Override
    public void registerServer(String processName,String customName, String ip, int port, Mods mods) {
        try {
            String viewName = processName;
            if (customName != null) {
                viewName = customName;
            }
            ServerInfo info = ProxyServer.getInstance().constructServerInfo(viewName, new InetSocketAddress(ip, port), "", false);
            ProxyServer.getInstance().getServers().put(viewName, info);
            servers.add(viewName);


            if (DNBungee.getInstance().configuration.isConnexionOnLobby()) {
                String lobbyName = DNBungee.getInstance().configuration.getLobby();
                if (processName.startsWith(lobbyName)) {
                    if (isCoreRegistered) {
                        ProxyServer.getInstance().getServers().remove(core.getName(), core);
                        ProxyServer.getInstance().getConfig().getListeners().forEach(listenerInfo -> {
                            listenerInfo.getServerPriority().remove(core.getName());
                        });
                        isCoreRegistered = true;
                    }
                    ProxyServer.getInstance().getConfig().getListeners().forEach(listenerInfo -> {
                        listenerInfo.getServerPriority().add(customName);
                    });
                }
            } else {
                ProxyServer.getInstance().getServers().remove(core.getName());
                ProxyServer.getInstance().getConfig().getListeners().forEach(listenerInfo -> {

                    if (isCoreRegistered) {
                        listenerInfo.getServerPriority().remove(core.getName());
                        isCoreRegistered = false;
                    }
                    listenerInfo.getServerPriority().add(customName);
                });
            }


            ProxyExecutor proxyService;
            String name = processName.split("-")[0];
            int id = Integer.parseInt(processName.split("-")[1]);
            String[] splitPath = name.split("/");
            String serverName = splitPath[splitPath.length - 1];
            String bundlePath = name.substring(0, (name.length() - serverName.length()) - 1);
            //create Bundle
            if (!networkBaseAPI.getBundles().containsKey(bundlePath)) {
                networkBaseAPI.getBundles().put(bundlePath, new RemoteBundle(bundlePath, false));
            }
            RemoteBundle remoteBundle = networkBaseAPI.getBundles().get(bundlePath);
            networkBaseAPI.getServices().put(name, proxyService = new ProxyExecutor(name, mods, true, remoteBundle));
            proxyService.createServer(name, id,customName);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void unregisterServer(String finalname) {
        if(DNBungee.getInstance().configuration.isConnexionOnLobby()){
            String lobbyName = DNBungee.getInstance().configuration.getLobby();
            if(finalname.startsWith(lobbyName)){
                ProxyServer.getInstance().getConfig().getListeners().forEach(listenerInfo -> {
                    if(listenerInfo.getServerPriority().size() == 1){
                        isCoreRegistered = true;

                        listenerInfo.getServerPriority().add(core.getName());
                    }
                    listenerInfo.getServerPriority().remove(finalname);

                });
                if(isCoreRegistered){
                    ProxyServer.getInstance().getServers().put(core.getName(), core);
                    for(ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()){
                        proxiedPlayer.disconnect();
                    }
                }

            }
        }else {
            if (ProxyServer.getInstance().getServers().size() == 1) {
                isCoreRegistered = true;
                ProxyServer.getInstance().getServers().put(core.getName(), core);
                ProxyServer.getInstance().getConfig().getListeners().forEach(listenerInfo -> {
                    listenerInfo.getServerPriority().add(core.getName());
                });
                for(ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()){
                    proxiedPlayer.disconnect();
                }
            }
        }
        ProxyServer.getInstance().getConfig().getListeners().forEach(listenerInfo -> {
            listenerInfo.getServerPriority().remove(finalname);
        });
        ProxyServer.getInstance().getServers().remove(finalname);
        servers.remove(finalname);
    }
}
