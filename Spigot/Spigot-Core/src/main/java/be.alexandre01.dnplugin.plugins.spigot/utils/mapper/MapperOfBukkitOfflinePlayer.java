package be.alexandre01.dnplugin.plugins.spigot.utils.mapper;

import be.alexandre01.dnplugin.api.utils.messages.Message;
import be.alexandre01.dnplugin.api.utils.messages.ObjectConverterMapper;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 04/11/2023 at 23:55
*/
public class MapperOfBukkitOfflinePlayer extends ObjectConverterMapper<OfflinePlayer, String> {
    @Override
    public String convert(OfflinePlayer player) {
        if (Bukkit.getServer().getOnlineMode()) {
            return player.getUniqueId().toString();
        }
        return player.getName();
    }

    @Override
    public OfflinePlayer read(String object) {
        if (Bukkit.getServer().getOnlineMode()) {
            UUID uuid;
            try {
                uuid = UUID.fromString(object);
            } catch (IllegalArgumentException e) {
                return Bukkit.getOfflinePlayer(object);
            }
            return Bukkit.getOfflinePlayer(uuid);
        }
        return Bukkit.getOfflinePlayer(object);
    }

    class Example {
        public void setConverter(ObjectConverterMapper<?, ?> mapper) {
            Message.getDefaultMapper().addMapper(mapper);
        }

        public void getMessage(Message message) {
            setConverter(new MapperOfBukkitOfflinePlayer());
            message.getOptional("Player", Player.class).ifPresent(player -> {
                // do something with player
                player.sendMessage("Hey !");
            });
        }
    }
}
