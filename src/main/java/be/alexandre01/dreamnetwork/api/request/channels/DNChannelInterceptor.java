package be.alexandre01.dreamnetwork.api.request.channels;

public interface DNChannelInterceptor {
    public void received(ChannelPacket receivedPacket);
}
