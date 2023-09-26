package be.alexandre01.dnplugin.api.connection.request.channels;

public interface DNChannelInterceptor {
    public void received(ChannelPacket receivedPacket);
}
