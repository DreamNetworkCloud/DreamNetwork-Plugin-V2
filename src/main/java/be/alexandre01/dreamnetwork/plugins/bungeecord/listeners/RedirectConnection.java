package be.alexandre01.dreamnetwork.plugins.bungeecord.listeners;

import be.alexandre01.dreamnetwork.plugins.bungeecord.DNBungee;
import be.alexandre01.dreamnetwork.plugins.bungeecord.api.DNBungeeAPI;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RedirectConnection implements Listener {
    public boolean connexionOnLobby = true;
    private Set<UUID> pending = new HashSet<>();
    private DNBungee dnBungee;
    private DNBungeeAPI dnBungeeAPI;

    public RedirectConnection(){
    dnBungee = DNBungee.getInstance();
    dnBungeeAPI = (DNBungeeAPI) DNBungeeAPI.getInstance();
    }

    @EventHandler
    public void onJoin(ServerConnectEvent event){
        if(dnBungee.slot != -2){
            if(dnBungee.getProxy().getPlayers().size()-1>= dnBungee.slot){
                if(!event.getPlayer().hasPermission("network.slot.bypass")){
                    event.getPlayer().disconnect(
                            new TextComponent("§8§m*------§7§m------§7§m-§b§m-----------§7§m-§7§m------§8§m------*\n§cServeur plein !"),
                            new TextComponent("\n\n§eVeuillez réessayer plus tard\n"),
                            new TextComponent("§8§m*------§7§m------§7§m-§b§m-----------§7§m-§7§m------§8§m------*§9\nplay.inazumauhc.fr\nNetwork System by Alexandre01"));

                    event.setCancelled(true);
                }
            }
            if(dnBungee.isMaintenance){
                if(!dnBungee.allowedPlayer.contains(event.getPlayer().getName().toLowerCase()) && !event.getPlayer().hasPermission("network.maintenance.bypass")){
                    event.getPlayer().disconnect(
                            new TextComponent("§8§m*------§7§m------§7§m-§b§m-----------§7§m-§7§m------§8§m------*\n§cServeur en maintenance!"),
                            new TextComponent("\n\n§eVeuillez réessayer plus tard\n"),
                            new TextComponent("§8§m*------§7§m------§7§m-§b§m-----------§7§m-§7§m------§8§m------*§9\n?????????????\nNetwork System by Alexandre01"));
                    event.setCancelled(true);
                }
            }
            dnBungee.tablistCustomizer.updateTab(event.getPlayer(),1,1);
        }else {
            if(dnBungee.isMaintenance){
                if(!dnBungee.allowedPlayer.contains(event.getPlayer().getName().toLowerCase()) && !event.getPlayer().hasPermission("network.maintenance.bypass")){
                    event.getPlayer().disconnect(new TextComponent("§8§m*------§7§m------§7§m-§b§m-----------§7§m-§7§m------§8§m------*\n§cServeur en maintenance!"),new TextComponent("\n\n§eVeuillez réessayer plus tard\n"),new TextComponent("§8§m*------§7§m------§7§m-§b§m-----------§7§m-§7§m------§8§m------*§9\n?????????????\nNetwork System by Alexandre01"));
                    event.setCancelled(true);
                }
            }
            dnBungee.tablistCustomizer.updateTab(event.getPlayer(),1,1);
        }
    }

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
            ServerInfo info;
            if(dnBungee.connexionOnLobby){
                 info = getServer(dnBungee.lobby);
            }else {
                info = getDefaultServer();
            }
            System.out.println(info);
            if (info == null) {
                System.out.println(dnBungeeAPI.getDnBungeeServersManager());
                System.out.println(dnBungeeAPI.getDnBungeeServersManager().servers);
                event.getPlayer().disconnect( new TextComponent("§8§m*------§7§m------§7§m-§b§m-----------§7§m-§7§m------§8§m------*\n§cAucuns lobbies disponible !"),
                        new TextComponent("\n\n§eVeuillez réessayer plus tard\n"),
                        new TextComponent("§8§m*------§7§m------§7§m-§b§m-----------§7§m-§7§m------§8§m------*§9\n?????????????\nNetwork System by Alexandre01"));
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

        System.out.println("GET DEFAULT SERV");
        System.out.println(dnBungeeAPI.getDnBungeeServersManager().servers);
        if(dnBungeeAPI.getDnBungeeServersManager().servers.isEmpty()){
            return null;
        }else{
            for (String s :dnBungeeAPI.getDnBungeeServersManager().servers){
                System.out.println("API"+s);
            }
            for (String s :  dnBungee.getProxy().getServers().keySet()){
                System.out.println("BungeeSide"+ s);
            }

            ServerInfo serverInfo = dnBungee.getProxy().getServerInfo(dnBungeeAPI.getDnBungeeServersManager().servers.get(0));
            return serverInfo;
        }
    }

    @EventHandler
    public void onServerKick(ServerKickEvent event) {
        if(dnBungee.cancelKick){
            ServerInfo s = getServer(dnBungee.kickServerRedirection);
            if(s == null || s == event.getCancelServer()){
                return;
            }
            event.setCancelled(true);
            event.setCancelServer(s);
        }
    }


    public ServerInfo getServer(String server){
        int i = 0;
        int max = 50;
        String word = null;
        boolean isFound = false;
        for (String str :dnBungeeAPI.getDnBungeeServersManager().servers){
            System.out.println(str);
            if(str.startsWith(dnBungee.lobby)){
                i = Integer.parseInt(str.split("-")[1]);
                word = str.split("-")[0];
                ServerInfo serverInfo = dnBungee.getProxy().getServerInfo(str);
                if(serverInfo.getPlayers().size() < 15){
                    return serverInfo;
                }

            }
        }
        return null;
    }
}
