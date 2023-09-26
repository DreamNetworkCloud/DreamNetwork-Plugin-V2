package be.alexandre01.dnplugin.connection.client.handler;


import be.alexandre01.dnplugin.api.connection.ICallbackManager;
import be.alexandre01.dnplugin.api.connection.request.DNCallback;
import be.alexandre01.dnplugin.api.connection.request.DNCallbackReceiver;
import be.alexandre01.dnplugin.api.connection.request.TaskHandler;

import java.util.HashMap;
import java.util.Optional;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 09/09/2023 at 11:32
*/
public class CallbackManager implements ICallbackManager {
    HashMap<Integer, TaskHandler> sendedCallbacksHashMap = new HashMap<>();

    public CallbackManager() {

    }
    public void addCallback(int MID, TaskHandler handler) {
        sendedCallbacksHashMap.put(MID, handler);
        TaskHandler.getTimeStamps().put(handler,System.currentTimeMillis()+(handler.getTimeOut()*1000L));
    }

    @Override
    public void removeCallback(int MID,TaskHandler handler) {
        sendedCallbacksHashMap.remove(MID);
        TaskHandler.getTimeStamps().remove(handler);
    }


    public void addCallback(int MID, DNCallback callback) {
        this.addCallback(MID, callback.getHandler());
    }
    @Override
    public Optional<TaskHandler> getHandlerOf(int MID){
        return Optional.ofNullable(sendedCallbacksHashMap.get(MID));
    }
}
