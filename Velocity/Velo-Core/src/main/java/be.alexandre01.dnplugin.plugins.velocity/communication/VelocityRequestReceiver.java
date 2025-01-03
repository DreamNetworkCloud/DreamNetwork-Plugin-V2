package be.alexandre01.dnplugin.plugins.velocity.communication;

import be.alexandre01.dnplugin.api.connection.request.RequestType;
import be.alexandre01.dnplugin.api.connection.request.channels.DNChannel;
import be.alexandre01.dnplugin.api.connection.request.communication.ClientReceiver;
import be.alexandre01.dnplugin.plugins.velocity.DNVelocity;
import be.alexandre01.dnplugin.plugins.velocity.api.DNVelocityAPI;
import be.alexandre01.dnplugin.api.utils.Mods;
import be.alexandre01.dnplugin.api.utils.messages.Message;
import com.google.gson.internal.LinkedTreeMap;
import io.netty.channel.ChannelHandlerContext;

import java.util.LinkedHashMap;
import java.util.List;

public class VelocityRequestReceiver extends ClientReceiver {

    public DNVelocityAPI dnVelocityAPI;

    public VelocityRequestReceiver(){
        this.dnVelocityAPI = (DNVelocityAPI) DNVelocityAPI.getInstance();

        addRequestInterceptor(RequestType.PROXY_REGISTER_SERVER,(message, ctx) -> {
            System.out.println("Registering server: " + message.getString("PROCESSNAME"));
            String customName = null;
            if(message.containsKey("CUSTOMNAME")){
                customName = message.getString("CUSTOMNAME");
            }
            dnVelocityAPI.getDnVelocityServersManager().registerServer(message.getString("PROCESSNAME"),customName,message.getString("REMOTEIP"),message.getInt("PORT"),Mods.valueOf(message.getString("MODS")));
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


        addRequestInterceptor(RequestType.CORE_REGISTER_CHANNEL,(message, ctx) -> {
            System.out.println("Registering channel");
            String channelName = message.getString("CHANNEL");
            LinkedHashMap<String,Object> map = (LinkedHashMap<String, Object>) message.get("MAP");
            System.out.println("Données ! > "+map);
            System.out.println("Nom du channel ! > "+ channelName);
            dnVelocityAPI.getChannelManager().getChannel(channelName).callRegisterEvent(map);
        });
        addRequestInterceptor(RequestType.CORE_REGISTER_CHANNELS_INFOS,(message, ctx) -> {
            List<String> channels = message.getList("CHANNELS",String.class);
            for(String channel : channels){
                System.out.println("New Channel = "+channel);
                if(dnVelocityAPI.getChannelManager().hasChannel(channel)){
                    continue;
                }
                dnVelocityAPI.getChannelManager().getChannels().put(channel,new DNChannel(channel));
            }
        });

        }

    @Override
    protected boolean preReader(Message message, ChannelHandlerContext ctx) {
        return true;
    }
}
