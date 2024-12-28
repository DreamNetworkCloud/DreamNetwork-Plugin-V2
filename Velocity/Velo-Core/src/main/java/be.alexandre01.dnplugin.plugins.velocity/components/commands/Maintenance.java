package be.alexandre01.dnplugin.plugins.velocity.components.commands;

import be.alexandre01.dnplugin.plugins.velocity.DNVelocity;
import be.alexandre01.dnplugin.api.utils.files.network.NetworkYAML;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Maintenance implements SimpleCommand {
    private DNVelocity dnVelocity;

    public Maintenance(){
        dnVelocity = DNVelocity.getInstance();
    }

    public enum SubCommand{
        ON,OFF,ADD,REMOVE,LIST
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();

        if(!sender.hasPermission("maintenance.use")){
            sender.sendMessage(Component.text("§cYou don't have the permission to use this command."));
            return;
        }
        String[] args = invocation.arguments();

        if(args.length == 0){
            sendHelp(sender);
            return;
        }

        List<String> l = Arrays.stream(SubCommand.values()).map(Enum::name).collect(Collectors.toList());
        if(!l.contains(args[0].toUpperCase())){
            sendHelp(sender);
            return;
        }
        NetworkYAML configuration = dnVelocity.getConfiguration();

        switch (SubCommand.valueOf(args[0].toUpperCase())){
            case ON:
                configuration.setMaintenance(true);
                dnVelocity.saveConfig();
                sender.sendMessage(Component.text("§aYou just §aenabled§a the maintenance."));
                break;
            case OFF:
                configuration.setMaintenance(false);
                dnVelocity.saveConfig();
                sender.sendMessage(Component.text("§aYou just §cdisabled§a the maintenance."));
                break;
            case ADD:
                if(args.length < 2){
                    sender.sendMessage(Component.text("§e - §9/maintenance §lADD§9 [Player]"));
                    return;
                }
                String playernameadd = args[1].toLowerCase();
                if(configuration.getMaintenanceAllowedPlayers() == null) {
                    configuration.setMaintenanceAllowedPlayers(new ArrayList<>());
                }
                if(!configuration.getMaintenanceAllowedPlayers().contains(playernameadd)){
                    configuration.getMaintenanceAllowedPlayers().add(playernameadd);
                    dnVelocity.saveConfig();
                    sender.sendMessage(Component.text("§aYou just added "+ args[1] +" to the list."));
                }
                break;
            case REMOVE:
                if(args.length < 2){
                    sender.sendMessage(Component.text("§e - §9/maintenance §lREMOVE§9 [Player]"));
                    return;
                }
                String playernamermv = args[1].toLowerCase();
                if(configuration.getMaintenanceAllowedPlayers() == null) {
                    configuration.setMaintenanceAllowedPlayers(new ArrayList<>());
                }
                if(configuration.getMaintenanceAllowedPlayers().contains(playernamermv)){
                    configuration.getMaintenanceAllowedPlayers().remove(playernamermv);
                    dnVelocity.saveConfig();
                    sender.sendMessage(Component.text("§aYou just removed "+ args[1] +" from the list."));
                }
                break;
            case LIST:
                sender.sendMessage(Component.text("§6List of allowed players:"));
                for (String allowedPlayer: configuration.getMaintenanceAllowedPlayers()){
                    sender.sendMessage(Component.text("§e - "+ allowedPlayer));
                }
                break;
        }
    }

    public void sendHelp(CommandSource sender){
        sender.sendMessage(Component.text("§6Maintenance System:"));
        sender.sendMessage(Component.text("§8§m*------§7§m------§7§m-§b§m-----------§7§m-§7§m------§8§m------*"));
        sender.sendMessage(Component.text("§e - §9/maintenance §lON/OFF§9"));
        sender.sendMessage(Component.text("§e - §9/maintenance §lADD§9 [Player]"));
        sender.sendMessage(Component.text("§e - §9/maintenance §lREMOVE§9 [Player]"));
        sender.sendMessage(Component.text("§e - §9/maintenance §lLIST§9"));
        sender.sendMessage(Component.text("§8§m*------§7§m------§7§m-§b§m-----------§7§m-§7§m------§8§m------*"));
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        SubCommand[] s = SubCommand.values();
        List<String> l = new ArrayList<>();
        for(SubCommand sc : s){
            l.add(sc.name().toLowerCase());
        }
        return l;
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return SimpleCommand.super.suggestAsync(invocation);
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return SimpleCommand.super.hasPermission(invocation);
    }
}
