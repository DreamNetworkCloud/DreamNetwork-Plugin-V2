package be.alexandre01.dnplugin.api.objects.player;

import be.alexandre01.dnplugin.api.objects.server.DNServer;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DNPlayer extends RemoteHuman {

    private final String name;
    private final UUID uuid;

    private final int id;

    public DNPlayer(String name, DNServer dnServer, int id){
        super(dnServer);
        this.name = name;
        this.id = id;
        this.uuid = null;
    }

    public DNPlayer(String name, UUID uuid, DNServer dnServer, int id){
        super(dnServer);
        this.name = name;
        this.uuid = uuid;
        this.id = id;
    }

    public String getName(){
        return name;
    }


    public UUID getUniqueID(){
        return uuid;
    }


}