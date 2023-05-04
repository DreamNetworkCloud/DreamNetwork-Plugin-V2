package be.alexandre01.dnplugin.api.request.channels;

public interface DNChannelInterceptor {
    public void received(ChannelPacket receivedPacket);
}
