package mc.ajneb97.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import mc.ajneb97.MineChess;

public class ConexionMySQL {

	private Connection connection;
	private String host;
	private String database;
	private String username;
	private String password;
	private int port;
	
	public void setupMySql(MineChess plugin,FileConfiguration config){
		host = config.getString("Config.mysql_database.host");
		port = Integer.valueOf(config.getString("Config.mysql_database.port"));
		database = config.getString("Config.mysql_database.database");		
		username = config.getString("Config.mysql_database.username");
		password = config.getString("Config.mysql_database.password");
		mySqlAbrirConexion();
		MySQL.createTable(plugin);
	}

	public String getDatabase() {
		return this.database;
	}
	
	private void mySqlAbrirConexion(){
		try {
			synchronized(this){
				if(getConnection() != null && !getConnection().isClosed()){
					Bukkit.getConsoleSender().sendMessage(MineChess.prefix+ChatColor.RED + "Error while connecting to the Database.");
					return;
				}
				Class.forName("com.mysql.jdbc.Driver");
				setConnection(DriverManager.getConnection("jdbc:mysql://"+this.host+":"+this.port+"/"+this.database,this.username,this.password));
				
				Bukkit.getConsoleSender().sendMessage(MineChess.prefix+ChatColor.GREEN + "Successfully connected to the Database.");
				return;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
	}
	
	
	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
}
