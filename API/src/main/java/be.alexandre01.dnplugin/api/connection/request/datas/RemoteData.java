package be.alexandre01.dnplugin.api.connection.request.datas;

import be.alexandre01.dnplugin.api.connection.request.DNCallback;
import be.alexandre01.dnplugin.api.connection.request.RequestType;
import be.alexandre01.dnplugin.api.connection.request.TaskHandler;
import be.alexandre01.dnplugin.api.connection.request.exception.NoDataFoundException;
import be.alexandre01.dnplugin.api.objects.server.NetEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 06/11/2023 at 15:28
*/


public class RemoteData<T> {
   @Getter private String key;
   protected T data;
   @Getter private NetEntity netEntity;
   @Getter private boolean isSubscribed = false;
   protected List<Consumer<T>> consumers;
   @Builder
   public RemoteData(String key,T data, NetEntity netEntity){
       this.key = key;
       this.data = data;
       this.netEntity = netEntity;
   }

    @Builder
    public RemoteData(String key,NetEntity netEntity){
        this.key = key;
        this.netEntity = netEntity;
    }
   public CompletableFuture<Boolean> subscribe(Consumer<T> consumer){
       if(consumers == null){
           consumers = new ArrayList<>();
       }
       if(isSubscribed){
           consumers.add(consumer);
           return CompletableFuture.completedFuture(true);
       }
       CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
       DNCallback.single(netEntity.getRequestManager().getRequest(RequestType.UNIVERSAL_SUBSCRIBE_DATA, key), new TaskHandler() {
           @Override
           public void onAccepted() {
               completableFuture.complete(true);
               consumers.add(consumer);
               isSubscribed = true;
           }

           @Override
              public void onRejected() {
                completableFuture.complete(false);
                isSubscribed = false;
              }
       }).send();
        return completableFuture;
   }

   public void clear(){
       consumers.clear();
   }

   public CompletableFuture<Boolean> unsubscribe() {
         CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
         DNCallback.single(netEntity.getRequestManager().getRequest(RequestType.UNIVERSAL_UNSUBSCRIBE_DATA, key), new TaskHandler() {
              @Override
              public void onAccepted() {
                completableFuture.complete(true);
                isSubscribed = false;
                consumers = null;
              }

              @Override
              public void onRejected() {
                completableFuture.complete(false);
                isSubscribed = true;
              }
         }).send();
         return completableFuture;
   }

    public CompletableFuture<Void> ask(Consumer<T> consumer){
       CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        DNCallback.single(netEntity.getRequestManager().getRequest(RequestType.UNIVERSAL_CALL_DATA, key), new TaskHandler() {
            @Override
            public void onCallback() {
                Optional<T> optional = getResponse().getOptional("data");
                if(optional.isPresent()){
                    consumer.accept(optional.get());
                    completableFuture.complete(null);
                }else {
                    completableFuture.completeExceptionally(new NoDataFoundException("Data is null"));
                }
            }
        }).send();
        return completableFuture;
    }

    public CompletableFuture<Boolean> overwrite(T data){
         CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
         DNCallback.single(netEntity.getRequestManager().getRequest(RequestType.UNIVERSAL_OVERWRITE_DATA, key,data), new TaskHandler() {
              @Override
              public void onAccepted() {
                    completableFuture.complete(true);
              }
              @Override
             public void onRejected() {
                    completableFuture.complete(false);
              }
         }).send();
         return completableFuture;
    }

    public Optional<T> getLocalData(){
        return Optional.ofNullable(data);
    }
}
