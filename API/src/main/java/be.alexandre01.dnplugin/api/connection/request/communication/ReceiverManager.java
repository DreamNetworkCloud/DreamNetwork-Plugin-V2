package be.alexandre01.dnplugin.api.connection.request.communication;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;

import java.lang.reflect.Method;
import java.util.Arrays;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 05/11/2023 at 20:20
*/
public class ReceiverManager {
    private NetworkBaseAPI baseAPI;

    public ReceiverManager(NetworkBaseAPI baseAPI) {
        this.baseAPI = baseAPI;
    }

    public void addResponse(ClientReceiver clientReceiver) {
        System.out.println("Add new response ! " + clientReceiver.getClass().getSimpleName());
        // get all overrided methods and print it
        baseAPI.getClientHandler().addResponse(clientReceiver);
        for (Class<?> c = clientReceiver.getClass(); c != null; c = c.getSuperclass()) {
            try {
                for (Method m : c.getDeclaredMethods()) {
                    if (m.isSynthetic()) {
                        System.out.println(m.getName() + "is synthetic");
                        continue;
                    }
                    // get parameters
                    System.out.println("Declared method for : " + clientReceiver.getClass().getSimpleName() + " : " + m.getName() + ":" + Arrays.toString(m.getParameterTypes()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public void removeResponse(ClientReceiver clientReceiver) {
        baseAPI.getClientHandler().removeResponse(clientReceiver);
    }

}
