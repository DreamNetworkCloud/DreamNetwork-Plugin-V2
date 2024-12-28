package be.alexandre01.dnplugin.api.connection.request;

import be.alexandre01.dnplugin.api.connection.request.channels.ChannelPacket;

public interface RequestFutureResponse {
    void onReceived(ChannelPacket receivedPacket);
}
