package be.alexandre01.dnplugin.plugins.velocity.utils.mapper;

import be.alexandre01.dnplugin.api.utils.messages.ObjectConverterMapper;
import be.alexandre01.dnplugin.plugins.velocity.DNVelocity;
import com.velocitypowered.api.proxy.Player;

import java.util.UUID;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 05/11/2023 at 20:10
*/
public class MapperOfVelocityPlayer extends ObjectConverterMapper<Player,String> {
    @Override
    public String convert(Player object) {
        return object.getUsername();
    }

    @Override
    public Player read(String object) {
        if (DNVelocity.getInstance().getServer().getConfiguration().isOnlineMode()) {
            UUID uuid;
            try {
                uuid = UUID.fromString(object);
            } catch (IllegalArgumentException e) {
                return DNVelocity.getInstance().getServer().getPlayer(object).orElse(null);
            }
            return DNVelocity.getInstance().getServer().getPlayer(uuid).orElse(null);
        }
        return DNVelocity.getInstance().getServer().getPlayer(object).orElse(null);
    }
}
