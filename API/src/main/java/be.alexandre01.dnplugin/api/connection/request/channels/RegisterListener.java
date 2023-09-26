package be.alexandre01.dnplugin.api.connection.request.channels;

import com.google.gson.internal.LinkedTreeMap;
import lombok.Getter;

public abstract class RegisterListener {
    @Getter
    private DNChannel channel;
    private LinkedTreeMap<String, Object> newData;

    public void createInitialData(String key, Object data) {
        if (!contains(key)) {
            channel.initDataIfNotExist(key, data);
            newData.put(key, data);
        }
    }

    protected void setChannel(DNChannel dnChannel) {
        this.channel = dnChannel;
    }

    public boolean contains(String key) {
        return channel.getNewData().containsKey(key);
    }

    public Object get(String key) {
        return channel.getNewData().get(key);
    }

    public <T> T get(String key, Class<T> tClass) {
        return (T) channel.getNewData().get(key);
    }

    public void executeNewData(LinkedTreeMap<String, Object> newData) {

       /* System.out.println("PUT ALL " + newData);
        System.out.println("OBJECTS ? " + channel.getObjects());
        System.out.println(channel.getName());
        System.out.println(channel);*/
        this.newData = newData;
        onNewDataReceived(this.newData);
        channel.getObjects().putAll(newData);
    }

    public abstract void onNewDataReceived(LinkedTreeMap<String, Object> newData);
}
