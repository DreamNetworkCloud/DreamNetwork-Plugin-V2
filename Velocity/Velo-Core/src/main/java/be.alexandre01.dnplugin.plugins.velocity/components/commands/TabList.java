package be.alexandre01.dnplugin.plugins.velocity.components.commands;

import be.alexandre01.dnplugin.plugins.velocity.DNVelocity;
import be.alexandre01.dnplugin.api.utils.files.tablist.TabListYAML;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TabList implements SimpleCommand {
    private final DNVelocity dnVelocity;

    public TabList(){
        dnVelocity = DNVelocity.getInstance();
    }

    public enum Options{
        ENABLE, DISABLE, RELOAD, DELAY;

        public static Options get(String option){
            for(Options o : Options.values()){
                if(o.toString().equals(option.toUpperCase())){
                    return o;
                }
            }
            return null;
        }
    }
    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();

        if(!sender.hasPermission("tablist.use")){
            sender.sendMessage(Component.text("§cYou don't have the permission to use this command."));
            return;
        }
        String[] args = invocation.arguments();

        if(args.length < 1){sendHelp(sender);return;}

        Options o = Options.get(args[0]);

        if(o == null){sendHelp(sender);return;}
        TabListYAML tabListYAML = dnVelocity.getYamlManager().getTabList();
        switch (o){
            case ENABLE:
                tabListYAML.setActivate(true);
                dnVelocity.getYamlManager().saveTabList();
                dnVelocity.getPlayerTabList().restart();
                sender.sendMessage(Component.text("§eLa TabList personnalisé a été §aactivé"));
                break;
            case DISABLE:
                tabListYAML.setActivate(false);
                dnVelocity.getYamlManager().saveTabList();
                dnVelocity.getPlayerTabList().stop();
                sender.sendMessage(Component.text("§eLa TabList personnalisé a été §cdésactivé"));
                break;
            case DELAY:
                if(args.length < 2){
                    sendHelp(sender);
                    return;
                }
                try{
                    int delay = Integer.parseInt(args[1]);
                    tabListYAML.setDelay(delay);
                    dnVelocity.getYamlManager().saveTabList();
                }catch (NumberFormatException e){
                    sender.sendMessage(Component.text("§4Le delay doit être un entier"));
                }
                break;
            case RELOAD:
                dnVelocity.getPlayerTabList().stop();
                dnVelocity.getYamlManager().reloadTabList();
                dnVelocity.getPlayerTabList().start();
                sender.sendMessage(Component.text("§aLa TabList a été rechargé"));
                break;
        }
    }

    private void sendHelp(CommandSource sender){
        sender.sendMessage(Component.text("§6TabList Command:"));
        sender.sendMessage(Component.text("§8§m*------§7§m------§7§m-§b§m-----------§7§m-§7§m------§8§m------*"));
        sender.sendMessage(Component.text("§e - §9/tablist enable/disable §e- §9Activer ou desactiver le tab personnalisé"));
        sender.sendMessage(Component.text("§e - §9/tablist reload §e- §9Recharger la tablist"));
        sender.sendMessage(Component.text("§e - §9/tablist delay [ticks] §e- §9Modifier le delay (en ticks [20 ticks = 1 seconde]) de la TabList"));
        sender.sendMessage(Component.text("§8§m*------§7§m------§7§m-§b§m-----------§7§m-§7§m------§8§m------*"));
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        TabList.Options[] s = TabList.Options.values();
        List<String> l = new ArrayList<>();
        for(TabList.Options opt : s){
            l.add(opt.name().toLowerCase());
        }
        return l;
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return SimpleCommand.super.suggestAsync(invocation);
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("tablist.use");
    }
}
