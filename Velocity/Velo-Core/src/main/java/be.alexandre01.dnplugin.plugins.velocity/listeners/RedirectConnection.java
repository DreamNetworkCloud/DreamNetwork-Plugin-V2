package be.alexandre01.dnplugin.plugins.velocity.listeners;


import be.alexandre01.dnplugin.plugins.velocity.DNVelocity;
import be.alexandre01.dnplugin.plugins.velocity.api.DNVelocityAPI;
import be.alexandre01.dnplugin.api.utils.files.network.NetworkYAML;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;


import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class RedirectConnection  {
    public boolean connexionOnLobby = true;
    private final Set<UUID> pending = new HashSet<>();
    private final DNVelocity dnVelocity;
    private final DNVelocityAPI dnVelocityAPI;
    private final int max;

    public RedirectConnection(){
        dnVelocity = DNVelocity.getInstance();
        dnVelocityAPI = (DNVelocityAPI) DNVelocityAPI.getInstance();
        max = dnVelocity.getConfiguration().getMaxPlayerPerLobby();
    }

    @Subscribe
    public void onPJoin(com.velocitypowered.api.event.connection.LoginEvent e){
        e.setResult(ResultedEvent.ComponentResult.allowed());
    }

    @Subscribe
    public void onJoin(ServerConnectedEvent event){
        Player player = event.getPlayer();
        RegisteredServer server = event.getServer();
        Optional<RegisteredServer> pastServer = event.getPreviousServer();

        NetworkYAML config = dnVelocity.getConfiguration();

        if(config.getSlots() != -2){
            if(dnVelocity.getServer().getPlayerCount()-1 >= config.getSlots()){
                if(!player.hasPermission("network.slot.bypass")){
                    if(config.isMaintenance() && !config.getMaintenanceAllowedPlayers().contains(player.getGameProfile().getName().toLowerCase())){
                        player.disconnect(
                                Component.text(dnVelocity.getMessage("connect.maintenance.header", player) + "\n")
                                        .append(Component.text(dnVelocity.getMessage("connect.maintenance.text-1", player) + "\n"))
                                        .append(Component.text("\n\n" + dnVelocity.getMessage("connect.maintenance.text-2", player) + "\n"))
                                        .append(Component.text(dnVelocity.getMessage("connect.maintenance.footer", player) + "\n"))
                                        .append(Component.text(dnVelocity.getMessage("general.ip", player)))
                        );
                        return;
                    }
                    player.disconnect(
                            Component.text(dnVelocity.getMessage("connect.slot-full.header", player) + "\n")
                                    .append(Component.text(dnVelocity.getMessage("connect.slot-full.text-1", player) + "\n"))
                                    .append(Component.text("\n\n" + dnVelocity.getMessage("connect.slot-full.text-2", player) + "\n"))
                                    .append(Component.text(dnVelocity.getMessage("connect.slot-full.footer", player) + "\n"))
                                    .append(Component.text(dnVelocity.getMessage("connect.slot-full.footer", player) + "\n"))
                                    .append(Component.text(dnVelocity.getMessage("general.ip", player)))
                    );
                    return;
                }
            }

            if(config.isMaintenance()){
                if(!config.getMaintenanceAllowedPlayers().contains(player.getGameProfile().getName().toLowerCase()) && !player.hasPermission("network.maintenance.bypass")){
                    player.disconnect(
                            Component.text(dnVelocity.getMessage("connect.maintenance.header",player) + "\n")
                                    .append(Component.text(dnVelocity.getMessage("connect.maintenance.text-1",player) + "\n"))
                                    .append(Component.text("\n\n"+ dnVelocity.getMessage("connect.maintenance.text-2",player) + "\n"))
                                    .append(Component.text(dnVelocity.getMessage("connect.maintenance.footer",player) + "\n"))
                                    .append(Component.text(dnVelocity.getMessage("general.ip",player))));
                }
            }
        }
    }

    @Subscribe
    public void onPlayerChoose(com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent event) {
        if(dnVelocity.getConfiguration().isConnexionOnLobby()){
            dnVelocity.getLogger().info("[DNBungee] Redirecting player to lobby server");
            pending.add(event.getPlayer().getGameProfile().getId());
            Optional<RegisteredServer> r = DNVelocity.getInstance().getServer().getServer(dnVelocity.getConfiguration().getLobby());
            if(r.isPresent()) {
                event.setInitialServer(r.get());
            }else {
                findAny().ifPresent(event::setInitialServer);
            }
        }else {
            findAny().ifPresent(event::setInitialServer);
        }
    }

    public Optional<RegisteredServer> findAny(){
        return DNVelocity.getInstance().getServer().getAllServers().stream().findAny();
    }

    @Subscribe
    public void onLogin(com.velocitypowered.api.event.connection.LoginEvent event) {
        event.setResult(ResultedEvent.ComponentResult.allowed());
    }

 /*   @EventHandler
    public void onJoin(ServerConnectEvent event){
        if(dnVelocity.slot != -2){
            if(dnVelocity.getProxy().getPlayers().size()-1>= dnVelocity.slot){
                if(!event.getPlayer().hasPermission("network.slot.bypass")){
                    event.getPlayer().disconnect(
                            new TextComponent(dnVelocity.getMessage("connect.slot-full.header",event.getPlayer())+"\n"),
                            new TextComponent(dnVelocity.getMessage("connect.slot-full.text-1",event.getPlayer())+  "\n"),
                            new TextComponent("\n\n"+ dnVelocity.getMessage("connect.slot-full.text-2",event.getPlayer())+ "\n"),
                            new TextComponent(dnVelocity.getMessage("connect.slot-full.footer",event.getPlayer())+ "\n"),
                            new TextComponent(dnVelocity.getMessage("general.ip",event.getPlayer())));
                    event.setCancelled(true);
                }
            }
            if(dnVelocity.isMaintenance){
                if(!dnVelocity.allowedPlayer.contains(event.getPlayer().getName().toLowerCase()) && !event.getPlayer().hasPermission("network.maintenance.bypass")){
                    event.getPlayer().disconnect(
                            new TextComponent(dnVelocity.getMessage("connect.maintenance.header",event.getPlayer())+"\n"),
                            new TextComponent(dnVelocity.getMessage("connect.maintenance.text-1",event.getPlayer())+  "\n"),
                            new TextComponent("\n\n"+ dnVelocity.getMessage("connect.maintenance.text-2",event.getPlayer())+ "\n"),
                            new TextComponent(dnVelocity.getMessage("connect.maintenance.footer",event.getPlayer())+ "\n"),
                            new TextComponent(dnVelocity.getMessage("general.ip",event.getPlayer())));
                    event.setCancelled(true);
                }
            }
            dnVelocity.tablistCustomizer.updateTab(event.getPlayer(),1,1);
        }else {
            if(dnVelocity.isMaintenance){
                if(!dnVelocity.allowedPlayer.contains(event.getPlayer().getName().toLowerCase()) && !event.getPlayer().hasPermission("network.maintenance.bypass")){
                    event.getPlayer().disconnect(
                            new TextComponent(dnVelocity.getMessage("connect.maintenance.header",event.getPlayer())+"\n"),
                            new TextComponent(dnVelocity.getMessage("connect.maintenance.text-1",event.getPlayer())+  "\n"),
                            new TextComponent("\n\n"+ dnVelocity.getMessage("connect.maintenance.text-2",event.getPlayer())+ "\n"),
                            new TextComponent(dnVelocity.getMessage("connect.maintenance.footer",event.getPlayer())+ "\n"),
                            new TextComponent(dnVelocity.getMessage("general.ip",event.getPlayer())));
                    event.setCancelled(true);
                }
            }
            dnVelocity.tablistCustomizer.updateTab(event.getPlayer(),1,1);
        }
    }

    @EventHandler
    public void onPlayerConnect(PostLoginEvent event) {
        if(connexionOnLobby){
            System.out.println("[DNBungee] Redirecting player to lobby server");
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
            if(dnVelocity.connexionOnLobby){
                 info = getServer(dnVelocity.lobby);
            }else {
                info = getDefaultServer();
            }
            if (info == null) {
                event.getPlayer().disconnect(
                        new TextComponent(dnVelocity.getMessage("connect.noLobby.header",event.getPlayer())+"\n"),
                        new TextComponent(dnVelocity.getMessage("connect.noLobby.text-1",event.getPlayer())+  "\n"),
                        new TextComponent("\n\n"+ dnVelocity.getMessage("connect.noLobby.text-2",event.getPlayer())+ "\n"),
                        new TextComponent(dnVelocity.getMessage("connect.noLobby.footer",event.getPlayer())+ "\n"),
                        new TextComponent(dnVelocity.getMessage("general.ip",event.getPlayer())));
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
        if(dnBungeeAPI.getDnBungeeServersManager().getServers().isEmpty()){
            return null;
        }else{
            ServerInfo serverInfo = dnVelocity.getProxy().getServerInfo(dnBungeeAPI.getDnBungeeServersManager().getServers().get(0));
            return serverInfo;
        }
    }

    @EventHandler
    public void onServerKick(ServerKickEvent event) {
        if(dnVelocity.cancelKick){
            ServerInfo s = getServer(dnVelocity.kickServerRedirection);
            if(s == null || s == event.getCancelServer()){
                return;
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
            if(str.startsWith(dnVelocity.lobby)){
                i = Integer.parseInt(str.split("-")[1]);
                word = str.split("-")[0];
                ServerInfo serverInfo = dnVelocity.getProxy().getServerInfo(str);

                if(serverInfo.getPlayers().size() < max){
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
    }*/
}
