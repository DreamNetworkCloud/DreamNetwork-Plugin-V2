package be.alexandre01.dnplugin.api.connection;



import be.alexandre01.dnplugin.api.connection.request.DNCallback;
import be.alexandre01.dnplugin.api.connection.request.DNCallbackReceiver;
import be.alexandre01.dnplugin.api.connection.request.TaskHandler;

import java.util.Optional;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 10/09/2023 at 22:54
*/
public interface ICallbackManager {

    void addCallback(int MID, TaskHandler handler);

    void removeCallback(int MID, TaskHandler handler);

    void addCallback(int MID, DNCallback callback);


    Optional<TaskHandler> getHandlerOf(int MID);
}