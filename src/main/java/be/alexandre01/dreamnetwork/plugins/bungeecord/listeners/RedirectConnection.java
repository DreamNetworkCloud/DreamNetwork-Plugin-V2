package be.alexandre01.dreamnetwork.plugins.bungeecord.listeners;

import be.alexandre01.dreamnetwork.plugins.bungeecord.DNBungee;
import be.alexandre01.dreamnetwork.plugins.bungeecord.api.DNBungeeAPI;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RedirectConnection implements Listener {
    public boolean connexionOnLobby = true;
    private final Set<UUID> pending = new HashSet<>();
    private final DNBungee dnBungee;
    private final DNBungeeAPI dnBungeeAPI;

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
                            new TextComponent(dnBungee.getMessage("connect.slot-full.header",event.getPlayer())+"\n"),
                            new TextComponent(dnBungee.getMessage("connect.slot-full.text-1",event.getPlayer())+  "\n"),
                            new TextComponent("\n\n"+dnBungee.getMessage("connect.slot-full.text-2",event.getPlayer())+ "\n"),
                            new TextComponent(dnBungee.getMessage("connect.slot-full.footer",event.getPlayer())+ "\n"),
                            new TextComponent(dnBungee.getMessage("general.ip",event.getPlayer())));
                    event.setCancelled(true);
                }
            }
            if(dnBungee.isMaintenance){
                if(!dnBungee.allowedPlayer.contains(event.getPlayer().getName().toLowerCase()) && !event.getPlayer().hasPermission("network.maintenance.bypass")){
                    event.getPlayer().disconnect(
                            new TextComponent(dnBungee.getMessage("connect.maintenance.header",event.getPlayer())+"\n"),
                            new TextComponent(dnBungee.getMessage("connect.maintenance.text-1",event.getPlayer())+  "\n"),
                            new TextComponent("\n\n"+dnBungee.getMessage("connect.maintenance.text-2",event.getPlayer())+ "\n"),
                            new TextComponent(dnBungee.getMessage("connect.maintenance.footer",event.getPlayer())+ "\n"),
                            new TextComponent(dnBungee.getMessage("general.ip",event.getPlayer())));
                    event.setCancelled(true);
                }
            }
            dnBungee.tablistCustomizer.updateTab(event.getPlayer(),1,1);
        }else {
            if(dnBungee.isMaintenance){
                if(!dnBungee.allowedPlayer.contains(event.getPlayer().getName().toLowerCase()) && !event.getPlayer().hasPermission("network.maintenance.bypass")){
                    event.getPlayer().disconnect(
                            new TextComponent(dnBungee.getMessage("connect.maintenance.header",event.getPlayer())+"\n"),
                            new TextComponent(dnBungee.getMessage("connect.maintenance.text-1",event.getPlayer())+  "\n"),
                            new TextComponent("\n\n"+dnBungee.getMessage("connect.maintenance.text-2",event.getPlayer())+ "\n"),
                            new TextComponent(dnBungee.getMessage("connect.maintenance.footer",event.getPlayer())+ "\n"),
                            new TextComponent(dnBungee.getMessage("general.ip",event.getPlayer())));
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
            if (info == null) {
                System.out.println(dnBungeeAPI.getDnBungeeServersManager());
                System.out.println(dnBungeeAPI.getDnBungeeServersManager().servers);
                event.getPlayer().disconnect(
                        new TextComponent(dnBungee.getMessage("connect.noLobby.header",event.getPlayer())+"\n"),
                        new TextComponent(dnBungee.getMessage("connect.noLobby.text-1",event.getPlayer())+  "\n"),
                        new TextComponent("\n\n"+dnBungee.getMessage("connect.noLobby.text-2",event.getPlayer())+ "\n"),
                        new TextComponent(dnBungee.getMessage("connect.noLobby.footer",event.getPlayer())+ "\n"),
                        new TextComponent(dnBungee.getMessage("general.ip",event.getPlayer())));
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

        System.out.println(dnBungeeAPI.getDnBungeeServersManager().servers);
        if(dnBungeeAPI.getDnBungeeServersManager().servers.isEmpty()){
            return null;
        }else{
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
