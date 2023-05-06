package be.alexandre01.dnplugin.plugins.velocity.utils;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.plugins.velocity.DNVelocity;
import be.alexandre01.dnplugin.plugins.velocity.api.DNVelocityAPI;
import be.alexandre01.dnplugin.plugins.velocity.api.DNVelocityServersManager;
import be.alexandre01.dnplugin.plugins.velocity.communication.objects.ProxyService;
import be.alexandre01.dnplugin.utils.Mods;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import lombok.Getter;


import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Optional;

public class VelocityServersManager implements DNVelocityServersManager {
    @Getter public ArrayList<String> servers = new ArrayList<>();
    NetworkBaseAPI networkBaseAPI;

    public VelocityServersManager(NetworkBaseAPI networkBaseAPI){
        this.networkBaseAPI = networkBaseAPI;
    }

    @Override
    public void registerServer(String processName, String ip, int port, Mods mods){
        try {
            ServerInfo info = new ServerInfo(processName,new InetSocketAddress(ip,port));//ProxyServer.getInstance().constructServerInfo(processName,new InetSocketAddress(ip,port) , "", false);
            RegisteredServer server = DNVelocity.getInstance().getServer().registerServer(info);
            System.out.println(DNVelocity.getInstance().getServer().getConfiguration().getServers().entrySet());
            System.out.println(info.getAddress().getHostString());
       //     DNVelocity.getInstance().getServer().getConfiguration().getServers().put(processName,info.getAddress().getHostString()+":"+info.getAddress().getPort());
            servers.add(processName);
            ProxyService proxyService;
            String name = processName.split("-")[0];
            int id = Integer.parseInt(processName.split("-")[1]);
            if(networkBaseAPI.getServices().isEmpty()){
                DNVelocity.getInstance().getServer().unregisterServer(DNVelocity.getInstance().getCoreTemp());
            }
            networkBaseAPI.getServices().put(name,proxyService = new ProxyService(name, mods,true));
            proxyService.createServer(name,id);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void unregisterServer(String finalname){
        Optional<RegisteredServer> server = DNVelocity.getInstance().getServer().getServer(finalname);
        if(!server.isPresent())return;
        if(networkBaseAPI.getServices().size() == 1){
            DNVelocity.getInstance().getServer().registerServer(DNVelocity.getInstance().getCoreTemp());
            return;
        }
        DNVelocity.getInstance().getServer().unregisterServer(server.get().getServerInfo());
        servers.remove(finalname);
    }
}
