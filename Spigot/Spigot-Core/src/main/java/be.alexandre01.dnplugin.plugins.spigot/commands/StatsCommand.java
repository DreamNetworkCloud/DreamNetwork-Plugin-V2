package be.alexandre01.dnplugin.plugins.spigot.commands;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.objects.RemoteExecutor;
import be.alexandre01.dnplugin.api.objects.server.DNServer;
import org.bukkit.command.CommandSender;

public class StatsCommand extends org.bukkit.command.Command{
    public StatsCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender sender, String cmd, String[] args) {
        if(args.length == 0){
            sender.sendMessage("§c/dnStats players (server)");
            return true;
        }

        if(args[0].equalsIgnoreCase("players")){
            RemoteExecutor remoteExecutor = null;
            NetworkBaseAPI api = NetworkBaseAPI.getInstance();
            if(args.length == 1){
                remoteExecutor = api.getByName(api.getProcessName()).orElse(null);
            }else{
                remoteExecutor = api.getByName(args[1]).orElse(null);
            }

            if(remoteExecutor == null){
                sender.sendMessage("§cCannot find server");
                return true;
            }

            sender.sendMessage("§aPlayers on " + remoteExecutor.getName());
            sender.sendMessage("Total : " + remoteExecutor.getPlayers().size());
            for (DNServer server : remoteExecutor.getServers().values()) {
                sender.sendMessage("§a" + server.getName() + " : " + server.getPlayers().size());
            }
            return true;
        }
        return true;
    }
}
