package be.alexandre01.dnplugin.api.objects.player;

import be.alexandre01.dnplugin.api.objects.server.DNServer;
import lombok.Getter;

public abstract class RemoteHuman {
    @Getter private DNServer server;

    protected RemoteHuman(DNServer server) {
        this.server = server;
    }

    public void updateServer(DNServer dnServer){
        this.server = dnServer;
    }
}
