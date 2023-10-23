package be.alexandre01.dnplugin.plugins.spigot.listeners;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.connection.request.channels.DNChannel;
import be.alexandre01.dnplugin.api.connection.request.channels.DNChannelManager;
import be.alexandre01.dnplugin.api.connection.request.channels.DataListener;
import be.alexandre01.dnplugin.api.connection.request.channels.RegisterListener;
import be.alexandre01.dnplugin.api.connection.request.channels.DNChannelInterceptor;
import be.alexandre01.dnplugin.api.connection.request.channels.ChannelPacket;
import be.alexandre01.dnplugin.api.connection.request.channels.GetDataThread;
import be.alexandre01.dnplugin.plugins.spigot.DNSpigot;
import be.alexandre01.dnplugin.plugins.spigot.ImplAPI;
import be.alexandre01.dnplugin.plugins.spigot.api.events.player.NetworkDisconnectEvent;
import be.alexandre01.dnplugin.plugins.spigot.api.events.player.NetworkJoinEvent;
import be.alexandre01.dnplugin.plugins.spigot.api.events.player.NetworkSwitchServerEvent;
import be.alexandre01.dnplugin.plugins.spigot.api.events.server.ServerAttachedEvent;
import be.alexandre01.dnplugin.plugins.spigot.api.events.server.ServerStartedEvent;
import be.alexandre01.dnplugin.plugins.spigot.api.events.server.ServerStoppedEvent;
import be.alexandre01.dnplugin.api.utils.messages.Message;
import com.google.gson.internal.LinkedTreeMap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.LinkedHashMap;

public class TestChannelListener implements Listener {
    int serverOnline = 0;
    int playerOnline = 0;
    DNChannel dnChannel;

    @EventHandler
    public void onAttached(ServerAttachedEvent event){
        System.out.println("Server est attaché à DreamNetwork youpi !");
        DNChannelManager dnChannelManager = NetworkBaseAPI.getInstance().getChannelManager();
        System.out.println(dnChannelManager);


        //Creation du channel


        //Ajout de plusieurs intercepteurs de données (exemple)
        dnChannel.setDataListener("server_online", Integer.class, new DataListener<Integer>() {
            @Override
            public void onUpdateData(Integer data) {
                System.out.println("Update de serveur en ligne > "+ data);
                serverOnline = data;
            }
        });
        dnChannel.setDataListener("players_online", Integer.class, new DataListener<Integer>() {
            @Override
            public void onUpdateData(Integer data) {
                System.out.println("Update de joueurs en ligne > "+ data);
                serverOnline = data;
            }
        });


        //Enregistrement du channel au prêt de DreamNetwork ! (exemple)
        // Et on peut récupérer les données inscrit sur le channel si on le souhaite
        dnChannelManager.registerChannel("Test", true, new RegisterListener() {
            @Override
            public void onNewDataReceived(LinkedHashMap<String, Object> newData) {
                createInitialData("server_online", 0);
                createInitialData("players_online", 0);

                System.out.println("New data : " + newData);

                if(contains("server_online")){
                    int newServerCount = get("server_online", Integer.class);
                    newServerCount += 1;
                    serverOnline = newServerCount;
                    getChannel().setData("server_online", serverOnline);
                }

                if(contains("players_online")){
                    playerOnline = get("players_online", Integer.class);
                }
            }
        });

        //EXEMPLE D'INTERCEPTOR de Message
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
                        receivedPacket.createRequest(newMessage);
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

        //EXEMPLE D'ENREGISTREMENT D'UNE VALEUR (ENVOI A TOUT LE MONDE SUR LE CHANNEL ET ENREGISTRE LOCALEMENT
        dnChannel.setData("test","wow");

        //EXEMPLE DE RECUPERATION D'UNE VALEUR ENREGISTREE LOCALEMENT
        dnChannel.getLocalData("test", String.class);

        //EXEMPLE DE RECUPERATION D'UNE VALEUR ENREGISTREE SUR LE CHANNEL ET LA TRAITER
        dnChannel.askData("test", String.class).get(new GetDataThread<String>() {
            @Override
            public void onComplete(String s) {
                System.out.println(s);
            }
        });

        ImplAPI dnSpigotAPI = (ImplAPI) DNSpigot.getAPI();
        dnSpigotAPI.autoRefreshPlayers();
    }

    @EventHandler
    public void onPlayerJoin(NetworkJoinEvent event){
        System.out.println("The player "+ event.getPlayer().getName() +" has joined the Network !");
        dnChannel.setData("players_online", playerOnline+1);
    }
    @EventHandler
    public void onPlayerQuit(NetworkDisconnectEvent event){
        System.out.println("The player "+ event.getPlayer().getName() +" has left the Network !");
        dnChannel.setData("players_online", playerOnline-1);
    }
    @EventHandler
    public void onPlayerSwitch(NetworkSwitchServerEvent event){
        System.out.println(event.getPlayer().getName());
        System.out.println(event.getPlayer().getId());
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
}
