package be.alexandre01.dnplugin.connection.client;

import be.alexandre01.dnplugin.api.DNNetworkUtilities;
import be.alexandre01.dnplugin.api.connection.IBasicClient;

import java.util.Optional;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 21/10/2023 at 21:52
*/
public class NetworkUtils extends DNNetworkUtilities {
    BasicClient client;

    @Deprecated
    public Optional<IBasicClient> initConnection(){
        if(client == null){
            client = new BasicClient();
        }

        if(!client.isRunning){
            client.connect();
            return Optional.ofNullable(client);
        }else {
            System.out.println("Client is already running");
            return Optional.empty();
        }
    }
}
