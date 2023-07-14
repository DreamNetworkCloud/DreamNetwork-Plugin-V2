package be.alexandre01.dnplugin.plugins.velocity.listeners;

import be.alexandre01.dnplugin.plugins.velocity.DNVelocity;
import be.alexandre01.dnplugin.utils.files.motd.MOTDYAML;
import be.alexandre01.dnplugin.utils.files.network.NetworkYAML;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public class ServerPingListener {
    private final DNVelocity dnVelocity;
    private final NetworkYAML config;
    private int maxPlayers;
    private int onlinePlayers;
    public ServerPingListener(){
        dnVelocity = DNVelocity.getInstance();
        config = dnVelocity.getConfiguration();
    }
    @Subscribe
    public void onPing(ProxyPingEvent event){
        MOTDYAML motd = dnVelocity.getYamlManager().getMotd();
        if(!motd.isActivated()){return;}

        ServerPing.Builder ping = event.getPing().asBuilder();

        onlinePlayers = ping.getOnlinePlayers();
        maxPlayers = (motd.isAutoSlotIncrement() ? onlinePlayers+1 : config.getSlots());

        ping.onlinePlayers(onlinePlayers);
        ping.maximumPlayers(maxPlayers);

        String l1 = "";
        String l2 = "";
        if(motd.getContent().size() >= 1){
            l1 = formatString(motd.getContent().get(0));
            if(motd.getContent().size() >= 2){
                l2 = formatString(motd.getContent().get(1));
            }
        }
        ping.description(
                Component.text(l1)
                        .append(Component.text("\n"))
                        .append(Component.text(l2))
        );

        if(motd.isCustomVersionProtocol()) {
            ServerPing.SamplePlayer[] samples = new ServerPing.SamplePlayer[motd.getVersionHover().size()];

            for(int i = 0 ; i != motd.getVersionHover().size() ; i++){
                String msgH = formatString(motd.getVersionHover().get(i));
                samples[i] = new ServerPing.SamplePlayer(msgH, UUID.fromString("0-0-0-0-0"));
            }

            ping.samplePlayers(samples);

            ping.version(new ServerPing.Version(-2, formatString(motd.getCustomVersionMessage())));
        }
        //dnVelocity.getLogger().info(String.valueOf(ping.getVersion().getProtocol()));
        event.setPing(ping.build());
    }

    private String formatString(String s){
        return s.replace("%online%", String.valueOf(onlinePlayers))
                .replace("%max%", String.valueOf(maxPlayers))
                .replace("&", "§");
    }
}
