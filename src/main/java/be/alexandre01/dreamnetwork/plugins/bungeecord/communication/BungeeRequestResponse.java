package be.alexandre01.dreamnetwork.plugins.bungeecord.communication;

import be.alexandre01.dreamnetwork.api.request.communication.ClientResponse;
import be.alexandre01.dreamnetwork.plugins.bungeecord.DNBungee;
import be.alexandre01.dreamnetwork.plugins.bungeecord.api.DNBungeeAPI;
import be.alexandre01.dreamnetwork.utils.messages.Message;
import com.google.gson.internal.LinkedTreeMap;
import io.netty.channel.ChannelHandlerContext;

public class BungeeRequestResponse extends ClientResponse {

    public DNBungeeAPI dnBungeeAPI;

    public BungeeRequestResponse(){
        this.dnBungeeAPI = (DNBungeeAPI) DNBungeeAPI.getInstance();
    }
    @Override
    public void onResponse(Message message, ChannelHandlerContext ctx) throws Exception {
        if (message.hasRequest()) {
            System.out.println("Request BUNGEE ?? !! ");
            switch (message.getRequest()){
                case BUNGEECORD_REGISTER_SERVER:
                    dnBungeeAPI.getDnBungeeServersManager().registerServer(message.getString("PROCESSNAME"),message.getString("REMOTEIP"),message.getInt("PORT"));
                    break;
                case BUNGEECORD_UNREGISTER_SERVER:
                    dnBungeeAPI.getDnBungeeServersManager().unregisterServer(message.getString("PROCESSNAME"));
                    break;
                case BUNGEECORD_EXECUTE_COMMAND:
                    DNBungee.getInstance().getProxy().getPluginManager().dispatchCommand(DNBungee.getInstance().getProxy().getConsole(),message.getString("CMD"));
                    break;
                case BUNGEECORD_LOG_MESSAGE:
                   dnBungeeAPI.getLogger().info(message.getString("LOG"));
                   break;
                case BUNGEECORD_WARNING_MESSAGE:
                    dnBungeeAPI.getLogger().warning(message.getString("LOG"));
                    break;
                case BUNGEECORD_ERROR_MESSAGE:
                    dnBungeeAPI.getLogger().severe(message.getString("LOG"));
                    break;
                case CORE_STOP_SERVER:
                    DNBungee.getInstance().getProxy().stop();
                    break;
                case CORE_REGISTER_CHANNEL:
                    System.out.println("Registering channel");
                    String channelName = message.getString("CHANNEL");
                    LinkedTreeMap<String,Object> map = (LinkedTreeMap<String, java.lang.Object>) message.get("MAP");
                    System.out.println("Données ! > "+map);
                    System.out.println("Nom du channel ! > "+ channelName);
                    dnBungeeAPI.getChannelManager().getChannel(channelName).callRegisterEvent(map);
                    break;
                }
            }
    }
}
