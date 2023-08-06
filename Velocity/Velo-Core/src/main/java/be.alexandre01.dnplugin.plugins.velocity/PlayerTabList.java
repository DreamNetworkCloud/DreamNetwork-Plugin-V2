package be.alexandre01.dnplugin.plugins.velocity;

import be.alexandre01.dnplugin.api.utils.files.tablist.TabList;
import be.alexandre01.dnplugin.api.utils.files.tablist.TabListYAML;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class PlayerTabList {
    private final ProxyServer server;
    private final DNVelocity dnVelocity;
    private boolean end;
    private HashMap<Integer, String> actualHeader;
    private HashMap<Integer, String> actualFooter;
    private int hIterationLeft;
    private int fIterationLeft;
    private int hNumber;
    private int fNumber;

    public PlayerTabList(ProxyServer server){
        this.server = server;
        this.dnVelocity = DNVelocity.getInstance();
    }

    public void start(){
        dnVelocity.getLogger().info("Bonjjjour");
        end = false;
        TabListYAML tabList = dnVelocity.getYamlManager().getTabList();
        TabList header = tabList.getHeader();
        TabList footer = tabList.getFooter();
        hIterationLeft = 0;
        fIterationLeft = 0;
        hNumber = 0;
        fNumber = 0;
        actualHeader = null;
        actualFooter = null;

        server.getScheduler()
                .buildTask(dnVelocity, () -> {
                    if(end || !tabList.isActivate()){return;}
                    try {
                        if (actualHeader == null) {
                            actualHeader = new HashMap<>();
                            for (int i = 0; i != header.getOverrideLines(); i++) {
                                actualHeader.put(i, "");
                            }
                            actualHeader.putAll(header.getDefaultLines());
                        }
                        if (actualFooter == null) {
                            actualFooter = new HashMap<>();
                            for (int i = 0; i != footer.getOverrideLines(); i++) {
                                actualFooter.put(i, "");
                            }
                            actualFooter.putAll(footer.getDefaultLines());
                        }

                        if (hIterationLeft == 0) {
                            hNumber = (header.getStates().containsKey(hNumber + 1) ? hNumber + 1 : 1);
                            if (header.getStates().containsKey(hNumber)) {
                                hIterationLeft = header.getStates().get(hNumber).getRepeat();
                                actualHeader.putAll(header.getStates().get(hNumber).getLines());
                            }
                        }
                        if (fIterationLeft == 0) {
                            fNumber = (footer.getStates().containsKey(fNumber + 1) ? fNumber + 1 : 1);
                            if (footer.getStates().containsKey(fNumber)) {
                                fIterationLeft = footer.getStates().get(fNumber).getRepeat();
                                actualFooter.putAll(footer.getStates().get(fNumber).getLines());
                            }
                        }

                        StringBuilder sbH = new StringBuilder();
                        actualHeader.forEach((l, v) -> {
                            sbH.append(v).append("\n");
                        });

                        StringBuilder sbF = new StringBuilder();
                        actualFooter.forEach((l, v) -> {
                            sbF.append(v).append("\n");
                        });

                        for (Player player : server.getAllPlayers()) {
                            player.getTabList().clearHeaderAndFooter();
                            player.getTabList().setHeaderAndFooter(
                                    Component.text(stringFormat(sbH.substring(0, sbH.toString().length() - 1), player)),
                                    Component.text(stringFormat(sbF.substring(0, sbF.toString().length() - 1), player))
                            );
                        //player.setTabHeader(
                        //        new TextComponent(convertToValidString(sbH.substring(0, sbH.toString().length()-1), player)),
                        //        new TextComponent(convertToValidString(sbF.substring(0, sbF.toString().length()-1), player))
                        //);
                        }
                        hIterationLeft--;
                        fIterationLeft--;
                    }catch (Exception e){
                        e.printStackTrace();
                        stop();
                    }
                })
                .repeat((long) ((double)tabList.getDelay()/20), TimeUnit.SECONDS)
                .schedule();
    }

    private String stringFormat(String s, Player player){
        return s.replace("&", "§")
                .replace("%online%", String.valueOf(server.getPlayerCount()))
                .replace("%max%", String.valueOf(dnVelocity.getConfiguration().getSlots()))
                .replace("%ping%", String.valueOf(player.getPing()));
    }

    public void stop(){
        end = true;
        for(Player p : server.getAllPlayers()){
            p.getTabList().clearHeaderAndFooter();
        }
    }

    public void restart(){
        stop();
        start();
    }
}
