package be.alexandre01.dreamnetwork.plugins.bungeecord.components.listeners;

import be.alexandre01.dreamnetwork.plugins.bungeecord.DNBungee;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.UUID;

public class MOTD implements Listener {
    DNBungee dnBungee;

    public MOTD(){
        dnBungee = DNBungee.getInstance();
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
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPingTheProxy(final ProxyPingEvent e) {
        final ServerPing srvPing = e.getResponse();
        final ServerPing.Protocol version = srvPing.getVersion();
        final ServerPing.Players players = srvPing.getPlayers();


        players.setSample(new ServerPing.PlayerInfo[]{
                new ServerPing.PlayerInfo("§7§m-----------------", UUID.randomUUID()),
                new ServerPing.PlayerInfo("§7 §4 §c §e", UUID.randomUUID()),
                new ServerPing.PlayerInfo("§7 §e §c §a §2 §5 §b §6 §9InazumaUHC", UUID.randomUUID()),
                new ServerPing.PlayerInfo("§7 §4 §c §e", UUID.randomUUID()),
                new ServerPing.PlayerInfo("§7 §eClassico§7:", UUID.randomUUID()),
                new ServerPing.PlayerInfo("§7 §4 §c §e", UUID.randomUUID()),
                new ServerPing.PlayerInfo("§7» §eMDJ CUSTOM", UUID.randomUUID()),
                new ServerPing.PlayerInfo("§7 §eSite§7:", UUID.randomUUID()),
                new ServerPing.PlayerInfo("§7 §b§owww.inazumauhc.fr", UUID.randomUUID()),
                new ServerPing.PlayerInfo("§7 §b §4", UUID.randomUUID()),
                new ServerPing.PlayerInfo("§7§m-----------------", UUID.randomUUID())});

        if(dnBungee.slot != -2 && dnBungee.slot <= players.getOnline()){
            srvPing.setPlayers(new ServerPing.Players(dnBungee.slot,players.getOnline(),players.getSample()));
        }else {
            srvPing.setPlayers(new ServerPing.Players(players.getOnline()+1,players.getOnline(),players.getSample()));
        }

        version.setName("§4[§e1.8§c+§4] §e("+ srvPing.getPlayers().getOnline()+"§c╱§e"+ srvPing.getPlayers().getMax()+")");
        version.setProtocol(999);


        srvPing.setVersion(version);
        TextComponent component = new TextComponent();
        component.addExtra("                     §e§l✯ §9§lInazumaUHC §e§l✯ §f§n§l\n");
       // component.addExtra("            §e▅▆▇ §6§lClassico §f|§9 Inazuma Eleven §f|§a Autres  §e▇▆▅");
        component.addExtra("            §e▅▆▇ §6§lDu lourd arrive ▇▆▅");
        srvPing.setDescriptionComponent(component);
        String address = e.getConnection().getAddress().toString().substring(1).split(":")[0].replace(".","-");


        e.setResponse(srvPing);
    }
}
