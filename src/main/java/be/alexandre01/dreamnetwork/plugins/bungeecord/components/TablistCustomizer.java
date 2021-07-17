package be.alexandre01.dreamnetwork.plugins.bungeecord.components;

import be.alexandre01.dreamnetwork.plugins.bungeecord.DNBungee;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.util.concurrent.TimeUnit;

public class TablistCustomizer {

    private Configuration configuration;
    private DNBungee dnBungee;
    public TablistCustomizer(Configuration configuration) {
        this.configuration = configuration;
        this.dnBungee = DNBungee.getInstance();
        dnBungee.getProxy().getScheduler().schedule(dnBungee, new Runnable() {
            int i = 0;
            @Override
            public void run() {

                for(ProxiedPlayer player: dnBungee.getProxy().getPlayers()){
                    updateTab(player,i);
                }

                if(i == 1){
                    i = 0;
                }else {
                    i++;
                }

            }
        },0,3, TimeUnit.SECONDS);
    }

    public void updateTab(ProxiedPlayer player, int i){
        TextComponent header = new TextComponent();
        TextComponent footer = new TextComponent();
        if(i == 0){
            header.addExtra("   §5▰▰▰▰▰▰▰▰▰▰▰ §9§lShirome §5▰▰▰▰▰▰▰▰▰▰▰");
        }else {
            header.addExtra("   §d▰▰▰▰▰▰▰▰▰▰▰ §3§lShirome §d▰▰▰▰▰▰▰▰▰▰▰");
        }

        header.addExtra("\n\n");
        header.addExtra("§7Joueurs en ligne: "+dnBungee.getProxy().getPlayers().size());
        header.addExtra("\n");
        footer.addExtra("\n");
        footer.addExtra("§2Ping : §a"+ player.getPing());
        footer.addExtra("\n\n");
        footer.addExtra("§7Site: §dwww§5.§dshirome§5.§deu\n");
        footer.addExtra("§7Discord: §ddiscord§5.§dshirome§5.§dfr");
        player.setTabHeader(header,footer);
    }
}
