package be.alexandre01.dnplugin.api.universal.player.server;

import be.alexandre01.dnplugin.api.objects.player.DNPlayer;
import be.alexandre01.dnplugin.api.objects.server.DNServer;
import be.alexandre01.dnplugin.api.universal.player.UniversalPlayer;

import java.util.UUID;

public abstract class DNServerPlayer extends UniversalPlayer {
    protected DNServerPlayer(String name, UUID uuid, DNServer dnServer,int id) {
        super(name, uuid, dnServer, id, false);
    }
}
