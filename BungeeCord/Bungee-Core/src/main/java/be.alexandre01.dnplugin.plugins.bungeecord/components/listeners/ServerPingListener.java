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
    DNBungee dnBungee;
    public ServerPingListener(){
        dnBungee = DNBungee.getInstance();
    }

    @EventHandler
    public void onPing(ProxyPingEvent event) throws IOException {
        MOTDYAML motd = dnBungee.getYamlManager().getMotd();
        if(!motd.isActivated()){return;}
        NetworkYAML config = dnBungee.getYamlManager().getNetwork();
        ServerPing s = event.getResponse();
        StringBuilder sb = new StringBuilder();
        for(String l : motd.getContent()){
            sb.append(l.replace("%online%", String.valueOf(dnBungee.getProxy().getPlayers().size())).replace("%max%", String.valueOf(config.getSlots())).replace("&", "§")).append("\n");
        }
        s.setDescriptionComponent(new TextComponent(sb.substring(0, sb.toString().length()-2)));

        ServerPing.PlayerInfo[] hover = new ServerPing.PlayerInfo[motd.getVersionHover().size()];
        for(int i = 0 ; i != hover.length ; i++){
            hover[i] = new ServerPing.PlayerInfo(motd.getVersionHover().get(i).replace("%online%", String.valueOf(dnBungee.getProxy().getPlayers().size())).replace("%max%", String.valueOf(config.getSlots())).replace("&", "§"), UUID.fromString("0-0-0-0-0"));
        }
        s.getPlayers().setSample(hover);
        s.setPlayers(new ServerPing.Players(config.getSlots() ,dnBungee.getProxy().getPlayers().size(), s.getPlayers().getSample()));

        if(motd.isCustomVersionProtocol()) {
            s.setVersion(new ServerPing.Protocol(motd.getCustomVersionMessage().replace("%online%", String.valueOf(dnBungee.getProxy().getPlayers().size())).replace("%max%", String.valueOf(config.getSlots())).replace("&", "§"), s.getVersion().getProtocol()));
        }
        s.setFavicon(Favicon.create(ImageIO.read(new File("server-icon.png"))));
        event.setResponse(s);
    }
}
