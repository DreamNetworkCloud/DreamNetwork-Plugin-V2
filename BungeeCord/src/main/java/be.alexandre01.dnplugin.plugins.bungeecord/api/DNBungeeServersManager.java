package be.alexandre01.dnplugin.plugins.bungeecord.api;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.plugins.bungeecord.communication.objects.ProxyService;
import be.alexandre01.dnplugin.utils.Mods;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;
import java.util.ArrayList;

public class DNBungeeServersManager {
    public ArrayList<String> servers = new ArrayList<>();
    NetworkBaseAPI networkBaseAPI;

    public DNBungeeServersManager(NetworkBaseAPI networkBaseAPI){
        this.networkBaseAPI = networkBaseAPI;
    }

    public void registerServer(String processName, String ip, int port,Mods mods){
        try {
            ServerInfo info = ProxyServer.getInstance().constructServerInfo(processName,new InetSocketAddress(ip,port) , "", false);
            ProxyServer.getInstance().getServers().put(processName, info);
            servers.add(processName);
            ProxyService proxyService;
            String name = processName.split("-")[0];
            int id = Integer.parseInt(processName.split("-")[1]);
            networkBaseAPI.getServices().put(name,proxyService = new ProxyService(name, mods,true));
            proxyService.createServer(name,id);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void unregisterServer(String finalname){
        ProxyServer.getInstance().getServers().remove(finalname);
        servers.remove(finalname);
    }
}
