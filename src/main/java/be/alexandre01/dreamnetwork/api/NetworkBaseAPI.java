package be.alexandre01.dreamnetwork.api;

import be.alexandre01.dreamnetwork.api.objects.RemoteService;
import be.alexandre01.dreamnetwork.api.objects.server.DNServer;
import be.alexandre01.dreamnetwork.api.request.RequestManager;
import be.alexandre01.dreamnetwork.api.request.channels.DNChannelManager;
import be.alexandre01.dreamnetwork.api.request.communication.ResponseManager;
import be.alexandre01.dreamnetwork.connection.client.handler.BasicClientHandler;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public abstract class NetworkBaseAPI {
    @Getter @Setter private HashMap<String,RemoteService> services = new HashMap<>();

    private static NetworkBaseAPI instance;

    public static NetworkBaseAPI getInstance() {
        return instance;
    }
    public NetworkBaseAPI(){
        instance = this;
    }

    public ResponseManager responseManager = new ResponseManager(this);


    public RemoteService getByName(String process){
        return services.get(process);
    }


    public abstract String getInfo();

    public abstract String getProcessName();

    public abstract void setProcessName(String processName);


    public abstract String getServerName();

    public abstract void setServerName(String serverName);

    public abstract int getID();

    public abstract void setID(int id);

    public abstract int getPort();

    public abstract Logger getLogger();

    public abstract RequestManager getRequestManager();

    public abstract DNChannelManager getChannelManager();

    public abstract void setRequestManager(RequestManager requestManager);

    public abstract BasicClientHandler getBasicClientHandler();

    public ResponseManager getResponseManager(){
        return responseManager;
    }


    public abstract void setBasicClientHandler(BasicClientHandler basicClientHandler);

    public abstract void shutdownProcess();



}
