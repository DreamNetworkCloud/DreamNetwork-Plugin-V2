package be.alexandre01.dreamnetwork.plugins.bungeecord.components.commands;

import be.alexandre01.dreamnetwork.plugins.bungeecord.DNBungee;
import be.alexandre01.dreamnetwork.plugins.spigot.command.NetworkCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Maintenance extends Command {

    public Maintenance(String name) {
        super(name, "maintenance.use");
    }

    public enum SubCommand{
        ON,OFF,ADD,REMOVE,LIST
    }



    @Override
    public void execute(CommandSender sender, String[] args) {

        if(args.length == 0){
            sendHelp(sender);
            return;
        }
        System.out.println(args[0]);
        List<String> l = Arrays.stream(SubCommand.values()).map(Enum::name).collect(Collectors.toList());
        if(!l.contains(args[0].toUpperCase())){
            sendHelp(sender);
            return;
        }
        DNBungee dnBungee = DNBungee.getInstance();

        switch (SubCommand.valueOf(args[0].toUpperCase())){
            case ON:
                dnBungee.isMaintenance = true;
                dnBungee.configuration.set("network.maintenance",true);
                dnBungee.saveConfig();
                sender.sendMessage("§aVous venez d'activer la maintenance.");
                break;
            case OFF:
                dnBungee.isMaintenance = false;
                dnBungee.configuration.set("network.maintenance",false);
                dnBungee.saveConfig();
                sender.sendMessage("§aVous venez de §cdésactiver§a la maintenance.");
                break;
            case ADD:
                if(args.length < 2){
                    sender.sendMessage("§e - §9/maintenance §lADD§9 [Player]");
                    return;
                }
                String playernameadd = args[1].toLowerCase();

                if(!dnBungee.allowedPlayer.contains(playernameadd)){
                    dnBungee.allowedPlayer.add(playernameadd);
                    dnBungee.configuration.set("network.allowed-players-maintenance",dnBungee.allowedPlayer);
                    dnBungee.saveConfig();
                    sender.sendMessage("§aVous venez d'ajouter "+ args[1] +" à la liste.");
                }
                break;
            case REMOVE:
                if(args.length < 2){
                    sender.sendMessage("§e - §9/maintenance §lREMOVE§9 [Player]");
                    return;
                }
                String playernamermv = args[1].toLowerCase();

                if(dnBungee.allowedPlayer.contains(playernamermv)){
                    dnBungee.allowedPlayer.remove(playernamermv);
                    dnBungee.configuration.set("network.allowed-players-maintenance",dnBungee.allowedPlayer);
                    dnBungee.saveConfig();
                    sender.sendMessage("§aVous venez de retirer "+ args[1] +" de la liste.");
                }
                break;
            case LIST:
                sender.sendMessage("§6Liste de la Maintenance :");
                for (String allowedPlayer: dnBungee.allowedPlayer){
                    sender.sendMessage("§e - "+ allowedPlayer);
                }
                break;
        }

    }

    public void sendHelp(CommandSender sender){
        sender.sendMessage("§6Maintenance System:");
        sender.sendMessage("§8§m*------§7§m------§7§m-§b§m-----------§7§m-§7§m------§8§m------*");
        sender.sendMessage("§e - §9/maintenance §lON/OFF§9");
        sender.sendMessage("§e - §9/maintenance §lADD§9 [Player]");
        sender.sendMessage("§e - §9/maintenance §lREMOVE§9 [Player]");
        sender.sendMessage("§e - §9/maintenance §lLIST§9");
        sender.sendMessage("§8§m*------§7§m------§7§m-§b§m-----------§7§m-§7§m------§8§m------*");
    }
}
