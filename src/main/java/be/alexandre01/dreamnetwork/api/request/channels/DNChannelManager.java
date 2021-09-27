package be.alexandre01.dreamnetwork.api.request.channels;

import java.util.ArrayList;
import java.util.HashMap;

public class DNChannelManager {
    final HashMap<String, DNChannel> channels;

    public DNChannelManager(){
        channels = new HashMap<>();
    }

    public DNChannel getChannel(String name){
        return channels.get(name);
    }
    public void addChannel(DNChannel dnChannel){
        channels.put(dnChannel.getName(),dnChannel);
    }
}
