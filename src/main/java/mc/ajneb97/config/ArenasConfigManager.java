package mc.ajneb97.config;

import mc.ajneb97.manager.ArenaManager;
import mc.ajneb97.MineChess;
import mc.ajneb97.config.model.CommonConfig;
import mc.ajneb97.model.Arena;
import mc.ajneb97.model.ArenaEndTimeMode;
import mc.ajneb97.model.game.GameStatus;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

public class ArenasConfigManager {

    private MineChess plugin;
    private CommonConfig configFile;

    public ArenasConfigManager(MineChess plugin){
        this.plugin = plugin;
        this.configFile = new CommonConfig("arenas.yml",plugin,null, false);
        this.configFile.registerConfig();
    }

    public void configure(){
        FileConfiguration config = configFile.getConfig();

        Location mainLobbyLocation = null;
        if(config.contains("main_lobby")){
            mainLobbyLocation = getExactLocationFromPath(config,"main_lobby");
        }

        ArrayList<Arena> arenas = new ArrayList<>();
        if(config.contains("arenas")){
            for(String key : config.getConfigurationSection("arenas").getKeys(false)){
                Arena arena = new Arena(key);
                String path = "arenas."+key;
                arena.setLobbyLocation(getExactLocationFromPath(config,path+".lobby"));
                arena.setBoardStartLocation(getExactLocationFromPath(config,path+".board_start_location"));
                arena.setSpawnPlayer1Location(getExactLocationFromPath(config,path+".spawn_1"));
                arena.setSpawnPlayer2Location(getExactLocationFromPath(config,path+".spawn_2"));

                arena.setStatus(!config.getBoolean(path+".enabled") ? GameStatus.DISABLED : GameStatus.WAITING);

                arena.setMaxTime(config.getInt(path+".gamemode_values.max_time"));
                arena.setTurnTime(config.getInt(path+".gamemode_values.turn_time"));
                if(config.contains(path+".end_time")){
                    arena.setEndTimeMode(ArenaEndTimeMode.valueOf(config.getString(path+".end_time")));
                }

                arena.setBoardBlackCellBlock(Material.valueOf(config.getString(path+".board_black_cell_block")));
                arena.setBoardWhiteCellBlock(Material.valueOf(config.getString(path+".board_white_cell_block")));

                arenas.add(arena);
            }
        }

        ArenaManager arenasManager = plugin.getArenaManager();
        arenasManager.setArenas(arenas);
        arenasManager.setMainLobbyLocation(mainLobbyLocation);
    }

    public void saveMainLobby(Location location){
        FileConfiguration config = configFile.getConfig();
        setConfigFromExactLocation(config,"main_lobby",location);
        saveConfig();
    }

    public void saveArena(Arena arena){
        FileConfiguration config = configFile.getConfig();
        String path = "arenas."+arena.getName();

        config.set(path,null);

        setConfigFromExactLocation(config,path+".lobby",arena.getLobbyLocation());
        setConfigFromExactLocation(config,path+".board_start_location",arena.getBoardStartLocation());
        setConfigFromExactLocation(config,path+".spawn_1",arena.getSpawnPlayer1Location());
        setConfigFromExactLocation(config,path+".spawn_2",arena.getSpawnPlayer2Location());
        config.set(path+".enabled",!arena.getStatus().equals(GameStatus.DISABLED));

        config.set(path+".gamemode","ARENA_TIME");
        config.set(path+".gamemode_values.max_time",arena.getMaxTime());
        config.set(path+".gamemode_values.turn_time",arena.getTurnTime());
        if(arena.getEndTimeMode() != null){
            config.set(path+".end_time",arena.getEndTimeMode().name());
        }
        config.set(path+".cell_size","LARGE");
        config.set(path+".pieces_type","MODEL");
        config.set(path+".board_black_cell_block",arena.getBoardBlackCellBlock().name());
        config.set(path+".board_white_cell_block",arena.getBoardWhiteCellBlock().name());

        saveConfig();
    }

    public void deleteArena(String name){
        FileConfiguration config = configFile.getConfig();
        config.set("arenas."+name,null);
        saveConfig();
    }

    private Location getExactLocationFromPath(FileConfiguration config,String path){
        if(!config.contains(path)){
            return null;
        }
        return new Location(
                Bukkit.getWorld(config.getString(path+".world")),
                config.getDouble(path+".x"),
                config.getDouble(path+".y"),
                config.getDouble(path+".z"),
                (float)config.getDouble(path+".yaw"),
                (float)config.getDouble(path+".pitch")
        );
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

    private void setConfigFromExactLocation(FileConfiguration config,String path,Location location){
        if(location == null){
            return;
        }
        config.set(path+".world",location.getWorld().getName());
        config.set(path+".x",location.getX());
        config.set(path+".y",location.getY());
        config.set(path+".z",location.getZ());
        config.set(path+".yaw",location.getYaw());
        config.set(path+".pitch",location.getPitch());
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
