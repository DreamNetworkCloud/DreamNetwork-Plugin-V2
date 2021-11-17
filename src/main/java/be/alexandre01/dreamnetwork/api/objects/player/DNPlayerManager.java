package be.alexandre01.dreamnetwork.api.objects.player;

import be.alexandre01.dreamnetwork.api.objects.server.DNServer;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DNPlayerManager {
    @Getter private final HashMap<Integer,DNPlayer> dnPlayers = new HashMap<>();
    @Getter private final HashMap<DNServer, ArrayList<DNPlayer>> dnPlayersByServer = new HashMap<>();

}
