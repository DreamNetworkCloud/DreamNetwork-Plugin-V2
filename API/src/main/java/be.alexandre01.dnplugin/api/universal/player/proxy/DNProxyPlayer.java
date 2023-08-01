package be.alexandre01.dnplugin.api.universal.player.proxy;

import be.alexandre01.dnplugin.api.objects.server.DNServer;
import be.alexandre01.dnplugin.api.universal.player.UniversalPlayer;

public abstract class DNProxyPlayer extends UniversalPlayer {
    protected DNProxyPlayer() {
        super();
    }

    public abstract int getPing();
    public abstract void sendData(String channel, byte[] data);
}
