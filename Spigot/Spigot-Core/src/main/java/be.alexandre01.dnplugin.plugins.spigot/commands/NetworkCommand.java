package be.alexandre01.dnplugin.plugins.spigot.commands;

import be.alexandre01.dnplugin.api.NetworkBaseAPI;
import be.alexandre01.dnplugin.api.connection.request.DNCallback;
import be.alexandre01.dnplugin.api.connection.request.TaskHandler;
import be.alexandre01.dnplugin.api.objects.server.DNServer;
import be.alexandre01.dnplugin.api.connection.request.RequestPacket;
import be.alexandre01.dnplugin.api.connection.request.RequestType;
import be.alexandre01.dnplugin.api.connection.request.channels.DNChannel;
import be.alexandre01.dnplugin.plugins.spigot.DNSpigot;
import be.alexandre01.dnplugin.api.utils.Mods;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.var;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class NetworkCommand extends Command {
    public enum SubCommand{
        GUI,GETSERVER,GETSERVERS,START,STOP,CMD,SEND,DATA;
    }
    public NetworkCommand(String name) {
        super(name);
    }
    @Override
    public boolean execute(CommandSender sender, String msg, String[] args) {
        if(!sender.hasPermission("network.use")){
            sender.sendMessage("§cYou do not have permission to access the infrastructure.");
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
                Integer id = null;
                try{
                    id = Integer.parseInt(args[1].split("-")[1]);
                }catch (Exception e){
                    sender.sendMessage("§cPlease put a valid id. example: servername-0");
                    return false;
                }
                if(!NetworkBaseAPI.getInstance().getServices().containsKey(server)){
                    sender.sendMessage("§cPlease put another server name because this one is invalid.");
                    return false;
                }
                if(!NetworkBaseAPI.getInstance().getServices().get(server).getServers().containsKey(id) || !NetworkBaseAPI.getInstance().getServices().get(server).isStarted()){
                    sender.sendMessage("§cThe server is not online.");
                    return false;
                }


                DNSpigot.getAPI().getRequestManager().getRequest(RequestType.SERVER_EXECUTE_COMMAND,args[1],cmd.toString()).dispatch();
                break;
            case STOP:
                if(args.length < 2){
                    sender.sendMessage("§e - §9/network §lSTOP§9 [SERVER]");
                    return false;
                }

                server = args[1].split("-")[0];
                id = null;
                try{
                    id = Integer.parseInt(args[1].split("-")[1]);
                }catch (Exception e){
                    sender.sendMessage("§cPlease put a valid id. example: servername-0");
                    return false;
                }
                if(!NetworkBaseAPI.getInstance().getServices().containsKey(server)){
                    sender.sendMessage("§cPlease put another server name because this one is invalid.");
                    return false;
                }
                if(!NetworkBaseAPI.getInstance().getServices().get(server).getServers().containsKey(id) || !NetworkBaseAPI.getInstance().getServices().get(server).isStarted()){
                    sender.sendMessage("§cThe server is not online.");
                    return false;
                }

                RequestPacket stop = DNSpigot.getAPI().getRequestManager().sendRequest(RequestType.CORE_STOP_SERVER,args[1]);
                sender.sendMessage("§aThe request to stop the server §l"+args[1]+"§a has been sent. Please wait.");
                stop.setRequestFutureResponse(message -> {
                    System.out.println(message);
                });

                break;
            case START:
                if(args.length < 2){
                    sender.sendMessage("§e - §9/network §lSTART§9 [SERVER]");
                    return false;
                }

                switch (args.length){
                    case 2:
                        if(!NetworkBaseAPI.getInstance().getServices().containsKey(args[1])){
                            sender.sendMessage("§cPlease put another server name because this one is invalid.");
                            return false;
                        }
                        AtomicReference<String> serverName = new AtomicReference<>(args[1]);
                        RequestPacket start = DNSpigot.getAPI().getRequestManager().getRequest(RequestType.CORE_START_SERVER,serverName);
                        DNCallback.multiple(start, new TaskHandler() {
                            @Override
                            public int getTimeOut() {
                                return 600;
                            }

                            @Override
                            public void onCallback() {
                                if(hasType(TaskType.CUSTOM)){
                                    if(getCustomType().equals("STARTED")){
                                        if(getResponse().containsKey("name")){
                                            serverName.set(getResponse().getString("name"));
                                        }
                                        sender.sendMessage("§aThe server "+ serverName.get()+" has been started");
                                    }
                                    if(getCustomType().equals("LINKED")){
                                        sender.sendMessage("§aThe server "+ serverName.get()+" is CONNECTED");
                                        destroy();
                                    }
                                }
                            }

                            @Override
                            public void onFailed() {
                                sender.sendMessage("§cThere is a problem to start the service "+ serverName.get());
                            }
                        }).send();

                        sender.sendMessage("§aThe request to start the server §l"+args[1]+"§a has been sent. Please wait.");
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
                    sender.sendMessage("§e - §9/network §lSEND§9 [PLAYER] [SERVER]");
                    return false;
                }
                Player player = null;
                try {
                     player = Bukkit.getPlayer(args[1]);
                    if(player == null){
                        sender.sendMessage("§cThe player is not online.");
                        return false;
                    }
                }catch (Exception e){
                    sender.sendMessage("§cThe player is not online.");
                    return false;
                }
                server = args[2].split("-")[0];
                id = null;
                try{
                    id = Integer.parseInt(args[2].split("-")[1]);
                }catch (Exception e){
                    sender.sendMessage("§cPlease put a valid id. example: servername-0");
                    return false;
                }
                if(!NetworkBaseAPI.getInstance().getServices().containsKey(server)){
                    sender.sendMessage("§cPlease put another server name because this one is invalid.");
                    return false;
                }
                if(!NetworkBaseAPI.getInstance().getServices().get(server).getServers().containsKey(id) || !NetworkBaseAPI.getInstance().getServices().get(server).isStarted()){
                    sender.sendMessage("§cThe server is not online.");
                    return false;
                }

                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Connect");
                out.writeUTF(server);
                player.sendPluginMessage(DNSpigot.getInstance(), "BungeeCord", out.toByteArray());
                break;
            case DATA:
                if(args.length < 2){
                    sender.sendMessage("§e - §9/network §lDATA§9 [TABLE]");
                    for(DNChannel c : DNSpigot.getAPI().getChannelManager().getChannels().values()){
                        sender.sendMessage(c.getName());
                    }
                    return true;
                }
                DNChannel c = DNSpigot.getAPI().getChannelManager().getChannel(args[1]);
                if(c == null){
                    sender.sendMessage("§cThe channel is not valid.");
                }
                sender.sendMessage(c.getName()+" :");
                for(String s : c.getObjects().keySet()){
                   sender.sendMessage(" "+s +" : "+ c.getObjects().get(s).toString());
                }


            case GUI:
                break;
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
        if(args.length == 2){
            if(args[1].equalsIgnoreCase("start")){
                List<String> optionsType = new ArrayList<>();
                for(String s : NetworkBaseAPI.getInstance().getServices().keySet()){
                    if(s.startsWith(args[1].toUpperCase()))
                        optionsType.add(s);
                }
                return optionsType;
            }
        }
        return null;
    }

    public void sendHelp(CommandSender sender){
        sender.sendMessage("§6DreamNetwork System Beta:");
        sender.sendMessage("§8§m*------§7§m------§7§m-§b§m-----------§7§m-§7§m------§8§m------*");
        sender.sendMessage("§e - §9/network §lSTART§9 [SERVER]");
        sender.sendMessage("§e - §9/network §lSTOP§9 [SERVER]");
        sender.sendMessage("§e - §9/network §lGETSERVER§9");
        sender.sendMessage("§e - §9/network §lGETSERVERS§9");
        sender.sendMessage("§e - §9/network §lCMD§9 [SERVER] [COMMANDS]");
        sender.sendMessage("§e - §9/network §lSEND§9 [Player] [Server]");
        sender.sendMessage("§8§m*------§7§m------§7§m-§b§m-----------§7§m-§7§m------§8§m------*");
    }
}
