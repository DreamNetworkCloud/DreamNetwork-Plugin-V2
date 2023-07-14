package be.alexandre01.dnplugin.plugins.velocity.listeners;

import be.alexandre01.dnplugin.plugins.velocity.DNVelocity;
import be.alexandre01.dnplugin.utils.files.motd.MOTDYAML;
import be.alexandre01.dnplugin.utils.files.network.NetworkYAML;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.ConnectionHandshakeEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

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
    public void onHandshake(ConnectionHandshakeEvent event) {

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
