package be.alexandre01.dnplugin.plugins.bungeecord.utils;

import be.alexandre01.dnplugin.api.utils.files.SearchText;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class BungeeText extends SearchText {
    public File file;
    public Configuration configuration;
    private boolean backup;
    public BungeeText(boolean backup) {
        super(backup);
        this.backup = backup;
        super.setDistributeText((message, objects) -> {
            try {
                if(message.contains("%player%"))
                    distribute(returnObjects(Class.forName("net.md_5.bungee.api.connection.ProxiedPlayer"),objects),"%player%","getName",message);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void init(){
        super.init();
    }

    @Override
    protected Set<String> getKeys(String path, boolean b) {
        return new HashSet<>(configuration.getSection(path).getKeys());
    }

    @Override
    protected boolean hasSubSection(String path) {
        try {
            configuration.getSection(path);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    @Override
    protected String getString(String path) {
        return configuration.getString(path);
    }

    @Override
    public void reloadConfig() {

        File theDir = new File(ProxyServer.getInstance().getPluginsFolder(), "/DreamNetwork/");
        if(!theDir.exists()){
            theDir.mkdirs();
        }
        if(!backup){
            file = new File(ProxyServer.getInstance().getPluginsFolder(), "/DreamNetwork/messages.yml");
        }else {
            file = new File(ProxyServer.getInstance().getPluginsFolder(), "/DreamNetwork/src-messages.yml");
        }

        try{
            if(!file.exists()){
                file.createNewFile();
                InputStream i = BungeeText.class.getResourceAsStream("/config/bungeecord/messages.yml");
                OutputStream o = new FileOutputStream(file);
                IOUtils.copy(i, o);
                IOUtils.closeQuietly(i);
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

    @Override
    public void saveFile() {
        saveConfig();
    }

    @Override
    public void setString(String path, String value) {
        configuration.set(path,value);
    }

    @Override
    public File getFile() {
        return file;
    }

    public void saveConfig(){
        try{
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
