package be.alexandre01.dreamnetwork.api.request.channels;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.utils.messages.Message;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public class DNChannel {
    private String name;
    private final ArrayList<DNChannelInterceptor> dnChannelInterceptors = new ArrayList<>();

    public DNChannel(String name){
        this.name = name;
    }


    public DNChannel addInterceptor(DNChannelInterceptor dnChannelInterceptor){
        dnChannelInterceptors.add(dnChannelInterceptor);
        return this;
    }
    public DNChannel sendMessage(Message message){
        message.setProvider(NetworkBaseAPI.getInstance().getInfo());
        message.setChannel(getName());
        ChannelPacket channelPacket = new ChannelPacket(message);
        channelPacket.createResponse(message);
        return this;
    }

    public interface DNChannelInterceptor{
        public void received(ChannelPacket receivedPacket);
    }
}
