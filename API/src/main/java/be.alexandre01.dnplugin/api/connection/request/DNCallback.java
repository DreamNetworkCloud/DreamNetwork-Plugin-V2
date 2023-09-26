package be.alexandre01.dnplugin.api.connection.request;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.utils.IdSet;
import be.alexandre01.dnplugin.api.utils.messages.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Synchronized;

import java.util.function.Supplier;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 06/09/2023 at 10:18
*/

@AllArgsConstructor @Getter
public class DNCallback {
    @Getter private final static IdSet currentId = new IdSet();

    final Packet packet;
    final TaskHandler handler;
    private final int id;

    private static DNCallback on(Packet packet, TaskHandler handler){
        Message message = packet.getMessage();
        int id = getNextId();
        message.setInRoot("MID",id);
        message.setInRoot("tOut",handler.getTimeOut());
        handler.MID = id;
        return new DNCallback(packet,handler,id);
    }

    public static DNCallback single(Packet packet, TaskHandler handler){
        handler.isSingle = true;
        return on(packet,handler);
    }

    public static DNCallback multiple(Packet packet,TaskHandler handler){
        handler.isSingle = false;
        return on(packet,handler);
    }

    @Synchronized
    private static int getNextId(){
        int next = currentId.getNextId();
        currentId.add(next);
        return next;
    }

    public void send(){
        System.out.println("Register and send");
        NetworkBaseAPI.getInstance().getClientHandler().getCallbackManager().addCallback(id,handler);
        packet.getReceiver().writeAndFlush(packet.getMessage());
    }
}
