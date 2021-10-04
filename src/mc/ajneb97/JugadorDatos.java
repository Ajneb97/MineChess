package mc.ajneb97;

import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;

import mc.ajneb97.mysql.MySQL;

public class JugadorDatos {

	private String player;
	private String uuid;
	private int wins;
	private int loses;
	private int ties;
	private long millisJugados;
	
	public JugadorDatos(String player, String uuid,int wins,int loses,int ties,long millisJugados) {
		this.uuid = uuid;
		this.player = player;
		this.wins = wins;
		this.loses = loses;
		this.ties = ties;
		this.millisJugados = millisJugados;
	}
	
	public void aumentarMillisJugados(long millis,FileConfiguration config,MineChess plugin) {
		this.millisJugados = this.millisJugados+millis;
		if(MySQL.isEnabled(config)) {
			MySQL.modificarJugador(plugin, this);
		}
	}
	
	public long getMillisJugados() {
		return this.millisJugados;
	}
	
	public String getUUID() {
		return uuid;
	}
	
	public String getPlayer() {
		return player;
	}

	public int getWins() {
		return wins;
	}

	public int getLoses() {
		return loses;
	}

	public int getTies() {
		return ties;
	}
	
	public void aumentarWins(FileConfiguration config,MineChess plugin) {
		this.wins++;
		if(MySQL.isEnabled(config)) {
			MySQL.modificarJugador(plugin, this);
		}
	}
	
	public void aumentarLoses(FileConfiguration config,MineChess plugin) {
		this.loses++;
		if(MySQL.isEnabled(config)) {
			MySQL.modificarJugador(plugin, this);
		}
	}
	
	public void aumentarTies(FileConfiguration config,MineChess plugin) {
		this.ties++;
		if(MySQL.isEnabled(config)) {
			MySQL.modificarJugador(plugin, this);
		}
	}
	
}
