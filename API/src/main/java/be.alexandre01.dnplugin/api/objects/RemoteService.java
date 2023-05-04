package be.alexandre01.dnplugin.api.objects;

import be.alexandre01.dnplugin.api.objects.player.DNPlayer;
import be.alexandre01.dnplugin.api.objects.server.DNServer;
import be.alexandre01.dnplugin.utils.Mods;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter @Setter
public abstract class RemoteService {
    protected List<DNPlayer> players = new ArrayList<>();
    protected HashMap<Integer, DNServer> servers = new HashMap<>();
    private String name;
    private Mods mods;

    protected boolean isStarted;

    public RemoteService(String name,Mods mods,boolean isStarted){
        this.name = name;
        this.mods = mods;
        this.isStarted = isStarted;
    }

    public abstract void start();
}