package be.alexandre01.dnplugin.connection.client.communication;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.connection.request.RequestInfo;
import be.alexandre01.dnplugin.api.connection.request.TaskHandler;
import be.alexandre01.dnplugin.api.connection.request.channels.ChannelPacket;
import be.alexandre01.dnplugin.api.connection.request.RequestPacket;
import be.alexandre01.dnplugin.api.connection.request.RequestType;
import be.alexandre01.dnplugin.api.connection.request.channels.DNChannel;
import be.alexandre01.dnplugin.api.connection.request.channels.DNChannelInterceptor;
import be.alexandre01.dnplugin.api.connection.request.communication.ClientResponse;
import be.alexandre01.dnplugin.api.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;

import java.util.logging.Level;


public class BasicTransmission extends ClientResponse {

    @Override
    public void onResponse(Message message, ChannelHandlerContext ctx) throws Exception {
        ChannelPacket receivedPacket = new ChannelPacket(message);
        if(message.getHeader() != null){
            if(message.getHeader().equals("channel")) {
                DNChannel dnChannel = NetworkBaseAPI.getInstance().getChannelManager().getChannel(message.getChannel());
                if(dnChannel != null){
                    if(!dnChannel.getDnChannelInterceptors().isEmpty()){
                        for (DNChannelInterceptor dnChannelInterceptor : dnChannel.getDnChannelInterceptors()){
                            dnChannelInterceptor.received(receivedPacket);
                        }
                    }
                    return;
                }
            }
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

                NetworkBaseAPI.getInstance().callServerAttachedEvent();
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

                            NetworkBaseAPI.getInstance().getLogger().log(Level.INFO,"Handler present => "+ handler.getCustomType());
                            NetworkBaseAPI.getInstance().getLogger().log(Level.INFO,"TaskType present => "+ handler.getTaskType());

                            switch (handler.getTaskType()) {
                                case ACCEPTED:
                                    handler.onAccepted();
                                    break;
                                case REFUSED:
                                    handler.onRefused();
                                    break;
                                case IGNORED:
                                    handler.onFailed();
                                    handler.onIgnored();
                                    break;
                                case FAILED:
                                    System.out.println("Failed");
                                    handler.onFailed();
                                    handler.destroy();
                                    break;
                                case TIMEOUT:
                                    handler.onFailed();
                                    handler.onTimeout();
                                    handler.destroy();
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
