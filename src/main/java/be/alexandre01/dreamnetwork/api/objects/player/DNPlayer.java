package be.alexandre01.dreamnetwork.api.objects.player;

import be.alexandre01.dreamnetwork.api.objects.server.DNServer;

import java.util.UUID;

public class DNPlayer {

    private final String name;
    private final UUID uuid;
    private final DNServer dnServer;
    private final int id;

    public DNPlayer(String name, DNServer dnServer, int id){
        this.name = name;
        this.id = id;
        this.uuid = null;
        this.dnServer = dnServer;
    }

    public DNPlayer(String name, UUID uuid, DNServer dnServer, int id){
        this.name = name;
        this.uuid = uuid;
        this.dnServer = dnServer;
        this.id = id;
    }

    public String getName(){
        return name;
    }


    public UUID getUniqueID(){
        return uuid;
    }
    public void sendMessage(String message){

    }

    public void kick(String reason){

    }

    public void sendToServer(DNServer server){
    }

    public void executeCustomCode(){

    }
}
