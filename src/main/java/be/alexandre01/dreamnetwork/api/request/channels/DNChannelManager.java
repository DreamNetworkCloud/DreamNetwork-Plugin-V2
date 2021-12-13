package be.alexandre01.dreamnetwork.api.request.channels;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.api.request.RequestType;

import java.util.ArrayList;
import java.util.HashMap;

public class DNChannelManager {
    final HashMap<String, DNChannel> channels;

    public DNChannelManager(){
        channels = new HashMap<>();
    }

    public boolean hasChannel(String name){
        return channels.containsKey(name);
    }
    public DNChannel getChannel(String name){
        return channels.get(name);
    }

    public DNChannel registerChannel(DNChannel dnChannel){
        return registerChannel(dnChannel,false);
    }
    public DNChannel registerChannel(DNChannel dnChannel,boolean receiveSendedMessage){
        if(hasChannel(dnChannel.getName())){
            System.out.println("Existe deja ?");
            return getChannel(dnChannel.getName());
        }
        System.out.println("Tu envoie fdp ?");
        NetworkBaseAPI.getInstance().getRequestManager().sendRequest(RequestType.CORE_REGISTER_CHANNEL,dnChannel.getName(),receiveSendedMessage);
        channels.put(dnChannel.getName(),dnChannel);

        return dnChannel;
    }

    public DNChannel registerChannel(String channelName,boolean receiveSendedMessage){
        if(hasChannel(channelName)){
            return getChannel(channelName);
        }
        NetworkBaseAPI.getInstance().getRequestManager().sendRequest(RequestType.CORE_REGISTER_CHANNEL,channelName,receiveSendedMessage);
        DNChannel dnChannel = new DNChannel(channelName);
        System.out.println(dnChannel);
        channels.put(channelName,dnChannel);
        return dnChannel;
    }
    public DNChannel registerChannel(String channelName){
        System.out.println("RegisterChannel");
        return registerChannel(channelName,false);
    }
    public void unRegisterChannel(DNChannel dnChannel){
        NetworkBaseAPI.getInstance().getRequestManager().sendRequest(RequestType.CORE_UNREGISTER_CHANNEL,dnChannel.getName());
        channels.remove(dnChannel.getName());
    }

}
