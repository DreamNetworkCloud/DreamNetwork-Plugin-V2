package be.alexandre01.dnplugin.plugins.spigot.listeners;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.connection.request.DNCallback;
import be.alexandre01.dnplugin.api.connection.request.RequestType;
import be.alexandre01.dnplugin.api.connection.request.TaskHandler;
import be.alexandre01.dnplugin.api.objects.server.DNServer;
import be.alexandre01.dnplugin.api.utils.messages.Message;
import be.alexandre01.dnplugin.plugins.spigot.api.communication.MessageReceivedEvent;
import be.alexandre01.dnplugin.plugins.spigot.api.events.server.ServerStartedEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 15/10/2023 at 19:23
*/
public class TestDispatchListener implements Listener {
    @EventHandler
    public void onNewServer(ServerStartedEvent event){
        System.out.println("New server started ! "+event.getServer().getName());

        Message message = new Message().set("Hello","toi");
        event.getServer().writeAndFlush(message);
        NetworkBaseAPI.getInstance().getRequestManager().getRequest(RequestType.CORE_START_SERVER,"main/lobby");

        DNCallback.multiple(new Message().set("EstIlOp", "Alexandre01").toPacket(event.getServer()), new TaskHandler(-1) {
            @Override
            public void onAccepted() {
                super.onAccepted();
            }

            @Override
            public void onFailed() {
                destroy();
                super.onFailed();
            }
        }).send();
    }


    @EventHandler
    public void onMessage(MessageReceivedEvent event){
        Message message = event.getMessage();
        System.out.println("Received message "+ message);
        message.getProvider().ifPresent(netEntity -> {
            System.out.println("Received message from "+ netEntity.getName());
        });

        message.getProvider(DNServer.class)
                .filter(netEntity -> netEntity.getName().equals("main/lobby-1")).ifPresent(dnServer -> {
                    message.getCallback().ifPresent(callback -> {
                        if (callback.getMessage().contains("EstIlOp")) {
                            String player = callback.getMessage().getString("EstIlOp");

                            if (Bukkit.getPlayer(player).isOp()) {
                                callback.send(TaskHandler.TaskType.ACCEPTED);
                            } else {
                                callback.send(TaskHandler.TaskType.FAILED);
                            }
                        }
                    });
                });
    }

}
