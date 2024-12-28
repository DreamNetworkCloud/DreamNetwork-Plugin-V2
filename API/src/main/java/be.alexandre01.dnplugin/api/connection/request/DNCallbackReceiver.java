package be.alexandre01.dnplugin.api.connection.request;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.objects.server.NetEntity;
import be.alexandre01.dnplugin.api.utils.messages.Message;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;

import java.util.Optional;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 06/09/2023 at 10:18
*/

@Getter
public class DNCallbackReceiver {
    int MID;
    int timeOut = 20;
    long creationTimeStamp;
    Message message;

    public DNCallbackReceiver(int MID, Message message) {
        this.MID = MID;
        this.message = message;
        if(message.containsKeyInRoot("tOut")){
            timeOut = (int) message.getInRoot("tOut");
        }
        creationTimeStamp = System.currentTimeMillis()-100;
    }

    public boolean isOutOfTime(){
        return (creationTimeStamp+(timeOut)*1000L) <= System.currentTimeMillis();
    }

    public boolean send(String custom, GenericFutureListener<? extends Future<? super Void>> listener){
        //response ID = RID
        // Message ID = MID
        return mergeAndSend(new Message(),custom,listener).isPresent();
    }

    public boolean send(String custom){
        return this.send(custom,null);
    }



    public boolean send(TaskHandler.TaskType taskType){
        return this.send(taskType.toString());
    }

    public boolean send(TaskHandler.TaskType taskType, GenericFutureListener<? extends Future<? super Void>> listener){
        return this.send(taskType.toString(),listener);
    }

    public Optional<Message> mergeAndSend(Message message, String custom){
        return mergeAndSend(message,custom,null);
    }
    public Optional<Message> mergeAndSend(Message message, String custom, GenericFutureListener<? extends Future<? super Void>> listener){
        if(isOutOfTime()) return Optional.empty();
        if(this.message.getProvider().isPresent()){
            NetEntity netEntity = this.message.getProvider().get();
            message.setInRoot("RID",MID);
            message.setInRoot("tType",custom);
            //set provider (from) to receiver (to)
            message.setReceiver((String) this.message.getInRoot("from"));
            netEntity.writeAndFlush(message,listener);
            return Optional.of(message);
        }else {
           return Optional.empty();
        }
    }

    public Optional<Message> mergeAndSend(Message message, TaskHandler.TaskType taskType){
        return mergeAndSend(message,taskType.toString());
    }
}
