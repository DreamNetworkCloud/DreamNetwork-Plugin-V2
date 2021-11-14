package be.alexandre01.dreamnetwork.api.request;

import be.alexandre01.dreamnetwork.api.request.channels.ChannelPacket;

public interface RequestFutureResponse {
    void onReceived(ChannelPacket receivedPacket);
}
