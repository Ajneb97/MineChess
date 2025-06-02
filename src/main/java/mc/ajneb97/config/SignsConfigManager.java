package mc.ajneb97.config;

import mc.ajneb97.MineChess;
import mc.ajneb97.config.model.CommonConfig;
import mc.ajneb97.model.ArenaSign;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

public class SignsConfigManager {

    private MineChess plugin;
    private CommonConfig configFile;

    public SignsConfigManager(MineChess plugin){
        this.plugin = plugin;
        this.configFile = new CommonConfig("signs.yml",plugin,null, false);
        this.configFile.registerConfig();
    }

    public void configure(){
        FileConfiguration config = configFile.getConfig();

        ArrayList<ArenaSign> arenaSigns = new ArrayList<>();
        if(config.contains("signs")){
            for(String key : config.getConfigurationSection("signs").getKeys(false)){
                String path = "signs."+key;

                String arenaName = config.getString(path+".arena");
                Location l = getBlockLocationFromPath(config,path+".location");
                arenaSigns.add(new ArenaSign(Integer.parseInt(key),arenaName,l));
            }
        }

        plugin.getSignManager().setArenaSigns(arenaSigns);
    }

    public void saveSign(ArenaSign arenaSign){
        FileConfiguration config = configFile.getConfig();
        String path = "signs."+arenaSign.getId();
        setConfigFromBlockLocation(config,path+".location",arenaSign.getLocation());
        config.set(path+".arena",arenaSign.getArenaName());
        saveConfig();
    }

    public void deleteSign(int id){
        FileConfiguration config = configFile.getConfig();
        config.set("signs."+id,null);
        saveConfig();
    }

    private Location getBlockLocationFromPath(FileConfiguration config,String path){
        if(!config.contains(path)){
            return null;
        }
        return new Location(
                Bukkit.getWorld(config.getString(path+".world")),
                config.getInt(path+".x"),
                config.getInt(path+".y"),
                config.getInt(path+".z")
        );
    }

    private void setConfigFromBlockLocation(FileConfiguration config,String path,Location location){
        if(location == null){
            return;
        }
        config.set(path+".world",location.getWorld().getName());
        config.set(path+".x",location.getBlockX());
        config.set(path+".y",location.getBlockY());
        config.set(path+".z",location.getBlockZ());
    }

    public void saveConfig(){
        configFile.saveConfig();
    }

    public boolean reloadConfig(){
        if(!configFile.reloadConfig()){
            return false;
        }
        configure();
        return true;
    }

    public FileConfiguration getConfig(){
        return configFile.getConfig();
    }
}
