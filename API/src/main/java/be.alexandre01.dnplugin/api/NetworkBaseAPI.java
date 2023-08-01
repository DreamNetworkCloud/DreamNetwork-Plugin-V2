package be.alexandre01.dnplugin.api;

import be.alexandre01.dnplugin.api.connection.IBasicClient;
import be.alexandre01.dnplugin.api.connection.IClientHandler;
import be.alexandre01.dnplugin.api.objects.RemoteBundle;
import be.alexandre01.dnplugin.api.objects.RemoteService;
import be.alexandre01.dnplugin.api.objects.player.DNPlayer;
import be.alexandre01.dnplugin.api.request.CustomRequestInfo;
import be.alexandre01.dnplugin.api.request.RequestManager;
import be.alexandre01.dnplugin.api.request.RequestType;
import be.alexandre01.dnplugin.api.request.channels.DNChannelManager;
import be.alexandre01.dnplugin.api.request.communication.ResponseManager;
import be.alexandre01.dnplugin.api.request.exception.IDNotFoundException;
import be.alexandre01.dnplugin.api.universal.player.UniversalPlayer;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public abstract class NetworkBaseAPI {
    @Getter @Setter private HashMap<String,RemoteService> services = new HashMap<>();
    @Getter @Setter private HashMap<String, RemoteBundle> bundles = new HashMap<>();

    private boolean isInit = false;
    private final ArrayList<RequestType> requestTypes = new ArrayList<>();
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

    public RemoteBundle getBundleByName(String name){
        return bundles.get(name);
    }


    public abstract String getInfo();

    public abstract String getProcessName();

    public abstract UniversalPlayer getUniversalPlayer(String name);
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

    public abstract IClientHandler getClientHandler();

    public ResponseManager getResponseManager(){
        return responseManager;
    }


    public abstract void setClientHandler(IClientHandler basicClientHandler);

    public abstract void callServerAttachedEvent();

    public abstract void shutdownProcess();

    @Deprecated
    public boolean initConnection(){
        //check if class be.alexandre01.dnplugin.connection.BaseClient exist
        if(isInit){
            throw new RuntimeException("Connection already initialized");
        }

        Class<?> clazz = null;
        try {
            clazz = Class.forName("be.alexandre01.dnplugin.connection.client.BasicClient");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        Object o;
        try {
             o = clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if(o instanceof IBasicClient){
            IBasicClient basicClient = (IBasicClient) o;
            Thread thread = new Thread(basicClient);
            thread.start();
            return isInit = true;
        }
        throw new RuntimeException("Class be.alexandre01.dnplugin.connection.client.BasicClient not found or castable");
    }

    public boolean isSpigot(){
        if(getInfo().startsWith("SPIGOT")){
            return true;
        }
        return false;
    }

    public boolean isBungee(){
        if(getInfo().startsWith("BUNGEE")){
            return true;
        }
        return false;
    }

    public boolean isVelocity(){
        if(getInfo().startsWith("VELOCITY")){
            return true;
        }
        return false;
    }
}
