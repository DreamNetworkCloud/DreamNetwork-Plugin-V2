package be.alexandre01.dnplugin.plugins.bungeecord.components.commands;

import be.alexandre01.dnplugin.plugins.bungeecord.DNBungee;
import be.alexandre01.dnplugin.api.utils.files.tablist.TabListYAML;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class TabList extends Command {
    public TabList(String name){super(name, "tablist.use");}

    public enum Options{
        ENABLE,
        DISABLE,
        RELOAD,
        DELAY;

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
    public void execute(CommandSender sender, String[] args) {
        DNBungee dnBungee = DNBungee.getInstance();
        if(args.length < 1){sendHelp(sender);return;}

        Options o = Options.get(args[0]);

        if(o == null){sendHelp(sender);return;}
        TabListYAML tabListYAML = dnBungee.getYamlManager().getTabList();
        switch (o){
            case ENABLE:
                tabListYAML.setActivate(true);
                dnBungee.getYamlManager().saveTabList();
                dnBungee.getPlayerTabList().restart();
                sender.sendMessage(new TextComponent("§eLa TabList personnalisé a été §aactivé"));
                break;
            case DISABLE:
                tabListYAML.setActivate(false);
                dnBungee.getYamlManager().saveTabList();
                dnBungee.getPlayerTabList().stop();
                sender.sendMessage(new TextComponent("§eLa TabList personnalisé a été §cdésactivé"));
                break;
            case DELAY:
                if(args.length < 2){
                    sendHelp(sender);
                    return;
                }
                try{
                    int delay = Integer.parseInt(args[1]);
                    tabListYAML.setDelay(delay);
                    dnBungee.getYamlManager().saveTabList();
                }catch (NumberFormatException e){
                    sender.sendMessage(new TextComponent("§4Le delay doit être un entier"));
                }
                break;
            case RELOAD:
                dnBungee.getPlayerTabList().stop();
                dnBungee.getYamlManager().reloadTabList();
                dnBungee.getPlayerTabList().start();
                sender.sendMessage(new TextComponent("§aLa TabList a été rechargé"));
                break;
        }
    }

    private void sendHelp(CommandSender sender){
        sender.sendMessage(new TextComponent("§6TabList Command:"));
        sender.sendMessage(new TextComponent("§8§m*------§7§m------§7§m-§b§m-----------§7§m-§7§m------§8§m------*"));
        sender.sendMessage(new TextComponent("§e - §9/tablist enable/disable §e- §9Activer ou desactiver le tab personnalisé"));
        sender.sendMessage(new TextComponent("§e - §9/tablist reload §e- §9Recharger la tablist"));
        sender.sendMessage(new TextComponent("§e - §9/tablist delay [ticks] §e- §9Modifier le delay (en ticks [20 ticks = 1 seconde]) de la TabList"));
        sender.sendMessage(new TextComponent("§8§m*------§7§m------§7§m-§b§m-----------§7§m-§7§m------§8§m------*"));
    }
}
