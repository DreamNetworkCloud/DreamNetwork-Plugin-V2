package be.alexandre01.dnplugin.api.objects.player;

import be.alexandre01.dnplugin.api.objects.server.DNServer;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;

public class DNPlayerManager {
    @Getter private final HashMap<Integer, DNPlayer> dnPlayers = new HashMap<>();
    @Getter private final HashMap<DNServer, ArrayList<DNPlayer>> dnPlayersByServer = new HashMap<>();

}
