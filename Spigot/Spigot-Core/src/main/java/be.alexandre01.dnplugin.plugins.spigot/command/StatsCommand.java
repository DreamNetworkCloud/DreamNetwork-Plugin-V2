package be.alexandre01.dnplugin.plugins.spigot.command;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.objects.RemoteService;
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
            RemoteService remoteService = null;
            NetworkBaseAPI api = NetworkBaseAPI.getInstance();
            if(args.length == 1){
                remoteService = api.getByName(api.getProcessName());
            }else{
                remoteService = api.getByName(args[1]);
            }

            if(remoteService == null){
                sender.sendMessage("§cCannot find server");
                return true;
            }

            sender.sendMessage("§aPlayers on " + remoteService.getName());
            sender.sendMessage("Total : " + remoteService.getPlayers().size());
            for (DNServer server : remoteService.getServers().values()) {
                sender.sendMessage("§a" + server.getFullName() + " : " + server.getPlayers().size());
            }
            return true;
        }
        return true;
    }
}
