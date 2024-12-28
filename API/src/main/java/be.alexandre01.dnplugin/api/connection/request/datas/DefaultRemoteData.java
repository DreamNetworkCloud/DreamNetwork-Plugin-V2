package be.alexandre01.dnplugin.api.connection.request.datas;

import be.alexandre01.dnplugin.api.connection.request.DNCallback;
import be.alexandre01.dnplugin.api.connection.request.RequestType;
import be.alexandre01.dnplugin.api.connection.request.TaskHandler;
import be.alexandre01.dnplugin.api.connection.request.exception.NoDataFoundException;
import be.alexandre01.dnplugin.api.objects.server.NetEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 06/11/2023 at 15:28
*/


public class DefaultRemoteData<T> extends RemoteData<T> {

    public DefaultRemoteData(String key, T data, NetEntity netEntity) {
        super(key, data, netEntity);
    }

    public DefaultRemoteData(String key, NetEntity netEntity) {
        super(key, netEntity);
    }

    public void setData(Object value){
        super.data = (T) value;
        getConsumers().forEach(consumer -> consumer.accept((T) value));
    }

    public List<Consumer<T>> getConsumers(){
        return super.consumers;
    }
}
