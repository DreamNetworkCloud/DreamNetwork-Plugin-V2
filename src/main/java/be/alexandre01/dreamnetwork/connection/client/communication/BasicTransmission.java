package be.alexandre01.dreamnetwork.connection.client.communication;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.api.objects.server.DNServer;
import be.alexandre01.dreamnetwork.api.request.channels.ChannelPacket;
import be.alexandre01.dreamnetwork.api.request.RequestPacket;
import be.alexandre01.dreamnetwork.api.request.RequestType;
import be.alexandre01.dreamnetwork.api.request.channels.DNChannel;
import be.alexandre01.dreamnetwork.api.request.channels.DNChannelInterceptor;
import be.alexandre01.dreamnetwork.api.request.communication.ClientResponse;
import be.alexandre01.dreamnetwork.plugins.bungeecord.api.DNBungeeAPI;
import be.alexandre01.dreamnetwork.plugins.spigot.DNSpigot;
import be.alexandre01.dreamnetwork.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;


public class BasicTransmission extends ClientResponse {

    @Override
    public void onResponse(Message message, ChannelHandlerContext ctx) throws Exception {
        ChannelPacket receivedPacket = new ChannelPacket(message);
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

                System.out.println(dnChannel.getDataListener().keySet());
                System.out.println(dnChannel.getDataListener().values());
                if(dnChannel.getDataListener().containsKey(key)){
                    dnChannel.getDataListener().get(key).onUpdateData(value);
                }
                dnChannel.getCompletables().get(key).supplyAsync(() -> value).complete(value);

                dnChannel.getObjects().put(key,value);
            }
            return;
        }
        if(message.hasProvider()){
            if(message.getProvider().equals(NetworkBaseAPI.getInstance().getProcessName()) && message.hasRequestID()){
                RequestPacket request = NetworkBaseAPI.getInstance().getRequestManager().getRequest(message.getRequestID());
                if(request != null)
                    request.getRequestFutureResponse().onReceived(receivedPacket);
            }
        }
        if(message.hasRequest()){
            RequestType requestType = message.getRequest();

            if(requestType.equals(RequestType.SPIGOT_HANDSHAKE_SUCCESS)) {
                final NetworkBaseAPI networkBaseAPI = NetworkBaseAPI.getInstance();
                String processName = message.getString("PROCESSNAME");
                 networkBaseAPI.setProcessName("s-" + processName);
                networkBaseAPI.setServerName(processName.split("-")[0]);
                try{
                    networkBaseAPI.setID(Integer.parseInt(processName.split("-")[1]));
                }catch (Exception e){
                    e.printStackTrace();
                }
                System.out.println("The connection has been established on the remote address: " + ctx.channel().remoteAddress());

                DNSpigot.getInstance().callServerAttachedEvent();
            }  else if(requestType.equals(RequestType.BUNGEECORD_HANDSHAKE_SUCCESS)){
                String processName = message.getString("PROCESSNAME");
                final NetworkBaseAPI networkBaseAPI = NetworkBaseAPI.getInstance();
                networkBaseAPI.setProcessName("p-"+processName);
                networkBaseAPI.setServerName(processName.split("-")[0]);
                try{
                    networkBaseAPI.setID(Integer.parseInt(processName.split("-")[1]));
                }catch (Exception e){
                    e.printStackTrace();
                }
                System.out.println("The connection has been established on the remote address: "+ ctx.channel().remoteAddress());
            }
        }
    }
}
