package be.alexandre01.dnplugin.api;

import be.alexandre01.dnplugin.api.objects.RemoteService;
import be.alexandre01.dnplugin.api.request.CustomRequestInfo;
import be.alexandre01.dnplugin.api.request.RequestManager;
import be.alexandre01.dnplugin.api.request.RequestType;
import be.alexandre01.dnplugin.api.request.channels.DNChannelManager;
import be.alexandre01.dnplugin.api.request.communication.ResponseManager;
import be.alexandre01.dnplugin.api.request.exception.IDNotFoundException;
import be.alexandre01.dnplugin.connection.client.handler.BasicClientHandler;
import lombok.Getter;
import lombok.Setter;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public abstract class NetworkBaseAPI {
    @Getter @Setter private HashMap<String,RemoteService> services = new HashMap<>();
    private ArrayList<RequestType> requestTypes = new ArrayList<>();
    private static NetworkBaseAPI instance;

    public static NetworkBaseAPI getInstance() {
        return instance;
    }
    public NetworkBaseAPI(){
        instance = this;
    }

    private ResponseManager responseManager = new ResponseManager(this);

    public void registerRequestType(String addonName, RequestType requestType){
        for (Field field : requestType.getClass().getFields()){
            if(field.getType() == CustomRequestInfo.class){
                try {
                    CustomRequestInfo requestInfo = (CustomRequestInfo) field.get(null);
                    requestInfo.setAddonName(addonName);
                    try {
                        requestInfo.setId(RequestType.getIdByName(requestInfo.name+"#"+addonName.replaceAll(" ","_")));
                    }catch (IDNotFoundException e){
                        continue;
                    }
                    RequestType.addRequestInfo(requestInfo);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        requestTypes.add(requestType);
    }
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
