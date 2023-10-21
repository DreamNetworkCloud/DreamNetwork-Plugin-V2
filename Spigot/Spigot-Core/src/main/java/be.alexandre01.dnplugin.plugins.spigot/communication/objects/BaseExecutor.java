package be.alexandre01.dnplugin.plugins.spigot.communication.objects;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.connection.request.DNCallback;
import be.alexandre01.dnplugin.api.connection.request.RequestPacket;
import be.alexandre01.dnplugin.api.connection.request.TaskHandler;
import be.alexandre01.dnplugin.api.objects.RemoteBundle;
import be.alexandre01.dnplugin.api.objects.RemoteExecutor;
import be.alexandre01.dnplugin.api.objects.server.DNServer;
import be.alexandre01.dnplugin.api.connection.request.RequestType;
import be.alexandre01.dnplugin.api.objects.server.ExecutorCallbacks;
import be.alexandre01.dnplugin.plugins.spigot.DNSpigot;
import be.alexandre01.dnplugin.plugins.spigot.api.events.server.ServerStartedEvent;
import be.alexandre01.dnplugin.plugins.spigot.api.events.server.ServerStoppedEvent;
import be.alexandre01.dnplugin.api.utils.Mods;
import org.bukkit.Bukkit;

import java.util.Optional;

public class BaseExecutor extends RemoteExecutor {
    public BaseExecutor(String name, Mods mods, boolean isStarted, RemoteBundle remoteBundle){
        super(name,mods,isStarted,remoteBundle);
    }

    @Override
    public Optional<DNServer> getServer(int id) {
        return Optional.ofNullable(servers.get(id));
    }

    @Override
    public ExecutorCallbacks start() {
        RequestPacket executecmd = DNSpigot.getAPI().getRequestManager().getRequest(RequestType.CORE_START_SERVER,getName());
        ExecutorCallbacks executorCallbacks = new ExecutorCallbacks();
        DNCallback.multiple(executecmd, new TaskHandler() {
            DNServer dnServer;
            @Override
            public void onCallback() {
                if(hasType(TaskType.CUSTOM)){
                    if(getCustomType().equals("STARTED")){
                        String name = getResponse().getString("name");
                        String[] splittedName = name.split("-");
                        int id = Integer.parseInt(splittedName[splittedName.length-1]);
                        servers.put(id,dnServer = new DNServer(name,id, BaseExecutor.this));
                        if(executorCallbacks.getStartList() != null){
                            executorCallbacks.getStartList().forEach(iCallbackStart -> iCallbackStart.whenStart(dnServer));
                        }
                        return;
                    }
                    if(getCustomType().equals("LINKED")){
                        if(dnServer == null){
                            System.out.println("DNServer is null");
                            onFailed();
                            return;
                        }
                        if(executorCallbacks.getConnectList() != null){
                            executorCallbacks.getConnectList().forEach(iCallbackConnect -> iCallbackConnect.whenConnect(dnServer));
                        }
                        destroy();
                        return;
                    }
                }
            }

            @Override
            public void onFailed() {
                if(executorCallbacks.getFailList() != null){
                    executorCallbacks.getFailList().forEach(ExecutorCallbacks.ICallbackFail::whenFail);
                }
                destroy();
            }
        }).send();
        return executorCallbacks;
    }

    public DNServer createServer(String serverName,int id){
        DNServer dnServer;
        if(servers.containsKey(id)){
            return servers.get(id);
        }else {
            dnServer = new DNServer(serverName,id,this);
            servers.put(id,dnServer);
        }
        if(!isStarted()){
            isStarted = true;
        }
        if(!serverName.equals(NetworkBaseAPI.getInstance().getProcessName())){
            ServerStartedEvent event = new ServerStartedEvent(dnServer);
            Bukkit.getScheduler().scheduleSyncDelayedTask(DNSpigot.getInstance(), () -> {
                Bukkit.getPluginManager().callEvent(event);
            });
        }
        return dnServer;
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
        Bukkit.getScheduler().scheduleSyncDelayedTask(DNSpigot.getInstance(), () -> {
            Bukkit.getPluginManager().callEvent(event);
        });

    }
}
