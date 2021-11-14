package be.alexandre01.dreamnetwork.plugins.spigot.listeners;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.api.request.channels.ChannelPacket;
import be.alexandre01.dreamnetwork.api.request.channels.DNChannel;
import be.alexandre01.dreamnetwork.api.request.channels.DNChannelManager;
import be.alexandre01.dreamnetwork.plugins.spigot.api.events.ServerAttachedEvent;
import be.alexandre01.dreamnetwork.utils.messages.Message;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TestChannelListener implements Listener {
    @EventHandler
    public void onAttached(ServerAttachedEvent event){
        System.out.println("Server est attaché à DreamNetwork youpi !");
        DNChannelManager dnChannelManager = NetworkBaseAPI.getInstance().getChannelManager();
        DNChannel dnChannel = dnChannelManager.registerChannel(new DNChannel("test"));

        //EXEMPLE D'INTERCEPTOR
        dnChannel.addInterceptor(new DNChannel.DNChannelInterceptor() {
            @Override
            public void received(ChannelPacket receivedPacket) {
                Message message = receivedPacket.getMessage();
                if(message.contains("STATUS")){
                    String status = message.getString("STATUS");
                    System.out.println(status);

                    if(status.equalsIgnoreCase("READY")){
                        System.out.println("Le serveur vient de générer le monde donc il est prêt !");

                        //ENVOI D'UNE REPONSE
                        Message newMessage = new Message().set("INFO","MERCI FRERO !");
                        receivedPacket.createResponse(newMessage);
                    }
                    if(status.equalsIgnoreCase("GENERATING")){
                        System.out.println("Le serveur est en train de générer le monde.");
                    }
                }
            }
        });


        //EXEMPLE D'ENVOI DANS LE CHANNEL
        Message message = new Message().set("STATUS","READY");
        dnChannel.sendMessage(message);


    }
}
