package be.alexandre01.dnplugin.plugins.spigot.utils.mapper;

import be.alexandre01.dnplugin.api.utils.messages.Message;
import be.alexandre01.dnplugin.api.utils.messages.ObjectConverterMapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 04/11/2023 at 23:55
*/
public class MapperOfBukkitPlayer extends ObjectConverterMapper<Player, String> {
    @Override
    public String convert(Player player) {
        if (Bukkit.getServer().getOnlineMode()) {
            return player.getUniqueId().toString();
        }
        return player.getName();
    }

    @Override
    public Player read(String object) {
        if (Bukkit.getServer().getOnlineMode()) {
            UUID uuid;
            try {
                uuid = UUID.fromString(object);
            } catch (IllegalArgumentException e) {
                return Bukkit.getPlayer(object);
            }
            return Bukkit.getPlayer(uuid);
        }
        return Bukkit.getPlayer(object);
    }

    class Example {
        public void setConverter(ObjectConverterMapper<?, ?> mapper) {
            Message.getDefaultMapper().addMapper(mapper);
        }

        public void getMessage(Message message) {
            setConverter(new MapperOfBukkitPlayer());
            message.getOptional("Player", Player.class).ifPresent(player -> {
                // do something with player
                player.sendMessage("Hey !");
            });
        }
    }
}
