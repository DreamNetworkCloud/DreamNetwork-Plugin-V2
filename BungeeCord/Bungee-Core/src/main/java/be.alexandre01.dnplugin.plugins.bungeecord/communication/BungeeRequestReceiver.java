package be.alexandre01.dnplugin.plugins.bungeecord.communication;

import be.alexandre01.dnplugin.api.connection.request.RequestType;
import be.alexandre01.dnplugin.api.connection.request.channels.DNChannel;
import be.alexandre01.dnplugin.api.connection.request.communication.ClientReceiver;
import be.alexandre01.dnplugin.plugins.bungeecord.DNBungee;
import be.alexandre01.dnplugin.plugins.bungeecord.api.DNBungeeAPI;
import be.alexandre01.dnplugin.api.utils.Mods;
import be.alexandre01.dnplugin.api.utils.messages.Message;
import com.google.gson.internal.LinkedTreeMap;
import io.netty.channel.ChannelHandlerContext;

import java.util.LinkedHashMap;
import java.util.List;

public class BungeeRequestReceiver extends ClientReceiver {

    public DNBungeeAPI dnBungeeAPI;

    public BungeeRequestReceiver(){
        this.dnBungeeAPI = (DNBungeeAPI) DNBungeeAPI.getInstance();

        addRequestInterceptor(RequestType.PROXY_REGISTER_SERVER,(message, ctx) -> {
            System.out.println("Registering server: " + message.getString("PROCESSNAME"));
            String customName = null;
            if(message.containsKey("CUSTOMNAME")){
                customName = message.getString("CUSTOMNAME");
            }
            dnBungeeAPI.getDnBungeeServersManager().registerServer(message.getString("PROCESSNAME"),customName,message.getString("REMOTEIP"),message.getInt("PORT"),Mods.valueOf(message.getString("MODS")));
        });

        addRequestInterceptor(RequestType.PROXY_UNREGISTER_SERVER,(message, ctx) -> {
            System.out.println("Unregistering server: " + message.getString("PROCESSNAME"));
            dnBungeeAPI.getDnBungeeServersManager().unregisterServer(message.getString("PROCESSNAME"));
        });

        addRequestInterceptor(RequestType.PROXY_EXECUTE_COMMAND,(message, ctx) -> {
            DNBungee.getInstance().getProxy().getPluginManager().dispatchCommand(DNBungee.getInstance().getProxy().getConsole(),message.getString("CMD"));
        });

        addRequestInterceptor(RequestType.PROXY_LOG_MESSAGE,(message, ctx) -> {
            dnBungeeAPI.getLogger().info(message.getString("LOG"));
        });
        addRequestInterceptor(RequestType.PROXY_WARNING_MESSAGE,(message, ctx) -> {
            dnBungeeAPI.getLogger().info(message.getString("LOG"));
        });
        addRequestInterceptor(RequestType.PROXY_ERROR_MESSAGE,(message, ctx) -> {
            dnBungeeAPI.getLogger().info(message.getString("LOG"));
        });


        addRequestInterceptor(RequestType.CORE_REGISTER_CHANNEL,(message, ctx) -> {
            System.out.println("Registering channel");
            String channelName = message.getString("CHANNEL");
            LinkedHashMap<String,Object> map = (LinkedHashMap<String, Object>) message.get("MAP");
            dnBungeeAPI.getChannelManager().getChannel(channelName).callRegisterEvent(map);
        });
        addRequestInterceptor(RequestType.CORE_REGISTER_CHANNELS_INFOS,(message, ctx) -> {
            List<String> channels = message.getList("CHANNELS",String.class);
            for(String channel : channels){
                if(dnBungeeAPI.getChannelManager().hasChannel(channel)){
                    continue;
                }
                dnBungeeAPI.getChannelManager().getChannels().put(channel,new DNChannel(channel));
            }
        });

        }

    @Override
    protected boolean preReader(Message message, ChannelHandlerContext ctx) {
        return true;
    }
}
