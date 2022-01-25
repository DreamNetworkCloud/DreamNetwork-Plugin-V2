package be.alexandre01.dreamnetwork.plugins.spigot.utils;

import be.alexandre01.dreamnetwork.plugins.bungeecord.utils.BungeeText;
import be.alexandre01.dreamnetwork.plugins.spigot.DNSpigot;
import be.alexandre01.dreamnetwork.utils.files.SearchText;
import org.apache.commons.io.IOUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class SpigotText extends SearchText {
    private FileConfiguration configuration;
    private File file;
    private boolean backup;

    public SpigotText(boolean backup) {
        super(backup);
        this.backup = backup;
        super.setDistributeText((message, objects) -> {
            try {
                if(message.contains("%player%"))
                    distribute(returnObjects(Class.forName("org.bukkit.entity.Player"),objects),"%player%","getName",message);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected Set<String> getKeys(String path, boolean b) {
        return configuration.getConfigurationSection(path).getKeys(b);
    }

    @Override
    protected boolean hasSubSection(String path) {
        return configuration.getConfigurationSection(path) != null;
    }

    @Override
    protected String getString(String path) {
        return configuration.getString(path);
    }

    @Override
    public void reloadConfig() {
        if(!backup){
            file = new File(DNSpigot.getInstance().getDataFolder(),"messages.yml");
        }else {
            file = new File(DNSpigot.getInstance().getDataFolder(),"src-messages.yml");
        }


        configuration = new YamlConfiguration();

        if(!file.exists()){
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
                InputStream i = SpigotText.class.getResourceAsStream("/config/spigot/messages.yml");
                OutputStream o = new FileOutputStream(file);
                assert i != null;
                IOUtils.copy(i, o);
                IOUtils.closeQuietly(i);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            configuration.load(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveFile() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setString(String path, String value) {
        configuration.set(path,value);
    }

    @Override
    public File getFile() {
        return file;
    }
}
