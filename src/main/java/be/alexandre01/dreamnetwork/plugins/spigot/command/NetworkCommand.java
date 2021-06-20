package be.alexandre01.dreamnetwork.plugins.spigot.command;

import be.alexandre01.dreamnetwork.plugins.spigot.DNSpigot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NetworkCommand extends Command {
    public enum SubCommand{
        GUI,GETSERVER,GETSERVERS,START,STOP,CMD,SEND;
    }
    public NetworkCommand(String name) {
        super(name);
    }
    @Override
    public boolean execute(CommandSender sender, String msg, String[] args) {
        if(args.length == 0){
            sendHelp(sender);
            return false;
        }
        List<String> l = Arrays.stream(SubCommand.values()).map(Enum::name).collect(Collectors.toList());
        if(!l.contains(args[0])){
            sendHelp(sender);
            return false;
        }
        switch (SubCommand.valueOf(args[0])){
            case CMD:
                break;
        }

    return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String msg, String[] args) throws IllegalArgumentException {
        if(args.length == 0){
            List<String> optionsType = new ArrayList<>();
            for(SubCommand s : SubCommand.values()){
                optionsType.add(s.name());
            }
            return optionsType;
        }
        return null;
    }

    public void sendHelp(CommandSender sender){
        sender.sendMessage("§6DreamNetwork System Beta:");
        sender.sendMessage("§8§m*------§7§m------§7§m-§b§m-----------§7§m-§7§m------§8§m------*");
        sender.sendMessage("§e - §9/network §lGUI§9 §e[§6§lNew§e]");
        sender.sendMessage("§e - §9/network §lSTART§9 [SERVER] (DYNAMIC/STATIC) (XMS) (XMX) (PORT)");
        sender.sendMessage("§e - §9/network §lSTOP§9 [SERVER]");
        sender.sendMessage("§e - §9/network §lGETSERVER§9");
        sender.sendMessage("§e - §9/network §lGETSERVERS§9 §e[§6New§e]");
        sender.sendMessage("§e - §9/network §lCMD§9 [SERVER] [COMMANDS]");
        sender.sendMessage("§e - §9/network §lSEND§9 [Player] [Server]  §e[§6New§e]");
        sender.sendMessage("§8§m*------§7§m------§7§m-§b§m-----------§7§m-§7§m------§8§m------*");
    }
}
