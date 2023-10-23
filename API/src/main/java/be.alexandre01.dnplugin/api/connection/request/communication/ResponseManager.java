package be.alexandre01.dnplugin.api.connection.request.communication;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;

import java.lang.reflect.Method;
import java.util.Arrays;

public class ResponseManager {
    private NetworkBaseAPI baseAPI;

    public ResponseManager(NetworkBaseAPI baseAPI) {
        this.baseAPI = baseAPI;
    }

    public void addResponse(ClientReceiver clientReceiver){
        System.out.println("Add new response ! "+ clientReceiver.getClass().getSimpleName());
        System.out.println("Fuck3");
        // get all overrided methods and print it
        System.out.println("Add response hihhiisdfhjpiusdfgoipçujfpoim");
        baseAPI.getClientHandler().addResponse(clientReceiver);
        for (Class<?> c = clientReceiver.getClass(); c != null; c = c.getSuperclass()) {
            System.out.println("Putain de classe "+ c.getSimpleName());
            try {
                for (Method m : c.getDeclaredMethods()) {
                    if (m.isSynthetic()) {
                        System.out.println(m.getName() + "is synthetic");
                        continue;
                    }

                    // get parameters

                    System.out.println("Declared method for : "+ clientReceiver.getClass().getSimpleName()+" : " + m.getName() + ":"+ Arrays.toString(m.getParameterTypes()));
                }
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("IO¨JHUQDSPOIUFSDFOUHFJOPMIUSFGDJIP%OZRE");
            }

        }

    }

    public void removeResponse(ClientReceiver clientReceiver){
        baseAPI.getClientHandler().removeResponse(clientReceiver);
    }
}
