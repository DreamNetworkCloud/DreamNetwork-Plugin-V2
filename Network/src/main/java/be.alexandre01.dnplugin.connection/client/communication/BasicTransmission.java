package be.alexandre01.dnplugin.connection.client.communication;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.connection.request.RequestInfo;
import be.alexandre01.dnplugin.api.connection.request.TaskHandler;
import be.alexandre01.dnplugin.api.connection.request.channels.ChannelPacket;
import be.alexandre01.dnplugin.api.connection.request.RequestType;
import be.alexandre01.dnplugin.api.connection.request.channels.DNChannel;
import be.alexandre01.dnplugin.api.connection.request.channels.DNChannelInterceptor;
import be.alexandre01.dnplugin.api.connection.request.communication.ClientReceiver;
import be.alexandre01.dnplugin.api.connection.request.datas.DefaultRemoteData;
import be.alexandre01.dnplugin.api.connection.request.datas.RemoteData;
import be.alexandre01.dnplugin.api.objects.server.NetEntity;
import be.alexandre01.dnplugin.api.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.NetUtil;


public class BasicTransmission extends ClientReceiver {
    final NetworkBaseAPI baseAPI;

    public BasicTransmission(){
        baseAPI = NetworkBaseAPI.getInstance();
        addRequestInterceptor(RequestType.CORE_STOP_SERVER,(message, ctx) -> {
            System.out.println("Stopping server");
            message.getCallback().ifPresent(receiver -> {
                System.out.println("Received callback");
                receiver.send(TaskHandler.TaskType.ACCEPTED,future -> {
                    System.out.println("Sending accepted");
                    NetworkBaseAPI.getInstance().shutdownProcess();
                });
            });
        });

        addRequestInterceptor(RequestType.UNIVERSAL_CALL_DATA,(message, ctx) -> {
            message.getCallback().ifPresent(receiver -> {
                String key;
                if(baseAPI.getLocalDatas().containsKey(key = message.getString("key"))){
                    receiver.mergeAndSend(new Message().set("data",baseAPI.getLocalDatas().get(key)),TaskHandler.TaskType.ACCEPTED);
                }else{
                    receiver.send(TaskHandler.TaskType.FAILED);
                }
            });
        });
        addRequestInterceptor(RequestType.UNIVERSAL_OVERWRITE_DATA,(message, ctx) -> {
            message.getCallback().ifPresent(receiver -> {
                try {
                    baseAPI.setLocalData(message.getString("key"),message.get("data"));
                    receiver.send(TaskHandler.TaskType.ACCEPTED);
                }catch (RuntimeException e){
                    receiver.send(TaskHandler.TaskType.FAILED);
                }
            });
        });

        addRequestInterceptor(RequestType.UNIVERSAL_SUBSCRIBE_DATA,(message, ctx) -> {
            message.getProvider().ifPresent(netEntity -> {
                message.getCallback().ifPresent(receiver -> {
                    String key = message.getString("key");
                    if(baseAPI.getDataSubscribers().containsKey(key)){
                        if(baseAPI.getDataSubscribers().get(key).contains(netEntity)){
                            receiver.send(TaskHandler.TaskType.IGNORED);
                            return;
                        }
                    }
                    baseAPI.getDataSubscribers().put(key,netEntity);
                    receiver.send(TaskHandler.TaskType.ACCEPTED);
                });
            });
        });

        addRequestInterceptor(RequestType.UNIVERSAL_UNSUBSCRIBE_DATA,(message, ctx) -> {
            message.getProvider().ifPresent(netEntity -> {
                message.getCallback().ifPresent(receiver -> {
                    String key = message.getString("key");
                    if(baseAPI.getDataSubscribers().containsKey(key)){
                        if(baseAPI.getDataSubscribers().get(key).contains(netEntity)){
                            baseAPI.getDataSubscribers().get(key).remove(netEntity);
                            receiver.send(TaskHandler.TaskType.ACCEPTED);
                            return;
                        }
                    }
                    receiver.send(TaskHandler.TaskType.IGNORED);
                });
            });
        });


        // quand le netserver reçoit une reponse
        addRequestInterceptor(RequestType.UNIVERSAL_SEND_DATA,(message, ctx) -> {
            NetEntity entity;
            if(!message.getProvider().isPresent()){
                entity = NetworkBaseAPI.getInstance();
            }else {
                entity = message.getProvider().get();
            }
            String key = message.getString("key");
            Object value = message.get("value");
            if(!entity.getDataManager().getDatas().containsKey(key)){
                entity.getDataManager().getDatas().put(key,new DefaultRemoteData<>(key,entity));
            }else {
                RemoteData<?> remoteData = entity.getDataManager().getDatas().get(key);
                if(remoteData instanceof DefaultRemoteData){
                    ((DefaultRemoteData<?>) remoteData).setData(value);
                }
            }
        });


    }


    @Override
    public void onReceive(Message message, ChannelHandlerContext ctx) throws Exception {
        ChannelPacket receivedPacket = new ChannelPacket(message);

            if(message.hasChannel()){
                System.out.println("Message from channel "+message.getChannel());
                DNChannel dnChannel = NetworkBaseAPI.getInstance().getChannelManager().getChannel(message.getChannel());
                System.out.println("Channel => ?"+ dnChannel);
                if(dnChannel != null){
                    if(!dnChannel.getDnChannelInterceptors().isEmpty()){
                        System.out.println("Interceptors => "+ dnChannel.getDnChannelInterceptors());
                        for (DNChannelInterceptor dnChannelInterceptor : dnChannel.getDnChannelInterceptors()){
                            dnChannelInterceptor.received(receivedPacket);
                        }
                    }
                    return;
                }
            }
        if(message.getHeader() != null){
            if(message.getHeader().equals("cData")) {
                DNChannel dnChannel = NetworkBaseAPI.getInstance().getChannelManager().getChannel(message.getChannel());
                if(dnChannel != null){
                    String key = message.getString("key");
                    Object value = message.get("value");
                /*System.out.println("key: " + key + " value: " + value);

                System.out.println(dnChannel.getDataListener().keySet());
                System.out.println(dnChannel.getDataListener().values());*/
                    if(dnChannel.getDataListener().containsKey(key)){
                        dnChannel.getDataListener().get(key).onUpdateData(value);
                    }
             /*   System.out.println(dnChannel.getName());
                System.out.println(dnChannel);*/
                    dnChannel.getObjects().put(key,value);
                /*System.out.println(dnChannel.getObjects().keySet());
                System.out.println(dnChannel.getObjects().values());*/

                }
                return;
            }
            if(message.getHeader().equals("cAsk")) {
                DNChannel dnChannel = NetworkBaseAPI.getInstance().getChannelManager().getChannel(message.getChannel());
                if(dnChannel != null){
                    String key = message.getString("key");
                    Object value = message.get("value");

                    if(dnChannel.getDataListener().containsKey(key)){
                        dnChannel.getDataListener().get(key).onUpdateData(value);
                    }
                    dnChannel.getCompletables().get(key).supplyAsync(() -> value).complete(value);

                    dnChannel.getObjects().put(key,value);
                }
                return;
            }
        }

      /*  if(message.hasProvider()){
            if(message.getProvider().equals(NetworkBaseAPI.getInstance().getProcessName()) && message.hasRequestID()){
                RequestPacket request = NetworkBaseAPI.getInstance().getRequestManager().getRequest(message.getMessageID());
                if(request != null){
                    if(request.getRequestFutureResponse() != null)
                        request.getRequestFutureResponse().onReceived(receivedPacket);
                }

            }
        }*/
        if(message.hasRequest()){
            RequestInfo requestInfo = message.getRequest();

            if(requestInfo.equals(RequestType.SERVER_HANDSHAKE_SUCCESS)) {
                final NetworkBaseAPI networkBaseAPI = NetworkBaseAPI.getInstance();
                String processName = message.getString("PROCESSNAME");
                networkBaseAPI.setProcessName(processName);
                networkBaseAPI.setServerName(processName.split("-")[0]);
                try{
                    networkBaseAPI.setID(Integer.parseInt(processName.split("-")[1]));
                }catch (Exception e){
                    e.printStackTrace();
                }
                System.out.println("The connection has been established on the remote address: " + ctx.channel().remoteAddress());
                NetworkBaseAPI.getInstance().callServiceAttachedEvent();
            }  else if(requestInfo.equals(RequestType.PROXY_HANDSHAKE_SUCCESS)){
                String processName = message.getString("PROCESSNAME");
                final NetworkBaseAPI networkBaseAPI = NetworkBaseAPI.getInstance();
                networkBaseAPI.setProcessName(processName);
                networkBaseAPI.setServerName(processName.split("-")[0]);
                try{
                    networkBaseAPI.setID(Integer.parseInt(processName.split("-")[1]));
                }catch (Exception e){
                    e.printStackTrace();
                }
                System.out.println("The connection has been established on the remote address: "+ ctx.channel().remoteAddress());
                NetworkBaseAPI.getInstance().callServiceAttachedEvent();
            }
        }

                // if core send data and received callback

                    if (message.containsKeyInRoot("RID")) {
                        System.out.println("Here "+ message);
                        int id = (int) message.getInRoot("RID");
                        System.out.println(id);
                        NetworkBaseAPI.getInstance().getClientHandler().getCallbackManager().getHandlerOf(id).ifPresent(handler -> {

                            handler.setupHandler(message);
                            handler.onCallback();

                           // NetworkBaseAPI.getInstance().getLogger().log(Level.INFO,"Handler present => "+ handler.getCustomType());
                           // NetworkBaseAPI.getInstance().getLogger().log(Level.INFO,"TaskType present => "+ handler.getTaskType());

                            switch (handler.getTaskType()) {
                                case ACCEPTED:
                                    handler.onAccepted();
                                    break;
                                case REJECTED:
                                    handler.onRejected();
                                    break;
                                case IGNORED:
                                    handler.onIgnored();
                                    break;
                                case FAILED:
                                    handler.onFailed();
                                    handler.destroy();
                                    break;
                                case TIMEOUT:
                                    handler.onFailed();
                                    handler.onTimeout();
                                    handler.destroy();
                                    break;
                                case CUSTOM:
                                    handler.onCustom(handler.getCustomType());
                                    break;
                            }
                            if(handler.isSingle()){
                                handler.destroy();
                            }
                        });
                    /*RequestPacket request = client.getRequestManager().getRequest(message.getMessageID());
                    if(request != null)
                        request.getRequestFutureResponse().onReceived(receivedPacket);*/
        }
            //RequestInfo request = message.getRequest();
    }
}
