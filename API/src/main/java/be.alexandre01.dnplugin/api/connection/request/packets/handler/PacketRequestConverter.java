package be.alexandre01.dnplugin.api.connection.request.packets.handler;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.connection.request.communication.ClientReceiver;
import be.alexandre01.dnplugin.api.connection.request.packets.PacketHandlingFactory;
import be.alexandre01.dnplugin.api.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 05/11/2023 at 12:42
*/
public class PacketRequestConverter extends ClientReceiver {
    final PacketHandlingFactory factory = NetworkBaseAPI.getInstance().getPacketFactory();

    @Override
    public void onReceive(Message message, ChannelHandlerContext ctx) throws Exception {
        if(message.hasHeader()){
            String header = message.getHeader();
            if(factory.getHeaders().containsKey(header)){
                factory.getHeaders().get(header).execute(message,ctx);
            }
        }
    }
}
