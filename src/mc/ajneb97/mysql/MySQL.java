package mc.ajneb97.mysql;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import mc.ajneb97.JugadorDatos;
import mc.ajneb97.MineChess;

public class MySQL {
	
	public static boolean isEnabled(FileConfiguration config){
		if(config.getString("Config.mysql_database.enabled").equals("true")){
			return true;
		}else{
			return false;
		}
	}
	
	public static void createTable(MineChess plugin) {
        try {
        	PreparedStatement statement = plugin.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS minechess_data (`UUID` varchar(200), `PLAYER_NAME` varchar(50), `WINS` INT(5), `LOSES` INT(5), `TIES` INT(5), `PLAYED_TIME` LONG )");
            statement.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	public static ArrayList<JugadorDatos> getJugadores(MineChess plugin){
		ArrayList<JugadorDatos> jugadores = new ArrayList<JugadorDatos>();
		try {
			PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM minechess_data");
			ResultSet resultado = statement.executeQuery();
			while(resultado.next()){	
				String uuid = resultado.getString("UUID");
				int wins = resultado.getInt("WINS");
				int loses = resultado.getInt("LOSES");
				int ties = resultado.getInt("TIES");
				long timePlayed = resultado.getLong("PLAYED_TIME");
				String name = resultado.getString("PLAYER_NAME");
				jugadores.add(new JugadorDatos(name,uuid,wins,loses,ties,timePlayed));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jugadores;
	}

	public static void getJugador(final String nombre,final MineChess plugin,final MySQLJugadorCallback callback){
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
            	int wins = 0;
            	int loses = 0;
            	int ties = 0;
            	long timePlayed = 0;
            	String uuid = "";
            	boolean encuentra = false;
        		try {
        			PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM minechess_data WHERE player_name=?");
        			statement.setString(1, nombre);
        			ResultSet resultado = statement.executeQuery();
        			if(resultado.next()){	
        				uuid = resultado.getString("UUID");
        				wins = resultado.getInt("WINS");
        				loses = resultado.getInt("LOSES");
        				ties = resultado.getInt("TIES");
        				timePlayed = resultado.getLong("PLAYED_TIME");
        				encuentra = true;
        			}
        		} catch (SQLException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        		if(encuentra) {
        			final JugadorDatos j = new JugadorDatos(nombre,uuid,wins,loses,ties,timePlayed);
        			Bukkit.getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            callback.alTerminar(j);
                        }
                    });
        		}else {
        			Bukkit.getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            callback.alTerminar(null);
                        }
                    });
        		}
            }
		});
	}

	public static void crearJugador(final MineChess plugin, final String name, final String uuid){
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
            	try{
        			PreparedStatement insert = plugin.getConnection()
        					.prepareStatement("INSERT INTO minechess_data (UUID,PLAYER_NAME,WINS,LOSES,TIES,PLAYED_TIME) VALUE (?,?,?,?,?,?)");
        			insert.setString(1, uuid);
        			insert.setString(2, name);
        			insert.setInt(3, 0);
        			insert.setInt(4, 0);
        			insert.setInt(5, 0);
        			insert.setLong(6, 0);
        			insert.executeUpdate();
        		} catch (SQLException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
            }
		});
	}
	
	public static void modificarJugador(final MineChess plugin,final JugadorDatos j){
		final String uuid = j.getUUID();
		final String name = j.getPlayer();
		final int wins = j.getWins();
		final int loses = j.getLoses();
		final int ties = j.getTies();
		final long timePlayed = j.getMillisJugados();
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
            	try {
            		PreparedStatement statement = plugin.getConnection().prepareStatement("UPDATE minechess_data SET player_name=?, wins=?, loses=?, ties=?, played_time=? WHERE (uuid=?)");
            		statement.setString(1, name);
    				statement.setInt(2, wins);
    				statement.setInt(3, loses);
    				statement.setInt(4, ties);
    				statement.setLong(5, timePlayed);
    				statement.setString(6, uuid);
    				statement.executeUpdate();
        		} catch (SQLException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
            }
		});	
	}
}
