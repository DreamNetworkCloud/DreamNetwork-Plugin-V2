package be.alexandre01.dnplugin.plugins.bungeecord.components.commands;

import be.alexandre01.dnplugin.plugins.bungeecord.DNBungee;
import be.alexandre01.dnplugin.api.utils.files.network.NetworkYAML;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
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

        List<String> l = Arrays.stream(SubCommand.values()).map(Enum::name).collect(Collectors.toList());
        if(!l.contains(args[0].toUpperCase())){
            sendHelp(sender);
            return;
        }
        DNBungee dnBungee = DNBungee.getInstance();
        NetworkYAML configuration = dnBungee.getConfiguration();

        switch (SubCommand.valueOf(args[0].toUpperCase())){
            case ON:
                configuration.setMaintenance(true);
                dnBungee.saveConfig();
                sender.sendMessage(new TextComponent("§aYou just §aenabled§a the maintenance."));
                break;
            case OFF:
                configuration.setMaintenance(false);
                dnBungee.saveConfig();
                sender.sendMessage(new TextComponent("§aYou just §cdisabled§a the maintenance."));
                break;
            case ADD:
                if(args.length < 2){
                    sender.sendMessage(new TextComponent("§e - §9/maintenance §lADD§9 [Player]"));
                    return;
                }
                String playernameadd = args[1].toLowerCase();
                if(configuration.getMaintenanceAllowedPlayers() == null) {
                    configuration.setMaintenanceAllowedPlayers(new ArrayList<>());
                }
                if(!configuration.getMaintenanceAllowedPlayers().contains(playernameadd)){
                    configuration.getMaintenanceAllowedPlayers().add(playernameadd);
                    dnBungee.saveConfig();
                    sender.sendMessage(new TextComponent("§aYou just added "+ args[1] +" to the list."));
                }
                break;
            case REMOVE:
                if(args.length < 2){
                    sender.sendMessage(new TextComponent("§e - §9/maintenance §lREMOVE§9 [Player]"));
                    return;
                }
                String playernamermv = args[1].toLowerCase();

                if(configuration.getMaintenanceAllowedPlayers().contains(playernamermv)){
                    configuration.getMaintenanceAllowedPlayers().remove(playernamermv);
                    dnBungee.saveConfig();
                    sender.sendMessage(new TextComponent("§aYou just removed "+ args[1] +" from the list."));
                }
                break;
            case LIST:
                sender.sendMessage(new TextComponent("§6List of allowed players:"));
                for (String allowedPlayer: configuration.getMaintenanceAllowedPlayers()){
                    sender.sendMessage(new TextComponent("§e - "+ allowedPlayer));
                }
                break;
        }

    }

    public void sendHelp(CommandSender sender){
        sender.sendMessage(new TextComponent("§6Maintenance System:"));
        sender.sendMessage(new TextComponent("§8§m*------§7§m------§7§m-§b§m-----------§7§m-§7§m------§8§m------*"));
        sender.sendMessage(new TextComponent("§e - §9/maintenance §lON/OFF§9"));
        sender.sendMessage(new TextComponent("§e - §9/maintenance §lADD§9 [Player]"));
        sender.sendMessage(new TextComponent("§e - §9/maintenance §lREMOVE§9 [Player]"));
        sender.sendMessage(new TextComponent("§e - §9/maintenance §lLIST§9"));
        sender.sendMessage(new TextComponent("§8§m*------§7§m------§7§m-§b§m-----------§7§m-§7§m------§8§m------*"));
    }
}
