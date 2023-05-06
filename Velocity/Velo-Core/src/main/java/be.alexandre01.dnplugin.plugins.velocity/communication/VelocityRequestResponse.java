package be.alexandre01.dnplugin.plugins.velocity.communication;

import be.alexandre01.dnplugin.api.request.RequestType;
import be.alexandre01.dnplugin.api.request.communication.ClientResponse;
import be.alexandre01.dnplugin.plugins.velocity.DNVelocity;
import be.alexandre01.dnplugin.plugins.velocity.api.DNVelocityAPI;
import be.alexandre01.dnplugin.utils.Mods;
import be.alexandre01.dnplugin.utils.messages.Message;
import com.google.gson.internal.LinkedTreeMap;
import io.netty.channel.ChannelHandlerContext;

public class VelocityRequestResponse extends ClientResponse {

    public DNVelocityAPI dnVelocityAPI;

    public VelocityRequestResponse(){
        this.dnVelocityAPI = (DNVelocityAPI) DNVelocityAPI.getInstance();

        addRequestInterceptor(RequestType.PROXY_REGISTER_SERVER,(message, ctx) -> {
            System.out.println("Registering server: " + message.getString("PROCESSNAME"));
            dnVelocityAPI.getDnVelocityServersManager().registerServer(message.getString("PROCESSNAME"),message.getString("REMOTEIP"),message.getInt("PORT"),Mods.valueOf(message.getString("MODS")));
        });

        addRequestInterceptor(RequestType.PROXY_UNREGISTER_SERVER,(message, ctx) -> {
            System.out.println("Unregistering server: " + message.getString("PROCESSNAME"));
            dnVelocityAPI.getDnVelocityServersManager().unregisterServer(message.getString("PROCESSNAME"));
        });

        addRequestInterceptor(RequestType.PROXY_EXECUTE_COMMAND,(message, ctx) -> {
            DNVelocity.getInstance().getServer().getCommandManager().executeAsync(DNVelocity.getInstance().getServer().getConsoleCommandSource(),message.getString("CMD"));
        });

        addRequestInterceptor(RequestType.PROXY_LOG_MESSAGE,(message, ctx) -> {
            dnVelocityAPI.getLogger().info(message.getString("LOG"));
        });
        addRequestInterceptor(RequestType.PROXY_WARNING_MESSAGE,(message, ctx) -> {
            dnVelocityAPI.getLogger().info(message.getString("LOG"));
        });
        addRequestInterceptor(RequestType.PROXY_ERROR_MESSAGE,(message, ctx) -> {
            dnVelocityAPI.getLogger().info(message.getString("LOG"));
        });

        addRequestInterceptor(RequestType.CORE_STOP_SERVER,(message, ctx) -> {
            DNVelocity.getInstance().getServer().shutdown();
        });

        addRequestInterceptor(RequestType.CORE_REGISTER_CHANNEL,(message, ctx) -> {
            System.out.println("Registering channel");
            String channelName = message.getString("CHANNEL");
            LinkedTreeMap<String,Object> map = (LinkedTreeMap<String, Object>) message.get("MAP");
            System.out.println("Données ! > "+map);
            System.out.println("Nom du channel ! > "+ channelName);
            dnVelocityAPI.getChannelManager().getChannel(channelName).callRegisterEvent(map);
        });

        }

    @Override
    protected boolean preReader(Message message, ChannelHandlerContext ctx) {
        System.out.println(message.toString());
        return true;
    }
}
