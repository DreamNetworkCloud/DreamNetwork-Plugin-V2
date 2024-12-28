package be.alexandre01.dnplugin.plugins.bungeecord.communication;

import be.alexandre01.dnplugin.api.connection.request.packets.RequestHandler;
import be.alexandre01.dnplugin.api.objects.server.DNServer;
import be.alexandre01.dnplugin.api.universal.player.proxy.communication.PlayerRequestHandler;
import be.alexandre01.dnplugin.api.universal.player.proxy.communication.PlayerRequests;
import be.alexandre01.dnplugin.plugins.bungeecord.DNBungee;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 06/11/2023 at 20:37
*/
@SuppressWarnings("unused")
public class BungeePlayerHandlerClass {
    @PlayerRequestHandler(id = PlayerRequests.SEND_SERVER)
    public void changeServer(ProxiedPlayer player, DNServer server){
        ServerInfo serverInfo = DNBungee.getInstance().getProxy().getServerInfo(server.getVisibleFullName());
        if(serverInfo == null){
            player.sendMessage("§cThe server "+server.getVisibleFullName()+" doesn't exist");
            return;
        }
        player.connect(serverInfo, (success, throwable) -> {
            if(!success){
                player.sendMessage("§cAn error occured while connecting to the server "+server.getVisibleFullName());
            }
        });
    }

    @PlayerRequestHandler(id = PlayerRequests.SEND_TITLE)
    public void createTitle(ProxiedPlayer player, String title, String subtitle, int fadeIn, int stay, int fadeOut){
        DNBungee.getInstance().getProxy().createTitle()
        .title(new TextComponent(title))
        .subTitle(new TextComponent(subtitle))
        .fadeIn(fadeIn)
        .stay(stay)
        .fadeOut(fadeOut)
        .send(player);
    }

    @PlayerRequestHandler(id = PlayerRequests.KICK, castOption = RequestHandler.PacketCastOption.NULLABLE)
    public void kick(ProxiedPlayer player, String message){
        if(message == null){
            player.disconnect();
            return;
        }
        if(message.isEmpty()){
            player.disconnect();
            return;
        }
        player.disconnect(new TextComponent(message));
    }
}
