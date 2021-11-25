package be.alexandre01.dreamnetwork.api;

import be.alexandre01.dreamnetwork.api.objects.RemoteService;
import be.alexandre01.dreamnetwork.api.request.RequestManager;
import be.alexandre01.dreamnetwork.api.request.channels.DNChannelManager;
import be.alexandre01.dreamnetwork.connection.client.handler.BasicClientHandler;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public abstract class NetworkBaseAPI {
    @Getter @Setter private ArrayList<String> servers = new ArrayList<>();
    @Getter @Setter private HashMap<String,RemoteService> services = new HashMap<>();

    private static NetworkBaseAPI instance;

    public static NetworkBaseAPI getInstance() {
        return instance;
    }
    public NetworkBaseAPI(){
        instance = this;
    }




    public abstract String getInfo();

    public abstract String getProcessName();

    public abstract void setProcessName(String processName);

    public abstract int getPort();

    public abstract Logger getLogger();

    public abstract RequestManager getRequestManager();

    public abstract DNChannelManager getChannelManager();

    public abstract void setRequestManager(RequestManager requestManager);

    public abstract BasicClientHandler getBasicClientHandler();

    public abstract void setBasicClientHandler(BasicClientHandler basicClientHandler);

    public abstract void shutdownProcess();



}
