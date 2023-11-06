package be.alexandre01.dnplugin.plugins.spigot;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.connection.request.packets.PacketCast;
import be.alexandre01.dnplugin.api.connection.request.packets.PacketGlobal;
import be.alexandre01.dnplugin.api.connection.request.packets.PacketHandler;
import be.alexandre01.dnplugin.api.utils.messages.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 30/10/2023 at 21:35
*/
@PacketGlobal(header = "MyServer", castType = PacketGlobal.PacketType.NORMAL,castOption = PacketGlobal.PacketCastOption.NULLABLE)
@SuppressWarnings(value = {"unused", "OptionalUsedAsFieldOrParameterType"})
public class TestClass {
    
    // idea auto converter for Message
    // Message can be {"Player":"Alexandre01Dev","SpecialMessage":"Hello World"}
    @PacketHandler(header = "SendMessage")
    public void onTestPacket(Message message, String player, @PacketCast(key = "SpecialMessage") Optional<String> msg) {
        Player p = Bukkit.getPlayer(player);
        if(p != null){
            p.sendMessage(msg.orElse("Default message"));
        }
    }
}

