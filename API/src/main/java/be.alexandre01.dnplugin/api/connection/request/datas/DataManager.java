package be.alexandre01.dnplugin.api.connection.request.datas;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.connection.request.datas.RemoteData;
import be.alexandre01.dnplugin.api.objects.server.NetEntity;
import lombok.Getter;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 06/11/2023 at 15:18
*/
public class DataManager {
    private final NetEntity netEntity;

    @Getter private final HashMap<String, RemoteData<?>> datas = new HashMap<>();

    public DataManager(NetEntity netEntity){
        this.netEntity = netEntity;
    }



    public <T> RemoteData<T> find(String key, Class<T> tClass){
        if(datas.containsKey(key)){
            return (RemoteData<T>) datas.get(key);
        }
        RemoteData<T> remoteData = new RemoteData<>(key,netEntity);
        datas.put(key,remoteData);
        return remoteData;
    }
}
