package be.alexandre01.dreamnetwork.connection.client.objects;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.api.objects.RemoteService;
import be.alexandre01.dreamnetwork.api.objects.server.DNServer;
import be.alexandre01.dreamnetwork.api.request.RequestType;
import be.alexandre01.dreamnetwork.plugins.spigot.api.events.server.ServerStartedEvent;
import be.alexandre01.dreamnetwork.plugins.spigot.api.events.server.ServerStoppedEvent;
import be.alexandre01.dreamnetwork.utils.Mods;
import org.bukkit.Bukkit;

public class BaseService extends RemoteService {
    public BaseService(String name,Mods mods,boolean isStarted) {
        super(name,mods,isStarted);
    }

    @Override
    public void start() {
        NetworkBaseAPI.getInstance().getRequestManager().sendRequest(RequestType.CORE_START_SERVER,getName());
    }

    @Deprecated
    public void createServer(String serverName){
        System.out.println(serverName);
        String[] data = serverName.split(";");

        String[] numSearch = serverName.split("-");
        int i = Integer.parseInt(numSearch[numSearch.length-1]);

        DNServer dnServer = new DNServer(numSearch[0],i,this);

        if(!isStarted())
            isStarted = true;

        servers.put(i,dnServer);
    }
    public void createServer(String serverName,int id){
        DNServer dnServer = new DNServer(serverName,id,this);

        servers.put(id,dnServer);

        if(!isStarted())
            isStarted = true;

        ServerStartedEvent event = new ServerStartedEvent(dnServer);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void removeServer(String serverName){
        System.out.println(serverName);
        String[] numSearch = serverName.split("-");
        int id = Integer.parseInt(numSearch[numSearch.length-1]);
        removeServer(id);
    }

    public void removeServer(int id){
        DNServer dnServer = servers.get(id);
        servers.remove(id);
        if(servers.isEmpty()){
            isStarted = false;
        }
        ServerStoppedEvent event = new ServerStoppedEvent(dnServer);
        Bukkit.getPluginManager().callEvent(event);
    }
}
