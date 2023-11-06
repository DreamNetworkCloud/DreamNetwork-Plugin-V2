package be.alexandre01.dnplugin.api;

import be.alexandre01.dnplugin.api.connection.IClientHandler;
import be.alexandre01.dnplugin.api.connection.request.CustomRequestInfo;
import be.alexandre01.dnplugin.api.connection.request.Packet;
import be.alexandre01.dnplugin.api.connection.request.RequestManager;
import be.alexandre01.dnplugin.api.connection.request.RequestType;
import be.alexandre01.dnplugin.api.connection.request.communication.ReceiverManager;
import be.alexandre01.dnplugin.api.connection.request.packets.PacketHandlingFactory;
import be.alexandre01.dnplugin.api.objects.RemoteBundle;
import be.alexandre01.dnplugin.api.objects.RemoteExecutor;
import be.alexandre01.dnplugin.api.connection.request.channels.DNChannelManager;
import be.alexandre01.dnplugin.api.connection.request.exception.IDNotFoundException;
import be.alexandre01.dnplugin.api.objects.core.NetCore;
import be.alexandre01.dnplugin.api.objects.server.DNServer;
import be.alexandre01.dnplugin.api.objects.server.NetEntity;
import be.alexandre01.dnplugin.api.universal.player.UniversalPlayer;
import be.alexandre01.dnplugin.api.utils.messages.Message;
import be.alexandre01.dnplugin.api.utils.messages.mapper.MapperOfDNServer;
import be.alexandre01.dnplugin.api.utils.messages.mapper.MapperOfDate;
import be.alexandre01.dnplugin.api.utils.messages.mapper.MapperOfRemoteExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Logger;

public abstract class NetworkBaseAPI extends NetCore{
    @Getter @Setter private HashMap<String, RemoteExecutor> services = new HashMap<>();
    @Getter @Setter private HashMap<String, RemoteBundle> bundles = new HashMap<>();

    private boolean isInit = false;
    private final ArrayList<RequestType> requestTypes = new ArrayList<>();
    private static NetworkBaseAPI instance;
    @Getter private final PacketHandlingFactory packetFactory = new PacketHandlingFactory();

    @Getter private boolean isExternal = false;
    @Setter private String connectionID = null;

    // ConfigService configService = new ConfigService();

    @Getter private final ReceiverManager receiverManager = new ReceiverManager(this);
    protected boolean isAttached;
    protected final List<Consumer<NetworkBaseAPI>> consumerList = new ArrayList<>();


    public static NetworkBaseAPI getInstance() {
        return instance;
    }
    public NetworkBaseAPI(){
        super();
        instance = this;
        Message.getDefaultMapper().addMapper(
                new MapperOfRemoteExecutor(),
                new MapperOfDNServer(),
                new MapperOfDate()
        );

    }



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
    public Optional<DNServer> getByName(String process,int id){
        return Optional.ofNullable(services.get(process)).map(remoteExecutor -> remoteExecutor.getServers().get(id));
    }

    public Optional<DNServer> getByFullName(String process){
        try {
            String[] split = process.split("-");
            if(split.length == 2){
                return getByName(split[0],Integer.parseInt(split[1]));
            }
        }catch (Exception e) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    public Optional<RemoteExecutor> getByName(String process){
        return Optional.ofNullable(services.get(process));
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


    public abstract void setClientHandler(IClientHandler basicClientHandler);

    public abstract void callServiceAttachedEvent();

    public abstract void shutdownProcess();

    @Deprecated
    public boolean initConnection(){

        if(isInit){
            throw new RuntimeException("Connection already initialized");
        }
        if(!DNNetworkUtilities.getInstance().initConnection().isPresent()){
            return false;
        }
        isInit = true;
        return true;
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
    @Override
    public Packet dispatch(Packet packet) {
        getClientHandler().writeAndFlush(packet.getMessage());
        return packet;
    }

    @Override
    public Packet dispatch(Packet packet, GenericFutureListener<? extends Future<? super Void>> future) {
        getClientHandler().writeAndFlush(packet.getMessage(),future);
        return packet;
    }

    public void restartCurrentServer() {
        System.out.println("Restarting "+ getProcessName());
        NetworkBaseAPI.getInstance().getRequestManager().getRequest(RequestType.CORE_RESTART_SERVER,getProcessName()).dispatch();
    }


    public void onInitialise(Consumer<NetworkBaseAPI> consumer) {
        System.out.println("Initialise called");
        if(isAttached){
            System.out.println("Initialise already attached");
            consumer.accept(this);
        }else{
            System.out.println("Initialise not yet attached");
            consumerList.add(consumer);
        }
    }

}
