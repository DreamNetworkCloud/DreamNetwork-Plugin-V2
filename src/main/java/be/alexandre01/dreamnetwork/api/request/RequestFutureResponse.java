package be.alexandre01.dreamnetwork.api.request;

import be.alexandre01.dreamnetwork.utils.messages.Message;

public interface RequestFutureResponse {
    void onReceived(ReceivedPacket receivedPacket);
}
