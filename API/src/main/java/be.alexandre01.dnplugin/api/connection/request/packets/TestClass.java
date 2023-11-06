package be.alexandre01.dnplugin.api.connection.request.packets;

import be.alexandre01.dnplugin.api.utils.messages.Message;

import java.util.Optional;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 30/10/2023 at 21:35
*/
@PacketGlobal(header = "", castType = PacketGlobal.PacketType.NORMAL)
@SuppressWarnings("unused")
public class TestClass {

    // idea auto converter for Message
    // Message can be {"Player":"Alexandre01Dev","SpecialMessage":"Hello World"}
    @MyRequestHandler(id = TestRequest.SendPlayer)
    public void onTestPacket(Message message, String player, @PacketCast(key = "SpecialMessage") Optional<String> msg) {
    }
}

