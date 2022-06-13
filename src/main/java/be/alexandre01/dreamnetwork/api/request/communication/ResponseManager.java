package be.alexandre01.dreamnetwork.api.request.communication;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;

public class ResponseManager {
    private NetworkBaseAPI baseAPI;

    public ResponseManager(NetworkBaseAPI baseAPI) {
        this.baseAPI = baseAPI;
    }

    public void addResponse(ClientResponse clientResponse){
        baseAPI.getBasicClientHandler().getResponses().add(clientResponse);
    }

    public void removeResponse(ClientResponse clientResponse){
        baseAPI.getBasicClientHandler().getResponses().remove(clientResponse);
    }
}
