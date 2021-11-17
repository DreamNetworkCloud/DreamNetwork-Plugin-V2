package be.alexandre01.dreamnetwork.plugins.spigot.listeners;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.api.objects.server.DNServer;
import be.alexandre01.dreamnetwork.api.request.RequestType;
import be.alexandre01.dreamnetwork.api.request.channels.ChannelPacket;
import be.alexandre01.dreamnetwork.api.request.channels.DNChannel;
import be.alexandre01.dreamnetwork.api.request.channels.DNChannelManager;
import be.alexandre01.dreamnetwork.plugins.spigot.api.events.player.NetworkDisconnectEvent;
import be.alexandre01.dreamnetwork.plugins.spigot.api.events.player.NetworkJoinEvent;
import be.alexandre01.dreamnetwork.plugins.spigot.api.events.player.NetworkSwitchServerEvent;
import be.alexandre01.dreamnetwork.plugins.spigot.api.events.server.ServerAttachedEvent;
import be.alexandre01.dreamnetwork.plugins.spigot.api.events.server.ServerStartedEvent;
import be.alexandre01.dreamnetwork.plugins.spigot.api.events.server.ServerStoppedEvent;
import be.alexandre01.dreamnetwork.utils.messages.Message;
import net.md_5.bungee.api.connection.Server;
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


        NetworkBaseAPI.getInstance().getRequestManager().sendRequest(RequestType.CORE_ASK_DATA,"ALWAYS");

    }

    @EventHandler
    public void onServerStart(ServerStartedEvent event){
        System.out.println("START");
        System.out.println(event.getServer().getName());
        System.out.println(event.getServer().getId());
    }

    @EventHandler
    public void onServerStop(ServerStoppedEvent event){
        System.out.println("STOP");
        System.out.println(event.getServer().getName());
        System.out.println(event.getServer().getId());
    }


    @EventHandler
    public void onPlayerJoin(NetworkJoinEvent event){
        System.out.println("JOIN");
        System.out.println(event.getPlayer().getName());
        System.out.println(event.getPlayer().getId());
    }
    @EventHandler
    public void onPlayerJoin(NetworkSwitchServerEvent event){
        System.out.println("SWITCH");
        System.out.println(event.getPlayer().getName());
        System.out.println(event.getPlayer().getId());
    }
    @EventHandler
    public void onPlayerJoin(NetworkDisconnectEvent event){
        System.out.println("DISCONNECT");
        System.out.println(event.getPlayer().getName());
        System.out.println(event.getPlayer().getId());
    }
}
