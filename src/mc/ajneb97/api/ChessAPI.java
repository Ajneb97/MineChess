package mc.ajneb97.api;

import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import mc.ajneb97.JugadorDatos;
import mc.ajneb97.MineChess;
import mc.ajneb97.juego.Estado;
import mc.ajneb97.juego.Partida;
import mc.ajneb97.otros.Utilidades;

public class ChessAPI {

	private static MineChess plugin;
	
	public ChessAPI(MineChess plugin) {
		this.plugin = plugin;
	}
	
	public static int getWins(String player) {
		JugadorDatos jugador = plugin.getJugador(player);
		if(jugador != null) {
			return jugador.getWins();
		}else {
			return 0;
		}
	}
	
	public static int getLoses(String player) {
		JugadorDatos jugador = plugin.getJugador(player);
		if(jugador != null) {
			return jugador.getLoses();
		}else {
			return 0;
		}
	}
	
	public static int getTies(String player) {
		JugadorDatos jugador = plugin.getJugador(player);
		if(jugador != null) {
			return jugador.getTies();
		}else {
			return 0;
		}
	}
	
	public static long getPlayedTime(String player) {
		JugadorDatos jugador = plugin.getJugador(player);
		if(jugador != null) {
			return jugador.getMillisJugados();
		}else {
			return 0;
		}
	}
	
	public static String getPlayedTimeFormatted(String player) {
		JugadorDatos jugador = plugin.getJugador(player);
		if(jugador != null) {
			long millis = jugador.getMillisJugados();
			String formatted = Utilidades.getTiempoJugado(millis);
			return formatted;
		}else {
			return "00:00";
		}
	}
	
	public static int getPlayersArena(String arena) {
		Partida partida = plugin.getPartida(arena);
		if(partida != null) {
			return partida.getCantidadActualJugadores();
		}else {
			return 0;
		}
	}
	
	public static String getStatusArena(String arena) {
		Partida partida = plugin.getPartida(arena);
		FileConfiguration config = plugin.getConfig();
		if(partida != null) {
			if(partida.getEstado().equals(Estado.COMENZANDO)) {
				return config.getString("Messages.signStatusStarting");
			}else if(partida.getEstado().equals(Estado.DESACTIVADA)) {
				return config.getString("Messages.signStatusDisabled");
			}else if(partida.getEstado().equals(Estado.ESPERANDO)) {
				return config.getString("Messages.signStatusWaiting");
			}else if(partida.getEstado().equals(Estado.JUGANDO)) {
				return config.getString("Messages.signStatusIngame");
			}else {
				return config.getString("Messages.signStatusFinishing");
			}
		}else {
			return null;
		}
	}
	
	public static ArrayList<ChessPlayer> getPlayerData(){
		ArrayList<JugadorDatos> jugadores = plugin.getJugadores();
		ArrayList<ChessPlayer> players = new ArrayList<ChessPlayer>();
		for(JugadorDatos j : jugadores) {
			players.add(new ChessPlayer(j.getPlayer(),j.getWins(),j.getLoses(),j.getTies(),j.getMillisJugados()));
		}
		return players;
	}
}
