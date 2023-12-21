package be.alexandre01.dnplugin.api.objects.player;

import be.alexandre01.dnplugin.api.objects.server.DNServer;
import be.alexandre01.dnplugin.api.universal.player.UniversalPlayer;
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
        if(dnPlayer instanceof UniversalPlayer)
            ((UniversalPlayer) dnPlayer).getPlayerJoin().forEach(UniversalPlayer.PlayerJoinListener::onPlayerJoin);
    }

    public void removePlayer(DNPlayer dnPlayer){
        removePlayerFromServer(dnPlayer);
        dnPlayers.remove(dnPlayer.getId());
        dnPlayersByName.remove(dnPlayer.getName());
        if(dnPlayer instanceof UniversalPlayer)
            ((UniversalPlayer) dnPlayer).getPlayerQuit().forEach(UniversalPlayer.PlayerQuitListener::onPlayerQuit);
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
        if(dnPlayer instanceof UniversalPlayer)
            ((UniversalPlayer) dnPlayer).getPlayerUpdates().forEach(playerUpdateServer -> playerUpdateServer.onPlayerUpdateServer(dnServer));
    }
}
