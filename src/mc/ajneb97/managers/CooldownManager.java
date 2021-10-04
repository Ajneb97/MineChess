package mc.ajneb97.managers;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitScheduler;

import mc.ajneb97.MineChess;
import mc.ajneb97.juego.Estado;
import mc.ajneb97.juego.Jugador;
import mc.ajneb97.juego.Partida;
import mc.ajneb97.lib.actionbarapi.ActionBarAPI;

public class CooldownManager {

	int taskID;
	int tiempo;
	private Partida partida;
	private MineChess plugin;
	public CooldownManager(MineChess plugin){		
		this.plugin = plugin;		
	}
	
	public void cooldownComenzarJuego(Partida partida,int cooldown){
		this.partida = partida;
		this.tiempo = cooldown;
		partida.setTiempo(tiempo);
		final FileConfiguration config = plugin.getConfig();
		final String prefix = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.prefix"));
		ArrayList<Jugador> jugadores = partida.getJugadores();
		for(int i=0;i<jugadores.size();i++) {
			jugadores.get(i).getJugador().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.arenaStartingMessage").replace("%time%", tiempo+"")));
		}
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
 	    taskID = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
		public void run(){
			if(!ejecutarComenzarJuego(config,prefix)){
				Bukkit.getScheduler().cancelTask(taskID);
				return;
			}	
 		   }
 	   }, 0L, 20L);
	}
	
	protected boolean ejecutarComenzarJuego(FileConfiguration config,String prefix) {
		if(partida != null && partida.getEstado().equals(Estado.COMENZANDO)) {
			if(tiempo <= 5 && tiempo > 0) {
				ArrayList<Jugador> jugadores = partida.getJugadores();
				for(int i=0;i<jugadores.size();i++) {
					jugadores.get(i).getJugador().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.arenaStartingMessage").replace("%time%", tiempo+"")));
				}
				partida.disminuirTiempo();
				tiempo--;
				return true;
			}else if(tiempo <= 0) {
				PartidaManager.iniciarPartida(partida,plugin);
				return false;
			}else {
				partida.disminuirTiempo();
				tiempo--;
				return true;
			}
		}else {
			ArrayList<Jugador> jugadores = partida.getJugadores();
			for(int i=0;i<jugadores.size();i++) {
				jugadores.get(i).getJugador().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.gameStartingCancelled")));
			}
			return false;
		}
	}
	
	public void cooldownJuego(Partida partida,int tiempoMaximo){
		this.partida = partida;
		this.tiempo = tiempoMaximo;
		partida.setTiempoMaximo(tiempoMaximo);
		partida.setTiempo(tiempo);
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
 	    taskID = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
		public void run(){
			if(!ejecutarJuego()){
				Bukkit.getScheduler().cancelTask(taskID);
				return;
			}	
 		   }
 	   }, 0L, 20L);
	}
	
	protected boolean ejecutarJuego() {
		if(partida != null && partida.getEstado().equals(Estado.JUGANDO)) {
			partida.disminuirTiempo();
			if(tiempo == 0) {
				PartidaManager.iniciarFaseFinalizacion(partida, plugin,partida.getGanador());
				return false;
			}else {
				tiempo--;
				return true;
			}
		}else {
			return false;
		}
	}
	
	public void cooldownFaseFinalizacion(Partida partida,int cooldown,final Jugador ganador){
		this.partida = partida;
		this.tiempo = cooldown;
		partida.setTiempo(tiempo);
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
 	    taskID = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
		public void run(){
			if(!ejecutarComenzarFaseFinalizacion(ganador)){
				Bukkit.getScheduler().cancelTask(taskID);
				return;
			}	
 		   }
 	   }, 0L, 20L);
	}
	
	protected boolean ejecutarComenzarFaseFinalizacion(Jugador ganador) {
		if(partida != null && partida.getEstado().equals(Estado.TERMINANDO)) {
			partida.disminuirTiempo();
			if(tiempo == 0) {
				PartidaManager.finalizarPartida(partida,ganador,plugin,false);
				return false;
			}else {
				tiempo--;
				if(partida.getJugador1() != null && partida.getJugador1().equals(ganador)) {
					PartidaManager.lanzarFuegos(ganador);
				}else if(partida.getJugador2() != null && partida.getJugador2().equals(ganador)) {
					PartidaManager.lanzarFuegos(ganador);
				}
//				if(ganador != null && partida.getCantidadActualJugadores() != 0) {
//					PartidaManager.lanzarFuegos(ganador);
//				}
				return true;
			}
		}else {
			return false;
		}
	}
	
	public void cooldownActionbar(Partida partida,final FileConfiguration config){
		this.partida = partida;
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
 	    taskID = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
		public void run(){
			if(!ejecutarActionbar(config)){
				Bukkit.getScheduler().cancelTask(taskID);
				return;
			}	
 		   }
 	   }, 0L, 20L);
	}

	protected boolean ejecutarActionbar(FileConfiguration config) {
		if(partida != null && partida.getEstado().equals(Estado.JUGANDO)) {
			ArrayList<Jugador> jugadores = partida.getJugadores();
			Jugador turno = partida.getTurno();
			String contrincante = "";
			if(turno.equals(partida.getJugador1())) {
				contrincante = partida.getJugador1().getJugador().getName();
			}else {
				contrincante = partida.getJugador2().getJugador().getName();
			}	
			for(int i=0;i<jugadores.size();i++) {
				if(turno != null) {
					String msg = "";
					if(jugadores.get(i).getJugador().getName().equals(turno.getJugador().getName())) {
						msg = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.actionbarYourTurn").replace("%time%", turno.getTiempo()+""));
					}else {
						msg = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.actionbarPlayerTurn").replace("%player%", contrincante)
								.replace("%time%", turno.getTiempo()+""));
					}
					ActionBarAPI.sendActionBar(jugadores.get(i).getJugador(), msg);
				}
			}
			return true;
		}else {
			return false;
		}
		
	}
}
