package be.alexandre01.dnplugin.api.request.channels;

import be.alexandre01.dnplugin.utils.messages.Message;
import lombok.Setter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AskedData<T>{
        @Setter
        private String key;
        @Setter private DNChannel dnChannel;


        public void get(GetDataThread<T> getDataThread){
            ExecutorService pool = Executors.newSingleThreadExecutor();
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    //dSystem.out.println("Submitted !");
                    CompletableFuture<T> completableFuture = new CompletableFuture<>();
                    new ChannelPacket(dnChannel.getName(), dnChannel.getNetworkBaseAPI().getProcessName()).createResponse(new Message().set("key", key),"cAsk");

                    if(dnChannel.getAutoSendObjects().containsKey(key) && dnChannel.getObjects().containsKey(key)){
                        getDataThread.onComplete(completableFuture.getNow((T) dnChannel.getObjects().get(key)));
                        pool.shutdown();
                        return;
                    }


                    dnChannel.getCompletables().put(key, completableFuture);
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
