package be.alexandre01.dnplugin.plugins.bungeecord.communication.objects;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.objects.RemoteBundle;
import be.alexandre01.dnplugin.api.objects.RemoteExecutor;
import be.alexandre01.dnplugin.api.objects.server.DNServer;
import be.alexandre01.dnplugin.api.connection.request.RequestType;
import be.alexandre01.dnplugin.api.objects.server.ExecutorCallbacks;
import be.alexandre01.dnplugin.api.utils.Mods;

import java.util.Optional;

public class ProxyExecutor extends RemoteExecutor {
    public ProxyExecutor(String name, Mods mods, boolean isStarted, RemoteBundle remoteBundle) {
        super(name,mods,isStarted,remoteBundle);
    }

    @Override
    public Optional<DNServer> getServer(int id) {
        return Optional.ofNullable(getServers().get(id));
    }

    @Override
    public ExecutorCallbacks start() {
        NetworkBaseAPI.getInstance().getRequestManager().sendRequest(RequestType.CORE_START_SERVER,getName());
        return null;
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
        System.out.println("Create Server " + serverName+"-"+id);
        servers.put(id,dnServer);

        if(!isStarted())
            isStarted = true;
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
    }
}
