package be.alexandre01.dnplugin.plugins.velocity.listeners;


import be.alexandre01.dnplugin.plugins.velocity.DNVelocity;
import be.alexandre01.dnplugin.plugins.velocity.api.DNVelocityAPI;
import be.alexandre01.dnplugin.api.utils.files.network.NetworkYAML;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.Opt;


import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class RedirectConnection  {
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
    public void onJoin(LoginEvent event){
        Player player = event.getPlayer();

        NetworkYAML config = dnVelocity.getConfiguration();


        if(config.getSlots() != -2){
            if(dnVelocity.getServer().getPlayerCount()-1 >= config.getSlots()){
                if(!player.hasPermission("network.slot.bypass")){
                    if(config.isMaintenance() && !config.getMaintenanceAllowedPlayers().contains(player.getGameProfile().getName().toLowerCase())){
                        event.setResult(ResultedEvent.ComponentResult.denied(
                                Component.text(dnVelocity.getMessage("connect.maintenance.header", player) + "\n")
                                        .append(Component.text(dnVelocity.getMessage("connect.maintenance.text-1", player) + "\n"))
                                        .append(Component.text("\n\n" + dnVelocity.getMessage("connect.maintenance.text-2", player) + "\n"))
                                        .append(Component.text(dnVelocity.getMessage("connect.maintenance.footer", player) + "\n"))
                                        .append(Component.text(dnVelocity.getMessage("general.ip", player)))
                        ));
                        return;
                    }
                    event.setResult(ResultedEvent.ComponentResult.denied(
                            Component.text(dnVelocity.getMessage("connect.slot-full.header", player) + "\n")
                                    .append(Component.text(dnVelocity.getMessage("connect.slot-full.text-1", player) + "\n"))
                                    .append(Component.text("\n\n" + dnVelocity.getMessage("connect.slot-full.text-2", player) + "\n"))
                                    .append(Component.text(dnVelocity.getMessage("connect.slot-full.footer", player) + "\n"))
                                    .append(Component.text(dnVelocity.getMessage("connect.slot-full.footer", player) + "\n"))
                                    .append(Component.text(dnVelocity.getMessage("general.ip", player)))
                    ));
                    return;
                }
            }

            if(config.isMaintenance()){
                if(!config.getMaintenanceAllowedPlayers().contains(player.getGameProfile().getName().toLowerCase()) && !player.hasPermission("network.maintenance.bypass")){
                    event.setResult(ResultedEvent.ComponentResult.denied(
                            Component.text(dnVelocity.getMessage("connect.maintenance.header",player) + "\n")
                                    .append(Component.text(dnVelocity.getMessage("connect.maintenance.text-1",player) + "\n"))
                                    .append(Component.text("\n\n"+ dnVelocity.getMessage("connect.maintenance.text-2",player) + "\n"))
                                    .append(Component.text(dnVelocity.getMessage("connect.maintenance.footer",player) + "\n"))
                                    .append(Component.text(dnVelocity.getMessage("general.ip",player)))));
                }
            }
        }
    }

    @Subscribe
    public void onPlayerChoose(com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent event) {
        if(dnVelocity.getConfiguration().isConnexionOnLobby()){
            dnVelocity.getLogger().info("[DNVelocity] Redirecting player to lobby server");
            pending.add(event.getPlayer().getGameProfile().getId());
            Optional<RegisteredServer> r = findLobby();
            if(r.isPresent()) {
                event.setInitialServer(r.get());
            }else {
                Player player = event.getPlayer();
                //kick
                Component component =  Component.text(dnVelocity.getMessage("connect.noLobby.header", player) + "\n")
                        .append(Component.text(dnVelocity.getMessage("connect.noLobby.text-1", player) + "\n"))
                        .append(Component.text("\n\n" + dnVelocity.getMessage("connect.noLobby.text-2", player) + "\n"))
                        .append(Component.text(dnVelocity.getMessage("connect.noLobby.footer", player) + "\n"))
                        .append(Component.text(dnVelocity.getMessage("general.ip", player)));
                event.getPlayer().disconnect(component);
                //findAny().ifPresent(event::setInitialServer);
            }
        }else {
            findAny().ifPresent(event::setInitialServer);
        }
    }

    public Optional<RegisteredServer> findAny(){
        return DNVelocity.getInstance().getServer().getAllServers().stream().findAny();
    }

    public Optional<RegisteredServer> findLobby(){
        return DNVelocity.getInstance().getServer().getAllServers().stream().filter(server -> server.getServerInfo().getName().startsWith(dnVelocity.getConfiguration().getLobby())).findAny();
    }

    public Optional<RegisteredServer> findFrom(String name){
        return DNVelocity.getInstance().getServer().getAllServers().stream().filter(server -> server.getServerInfo().getName().startsWith(name)).findAny();
    }

    @Subscribe
    public void onLogin(com.velocitypowered.api.event.connection.LoginEvent event) {
        event.setResult(ResultedEvent.ComponentResult.allowed());
    }
    @Subscribe
    public void onServerKick(KickedFromServerEvent event) {
        NetworkYAML config = dnVelocity.getConfiguration();
        if(config.isKickRedirectionEnabled()){
            Optional<RegisteredServer> s = findFrom(config.getKickRedirectionServer());

            if(!s.isPresent() || s.get() == event.getServer()){
                return;
            }
            event.getServerKickReason().ifPresent(component -> {
                event.getPlayer().sendMessage(() -> {
                    return Component.text("§c").append(component);
                });
            });
            event.setResult(KickedFromServerEvent.RedirectPlayer.create(s.get()));
        }
    }
}
