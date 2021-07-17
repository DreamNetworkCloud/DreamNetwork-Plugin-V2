package be.alexandre01.dreamnetwork.plugins.bungeecord.components.commands;

import be.alexandre01.dreamnetwork.plugins.bungeecord.DNBungee;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class Slot extends Command {
    public Slot(String name) {
        super(name,"slot.use");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        DNBungee dnBungee = DNBungee.getInstance();
        if(args.length == 0){
            sendHelp(sender);
            return;
        }
        int i;
        try {
            i = Integer.parseInt(args[0]);
        }catch (Exception e){
            sendHelp(sender);
            return;
        }
        dnBungee.slot = i;

        dnBungee.configuration.set("network.slot",i);
        dnBungee.saveConfig();

        sender.sendMessage("§aVous venez de changer le nombre de slot à "+ i);
    }

    public void sendHelp(CommandSender sender){
        sender.sendMessage("§6Slot System:");
        sender.sendMessage("§8§m*------§7§m------§7§m-§b§m-----------§7§m-§7§m------§8§m------*");
        sender.sendMessage("§e - §9/slot [Nombre]");
        sender.sendMessage("§8§m*------§7§m------§7§m-§b§m-----------§7§m-§7§m------§8§m------*");
    }
}
