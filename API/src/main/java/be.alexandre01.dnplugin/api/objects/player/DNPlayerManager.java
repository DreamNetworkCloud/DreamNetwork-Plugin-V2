package be.alexandre01.dnplugin.api.objects.player;

import be.alexandre01.dnplugin.api.objects.server.DNServer;
import lombok.Getter;

import java.util.HashMap;

public class DNPlayerManager {
    @Getter private final HashMap<Integer, DNPlayer> dnPlayers = new HashMap<>();
    @Getter private final HashMap<String, DNPlayer> dnPlayersByName = new HashMap<>();

    public void addPlayer(DNPlayer dnPlayer){
        dnPlayer.getServer().getPlayers().add(dnPlayer);
        dnPlayer.getServer().getRemoteExecutor().getPlayers().add(dnPlayer);
        dnPlayers.put(dnPlayer.getId(), dnPlayer);
        dnPlayersByName.put(dnPlayer.getName(), dnPlayer);
        dnPlayer.getUniversalPlayer().getPlayerJoin().forEach(playerJoinListener -> {
            playerJoinListener.onPlayerJoin(dnPlayer.getUniversalPlayer());
        });
    }

    public void removePlayer(DNPlayer dnPlayer){
        removePlayerFromServer(dnPlayer);
        dnPlayers.remove(dnPlayer.getId());
        dnPlayersByName.remove(dnPlayer.getName());
        dnPlayer.getUniversalPlayer().getPlayerQuit().forEach(playerQuitListener -> {
            playerQuitListener.onPlayerQuit(dnPlayer.getUniversalPlayer());
        });
    }

    private void removePlayerFromServer(DNPlayer dnPlayer){
        dnPlayer.getServer().getPlayers().remove(dnPlayer);
        dnPlayer.getServer().getRemoteExecutor().getPlayers().remove(dnPlayer);
    }

    public void addPlayerFromServer(DNPlayer dnPlayer){
        dnPlayer.getServer().getPlayers().add(dnPlayer);
        dnPlayer.getServer().getRemoteExecutor().getPlayers().add(dnPlayer);
    }

    public void updatePlayer(DNPlayer dnPlayer,DNServer dnServer){
        removePlayerFromServer(dnPlayer);
        dnPlayer.updateServer(dnServer);
        addPlayerFromServer(dnPlayer);
        dnPlayer.getUniversalPlayer().getPlayerUpdates().forEach(playerUpdateServer -> playerUpdateServer.onPlayerUpdateServer(dnPlayer.getUniversalPlayer(),dnServer));
    }
}
