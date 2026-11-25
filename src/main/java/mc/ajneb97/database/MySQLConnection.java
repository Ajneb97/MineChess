package mc.ajneb97.database;

import mc.ajneb97.MineChess;
import mc.ajneb97.manager.MessagesManager;
import mc.ajneb97.model.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class MySQLConnection {

    private MineChess plugin;
    private HikariConnection connection;
    private final Queue<Runnable> writeQueue = new ConcurrentLinkedQueue<>();
    private final Object writeLock = new Object();
    private final AtomicBoolean draining = new AtomicBoolean(false);
    private BukkitTask writeTask;
    private boolean shuttingDown;

    public MySQLConnection(MineChess plugin){
        this.plugin = plugin;
    }

    public void setupMySql(){
        FileConfiguration config = plugin.getConfigsManager().getMainConfigManager().getConfig();
        try {
            connection = new HikariConnection(config);
            createTables();
            startWriteWorker();
            Bukkit.getConsoleSender().sendMessage(MessagesManager.getLegacyColoredMessage(plugin.prefix+"&aSuccessfully connected to the Database."));
        }catch(Exception e) {
            Bukkit.getConsoleSender().sendMessage(MessagesManager.getLegacyColoredMessage(plugin.prefix+"&cError while connecting to the Database."));
        }
    }

    public Connection getConnection() throws SQLException {
        if(connection == null){
            throw new SQLException("MySQL connection is not initialized");
        }
        return connection.getHikari().getConnection();
    }

    private void enqueue(Runnable task){
        synchronized(writeLock){
            if(shuttingDown){
                return;
            }
            writeQueue.add(task);
            if(writeTask == null || writeTask.isCancelled()){
                startWriteWorkerLocked();
            }
        }
    }

    private void startWriteWorker(){
        synchronized(writeLock){
            startWriteWorkerLocked();
        }
    }

    private void startWriteWorkerLocked(){
        if(shuttingDown || (writeTask != null && !writeTask.isCancelled())){
            return;
        }
        writeTask = new BukkitRunnable(){
            @Override
            public void run(){
                if(shuttingDown || !draining.compareAndSet(false,true)){
                    return;
                }
                try{
                    Runnable queuedTask;
                    while((queuedTask = writeQueue.poll()) != null){
                        runWriteTask(queuedTask);
                    }
                }finally{
                    draining.set(false);
                }
            }
        }.runTaskTimerAsynchronously(plugin,1L,1L);
    }

    private void runWriteTask(Runnable task){
        try{
            task.run();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void shutdown(){
        synchronized(writeLock){
            shuttingDown = true;
            if(writeTask != null){
                writeTask.cancel();
                writeTask = null;
            }
        }

        while(draining.get()){
            try{
                Thread.sleep(1);
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
                break;
            }
        }

        Runnable queuedTask;
        while((queuedTask = writeQueue.poll()) != null){
            runWriteTask(queuedTask);
        }

        synchronized(writeLock){
            if(connection != null){
                connection.disable();
                connection = null;
            }
        }
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
        final String uuid = player.getUuid().toString();
        final String playerName = player.getName();
        final int wins = player.getWins();
        final int loses = player.getLoses();
        final int ties = player.getTies();
        final long millisPlayed = player.getMillisPlayed();
        enqueue(() -> {
            try(Connection connection = getConnection()){
                try(PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO minechess_players " +
                                "(UUID, PLAYER_NAME, WINS, LOSES, TIES, MILLIS_PLAYED) VALUE (?,?,?,?,?,?) " +
                                "ON DUPLICATE KEY UPDATE WINS = VALUES(WINS), LOSES = VALUES(LOSES), " +
                                "TIES = VALUES(TIES), MILLIS_PLAYED = VALUES(MILLIS_PLAYED)"
                )){

                    statement.setString(1, uuid);
                    statement.setString(2, playerName);
                    statement.setInt(3, wins);
                    statement.setInt(4, loses);
                    statement.setInt(5, ties);
                    statement.setLong(6, millisPlayed);
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void updatePlayerName(PlayerData player){
        final String uuid = player.getUuid().toString();
        final String playerName = player.getName();
        enqueue(() -> {
            try(Connection connection = getConnection()){
                try(PreparedStatement statement = connection.prepareStatement(
                        "UPDATE minechess_players SET " +
                                "PLAYER_NAME=? WHERE UUID=?")){

                    statement.setString(1, playerName);
                    statement.setString(2, uuid);
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
