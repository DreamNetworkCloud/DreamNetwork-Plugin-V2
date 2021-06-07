package be.alexandre01.dreamnetwork.spigot;

import be.alexandre01.dreamnetwork.utils.ASCII;
import io.netty.util.NettyRuntime;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Spigot extends JavaPlugin {
    @Override
    public void onEnable(){
        int port = 25565;

        port = getServer().getPort()+1;

        ASCII.sendDNText();

        System.out.println("\n");

        getLogger().log(Level.INFO,"Enabling the Network Connection on the port "+port+"...");


    }

    @Override
    public void onDisable(){

    }
}
