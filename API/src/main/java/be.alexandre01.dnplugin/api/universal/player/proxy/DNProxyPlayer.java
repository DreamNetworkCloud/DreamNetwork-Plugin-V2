package be.alexandre01.dnplugin.api.universal.player.proxy;

import be.alexandre01.dnplugin.api.objects.server.DNServer;
import be.alexandre01.dnplugin.api.universal.player.UniversalPlayer;

import java.util.UUID;

public abstract class DNProxyPlayer extends UniversalPlayer {
    protected DNProxyPlayer(String name, UUID uuid, DNServer dnServer, int id) {
        super(name, uuid, dnServer, id, true);
    }

    public abstract int getPing();
    public abstract void sendData(String channel, byte[] data);
}
