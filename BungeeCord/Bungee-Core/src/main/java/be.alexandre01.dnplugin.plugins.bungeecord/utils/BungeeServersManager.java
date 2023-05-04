package be.alexandre01.dnplugin.plugins.bungeecord.utils;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.objects.RemoteService;
import be.alexandre01.dnplugin.plugins.bungeecord.api.DNBungeeServersManager;
import be.alexandre01.dnplugin.plugins.bungeecord.communication.objects.ProxyService;
import be.alexandre01.dnplugin.utils.Mods;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;
import java.util.ArrayList;

public class BungeeServersManager implements DNBungeeServersManager {
    @Getter public ArrayList<String> servers = new ArrayList<>();
    NetworkBaseAPI networkBaseAPI;

    public BungeeServersManager(NetworkBaseAPI networkBaseAPI){
        this.networkBaseAPI = networkBaseAPI;
    }

    @Override
    public void registerServer(String processName, String ip, int port, Mods mods){
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

    @Override
    public void unregisterServer(String finalname){
        ProxyServer.getInstance().getServers().remove(finalname);
        servers.remove(finalname);
    }
}
