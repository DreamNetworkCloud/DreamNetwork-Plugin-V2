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
        NetworkBaseAPI.getInstance().getRequestManager().sendRequest(RequestType.CORE_REGISTER_CHANNEL,dnChannel.getName());
         channels.put(dnChannel.getName(),dnChannel);
         return dnChannel;
    }

    public DNChannel unRegisterChannel(DNChannel dnChannel){
        NetworkBaseAPI.getInstance().getRequestManager().sendRequest(RequestType.CORE_UNREGISTER_CHANNEL,dnChannel.getName());
        return channels.put(dnChannel.getName(),dnChannel);
    }

}
