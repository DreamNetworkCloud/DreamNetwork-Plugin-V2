package be.alexandre01.dnplugin.plugins.bungeecord.components.listeners;

import be.alexandre01.dnplugin.plugins.bungeecord.DNBungee;
import be.alexandre01.dnplugin.utils.files.motd.MOTDYAML;
import be.alexandre01.dnplugin.utils.files.network.NetworkYAML;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ServerPingListener implements Listener {
    private final DNBungee dnBungee;
    private final NetworkYAML config;
    private int maxPlayers;
    private int onlinePlayers;

    public ServerPingListener(){
        dnBungee = DNBungee.getInstance();
        config = dnBungee.getConfiguration();
    }

    @EventHandler
    public void onPing(ProxyPingEvent event) throws IOException {
        MOTDYAML motd = dnBungee.getYamlManager().getMotd();
        if(!motd.isActivated()){return;}

        onlinePlayers = dnBungee.getProxy().getPlayers().size();
        maxPlayers = (motd.isAutoSlotIncrement() ? onlinePlayers+1 : config.getSlots());

        ServerPing s = event.getResponse();
        StringBuilder sb = new StringBuilder();
        for(String l : motd.getContent()){
            sb.append(formatString(l)).append("\n");
        }
        s.setDescriptionComponent(new TextComponent(sb.substring(0, sb.toString().length()-2)));

        if(motd.isCustomVersionProtocol()) {
            s.setVersion(new ServerPing.Protocol(formatString(motd.getCustomVersionMessage()), s.getVersion().getProtocol()));

            ServerPing.PlayerInfo[] hover = new ServerPing.PlayerInfo[motd.getVersionHover().size()];
            for(int i = 0 ; i != hover.length ; i++){
                hover[i] = new ServerPing.PlayerInfo(formatString(motd.getVersionHover().get(i)), UUID.fromString("0-0-0-0-0"));
            }
            s.getPlayers().setSample(hover);
            s.setPlayers(new ServerPing.Players(config.getSlots() ,dnBungee.getProxy().getPlayers().size(), s.getPlayers().getSample()));

        }
        s.setFavicon(Favicon.create(ImageIO.read(new File("server-icon.png"))));
        event.setResponse(s);
    }

    private String formatString(String s){
        return s.replace("%online%", String.valueOf(onlinePlayers))
                .replace("%max%", String.valueOf(maxPlayers))
                .replace("&", "§");
    }
}
