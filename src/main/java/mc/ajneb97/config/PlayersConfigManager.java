package mc.ajneb97.config;

import mc.ajneb97.MineChess;
import mc.ajneb97.config.model.CommonConfig;
import mc.ajneb97.model.data.PlayerDataBackup;
import mc.ajneb97.model.data.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayersConfigManager extends DataFolderConfigManager{


    public PlayersConfigManager(MineChess plugin, String folderName) {
        super(plugin, folderName);
    }

    @Override
    public void createFiles() {

    }

    @Override
    public void loadConfigs(){
        Map<UUID, PlayerData> players = new HashMap<>();
        Map<UUID, PlayerDataBackup> playersBackup = new HashMap<>();

        ArrayList<CommonConfig> configFiles = getConfigs();
        for(CommonConfig commonConfig : configFiles){
            FileConfiguration config = commonConfig.getConfig();
            String uuidString = commonConfig.getPath().replace(".yml", "");
            String name = config.getString("name");
            int wins = config.getInt("wins");
            int loses = config.getInt("loses");
            int ties = config.getInt("ties");
            long millisPlayed = config.getLong("millis_played");

            PlayerDataBackup backup = null;
            if(config.contains("backup")){
                GameMode gamemode = GameMode.valueOf(config.getString("backup.gamemode"));
                float xp = (float)config.getDouble("backup.xp");
                int level = config.getInt("backup.level");
                int food = config.getInt("backup.food");
                double health = config.getDouble("backup.health");
                double maxHealth = config.getDouble("backup.max_health");
                boolean allowFlight = config.getBoolean("backup.allow_flight");
                boolean isFlying = config.getBoolean("backup.is_flying");
                ItemStack[] inventory = new ItemStack[41];
                if(config.contains("backup.inventory")){
                    for(String key : config.getConfigurationSection("backup.inventory").getKeys(false)){
                        inventory[Integer.parseInt(key)] = config.getItemStack("backup.inventory."+key);
                    }
                }

                backup = new PlayerDataBackup(inventory,gamemode,xp,level,food,health,maxHealth,allowFlight,isFlying);
            }

            UUID uuid = UUID.fromString(uuidString);
            PlayerData playerData = new PlayerData(uuid,name);
            playerData.setWins(wins);
            playerData.setLoses(loses);
            playerData.setTies(ties);
            playerData.setMillisPlayed(millisPlayed);

            if(backup != null){
                playersBackup.put(uuid,backup);
            }

            players.put(uuid,playerData);
        }

        plugin.getPlayerDataManager().setPlayers(players);
        plugin.getPlayerDataManager().getPlayerDataBackupManager().setPlayers(playersBackup);
    }

    public void saveConfig(PlayerData playerData){
        String playerName = playerData.getName();
        CommonConfig playerConfig = getConfigFile(playerData.getUuid()+".yml");
        FileConfiguration config = playerConfig.getConfig();

        config.set("name", playerName);
        config.set("wins",playerData.getWins());
        config.set("loses",playerData.getLoses());
        config.set("ties",playerData.getTies());
        config.set("millis_played",playerData.getMillisPlayed());

        playerConfig.saveConfig();
    }

    public void saveBackupConfig(String uuid,PlayerDataBackup playerDataBackup){
        CommonConfig playerConfig = getConfigFile(uuid+".yml");
        FileConfiguration config = playerConfig.getConfig();

        config.set("backup",null);
        if(playerDataBackup != null){
            config.set("backup.gamemode",playerDataBackup.getGamemode().name());
            config.set("backup.xp",playerDataBackup.getXp());
            config.set("backup.level",playerDataBackup.getLevel());
            config.set("backup.food",playerDataBackup.getFood());
            config.set("backup.health",playerDataBackup.getHealth());
            config.set("backup.max_health",playerDataBackup.getMaxHealth());
            config.set("backup.allow_flight",playerDataBackup.isAllowFlight());
            config.set("backup.is_flying",playerDataBackup.isFlying());
            ItemStack[] items = playerDataBackup.getInventory();
            for(int i=0;i<items.length;i++){
                config.set("backup.inventory."+i,items[i]);
            }
        }

        playerConfig.saveConfig();
    }

    @Override
    public void saveConfigs(){
        Map<UUID, PlayerData> players = plugin.getPlayerDataManager().getPlayers();
        boolean isMySQL = plugin.getConfigsManager().getMainConfigManager().isMySQL();
        if(!isMySQL){
            for(Map.Entry<UUID, PlayerData> entry : players.entrySet()) {
                PlayerData playerData = entry.getValue();
                if(playerData.isModified()){
                    saveConfig(playerData);
                }
                playerData.setModified(false);
            }
        }
    }

}
