package be.alexandre01.dnplugin.plugins.spigot.utils.mapper;

import be.alexandre01.dnplugin.api.utils.messages.Message;
import be.alexandre01.dnplugin.api.utils.messages.ObjectConverterMapper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 04/11/2023 at 23:55
*/
public class MapperOfLocation extends ObjectConverterMapper<Location, String> {


    @Override
    public String convert(Location location) {
        if(location.getPitch() == 0 && location.getYaw() == 0){
            return location.getWorld().getName()+";"+location.getX()+";"+location.getY()+";"+location.getZ();
        }
        return location.getWorld().getName()+";"+location.getX()+";"+location.getY()+";"+location.getZ()+";"+location.getYaw()+";"+location.getPitch();
    }

    @Override
    public Location read(String string) {
        String[] args = string.split(";");
        if(args.length == 4){
            return new Location(Bukkit.getWorld(args[0]),Double.parseDouble(args[1]),Double.parseDouble(args[2]),Double.parseDouble(args[3]));
        }else {
            return new Location(Bukkit.getWorld(args[0]),Double.parseDouble(args[1]),Double.parseDouble(args[2]),Double.parseDouble(args[3]),Float.parseFloat(args[4]),Float.parseFloat(args[5]));
        }
    }

}
