package be.alexandre01.dnplugin.api.request;

import be.alexandre01.dnplugin.api.request.channels.ChannelPacket;

public interface RequestFutureResponse {
    void onReceived(ChannelPacket receivedPacket);
}
