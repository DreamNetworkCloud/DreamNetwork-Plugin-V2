package be.alexandre01.dnplugin.api.connection.request.channels;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.connection.request.RequestType;
import lombok.Getter;

import java.util.HashMap;

public class DNChannelManager {
    @Getter final HashMap<String, DNChannel> channels;

    public DNChannelManager(){
        channels = new HashMap<>();
    }

    public boolean hasChannel(String name){
        return channels.containsKey(name);
    }
    public DNChannel getChannel(String name){
        return channels.get(name);
    }

    public DNChannel registerChannel(String channelName,boolean receiveSendedMessage){
        return registerChannel(channelName,receiveSendedMessage,null);
    }
    public DNChannel registerChannel(String channelName, boolean receiveSendedMessage, RegisterListener registerListener){
        if(hasChannel(channelName)){
            DNChannel channel = getChannel(channelName);
            if(registerListener != null)
                channel.setRegisterListener(registerListener);
            channel.callRegisterEvent(true);
            channel.isAccessible = true;
            return channel;
        }
        DNChannel channel = new DNChannel(channelName);
        if(registerListener != null)
            channel.setRegisterListener(registerListener);
        NetworkBaseAPI.getInstance().getRequestManager().sendRequest(RequestType.CORE_REGISTER_CHANNEL,channel.getName(),receiveSendedMessage);
        channels.put(channel.getName(),channel);
        channel.isAccessible = true;
        return channel;
    }

    public DNChannel registerChannel(String channelName){
        return registerChannel(channelName,false,null);
    }
    public void unRegisterChannel(DNChannel dnChannel){
        NetworkBaseAPI.getInstance().getRequestManager().sendRequest(RequestType.CORE_UNREGISTER_CHANNEL,dnChannel.getName());
        //channels.remove(dnChannel.getName());
    }

}
