package be.alexandre01.dnplugin.api.objects.player;

import be.alexandre01.dnplugin.api.objects.server.DNServer;
import be.alexandre01.dnplugin.api.universal.player.UniversalPlayer;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DNPlayer extends RemoteHuman {

    private final String name;
    private final UUID uuid;

    private final int id;
    private final UniversalPlayer universalPlayer;


    public DNPlayer(String name, DNServer dnServer, int id, UniversalPlayer universalPlayer){
        super(dnServer);
        this.name = name;
        this.id = id;
        this.universalPlayer = universalPlayer;
        this.uuid = null;

    }

    public DNPlayer(String name, UUID uuid, DNServer dnServer, int id, UniversalPlayer universalPlayer){
        super(dnServer);
        this.name = name;
        this.uuid = uuid;
        this.id = id;
        this.universalPlayer = universalPlayer;
    }

    public String getName(){
        return name;
    }


    public UUID getUniqueId(){
        return uuid;
    }


}
