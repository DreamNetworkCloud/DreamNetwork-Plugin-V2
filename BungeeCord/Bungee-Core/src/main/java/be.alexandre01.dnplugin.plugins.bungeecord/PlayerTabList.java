package be.alexandre01.dnplugin.plugins.bungeecord;

import be.alexandre01.dnplugin.api.utils.files.tablist.TabList;
import be.alexandre01.dnplugin.api.utils.files.tablist.TabListYAML;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class PlayerTabList {
    private final DNBungee dnBungee;
    private boolean end = false;

    public PlayerTabList(){
        this.dnBungee = DNBungee.getInstance();
    }

    private HashMap<Integer, String> actualHeader;
    private HashMap<Integer, String> actualFooter;
    private int hIterationLeft;
    private int fIterationLeft;
    private int hNumber;
    private int fNumber;

    public void start(){
        end = false;
        TabListYAML tabList = dnBungee.getYamlManager().getTabList();
        TabList header = tabList.getHeader();
        TabList footer = tabList.getFooter();
        hIterationLeft = 0;
        fIterationLeft = 0;
        hNumber = 0;
        fNumber = 0;
        actualHeader = null;
        actualFooter = null;
        dnBungee.getProxy().getScheduler().schedule(dnBungee, () -> {
            if(end || !tabList.isActivate()){return;}
            if(actualHeader == null){
                actualHeader = new HashMap<>();
                for(int i = 0 ; i != header.getOverrideLines() ; i++){actualHeader.put(i, "");}
                actualHeader.putAll(header.getDefaultLines());
            }
            if(actualFooter == null){
                actualFooter = new HashMap<>();
                for(int i = 0 ; i != footer.getOverrideLines() ; i++){actualFooter.put(i, "");}
                actualFooter.putAll(footer.getDefaultLines());
            }

            if(hIterationLeft == 0){
                hNumber = (header.getStates().containsKey(hNumber+1) ? hNumber+1 : 1);
                if(header.getStates().containsKey(hNumber)){
                    hIterationLeft = header.getStates().get(hNumber).getRepeat();
                    actualHeader.putAll(header.getStates().get(hNumber).getLines());
                }
            }
            if(fIterationLeft == 0){
                fNumber = (footer.getStates().containsKey(fNumber+1) ? fNumber+1 : 1);
                if(footer.getStates().containsKey(fNumber)){
                    fIterationLeft = footer.getStates().get(fNumber).getRepeat();
                    actualFooter.putAll(footer.getStates().get(fNumber).getLines());
                }
            }

            StringBuilder sbH = new StringBuilder();
            actualHeader.forEach((l, v) -> {
                sbH.append(v).append("\n");
            });

            StringBuilder sbF = new StringBuilder();
            actualFooter.forEach((l,v) -> {
                sbF.append(v).append("\n");
            });

            for(ProxiedPlayer player : dnBungee.getProxy().getPlayers()){
                player.setTabHeader(
                        new TextComponent(stringFormat(sbH.substring(0, sbH.toString().length()-1), player)),
                        new TextComponent(stringFormat(sbF.substring(0, sbF.toString().length()-1), player))
                );
            }
            hIterationLeft--;
            fIterationLeft--;
        }, 0L, tabList.getDelay()/20, TimeUnit.SECONDS);
    }

    private String stringFormat(String s, ProxiedPlayer player){
        return s.replace("&", "§")
                .replace("%online%", String.valueOf(dnBungee.getProxy().getPlayers().size()))
                .replace("%max%", String.valueOf(dnBungee.getConfiguration().getSlots()))
                .replace("%ping%", String.valueOf(player.getPing()));
    }

    public void stop(){
        end = true;
        for(ProxiedPlayer player : dnBungee.getProxy().getPlayers()){
            player.resetTabHeader();
        }
    }

    public void restart(){
        stop();
        start();
    }
}
