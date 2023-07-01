package be.alexandre01.dnplugin.plugins.velocity.listeners;

import be.alexandre01.dnplugin.plugins.velocity.DNVelocity;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.ConnectionHandshakeEvent;

public class PlayerListener {


    @Subscribe
    public void onPing(ConnectionHandshakeEvent event) {
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
