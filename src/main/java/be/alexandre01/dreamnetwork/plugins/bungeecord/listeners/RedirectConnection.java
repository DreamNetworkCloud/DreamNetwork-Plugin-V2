package be.alexandre01.dreamnetwork.plugins.bungeecord.listeners;

import be.alexandre01.dreamnetwork.plugins.bungeecord.DNBungee;
import be.alexandre01.dreamnetwork.plugins.bungeecord.api.DNBungeeAPI;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RedirectConnection implements Listener {
    public boolean connexionOnLobby = true;
    private Set<UUID> pending = new HashSet<>();

    @EventHandler
    public void onPlayerConnect(PostLoginEvent event) {
        if(connexionOnLobby){
            System.out.println("Connection by "+ event.getPlayer().getName());
            pending.add(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        if(connexionOnLobby){

            if (!isPending(event.getPlayer().getUniqueId())) {
                return;
            }
            ProxiedPlayer player = event.getPlayer();
            ServerInfo info = getDefaultServer();
            System.out.println(info);
            if (info == null) {
                return;
            }
            event.setTarget(info);
            pending.remove(player.getUniqueId());
        }
    }
    private boolean isPending(UUID uuid) {
        return pending.contains(uuid);
    }

    private ServerInfo getDefaultServer(){
        DNBungeeAPI dnBungeeAPI = (DNBungeeAPI) DNBungeeAPI.getInstance();
        System.out.println("GET DEFAULT SERV");
        System.out.println(dnBungeeAPI.getDnBungeeServersManager().servers);
        if(dnBungeeAPI.getDnBungeeServersManager().servers.isEmpty()){
            return null;
        }else{
            for (String s : dnBungeeAPI.getDnBungeeServersManager().servers){
                System.out.println("API"+s);
            }
            for (String s :  DNBungee.getInstance().getProxy().getServers().keySet()){
                System.out.println("BungeeSide"+ s);
            }

            ServerInfo serverInfo = DNBungee.getInstance().getProxy().getServerInfo(dnBungeeAPI.getDnBungeeServersManager().servers.get(0));
            return serverInfo;
        }
    }
}
