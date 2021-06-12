package be.alexandre01.dreamnetwork.api;

import be.alexandre01.dreamnetwork.api.request.RequestManager;
import be.alexandre01.dreamnetwork.connection.client.handler.BasicClientHandler;
import lombok.Getter;
import lombok.Setter;


import java.util.logging.Logger;

public abstract class NetworkBaseAPI {
    private static NetworkBaseAPI instance;

    public static NetworkBaseAPI getInstance() {
        return instance;
    }
    public NetworkBaseAPI(){
        instance = this;
    }



    public abstract String getInfo();

    public abstract int getPort();

    public abstract Logger getLogger();

    public abstract RequestManager getRequestManager();

    public abstract void setRequestManager(RequestManager requestManager);

    public abstract BasicClientHandler getBasicClientHandler();

    public abstract void setBasicClientHandler(BasicClientHandler basicClientHandler);
}
