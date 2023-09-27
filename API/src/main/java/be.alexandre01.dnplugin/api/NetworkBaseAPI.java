package be.alexandre01.dnplugin.api;

import be.alexandre01.dnplugin.api.connection.IBasicClient;
import be.alexandre01.dnplugin.api.connection.IClientHandler;
import be.alexandre01.dnplugin.api.connection.request.Packet;
import be.alexandre01.dnplugin.api.connection.request.RequestManager;
import be.alexandre01.dnplugin.api.objects.RemoteBundle;
import be.alexandre01.dnplugin.api.objects.RemoteService;
import be.alexandre01.dnplugin.api.connection.request.CustomRequestInfo;
import be.alexandre01.dnplugin.api.connection.request.RequestType;
import be.alexandre01.dnplugin.api.connection.request.channels.DNChannelManager;
import be.alexandre01.dnplugin.api.connection.request.communication.ResponseManager;
import be.alexandre01.dnplugin.api.connection.request.exception.IDNotFoundException;
import be.alexandre01.dnplugin.api.objects.core.NetCore;
import be.alexandre01.dnplugin.api.objects.server.NetEntity;
import be.alexandre01.dnplugin.api.universal.player.UniversalPlayer;
import be.alexandre01.dnplugin.api.utils.messages.Message;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Logger;

public abstract class NetworkBaseAPI extends NetCore{
    @Getter @Setter private HashMap<String,RemoteService> services = new HashMap<>();
    @Getter @Setter private HashMap<String, RemoteBundle> bundles = new HashMap<>();

    private boolean isInit = false;
    private final ArrayList<RequestType> requestTypes = new ArrayList<>();
    private static NetworkBaseAPI instance;

    @Getter private boolean isExternal = false;
    @Setter private String connectionID = null;


    public static NetworkBaseAPI getInstance() {
        return instance;
    }
    public NetworkBaseAPI(){
        super();
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


    public abstract IClientHandler getClientHandler();

    public ResponseManager getResponseManager(){
        return responseManager;
    }


    public abstract void setClientHandler(IClientHandler basicClientHandler);

    public abstract void callServerAttachedEvent();

    public abstract void shutdownProcess();

    @Deprecated
    public boolean initConnection(){

        if(isInit){
            throw new RuntimeException("Connection already initialized");
        }
        //check if class be.alexandre01.dnplugin.connection.BaseClient exist
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
            isExternal = basicClient.isExternal();

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

    public Optional<String> getConnectionID(){
        return Optional.ofNullable(connectionID);
    }

    @Override
    public Packet writeAndFlush(Message message) {
        return writeAndFlush(message, null);
    }

    @Override
    public Packet writeAndFlush(Message message, GenericFutureListener<? extends Future<? super Void>> listener) {
        getClientHandler().writeAndFlush(message,listener);
        return new Packet() {
            @Override
            public Message getMessage() {
                return message;
            }

            @Override
            public String getProvider() {
                return getProcessName();
            }

            @Override
            public NetEntity getReceiver() {
                return NetworkBaseAPI.this;
            }
        };
    }
}
