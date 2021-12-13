package be.alexandre01.dreamnetwork.plugins.spigot.listeners;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.api.request.RequestType;
import be.alexandre01.dreamnetwork.api.request.channels.ChannelPacket;
import be.alexandre01.dreamnetwork.api.request.channels.DNChannel;
import be.alexandre01.dreamnetwork.api.request.channels.DNChannelInterceptor;
import be.alexandre01.dreamnetwork.api.request.channels.DNChannelManager;
import be.alexandre01.dreamnetwork.plugins.spigot.api.events.player.NetworkDisconnectEvent;
import be.alexandre01.dreamnetwork.plugins.spigot.api.events.player.NetworkJoinEvent;
import be.alexandre01.dreamnetwork.plugins.spigot.api.events.player.NetworkSwitchServerEvent;
import be.alexandre01.dreamnetwork.plugins.spigot.api.events.server.ServerAttachedEvent;
import be.alexandre01.dreamnetwork.plugins.spigot.api.events.server.ServerStartedEvent;
import be.alexandre01.dreamnetwork.plugins.spigot.api.events.server.ServerStoppedEvent;
import be.alexandre01.dreamnetwork.utils.messages.Message;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Date;
import java.util.concurrent.ExecutionException;

public class TestChannelListener implements Listener {
    int online = 0;
    @EventHandler
    public void onAttached(ServerAttachedEvent event){
        System.out.println("Server est attaché à DreamNetwork youpi !");
        DNChannelManager dnChannelManager = NetworkBaseAPI.getInstance().getChannelManager();
        System.out.println(dnChannelManager);

        DNChannel dnChannel = new DNChannel("test");
        dnChannel.setDataListener("online", Integer.class, new DNChannel.DataListener<Integer>() {
            @Override
            public void onUpdateData(Integer data) {
                System.out.println("Nbre de serveur online > "+ data);
                online = data;

            }
        });
        /*dnChannel.setDataListener("test", String.class, new DNChannel.DataListener<String>() {
            @Override
            public void onUpdateData(String data) {
                System.out.println("Data updated : " + data);
                set(data+"1");
            }
        });*/

        long i = System.currentTimeMillis();
        dnChannelManager.registerChannel(dnChannel,true);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(dnChannel.getName());


        System.out.println("Channel registered in " + (System.currentTimeMillis() - i) + "ms");
        dnChannel.initDataIfNotExist("online",0);
        System.out.println("After"+ (System.currentTimeMillis() - i) + "ms");
        long date = System.currentTimeMillis();
        dnChannel.askData("online", Integer.class).get(new DNChannel.GetDataThread<Integer>() {
            @Override
            public void onComplete(Integer integer) {
                online = integer;
                System.out.println("Nbre de serveur online > "+ integer);
                System.out.println("Time > "+ (System.currentTimeMillis()-date));
                dnChannel.setData("online", integer+1);
            }
        });



        //EXEMPLE D'INTERCEPTOR
        dnChannel.addInterceptor(new DNChannelInterceptor() {
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

        //ENREGISTREMENT D'UNE VALEUR
        dnChannel.setData("test","wow");



        NetworkBaseAPI.getInstance().getRequestManager().sendRequest(RequestType.CORE_ASK_DATA,"PLAYERS","ALWAYS");

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
    public void onPlayerSwitch(NetworkSwitchServerEvent event){
        System.out.println("SWITCH");
        System.out.println(event.getPlayer().getName());
        System.out.println(event.getPlayer().getId());
    }
    @EventHandler
    public void onPlayerQuit(NetworkDisconnectEvent event){
        System.out.println("DISCONNECT");
        System.out.println(event.getPlayer().getName());
        System.out.println(event.getPlayer().getId());
    }
}
