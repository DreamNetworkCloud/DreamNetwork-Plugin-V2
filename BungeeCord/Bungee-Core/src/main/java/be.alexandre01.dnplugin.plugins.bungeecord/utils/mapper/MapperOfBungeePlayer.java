package be.alexandre01.dnplugin.plugins.bungeecord.utils.mapper;

import be.alexandre01.dnplugin.api.utils.messages.ObjectConverterMapper;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 05/11/2023 at 20:10
*/
public class MapperOfBungeePlayer extends ObjectConverterMapper<ProxiedPlayer,String> {
    @Override
    public String convert(ProxiedPlayer object) {
        return object.getName();
    }

    @Override
    public ProxiedPlayer read(String object) {
        if (ProxyServer.getInstance().getConfig().isOnlineMode()) {
            UUID uuid;
            try {
                uuid = UUID.fromString(object);
            } catch (IllegalArgumentException e) {
                return ProxyServer.getInstance().getPlayer(object);
            }
            return ProxyServer.getInstance().getPlayer(uuid);
        }
        return ProxyServer.getInstance().getPlayer(object);
    }
}
