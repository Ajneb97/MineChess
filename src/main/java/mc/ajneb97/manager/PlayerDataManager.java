package mc.ajneb97.manager;

import mc.ajneb97.MineChess;
import mc.ajneb97.database.MySQLConnection;
import mc.ajneb97.utils.OtherUtils;
import mc.ajneb97.model.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private MineChess plugin;
    private Map<UUID, PlayerData> players;
    private Map<String,UUID> playerNames;
    private PlayerDataBackupManager playerDataBackupManager;

    public PlayerDataManager(MineChess plugin){
        this.plugin = plugin;
        this.playerNames = new HashMap<>();
        this.players = new HashMap<>();
        this.playerDataBackupManager = new PlayerDataBackupManager(plugin);
    }

    public Map<UUID,PlayerData> getPlayers() {
        return players;
    }

    public void addPlayer(PlayerData p){
        players.put(p.getUuid(),p);
        playerNames.put(p.getName(), p.getUuid());
    }

    public PlayerData getPlayer(Player player, boolean create){
        PlayerData playerData = players.get(player.getUniqueId());
        if(playerData == null && create){
            playerData = new PlayerData(player.getUniqueId(),player.getName());
            addPlayer(playerData);
        }
        return playerData;
    }

    private void updatePlayerName(String oldName,String newName,UUID uuid){
        if(oldName != null){
            playerNames.remove(oldName);
        }
        playerNames.put(newName,uuid);
    }

    public PlayerData getPlayerByUUID(UUID uuid){
        return players.get(uuid);
    }

    private UUID getPlayerUUID(String name){
        return playerNames.get(name);
    }

    public PlayerData getPlayerByName(String name){
        UUID uuid = getPlayerUUID(name);
        return players.get(uuid);
    }

    public void removePlayerByUUID(UUID uuid){
        players.remove(uuid);
    }

    public void removePlayer(PlayerData playerData){
        players.remove(playerData.getUuid());
        playerNames.remove(playerData.getName());
    }

    public void manageJoin(Player player){
        // Load player data from file if exists
        // Always because of possible backup
        plugin.getConfigsManager().getPlayersConfigManager().loadConfig(player.getUniqueId(), playerDataFile -> {
            // Load backup
            if(playerDataFile != null && playerDataFile.getBackup() != null){
                playerDataBackupManager.addPlayerBackup(playerDataFile);
                playerDataBackupManager.restorePlayerDataBackup(player,true);
                playerDataBackupManager.clearPlayerDataBackup(player,true);
            }

            if(plugin.getMySQLConnection() != null) {
                MySQLConnection mySQLConnection = plugin.getMySQLConnection();
                UUID uuid = player.getUniqueId();
                mySQLConnection.getPlayer(uuid.toString(), playerDataSQL -> {
                    if (playerDataSQL != null) {
                        addPlayer(playerDataSQL);
                        //Update name if different
                        if (!playerDataSQL.getName().equals(player.getName())) {
                            updatePlayerName(playerDataSQL.getName(), player.getName(), player.getUniqueId());
                            playerDataSQL.setName(player.getName());
                            mySQLConnection.updatePlayerName(playerDataSQL);
                        }
                    }
                });
            }else{
                if(playerDataFile != null){
                    addPlayer(playerDataFile);
                    if(playerDataFile.getName() == null || !playerDataFile.getName().equals(player.getName())){
                        updatePlayerName(playerDataFile.getName(),player.getName(),player.getUniqueId());
                        playerDataFile.setName(player.getName());
                        playerDataFile.setModified(true);
                    }
                }
            }
        });
    }

    public void manageLeave(Player player){
        GamePlayerManager gamePlayerManager = plugin.getArenaManager().getGamePlayerManager();
        PlayerData playerData = getPlayer(player,false);

        if(gamePlayerManager.getGamePlayer(player) == null){
            // Not in game, must only save data file
            if(playerData != null){
                if(plugin.getMySQLConnection() == null) {
                    if(playerData.isModified()){
                        new BukkitRunnable(){
                            @Override
                            public void run() {
                                plugin.getConfigsManager().getPlayersConfigManager().saveConfig(playerData);
                            }
                        }.runTaskAsynchronously(plugin);
                    }
                }
                removePlayer(playerData);
            }
        }else{
            // In game, must save data file + backup
            gamePlayerManager.leaveServer(player); // Restore backup

            // Save player data into file and remove from map
            if(playerData != null){
                if(plugin.getMySQLConnection() == null) {
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            plugin.getConfigsManager().getPlayersConfigManager().saveConfig(playerData);
                        }
                    }.runTaskAsynchronously(plugin);
                }else{
                    playerDataBackupManager.clearPlayerDataBackup(player,true);
                }

                removePlayer(playerData);
            }else{
                playerDataBackupManager.clearPlayerDataBackup(player,true);
            }
        }
    }

    public int getWins(Player player){
        PlayerData playerData = getPlayer(player,false);
        if(playerData != null){
            return playerData.getWins();
        }
        return 0;
    }

    public int getLoses(Player player){
        PlayerData playerData = getPlayer(player,false);
        if(playerData != null){
            return playerData.getLoses();
        }
        return 0;
    }

    public int getTies(Player player){
        PlayerData playerData = getPlayer(player,false);
        if(playerData != null){
            return playerData.getTies();
        }
        return 0;
    }

    public String getTimePlayed(Player player){
        PlayerData playerData = getPlayer(player,false);
        if(playerData != null){
            return OtherUtils.getTimeFormat2(playerData.getMillisPlayed()/1000,plugin.getMessagesManager());
        }
        return OtherUtils.getTimeFormat2(0,plugin.getMessagesManager());
    }

    public void endGame(Player player,boolean win,boolean lose,long millisPlayed){
        PlayerData playerData = getPlayer(player,true);
        if(win){
            playerData.setWins(playerData.getWins()+1);
        }else if(lose){
            playerData.setLoses(playerData.getLoses()+1);
        }else{
            playerData.setTies(playerData.getTies()+1);
        }
        playerData.setMillisPlayed(playerData.getMillisPlayed()+millisPlayed);
        playerData.setModified(true);

        if(plugin.getMySQLConnection() != null){
            plugin.getMySQLConnection().updatePlayer(playerData);
        }
    }

    public PlayerDataBackupManager getPlayerDataBackupManager() {
        return playerDataBackupManager;
    }
}
