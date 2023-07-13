package be.alexandre01.dnplugin.plugins.velocity.listeners;

import be.alexandre01.dnplugin.plugins.velocity.DNVelocity;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.ConnectionHandshakeEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PlayerListener {
    private DNVelocity dnVelocity;

    public PlayerListener(){
        this.dnVelocity = DNVelocity.getInstance();
    }

    @Subscribe
    public void onPing(ConnectionHandshakeEvent event) {

        System.out.println("test");
    }

    @Subscribe
    public void test(ProxyPingEvent event){
        ServerPing defaultPing = event.getPing();

        ServerPing.Version version = new ServerPing.Version(0, "Bonjour !");

        List<ServerPing.SamplePlayer> sample = Arrays.asList(
                new ServerPing.SamplePlayer("Test 1", UUID.fromString("0-0-0-0-0")),
                new ServerPing.SamplePlayer("Test 2", UUID.fromString("0-0-0-0-0"))
        );
        ServerPing.Players players = new ServerPing.Players(0, 0, sample);

        //Favicon favicon = new Favicon("URL ?");

        //event.setPing(new ServerPing(version, players, component, favicon));
    }

    @Subscribe
    public void onLog(com.velocitypowered.api.event.connection.LoginEvent event) {
        event.setResult(ResultedEvent.ComponentResult.allowed());
    }
    @Subscribe
    public void onLogin(com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent event) {
        event.setInitialServer(DNVelocity.getInstance().getServer().getAllServers().stream().findAny().get());
    }
}
