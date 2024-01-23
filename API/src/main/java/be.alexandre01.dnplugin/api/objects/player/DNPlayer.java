package be.alexandre01.dnplugin.api.objects.player;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.objects.RemoteExecutor;
import be.alexandre01.dnplugin.api.objects.server.DNServer;
import be.alexandre01.dnplugin.api.universal.player.UniversalPlayer;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
public class DNPlayer extends RemoteHuman {

    private final String name;
    @Getter(AccessLevel.NONE) private final UUID uuid;

    private final int id;


    public DNPlayer(String name, UUID uuid, DNServer dnServer, int id){
        super(dnServer);
        this.name = name;
        this.uuid = uuid;
        this.id = id;
    }

    public Optional<UUID> getUniqueId(){
        return Optional.ofNullable(uuid);
    }

   /* public UniversalPlayer cast(){
        return cast(UniversalPlayer.class);
    }
    */

    public DNServer getProxy(){
        return NetworkBaseAPI.getInstance().getMainProxy().map(remoteExecutor -> remoteExecutor.getServers().get(1)).orElseThrow(UnsupportedOperationException::new);
    }

    /*public <T> T cast(Class<? extends T> clazz){
        if(clazz.isInstance(this)){
            return (T) this;
        }else {
            throw new UnsupportedOperationException("This player is not a "+clazz.getSimpleName());
        }
    }*/
}
