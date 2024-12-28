package be.alexandre01.dnplugin.plugins.bungeecord.listeners;

import be.alexandre01.dnplugin.plugins.bungeecord.DNBungee;
import be.alexandre01.dnplugin.plugins.bungeecord.api.DNBungeeAPI;
import be.alexandre01.dnplugin.api.utils.files.network.NetworkYAML;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RedirectConnection implements Listener {


    private final Set<UUID> pending = new HashSet<>();
    private final DNBungee dnBungee;
    private final DNBungeeAPI dnBungeeAPI;

    public RedirectConnection(){
        dnBungee = DNBungee.getInstance();
        dnBungeeAPI = DNBungeeAPI.getInstance();
    }

    @EventHandler
    public void onJoin(ServerConnectEvent event){
        NetworkYAML config = dnBungee.getConfiguration();
        ProxiedPlayer player = event.getPlayer();
        if(config.getSlots() != -2){
            if(dnBungee.getProxy().getPlayers().size()-1 >= config.getSlots()){
                if(!player.hasPermission("network.slot.bypass")){
                    player.disconnect(
                            new TextComponent(dnBungee.getMessage("connect.slot-full.header", player)+"\n"),
                            new TextComponent(dnBungee.getMessage("connect.slot-full.text-1", player)+  "\n"),
                            new TextComponent("\n\n"+dnBungee.getMessage("connect.slot-full.text-2", player)+ "\n"),
                            new TextComponent(dnBungee.getMessage("connect.slot-full.footer", player)+ "\n"),
                            new TextComponent(dnBungee.getMessage("general.ip", player)));
                    event.setCancelled(true);
                }
            }
            if(config.isMaintenance()){
                if(!config.getMaintenanceAllowedPlayers().contains(player.getName().toLowerCase()) && !player.hasPermission("network.maintenance.bypass")){
                    player.disconnect(
                            new TextComponent(dnBungee.getMessage("connect.maintenance.header", player)+"\n"),
                            new TextComponent(dnBungee.getMessage("connect.maintenance.text-1", player)+  "\n"),
                            new TextComponent("\n\n"+dnBungee.getMessage("connect.maintenance.text-2", player)+ "\n"),
                            new TextComponent(dnBungee.getMessage("connect.maintenance.footer", player)+ "\n"),
                            new TextComponent(dnBungee.getMessage("general.ip", player)));
                    event.setCancelled(true);
                }
            }
            //dnBungee.tablistCustomizer.updateTab(event.getPlayer(),1,1);
        }else {
            if(config.isMaintenance()){
                if(!config.getMaintenanceAllowedPlayers().contains(player.getName().toLowerCase()) && !player.hasPermission("network.maintenance.bypass")){
                    player.disconnect(
                            new TextComponent(dnBungee.getMessage("connect.maintenance.header", player)+"\n"),
                            new TextComponent(dnBungee.getMessage("connect.maintenance.text-1", player)+  "\n"),
                            new TextComponent("\n\n"+dnBungee.getMessage("connect.maintenance.text-2", player)+ "\n"),
                            new TextComponent(dnBungee.getMessage("connect.maintenance.footer", player)+ "\n"),
                            new TextComponent(dnBungee.getMessage("general.ip", player)));
                    event.setCancelled(true);
                }
            }
            //dnBungee.tablistCustomizer.updateTab(event.getPlayer(),1,1);
        }
    }

    @EventHandler
    public void onPlayerConnect(PostLoginEvent event) {
        if(dnBungee.getConfiguration().isConnexionOnLobby()){
            pending.add(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        if(!dnBungeeAPI.isManagingConnections()){
            return;
        }
        NetworkYAML config = dnBungee.getConfiguration();
        ProxiedPlayer player = event.getPlayer();
            if (!isPending(player.getUniqueId())) {
                return;
            }
            ServerInfo info;
            if(config.isConnexionOnLobby()){
                 info = getServer(config.getLobby());
            }else {
                info = getDefaultServer();
            }
            if (info == null) {
                player.disconnect(
                        new TextComponent(dnBungee.getMessage("connect.noLobby.header", player)+"\n"),
                        new TextComponent(dnBungee.getMessage("connect.noLobby.text-1", player)+  "\n"),
                        new TextComponent("\n\n"+dnBungee.getMessage("connect.noLobby.text-2", player)+ "\n"),
                        new TextComponent(dnBungee.getMessage("connect.noLobby.footer", player)+ "\n"),
                        new TextComponent(dnBungee.getMessage("general.ip", player)));
                return;
            }
            event.setTarget(info);
            pending.remove(player.getUniqueId());
    }
    private boolean isPending(UUID uuid) {
        return pending.contains(uuid);
    }

    private ServerInfo getDefaultServer(){
        if(dnBungeeAPI.getDnBungeeServersManager().getServers().isEmpty()){
            return null;
        }else{
            ServerInfo serverInfo = dnBungee.getProxy().getServerInfo(dnBungeeAPI.getDnBungeeServersManager().getServers().get(0));
            return serverInfo;
        }
    }

    @EventHandler
    public void onServerKick(ServerKickEvent event) {
        NetworkYAML config = dnBungee.getConfiguration();
        if(config.isKickRedirectionEnabled()){
            ServerInfo s = getServer(config.getKickRedirectionServer());
            if(s == null || s == event.getCancelServer()){
                return;
            }
            BaseComponent[] baseComponents = new BaseComponent[event.getKickReasonComponent().length+1];
            baseComponents[0] = new TextComponent("§c");
            for(int i = 0; i < event.getKickReasonComponent().length; i++){
                baseComponents[i+1] = event.getKickReasonComponent()[i];
            }

            if(event.getPlayer() != null || event.getPlayer().isConnected() || event.getCancelServer() != null){
                event.getPlayer().sendMessage(baseComponents);
            }

            event.setCancelled(true);
            event.setCancelServer(s);
        }
    }
    public ServerInfo getServer(String server){
        int i = 0;
        ServerInfo serverInfoFree = null;
        String word = null;

        for (String str :dnBungeeAPI.getDnBungeeServersManager().getServers()){
            if(str.startsWith(dnBungee.getConfiguration().getLobby())){
                i = Integer.parseInt(str.split("-")[1]);
                word = str.split("-")[0];
                ServerInfo serverInfo = dnBungee.getProxy().getServerInfo(str);

                if(serverInfo.getPlayers().size() < dnBungee.getConfiguration().getMaxPlayerPerLobby()){
                    if(serverInfoFree != null){
                        if(serverInfoFree.getPlayers().size() < serverInfo.getPlayers().size()){
                            serverInfoFree = serverInfo;
                        }
                    }else {
                        serverInfoFree = serverInfo;
                    }
                }
            }
        }
        if(serverInfoFree != null){
            return serverInfoFree;
        }
        return null;
    }
}
