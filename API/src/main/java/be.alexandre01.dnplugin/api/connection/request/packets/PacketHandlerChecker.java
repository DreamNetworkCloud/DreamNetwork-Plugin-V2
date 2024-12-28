package be.alexandre01.dnplugin.api.connection.request.packets;

import be.alexandre01.dnplugin.api.connection.request.packets.exceptions.PacketParameterCastException;
import be.alexandre01.dnplugin.api.connection.request.packets.exceptions.PacketParameterNullException;
import be.alexandre01.dnplugin.api.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;
import lombok.Builder;

import java.lang.reflect.Method;
import java.util.*;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 04/11/2023 at 22:11
*/
public class PacketHandlerChecker extends HandlerChecker{
    private final PacketHandler packetHandler;
    @Builder
    public PacketHandlerChecker(PacketHandler handler, PacketGlobal packetGlobal, PacketHandlerParameter[] parameters, Method method) {
        super(packetGlobal, parameters, method);
        this.packetHandler = handler;
    }

    public void execute(Message message, ChannelHandlerContext ctx) {
        List<String> channels = new ArrayList<>();
        if(packetHandler.channels() != null){
            channels.addAll(Arrays.asList(packetHandler.channels()));
        }
        super.execute(message, ctx, packetHandler.header(), channels.toArray(new String[0]), packetHandler.castOption().name());
    }
}
