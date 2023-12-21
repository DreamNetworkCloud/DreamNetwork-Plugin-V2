package be.alexandre01.dnplugin.plugins.velocity.communication;

import be.alexandre01.dnplugin.api.connection.request.packets.RequestHandler;
import be.alexandre01.dnplugin.api.objects.server.DNServer;
import be.alexandre01.dnplugin.api.universal.player.proxy.communication.PlayerRequestHandler;
import be.alexandre01.dnplugin.api.universal.player.proxy.communication.PlayerRequests;
import be.alexandre01.dnplugin.plugins.velocity.DNVelocity;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 06/11/2023 at 20:37
*/
@SuppressWarnings("unused")
public class VeloPlayerHandlerClass {
    @PlayerRequestHandler(id = PlayerRequests.SEND_SERVER)
    public void changeServer(Player player, DNServer server){
        RegisteredServer registeredServer = DNVelocity.getInstance().getServer().getServer(server.getVisibleFullName()).orElse(null);
        if(registeredServer == null){
            player.sendMessage(Component.text("§cThe server "+server.getVisibleFullName()+" doesn't exist"));
            return;
        }
        player.createConnectionRequest(registeredServer).fireAndForget();
    }

    @PlayerRequestHandler(id = PlayerRequests.SEND_TITLE, castOption = RequestHandler.PacketCastOption.NULLABLE)
    public void createTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut){
        if(title == null){
            title = "";
        }
        if(subtitle == null){
            subtitle = "";
        }
        Title.Times times = Title.Times.of(Duration.ofMillis(fadeIn), Duration.ofMillis(stay), Duration.ofMillis(fadeOut));
        player.showTitle(Title.title(Component.text(title), Component.text(subtitle),times));
    }

    @PlayerRequestHandler(id = PlayerRequests.KICK, castOption = RequestHandler.PacketCastOption.NULLABLE)
    public void kick(Player player, String message){
        if(message == null){
            player.disconnect(Component.empty());
            return;
        }
        if(message.isEmpty()){
            player.disconnect(Component.empty());
            return;
        }
        player.disconnect(Component.text(message));
    }
}
