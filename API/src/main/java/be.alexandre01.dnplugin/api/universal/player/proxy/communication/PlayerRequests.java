package be.alexandre01.dnplugin.api.universal.player.proxy.communication;

import be.alexandre01.dnplugin.api.connection.request.packets.RequestExample;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 05/11/2023 at 11:53
*/
@SuppressWarnings("all")
public enum PlayerRequests implements RequestExample<PlayerRequestHandler> {
    SEND_SERVER,
    SEND_TITLE,
    SEND_ACTION_BAR,
    SEND_MESSAGE,
    SEND_CHATCOMPONENT,
    KICK,
    OPERATION;


    final private String value;
    PlayerRequests() {
        this.value = getProjectName() +"~"+ ordinal();
    }


    public String value(){
        return value;
    }

    @Override
    public String getProjectName() {
        return "DNPlayer";
    }
}
