package be.alexandre01.dnplugin.plugins.bungeecord.utils.mapper;

import be.alexandre01.dnplugin.api.utils.messages.ObjectConverterMapper;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.chat.ComponentSerializer;

import java.util.UUID;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 05/11/2023 at 20:10
*/
public class MapperOfBungeeChatComponent extends ObjectConverterMapper<BaseComponent[],String> {
    @Override
    public String convert(BaseComponent[] object) {
        return ComponentSerializer.toString(object);
    }

    @Override
    public BaseComponent[] read(String object) {
        return ComponentSerializer.parse(object);
    }
}
