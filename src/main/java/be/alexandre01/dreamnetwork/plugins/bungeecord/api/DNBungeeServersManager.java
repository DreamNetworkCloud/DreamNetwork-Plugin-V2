package be.alexandre01.dreamnetwork.plugins.bungeecord.api;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;
import java.util.ArrayList;

public class DNBungeeServersManager {
    public ArrayList<String> servers = new ArrayList<>();
    public void registerServer(String processName, String ip, int port){
        try {
            ServerInfo info = ProxyServer.getInstance().constructServerInfo(processName,new InetSocketAddress(ip,port) , "", false);
            ProxyServer.getInstance().getServers().put(processName, info);
            servers.add(processName);
            System.out.println("ADD SERVER "+ processName);
            System.out.println(servers);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void unregisterServer(String finalname){
        ProxyServer.getInstance().getServers().remove(finalname);
        servers.remove(finalname);
    }
}
