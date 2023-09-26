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

    @Deprecated
    public DNChannel registerChannel(DNChannel dnChannel){
        return registerChannel(dnChannel,false,null);
    }
    @Deprecated
    public DNChannel registerChannel(DNChannel dnChannel,boolean receiveSendedMessage){
        return registerChannel(dnChannel,receiveSendedMessage,null);
    }
    @Deprecated
    public DNChannel registerChannel(DNChannel dnChannel, boolean receiveSendedMessage, RegisterListener registerListener){
        if(hasChannel(dnChannel.getName())){
            DNChannel channel = getChannel(dnChannel.getName());
            if(registerListener != null)
                channel.setRegisterListener(registerListener);
            channel.callRegisterEvent(true);
            return channel;
        }
        if(registerListener != null)
            dnChannel.setRegisterListener(registerListener);
        NetworkBaseAPI.getInstance().getRequestManager().sendRequest(RequestType.CORE_REGISTER_CHANNEL,dnChannel.getName(),receiveSendedMessage);
        channels.put(dnChannel.getName(),dnChannel);

        return dnChannel;
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
            return getChannel(channelName);
        }
        DNChannel dnChannel = new DNChannel(channelName);
        if(registerListener != null)
            dnChannel.setRegisterListener(registerListener);
        NetworkBaseAPI.getInstance().getRequestManager().sendRequest(RequestType.CORE_REGISTER_CHANNEL,dnChannel.getName(),receiveSendedMessage);
        channels.put(dnChannel.getName(),dnChannel);
        return dnChannel;
    }

    public DNChannel registerChannel(String channelName){
        return registerChannel(channelName,false,null);
    }
    public void unRegisterChannel(DNChannel dnChannel){
        NetworkBaseAPI.getInstance().getRequestManager().sendRequest(RequestType.CORE_UNREGISTER_CHANNEL,dnChannel.getName());
        channels.remove(dnChannel.getName());
    }

}
