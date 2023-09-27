package be.alexandre01.dnplugin.plugins.velocity.listeners;

import be.alexandre01.dnplugin.plugins.velocity.DNVelocity;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.ConnectionHandshakeEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;

public class PlayerServerListener {
    private DNVelocity dnVelocity;

    public PlayerServerListener(){
        this.dnVelocity = DNVelocity.getInstance();
    }

    /*@Subscribe
    public void onSwitch(ServerConnectedEvent event){
        dnVelocity.getPlayerManagement().updatePlayer(event.getPlayer());
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event){
        dnVelocity.getPlayerManagement().removePlayer(event.getPlayer());
    }*/

    @Subscribe
    public void onHandshake(ConnectionHandshakeEvent event) {

    }



}
