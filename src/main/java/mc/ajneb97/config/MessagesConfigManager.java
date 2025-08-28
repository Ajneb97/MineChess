package mc.ajneb97.config;

import mc.ajneb97.MineChess;
import mc.ajneb97.config.model.CommonConfig;
import mc.ajneb97.manager.MessagesManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MessagesConfigManager {

    private MineChess plugin;
    private CommonConfig configFile;

    public MessagesConfigManager(MineChess plugin){
        this.plugin = plugin;
        this.configFile = new CommonConfig("messages.yml",plugin,null,false);
        configFile.registerConfig();
        checkUpdates();
    }

    public void configure() {
        FileConfiguration config = configFile.getConfig();

        MessagesManager messagesManager = new MessagesManager();
        messagesManager.setPrefix(config.getString("prefix"));
        messagesManager.setTimeSeconds(config.getString("seconds"));
        messagesManager.setTimeMinutes(config.getString("minutes"));
        messagesManager.setTimeHours(config.getString("hours"));
        messagesManager.setTimeDays(config.getString("days"));

        plugin.setMessagesManager(messagesManager);
    }

    public CommonConfig getConfigFile() {
        return configFile;
    }

    public boolean reloadConfig(){
        if(!configFile.reloadConfig()){
            return false;
        }
        configure();
        return true;
    }

    public void checkUpdates(){
        Path pathConfig = Paths.get(configFile.getRoute());
        try{
            String text = new String(Files.readAllBytes(pathConfig));
            FileConfiguration config = configFile.getConfig();

            if(!text.contains("playerSpectateNotPlaying:")){
                config.set("playerSpectateNotPlaying", "&cThat player is not playing.");
                configFile.saveConfig();
            }

            if(!text.contains("inventoryEditingArenaLocationResetItemName:")){
                config.set("inventoryEditingArenaLocationResetItemName", "&4&lReset Arena Locations");
                List<String> list = new ArrayList<>();
                list.add("&7This will reset the locations of player");
                list.add("&7spawns and arena lobby to the default values.");
                list.add("");
                list.add("&a&lCLICK &ato reset");
                config.set("inventoryEditingArenaLocationResetItemLore", list);
                config.set("inventoryEditingArenaLocationsReset", "&aLocations reset.");
                configFile.saveConfig();
            }

            if(!text.contains("commandJoinComputerError:")){
                config.set("commandJoinComputerError", "&cYou must use &7/minechess joincomputer <difficulty>");
                config.set("noValidDifficulty", "&cThat's not a valid difficulty.");
                config.set("computerGameJoin", "&eYou have joined a player vs computer game.");
                config.set("computerGameLeave", "&eYou have left the game.");
                config.set("computerGameCanNotJoin", "&cA player is currently playing against a computer on that arena.");
                config.set("computerDisplayName", "Computer");
                configFile.saveConfig();
            }

            if(!text.contains("noAvailableArenas:")){
                config.set("noAvailableArenas", "&cThere are no available arenas at the moment.");
                configFile.saveConfig();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig(){
        return configFile.getConfig();
    }
}
