package be.alexandre01.dreamnetwork.api.request.channels;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.utils.messages.Message;
import com.google.gson.internal.LinkedTreeMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

@Getter
public class DNChannel {
    private final HashMap<String,DataListener> dataListener = new HashMap<>();
    private RegisterListener registerListener = null;
    @Getter(value = AccessLevel.NONE)
    private boolean hasBeenRegistered = false;
    @Getter(value = AccessLevel.NONE)
    private boolean hasCalledNewData = false;
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private LinkedTreeMap<String,Object> newData = null;
    private final String name;
    private final NetworkBaseAPI networkBaseAPI;
    private final HashMap<String, Object> objects = new HashMap<>();
    private final HashMap<String, Boolean> autoSendObjects = new HashMap<>();
    private final ArrayList<DNChannelInterceptor> dnChannelInterceptors = new ArrayList<>();
    private final HashMap<String, CompletableFuture> completables = new HashMap<>();
    public DNChannel(String name){
        this.name = name;
        this.networkBaseAPI = NetworkBaseAPI.getInstance();
    }
    public <T> void setDataListener(String key, Class<T> clazz, DataListener<T> dataListener){
        dataListener.setDnChannel(this);
        dataListener.setKey(key);
        dataListener.setAClass(clazz);
        this.dataListener.put(key, dataListener);
    }
    public Object askData(String key){
        return askData(key,Object.class);
    }

    public void initDataIfNotExist(String key, Object object){
        new ChannelPacket(getName(), networkBaseAPI.getProcessName()).createResponse(new Message().set("key", key).set("value",object).set("init",true),"cData");
    }

    public void setRegisterListener(RegisterListener registerListener){
        this.registerListener = registerListener;
        registerListener.setChannel(this);
        if(hasBeenRegistered && !hasCalledNewData){
            if(newData != null){
                registerListener.executeNewData(newData);
            }
        }
        hasCalledNewData = true;
    }

    public Object getLocalData(String key){
        return objects.get(key);
    }
    public <T> Object getLocalData(String key, Class<T> clazz){
        return objects.get(key);
    }

    public <T> AskedData<T> askData(String key, Class<T> clazz){
        AskedData<T> askedData = new AskedData<T>();
        askedData.setDnChannel(this);
        askedData.setKey(key);

        return askedData;
    }


    public DNChannel setData(String key, Object object){
        objects.put(key, object);
        ChannelPacket channelPacket = new ChannelPacket(getName(), networkBaseAPI.getProcessName());

        if(!autoSendObjects.containsKey(key)){
            autoSendObjects.put(key, true);
            channelPacket.createResponse(new Message().set("key", key).set("value", object).set("update",true),"cData");
            return this;
        }
        channelPacket.createResponse(new Message().set("key", key).set("value", object),"cData");
        setData("",new ArrayList<>());
        return this;
    }

    public void setData(String key, Object object,boolean autoSend){
        ChannelPacket channelPacket = new ChannelPacket(getName(), networkBaseAPI.getProcessName());
        Message message;
        objects.put(key, object);
        if(autoSendObjects.containsKey(key)){
            if(autoSend == autoSendObjects.get(key)){
                channelPacket.createResponse(new Message().set("key", key).set("value", object),"cData");
            }
        }
        autoSendObjects.put(key, autoSend);
        if(autoSend){
            message = new Message().set("key", key).set("value", object).set("update", true);
        }else {
            message = new Message().set("key", key).set("value", object);
        }
        channelPacket.createResponse(message,"cData");
    }

    public boolean hasBeenCalled() {
        return hasCalledNewData;
    }

    public void callRegisterEvent(LinkedTreeMap<String,Object> map){
        callRegisterEvent(map,false);
    }
    public void callRegisterEvent(LinkedTreeMap<String,Object> map,boolean force){
        newData = map;
        hasBeenRegistered = true;
        if(getRegisterListener() != null && !hasCalledNewData || force){
            getRegisterListener().executeNewData(map);
            hasBeenRegistered = true;
        }
    }
    public void callRegisterEvent(boolean force){
        if(newData != null){
            hasBeenRegistered = true;
            if(getRegisterListener() != null && !hasCalledNewData || force){
                getRegisterListener().executeNewData(newData);
                hasBeenRegistered = true;
            }
        }
    }
    public DNChannel addInterceptor(DNChannelInterceptor dnChannelInterceptor){
        dnChannelInterceptors.add(dnChannelInterceptor);
        return this;
    }
    public DNChannel sendMessage(Message message){
        ChannelPacket channelPacket = new ChannelPacket(getName(), networkBaseAPI.getProcessName());
        channelPacket.createResponse(message);
        return this;
    }
    public abstract static class RegisterListener{
        @Getter private DNChannel channel;
        private LinkedTreeMap<String,Object> newData;
        public void createInitialData(String key, Object data){
            if(!contains(key)){
                channel.initDataIfNotExist(key,data);
                newData.put(key,data);
            }
        }

        protected void setChannel(DNChannel dnChannel){
            this.channel = dnChannel;
        }
        public boolean contains(String key){
            return channel.newData.containsKey(key);
        }
        public Object get(String key){
            return channel.newData.get(key);
        }
        public <T> T get(String key, Class<T> tClass){
            return (T) channel.newData.get(key);
        }

        private void executeNewData(LinkedTreeMap<String,Object> newData){
            channel.objects.putAll(newData);
            this.newData = newData;
            onNewDataReceived(this.newData);
        }
        public abstract void onNewDataReceived(LinkedTreeMap<String,Object> newData);
    }

    public abstract static class DataListener<T>{
        @Getter @Setter private DNChannel dnChannel;
        @Setter private String key;
        @Setter @Getter private Class aClass;


        public void set(T data){
            dnChannel.setData(key,data);
        }
        public abstract void onUpdateData(T data);
    }

    public static class AskedData<T>{
        @Setter private String key;
        @Setter private DNChannel dnChannel;


        public void get(GetDataThread<T> getDataThread){
            ExecutorService pool = Executors.newSingleThreadExecutor();
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    //dSystem.out.println("Submitted !");
                    CompletableFuture<T> completableFuture = new CompletableFuture<>();
                    new ChannelPacket(dnChannel.getName(), dnChannel.networkBaseAPI.getProcessName()).createResponse(new Message().set("key", key),"cAsk");

                    if(dnChannel.getAutoSendObjects().containsKey(key) && dnChannel.getObjects().containsKey(key)){
                        getDataThread.onComplete(completableFuture.getNow((T) dnChannel.getObjects().get(key)));
                        pool.shutdown();
                        return;
                    }


                    dnChannel.completables.put(key, completableFuture);
                    try {
                        getDataThread.onComplete(completableFuture.get());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }


    public abstract static class GetDataThread<T>{
        public abstract void onComplete(T t);
    }
}
