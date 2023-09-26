package be.alexandre01.dnplugin.api.connection.request.communication;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;

public class ResponseManager {
    private NetworkBaseAPI baseAPI;

    public ResponseManager(NetworkBaseAPI baseAPI) {
        this.baseAPI = baseAPI;
    }

    public void addResponse(ClientResponse clientResponse){
        baseAPI.getClientHandler().getResponses().add(clientResponse);
    }

    public void removeResponse(ClientResponse clientResponse){
        baseAPI.getClientHandler().getResponses().remove(clientResponse);
    }
}
