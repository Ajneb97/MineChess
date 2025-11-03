package mc.ajneb97.database;

import mc.ajneb97.MineChess;
import mc.ajneb97.manager.MessagesManager;
import mc.ajneb97.model.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MySQLConnection {

    private MineChess plugin;
    private HikariConnection connection;

    public MySQLConnection(MineChess plugin){
        this.plugin = plugin;
    }

    public void setupMySql(){
        FileConfiguration config = plugin.getConfigsManager().getMainConfigManager().getConfig();
        try {
            connection = new HikariConnection(config);
            connection.getHikari().getConnection();
            createTables();
            loadData();
            Bukkit.getConsoleSender().sendMessage(MessagesManager.getLegacyColoredMessage(plugin.prefix+"&aSuccessfully connected to the Database."));
        }catch(Exception e) {
            Bukkit.getConsoleSender().sendMessage(MessagesManager.getLegacyColoredMessage(plugin.prefix+"&cError while connecting to the Database."));
        }
    }

    public Connection getConnection() {
        try {
            return connection.getHikari().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void loadData(){
        Map<UUID, PlayerData> playerMap = new HashMap<>();
        try(Connection connection = getConnection()){
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT UUID, PLAYER_NAME, WINS, LOSES, TIES, MILLIS_PLAYED " +
                            "FROM minechess_players");

            ResultSet result = statement.executeQuery();
            while(result.next()){
                UUID uuid = UUID.fromString(result.getString("UUID"));
                String playerName = result.getString("PLAYER_NAME");
                int wins = result.getInt("WINS");
                int loses = result.getInt("LOSES");
                int ties = result.getInt("TIES");
                long millisPlayed = result.getLong("MILLIS_PLAYED");

                PlayerData player = playerMap.get(uuid);
                if(player == null){
                    //Create and add it
                    player = new PlayerData(uuid,playerName);
                    playerMap.put(uuid, player);
                }

                player.setWins(wins);
                player.setLoses(loses);
                player.setTies(ties);
                player.setMillisPlayed(millisPlayed);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }



        plugin.getPlayerDataManager().setPlayers(playerMap);
    }

    public void createTables() {
        try(Connection connection = getConnection()){
            PreparedStatement statement1 = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS minechess_players" +
                    " (UUID varchar(36) NOT NULL, " +
                    " PLAYER_NAME varchar(50), " +
                    " WINS INT, " +
                    " LOSES INT, " +
                    " TIES INT, " +
                    " MILLIS_PLAYED BIGINT, " +
                    " PRIMARY KEY ( UUID ))"
            );
            statement1.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getPlayer(String uuid,PlayerCallback callback){
        new BukkitRunnable(){
            @Override
            public void run() {
                PlayerData player = null;
                try(Connection connection = getConnection()){
                    PreparedStatement statement = connection.prepareStatement(
                            "SELECT UUID, PLAYER_NAME, WINS, LOSES, TIES, MILLIS_PLAYED " +
                                    "FROM minechess_players " +
                                    "WHERE UUID = ?");
                    statement.setString(1, uuid);
                    ResultSet result = statement.executeQuery();

                    while(result.next()){
                        UUID uuid = UUID.fromString(result.getString("UUID"));
                        String playerName = result.getString("PLAYER_NAME");
                        int wins = result.getInt("WINS");
                        int loses = result.getInt("LOSES");
                        int ties = result.getInt("TIES");
                        long millisPlayed = result.getLong("MILLIS_PLAYED");

                        if(player == null){
                            player = new PlayerData(uuid,playerName);
                        }

                        player.setWins(wins);
                        player.setLoses(loses);
                        player.setTies(ties);
                        player.setMillisPlayed(millisPlayed);
                    }

                    PlayerData finalPlayer = player;
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            callback.onDone(finalPlayer);
                        }
                    }.runTask(plugin);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void updatePlayer(PlayerData player){
        new BukkitRunnable(){
            @Override
            public void run() {
                try(Connection connection = getConnection()){
                    PreparedStatement statement = connection.prepareStatement(
                            "INSERT INTO minechess_players " +
                                    "(UUID, PLAYER_NAME, WINS, LOSES, TIES, MILLIS_PLAYED) VALUE (?,?,?,?,?,?) " +
                                    "ON DUPLICATE KEY UPDATE WINS = VALUES(WINS), LOSES = VALUES(LOSES), " +
                                    "TIES = VALUES(TIES), MILLIS_PLAYED = VALUES(MILLIS_PLAYED)"
                    );

                    statement.setString(1, player.getUuid().toString());
                    statement.setString(2, player.getName());
                    statement.setInt(3,player.getWins());
                    statement.setInt(4,player.getLoses());
                    statement.setInt(5,player.getTies());
                    statement.setLong(6,player.getMillisPlayed());
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void updatePlayerName(PlayerData player){
        new BukkitRunnable(){
            @Override
            public void run() {
                try(Connection connection = getConnection()){
                    PreparedStatement statement = connection.prepareStatement(
                            "UPDATE minechess_players SET " +
                                    "PLAYER_NAME=? WHERE UUID=?");

                    statement.setString(1, player.getName());
                    statement.setString(2, player.getUuid().toString());
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }
}
