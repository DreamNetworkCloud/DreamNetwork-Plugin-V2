package be.alexandre01.dnplugin.plugins.velocity.components.commands;

import be.alexandre01.dnplugin.plugins.velocity.DNVelocity;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;

public class Slot implements SimpleCommand {
    private DNVelocity dnVelocity;

    public Slot(){
        dnVelocity = DNVelocity.getInstance();
    }
    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();

        String[] args = invocation.arguments();

        if(args.length < 1){
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

        dnVelocity.getConfiguration().setSlots(i);
        dnVelocity.saveConfig();

        sender.sendMessage(Component.text("§aVous venez de changer le nombre de slot à "+ i));
    }

    private void sendHelp(CommandSource sender){
        sender.sendMessage(Component.text("§6Slot System:"));
        sender.sendMessage(Component.text("§8§m*------§7§m------§7§m-§b§m-----------§7§m-§7§m------§8§m------*"));
        sender.sendMessage(Component.text("§e - §9/slot [Nombre]"));
        sender.sendMessage(Component.text("§8§m*------§7§m------§7§m-§b§m-----------§7§m-§7§m------§8§m------*"));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("slot.use");
    }
}
