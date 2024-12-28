package be.alexandre01.dnplugin.plugins.spigot.api.communication;

import be.alexandre01.dnplugin.api.objects.player.DNPlayer;


import be.alexandre01.dnplugin.api.utils.messages.Message;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 15/10/2023 at 20:43
*/
public class MessageReceivedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    @Getter private final Message message;

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public MessageReceivedEvent(Message message){
        this.message = message;
    }
}
