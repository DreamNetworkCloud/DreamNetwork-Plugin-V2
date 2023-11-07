package be.alexandre01.dnplugin.plugins.bungeecord.api;

import be.alexandre01.dnplugin.api.utils.Mods;

import java.util.ArrayList;

public interface DNBungeeServersManager {
    void registerServer(String processName, String customName,String ip, int port, Mods mods);

    void unregisterServer(String finalname);

    ArrayList<String> getServers();
}
