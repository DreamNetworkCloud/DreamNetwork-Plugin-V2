package be.alexandre01.dnplugin.plugins.bungeecord.communication;

import be.alexandre01.dnplugin.api.request.RequestType;
import be.alexandre01.dnplugin.api.request.communication.ClientResponse;
import be.alexandre01.dnplugin.plugins.bungeecord.DNBungee;
import be.alexandre01.dnplugin.plugins.bungeecord.api.DNBungeeAPI;
import be.alexandre01.dnplugin.utils.Mods;
import be.alexandre01.dnplugin.utils.messages.Message;
import com.google.gson.internal.LinkedTreeMap;
import io.netty.channel.ChannelHandlerContext;

public class BungeeRequestResponse extends ClientResponse {

    public DNBungeeAPI dnBungeeAPI;

    public BungeeRequestResponse(){
        this.dnBungeeAPI = (DNBungeeAPI) DNBungeeAPI.getInstance();

        addRequestInterceptor(RequestType.BUNGEECORD_REGISTER_SERVER,(message, ctx) -> {
            System.out.println("Registering server: " + message.getString("serverName"));
            dnBungeeAPI.getDnBungeeServersManager().registerServer(message.getString("PROCESSNAME"),message.getString("REMOTEIP"),message.getInt("PORT"),Mods.valueOf(message.getString("MODS")));
        });

        addRequestInterceptor(RequestType.BUNGEECORD_UNREGISTER_SERVER,(message, ctx) -> {
            System.out.println("Unregistering server: " + message.getString("serverName"));
            dnBungeeAPI.getDnBungeeServersManager().unregisterServer(message.getString("PROCESSNAME"));
        });

        addRequestInterceptor(RequestType.BUNGEECORD_EXECUTE_COMMAND,(message, ctx) -> {
            DNBungee.getInstance().getProxy().getPluginManager().dispatchCommand(DNBungee.getInstance().getProxy().getConsole(),message.getString("CMD"));
        });

        addRequestInterceptor(RequestType.BUNGEECORD_LOG_MESSAGE,(message, ctx) -> {
            dnBungeeAPI.getLogger().info(message.getString("LOG"));
        });
        addRequestInterceptor(RequestType.BUNGEECORD_WARNING_MESSAGE,(message, ctx) -> {
            dnBungeeAPI.getLogger().info(message.getString("LOG"));
        });
        addRequestInterceptor(RequestType.BUNGEECORD_ERROR_MESSAGE,(message, ctx) -> {
            dnBungeeAPI.getLogger().info(message.getString("LOG"));
        });

        addRequestInterceptor(RequestType.CORE_STOP_SERVER,(message, ctx) -> {
            DNBungee.getInstance().getProxy().stop();
        });

        addRequestInterceptor(RequestType.CORE_REGISTER_CHANNEL,(message, ctx) -> {
            System.out.println("Registering channel");
            String channelName = message.getString("CHANNEL");
            LinkedTreeMap<String,Object> map = (LinkedTreeMap<String, Object>) message.get("MAP");
            System.out.println("Données ! > "+map);
            System.out.println("Nom du channel ! > "+ channelName);
            dnBungeeAPI.getChannelManager().getChannel(channelName).callRegisterEvent(map);
        });

        }

    @Override
    protected boolean preReader(Message message, ChannelHandlerContext ctx) {
        System.out.println(message.toString());
        return true;
    }
}
