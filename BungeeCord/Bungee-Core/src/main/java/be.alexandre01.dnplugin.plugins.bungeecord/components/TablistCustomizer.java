package be.alexandre01.dnplugin.plugins.bungeecord.components;

import be.alexandre01.dnplugin.plugins.bungeecord.DNBungee;
import be.alexandre01.dnplugin.plugins.bungeecord.utils.BaseComponentBuilder;
import be.alexandre01.dnplugin.api.utils.Config;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TablistCustomizer {
    private File file;
    private Configuration configuration;
    private final DNBungee dnBungee;
    private final AtomicInteger header_lineToOverride = new AtomicInteger(0);
    private final AtomicInteger footer_lineToOverride = new AtomicInteger(0);
    private final HashMap<Integer, String> header_default = new HashMap<>();
    private final HashMap<Integer, String> footer_default = new HashMap<>();
    private final HashMap<Integer, HashMap<Integer, String>> header_lines_by_tick = new HashMap<>();
    private final HashMap<Integer, HashMap<Integer, String>> footer_lines_by_tick = new HashMap<>();
    private final HashMap<Integer,Integer> header_repeat_by_tick = new HashMap<>();
    private final HashMap<Integer, Integer> footer_repeat_by_tick = new HashMap<>();

    private final HashMap<Integer,TextComponent> header_final = new HashMap<>();
    private final HashMap<Integer, TextComponent> footer_final = new HashMap<>();

    public TablistCustomizer() {
        loadConfig();
        this.dnBungee = DNBungee.getInstance();
        boolean isEnabled = true;
        if(!configuration.getBoolean("tablist.activated")){
            isEnabled = false;
            return;
        }
        if(!readTablist()){
            isEnabled = false;
            return;
        }

        final int maxSize_header = header_final.size();
        final int maxSize_footer = footer_final.size();
        dnBungee.getProxy().getScheduler().schedule(dnBungee, new Runnable() {
            int i_header = 0;
            int i_footer = 0;
            int ticks_header = 1;
            int ticks_footer = 1;
            @Override
            public void run() {
                for(ProxiedPlayer player: dnBungee.getProxy().getPlayers()){
                    updateTab(player,ticks_header,ticks_footer);
                }

                i_header++;
                i_footer++;
                if(i_header > header_repeat_by_tick.get(ticks_header)){
                    ticks_header++;
                    i_header = 0;
                    if(ticks_header > maxSize_header){
                        ticks_header = 1;
                    }

                }

                if(i_footer > footer_repeat_by_tick.get(ticks_footer)){
                    ticks_footer++;
                    i_footer = 0;
                    if(ticks_footer > maxSize_footer){
                        ticks_footer = 1;
                    }

                }

            }
        },0,configuration.getInt("tablist.ticks")* 50L, TimeUnit.MILLISECONDS);
    }

    public void updateTab(ProxiedPlayer player, int tick_header,int tick_footer){
        TextComponent header = new TextComponent(header_final.get(tick_header));
        TextComponent footer = new TextComponent(footer_final.get(tick_footer));
        for(BaseComponent baseComponent : header.getExtra()){
            TextComponent textComponent = (TextComponent) baseComponent;
            textComponent.setText(textComponent.getText()
                    .replaceAll("%playercount%",dnBungee.getProxy().getPlayers().size()+"")
                    .replaceAll("%ping%",player.getPing()+"")
                    .replaceAll("%player%",player.getName()));
        }
        for(BaseComponent baseComponent : footer.getExtra()){
            TextComponent textComponent = (TextComponent) baseComponent;
            textComponent.setText(textComponent.getText()
                    .replaceAll("%playercount%",dnBungee.getProxy().getPlayers().size()+"")
                    .replaceAll("%ping%",player.getPing()+"")
                    .replaceAll("%player%",player.getName()));
        }
        player.setTabHeader(header,footer);
    }



    public boolean readTablist(){
        boolean t = false;
        t = animationLoader("header",header_default,header_lines_by_tick,header_repeat_by_tick,header_final,header_lineToOverride);
        if(!t)
            return false;
        t = animationLoader("footer",footer_default,footer_lines_by_tick,footer_repeat_by_tick,footer_final,footer_lineToOverride);
        return t;
    }

    public boolean animationLoader(String type,HashMap<Integer, String> defaultH, HashMap<Integer,HashMap<Integer, String>> lines_by_ticks,HashMap<Integer, Integer> repeat,HashMap<Integer,TextComponent> finalH,AtomicInteger lineToOverride){
        try {
            //Get subSection

            for(String key : configuration.getSection("tablist."+type+"-animation").getKeys()){
                try{
                    for(String subKey: configuration.getSection("tablist."+type+"-animation."+key).getKeys()){
                        String f = key+"."+subKey;
                        String[] sections = f.split("\\.");
                        if(sections[0].equalsIgnoreCase("override_lines")){
                            lineToOverride.set(configuration.getInt("tablist."+type+"-animation."+sections[0]));
                        }
                        if(sections[0].equalsIgnoreCase("default")){
                            if(sections.length < 2)
                                continue;
                            distributeText(defaultH,sections,type);
                        }
                        if(Character.isDigit(sections[0].charAt(0)) && sections[0].length() < 2){
                            if(sections.length < 2)
                                continue;
                            int tick = Character.getNumericValue(sections[0].charAt(0));
                            if(sections[1].equalsIgnoreCase("repeat")) {
                                repeat.put(tick,configuration.getInt("tablist."+type+"-animation."+sections[0]+"."+sections[1]));
                                continue;
                            }
                            HashMap<Integer, String> workingHM = new HashMap<>();

                            if(!distributeText(workingHM,sections,type)) continue;


                            lines_by_ticks.put(tick,workingHM);

                        }
                    }
                }catch (Exception ignored){
                    //Ignored exception
                }

            }
            HashMap<Integer, BaseComponentBuilder> b = new HashMap<>();

            for(HashMap<Integer,String > f : lines_by_ticks.values());
            if(lines_by_ticks.isEmpty()){
                lines_by_ticks.put(1,new HashMap<>());
            }
            for (int i = 1; i < lines_by_ticks.size()+1; i++) {
                if(!repeat.containsKey(i)){
                    repeat.put(i,0);
                }
                b.put(i, new BaseComponentBuilder());
            }
            defaultH.forEach((key, value) -> {
                for (int i = 1; i < lines_by_ticks.size() + 1; i++) {
                    BaseComponentBuilder baseComponentBuilder;
                    baseComponentBuilder = b.get(i);
                    baseComponentBuilder.setLine(key, value);
                }
            });

            lines_by_ticks.forEach((key, value) -> {
                BaseComponentBuilder baseComponentBuilder;

                baseComponentBuilder = b.get(key);
                for (Integer line : value.keySet()) {
                    baseComponentBuilder.setLine(line, value.get(line));
                }
            });

            for (int i = 1; i < lines_by_ticks.size()+1; i++) {
                finalH.put(i, b.get(i).toTextComponent());
            }

            System.out.println("La configuration de la TABLIST-"+type+" a bien été chargé !");
            return true;
        }catch (Exception e){
            System.out.println("La configuration de la TABLIST-"+type+"a eu un problème.");
            e.printStackTrace();
            return false;
        }
    }

    public boolean distributeText(HashMap<Integer, String> h, String[] sections,String type){
        if(sections[1].toLowerCase().startsWith("line-") && Character.isDigit(sections[1].charAt(sections[1].length()-1))){
            int line = Character.getNumericValue(sections[1].charAt(sections[1].length()-1));
            h.put(line,configuration.getString("tablist."+type+"-animation."+sections[0]+"."+sections[1]));
            return true;
        }
        return false;
    }
    public void loadConfig(){
        File theDir = new File(ProxyServer.getInstance().getPluginsFolder(), "/DreamNetwork/");
        if(!theDir.exists()){
            theDir.mkdirs();
        }

        file = new File(ProxyServer.getInstance().getPluginsFolder(), "/DreamNetwork/tablist.yml");
        try{
            if(!file.exists()){
                file.createNewFile();

                try (InputStream is = getClass().getClassLoader().getResourceAsStream("bungeecord/tablist.yml")) {
                    if(is == null){
                        System.out.println("no configuration for tablist.yml");
                        return;
                    }
                    Config.write(is, file);
                } catch (IOException e) {
                    System.out.println("no input configuration for tablist.yml");
                    e.printStackTrace();
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void saveConfig(){
        try{
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
        }catch (IOException e){
            e.printStackTrace();
        }
    }


}
