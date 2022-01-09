package be.alexandre01.dreamnetwork.plugins.spigot.command;

import be.alexandre01.dreamnetwork.api.NetworkBaseAPI;
import be.alexandre01.dreamnetwork.api.objects.server.DNServer;
import be.alexandre01.dreamnetwork.api.request.RequestPacket;
import be.alexandre01.dreamnetwork.api.request.RequestType;
import be.alexandre01.dreamnetwork.plugins.spigot.api.DNSpigotAPI;
import be.alexandre01.dreamnetwork.utils.Mods;
import lombok.var;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import sun.nio.ch.Net;

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
        if(!sender.hasPermission("network.use")){
            sender.sendMessage("§cVous n'avez pas la permission d'accèder à l'infrastructure.");
            return false;
        }
        if(args.length == 0){
            sendHelp(sender);
            return false;
        }
        List<String> l = Arrays.stream(SubCommand.values()).map(Enum::name).collect(Collectors.toList());
        if(!l.contains(args[0].toUpperCase())){
            sendHelp(sender);
            return false;
        }
        switch (SubCommand.valueOf(args[0].toUpperCase())){
            case CMD:
                if(args.length < 3){
                    sender.sendMessage("§e - §9/network §lCMD§9 [SERVER] [COMMANDS]");
                    return false;
                }
                StringBuilder cmd = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    cmd.append(args[i]);
                    if(i != args.length-1)
                        cmd.append(" ");
                }
                String server = args[1].split("-")[0];
                int id;
                try{
                    id = Integer.parseInt(args[1].split("-")[1]);
                }catch (Exception e){
                    sender.sendMessage("§cVeuillez mettre un id valide. exemple: nomduserver-0");
                    return false;
                }
                if(NetworkBaseAPI.getInstance().getServices().containsKey(server)){
                    sender.sendMessage("§cVeuillez mettre un autre nom de serveur car celui là est invalide.");
                    return false;
                }
                if(!NetworkBaseAPI.getInstance().getServices().get(server).getServers().containsKey(id) || !NetworkBaseAPI.getInstance().getServices().get(server).isStarted()){
                    sender.sendMessage("§cLe serveur n'est pas allumé");
                    return false;
                }
                RequestPacket executecmd = DNSpigotAPI.getInstance().getRequestManager().sendRequest(RequestType.SPIGOT_EXECUTE_COMMAND,args[1],cmd.toString());

                executecmd.setRequestFutureResponse(message -> {
                    System.out.println(message);
                });
                break;
            case STOP:
                break;
            case START:
                if(args.length < 2){
                    sender.sendMessage("§e - §9/network §lSTART§9 [SERVER] (DYNAMIC/STATIC) (XMS) (XMX) (PORT)");
                    return false;
                }

                switch (args.length){
                    case 2:
                        if(!NetworkBaseAPI.getInstance().getServices().containsKey(args[1])){
                            sender.sendMessage("§cVeuillez mettre un autre nom de serveur car celui là est invalide.");
                            return false;
                        }
                        RequestPacket start = DNSpigotAPI.getInstance().getRequestManager().sendRequest(RequestType.CORE_START_SERVER,args[1]);
                        sender.sendMessage("§aLa requête pour allumer le serveur §l"+args[1]+"§a a bien été envoyé. Veuillez attendre.");
                        start.setRequestFutureResponse(message -> {
                            System.out.println(message);
                        });
                  break;
                }
                break;
            case GETSERVER:
                sender.sendMessage(NetworkBaseAPI.getInstance().getProcessName());
                break;
            case GETSERVERS:
                for (String s : NetworkBaseAPI.getInstance().getServices().keySet()) {
                    var services = NetworkBaseAPI.getInstance().getServices().get(s);
                    String color;

                    if(services.isStarted()){
                        color = "§a";
                    }else {
                        color = "§c";
                    }
                    if(services.getMods() == Mods.STATIC){
                        s += "-0 §7[§e"+services.getMods().name()+"§7]";
                    }else{
                        s += " §7[§b"+services.getMods().name()+"§7]";
                    }

                    sender.sendMessage(color+s);
                    if(!services.getServers().isEmpty()){
                        for (DNServer dnServer : services.getServers().values()) {
                            sender.sendMessage("§b     "+ dnServer.getName()+" - "+dnServer.getId());
                        }
                    }
                }
                break;
            case SEND:
                if(args.length < 3){

                }
                break;
            case GUI:

        }
    return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String msg, String[] args) throws IllegalArgumentException {
        if(args.length == 1){
            List<String> optionsType = new ArrayList<>();
            for(SubCommand s : SubCommand.values()){
                if(s.name().startsWith(args[0].toUpperCase()))
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
