package be.alexandre01.dnplugin.api.objects.player;

import be.alexandre01.dnplugin.api.objects.server.DNServer;
import be.alexandre01.dnplugin.api.universal.player.UniversalPlayer;
import lombok.Getter;

import java.util.HashMap;

public class DNPlayerManager {
    @Getter private final HashMap<Integer, UniversalPlayer> dnPlayers = new HashMap<>();
    @Getter private final HashMap<String, UniversalPlayer> dnPlayersByName = new HashMap<>();

    public void addPlayer(UniversalPlayer dnPlayer){
        dnPlayer.getServer().getPlayers().add(dnPlayer);
        dnPlayer.getServer().getRemoteExecutor().getPlayers().add(dnPlayer);
        dnPlayers.put(dnPlayer.getId(), dnPlayer);
        dnPlayersByName.put(dnPlayer.getName(), dnPlayer);
        ((UniversalPlayer) dnPlayer).getPlayerJoin().forEach(UniversalPlayer.PlayerJoinListener::onPlayerJoin);
    }

    public void removePlayer(UniversalPlayer dnPlayer){
        removePlayerFromServer(dnPlayer);
        dnPlayers.remove(dnPlayer.getId());
        dnPlayersByName.remove(dnPlayer.getName());
        ((UniversalPlayer) dnPlayer).getPlayerQuit().forEach(UniversalPlayer.PlayerQuitListener::onPlayerQuit);
    }

    private void removePlayerFromServer(DNPlayer dnPlayer){
        dnPlayer.getServer().getPlayers().remove(dnPlayer);
        dnPlayer.getServer().getRemoteExecutor().getPlayers().remove(dnPlayer);
    }

    public void addPlayerFromServer(UniversalPlayer dnPlayer){
        dnPlayer.getServer().getPlayers().add(dnPlayer);
        dnPlayer.getServer().getRemoteExecutor().getPlayers().add(dnPlayer);
    }

    public void updatePlayer(UniversalPlayer dnPlayer,DNServer dnServer){
        removePlayerFromServer(dnPlayer);
        dnPlayer.updateServer(dnServer);
        addPlayerFromServer(dnPlayer);
        ((UniversalPlayer) dnPlayer).getPlayerUpdates().forEach(playerUpdateServer -> playerUpdateServer.onPlayerUpdateServer(dnServer));
    }
}
