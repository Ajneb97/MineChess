package mc.ajneb97.manager;

import mc.ajneb97.MineChess;
import mc.ajneb97.database.MySQLConnection;
import mc.ajneb97.utils.OtherUtils;
import mc.ajneb97.model.data.PlayerData;
import org.bukkit.entity.Player;

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
        this.playerDataBackupManager = new PlayerDataBackupManager(plugin);
    }

    public Map<UUID,PlayerData> getPlayers() {
        return players;
    }

    public void setPlayers(Map<UUID,PlayerData> players) {
        this.players = players;
        for(Map.Entry<UUID, PlayerData> entry : players.entrySet()){
            playerNames.put(entry.getValue().getName(),entry.getKey());
        }
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
        playerNames.remove(oldName);
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

    public void setJoinPlayerData(Player player){
        if(plugin.getMySQLConnection() != null) {
            MySQLConnection mySQLConnection = plugin.getMySQLConnection();
            UUID uuid = player.getUniqueId();
            mySQLConnection.getPlayer(uuid.toString(), playerData -> {
                removePlayerByUUID(uuid); //Remove data if already exists
                if (playerData != null) {
                    addPlayer(playerData);
                    //Update name if different
                    if (!playerData.getName().equals(player.getName())) {
                        updatePlayerName(playerData.getName(), player.getName(), player.getUniqueId());
                        playerData.setName(player.getName());
                        mySQLConnection.updatePlayerName(playerData);
                    }
                }
            });
        }else{
            PlayerData playerData = getPlayer(player,false);
            if(playerData != null){
                if(playerData.getName() == null || !playerData.getName().equals(player.getName())){
                    updatePlayerName(playerData.getName(),player.getName(),player.getUniqueId());
                    playerData.setName(player.getName());
                    playerData.setModified(true);
                }
            }
        }

        playerDataBackupManager.restorePlayerDataBackup(player,true,true);
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
