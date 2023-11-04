package be.alexandre01.dnplugin.api.connection.request.communication;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.connection.request.RequestType;
import be.alexandre01.dnplugin.api.utils.messages.Message;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 30/10/2023 at 21:35
*/
public class TestClass {

    @PacketHandler(header = "Test-Edalia")
    public void onTestPacket(Message message){
        System.out.println("Message received");
        message.getProvider().ifPresent(netEntity -> {

        });
        // lobby le plus grand nombre de joueur et moins de 20 joueurs
        NetworkBaseAPI.getInstance().getByName("main/lobby").ifPresent(remoteExecutor -> {
            remoteExecutor.getServers().values().stream().filter(dnServer -> dnServer.getPlayers().size() < 20).mapToInt(server -> server.getPlayers().size()).max().ifPresent(value -> {
                // send le player
            });
        });
        // do my code
    }

   // @RequestHandler(requestInfo = RequestType.CORE_ASK_DATA.name())
}
