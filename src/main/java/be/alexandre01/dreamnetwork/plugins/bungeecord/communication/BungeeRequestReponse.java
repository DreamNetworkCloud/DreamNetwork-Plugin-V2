package be.alexandre01.dreamnetwork.plugins.bungeecord.communication;

import be.alexandre01.dreamnetwork.api.request.communication.ClientResponse;
import be.alexandre01.dreamnetwork.plugins.bungeecord.DNBungee;
import be.alexandre01.dreamnetwork.plugins.bungeecord.api.DNBungeeAPI;
import be.alexandre01.dreamnetwork.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;

public class BungeeRequestReponse extends ClientResponse {

    public DNBungeeAPI dnBungeeAPI;

    public BungeeRequestReponse(){
        this.dnBungeeAPI = (DNBungeeAPI) DNBungeeAPI.getInstance();
    }
    @Override
    public void onResponse(Message message, ChannelHandlerContext ctx) throws Exception {
        System.out.println("RESPONSES");
        if (message.hasRequest()) {
            switch (message.getRequest()){
                case BUNGEECORD_REGISTER_SERVER:
                    System.out.println(message);
                    dnBungeeAPI.getDnBungeeServersManager().registerServer(message.getString("PROCESSNAME"),message.getString("REMOTEIP"),message.getInt("PORT"));
                    break;
                case BUNGEECORD_UNREGISTER_SERVER:
                    dnBungeeAPI.getDnBungeeServersManager().unregisterServer(message.getString("PROCESSNAME"));
                    break;
                case BUNGEECORD_LOG_MESSAGE:
                   dnBungeeAPI.getLogger().info(message.getString("LOG"));
                case BUNGEECORD_WARNING_MESSAGE:
                    dnBungeeAPI.getLogger().warning(message.getString("LOG"));
                case BUNGEECORD_ERROR_MESSAGE:
                    dnBungeeAPI.getLogger().severe(message.getString("LOG"));
                case CORE_STOP_SERVER:
                    DNBungee.getInstance().getProxy().stop();
            }
        }
    }
}
