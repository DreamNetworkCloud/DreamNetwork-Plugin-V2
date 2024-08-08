package be.alexandre01.dnplugin.api.connection.request;


import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.utils.messages.Message;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 06/09/2023 at 10:18
*/
@Getter
public abstract class TaskHandler {


    public TaskType taskType;
    public Message response;
    public boolean isSingle;
    private int timeOut = 20;
    public int MID;

    @Getter static final HashMap<TaskHandler,Long> timeStamps = new HashMap<>();

    public TaskHandler(int timeOutInSeconds){
        this.timeOut = timeOutInSeconds;
    }

    public TaskHandler(){

    }

    public void setTimeOut(int seconds){
        this.timeOut = seconds;
    }


    public boolean hasType(TaskType taskType){
        return this.taskType == taskType;
    }
    public String getCustomType(){
        return (String) response.getInRoot("tType");
    }

    public void onAccepted(){}

    public void onRejected(){}

    public void onIgnored(){}

    public void onFailed(){}
    public void onCallback(){}

    public void onTimeout(){}

    public void onCustom(String custom){}

    public void destroy() {
        DNCallback.getCurrentId().remove(MID);
        NetworkBaseAPI.getInstance().getClientHandler().getCallbackManager().removeCallback(MID,this);
    }

    public enum TaskType{
        IGNORED,
        REJECTED,
        ACCEPTED,
        SUCCESS,
        FAILED,
        CUSTOM,
        TIMEOUT;
    }


    public void setupHandler(Message message){
        this.response = message;
        try {
            this.taskType = TaskType.valueOf(message.getInRoot("tType").toString());
        }catch (Exception e){
            this.taskType = TaskType.CUSTOM;
        }
        this.MID = (int) message.getInRoot("RID");
    }
    static {
        Executors.newScheduledThreadPool(2).scheduleAtFixedRate(() -> {
            Long l = System.currentTimeMillis();

            //non blocking timeStamps loop
            List<TaskHandler> handlersToRemove = new ArrayList<>();

            timeStamps.forEach((taskHandler, timestamp) -> {
                if(timestamp-l <= 0){
                    taskHandler.onTimeout();
                    taskHandler.onFailed();
                    handlersToRemove.add(taskHandler);
                }
            });

            handlersToRemove.forEach(handler -> {
                handler.destroy();
                timeStamps.remove(handler);
            });
        },5,5, TimeUnit.SECONDS);
    }
}
