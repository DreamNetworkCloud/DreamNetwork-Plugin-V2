package be.alexandre01.dnplugin.plugins.bungeecord.api;

import be.alexandre01.dnplugin.utils.Mods;

import java.util.ArrayList;

public interface DNBungeeServersManager {
    void registerServer(String processName, String ip, int port, Mods mods);

    void unregisterServer(String finalname);

    ArrayList<String> getServers();
}
