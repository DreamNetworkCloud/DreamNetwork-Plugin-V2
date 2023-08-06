package be.alexandre01.dnplugin.api.request.channels;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.utils.messages.Message;
import com.google.gson.internal.LinkedTreeMap;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Getter
public class DNChannel {
    private final HashMap<String, DataListener> dataListener = new HashMap<>();
    private RegisterListener registerListener = null;
    @Getter(value = AccessLevel.NONE)
    private boolean hasBeenRegistered = false;
    @Getter(value = AccessLevel.NONE)
    private boolean hasCalledNewData = false;

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
                hasCalledNewData = true;
            }
        }

    }

    public Object getLocalData(String key){
        return objects.get(key);
    }
    public <T> T getLocalData(String key, Class<T> clazz){
        return (T) objects.get(key);
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
        if((getRegisterListener() != null && !hasCalledNewData) || force){
            getRegisterListener().executeNewData(map);
            hasBeenRegistered = true;
        }

        System.out.println("Called Register Event ! " + map);


        for(Map.Entry<String,Object> entry : newData.entrySet()){
           /* System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
            System.out.println(getDataListener());
            System.out.println(getDataListener().values());
            System.out.println(getDataListener().keySet());
            System.out.println(getObjects().values());
            System.out.println(this);-*/
            if(getDataListener().containsKey(entry.getKey())){
                getDataListener().get(entry.getKey()).onUpdateData(entry.getValue());
            }
        }
    }
    public void callRegisterEvent(boolean force){
        System.out.println("Called Register Event ! " + newData);
        System.out.println(2);
        //if(newData != null){
            hasBeenRegistered = true;
            if((getRegisterListener() != null && !hasCalledNewData) || force){
                getRegisterListener().executeNewData(newData);
                hasBeenRegistered = true;
            }
            for(Map.Entry<String,Object> entry : newData.entrySet()){
                if(getDataListener().containsKey(entry.getKey())){
                    getDataListener().get(entry.getKey()).onUpdateData(entry.getValue());
                }
            }
       // }
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


}
