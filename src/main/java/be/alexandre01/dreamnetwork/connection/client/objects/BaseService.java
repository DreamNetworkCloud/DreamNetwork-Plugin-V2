package be.alexandre01.dreamnetwork.connection.client.objects;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.api.objects.RemoteService;
import be.alexandre01.dreamnetwork.api.objects.server.DNServer;
import be.alexandre01.dreamnetwork.api.request.RequestType;

public class BaseService extends RemoteService {
    public BaseService(String name) {
        super(name);
    }

    @Override
    public void start() {
        NetworkBaseAPI.getInstance().getRequestManager().sendRequest(RequestType.CORE_CREATE_SERVER,getName());
    }

    public void createServer(String serverName){
        System.out.println(serverName);
        String[] numSearch = serverName.split("-");
        int i = Integer.parseInt(numSearch[numSearch.length-1]);

        DNServer dnServer = new DNServer(numSearch[0],i);

        dnServers.put(i,dnServer);
    }
    public void createServer(String serverName,int id){
        DNServer dnServer = new DNServer(serverName,id);

        dnServers.put(id,dnServer);
    }
    public void removeServer(String serverName){
        System.out.println(serverName);
        String[] numSearch = serverName.split("-");
        int i = Integer.parseInt(numSearch[numSearch.length-1]);

        dnServers.remove(i);
    }

    public void removeServer(int id){
        dnServers.remove(id);
    }
}
