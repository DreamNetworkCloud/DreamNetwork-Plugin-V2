package be.alexandre01.dnplugin.utils.files.network;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class NetworkYAML {
    private String lobby;
    private boolean connexionOnLobby;
    private int maxPlayerPerLobby;
    private boolean maintenance;
    private boolean kickRedirectionEnabled;
    private String kickRedirectionServer;
    private boolean statusLogo;
    private List<String> maintenanceAllowedPlayers;
    private boolean autoSendPlayers;
    private int slots;
}
