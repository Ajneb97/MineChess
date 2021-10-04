package mc.ajneb97.managers;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitScheduler;

import mc.ajneb97.MineChess;
import mc.ajneb97.juego.Estado;
import mc.ajneb97.juego.Jugador;
import mc.ajneb97.juego.MovimientoPosible;
import mc.ajneb97.juego.Partida;
import mc.ajneb97.juego.Pieza;
import mc.ajneb97.otros.Utilidades;

public class CooldownParticulaPieza {

	int taskID;
	private MineChess plugin;
	private Jugador jugador;
	private Partida partida;
	private Pieza pieza;
	public CooldownParticulaPieza(MineChess plugin){		
		this.plugin = plugin;		
	}
	
	public void cooldownParticula(Jugador j,Partida partida,Pieza pieza){
		this.jugador = j;
		this.partida = partida;
		this.pieza = pieza;
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
 	    taskID = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
		public void run(){
			if(!ejecutarCooldownParticula()){
				Bukkit.getScheduler().cancelTask(taskID);
				return;
			}	
 		   }
 	   }, 0L, 5L);
	}
	
	protected boolean ejecutarCooldownParticula() {
		if(jugador != null) {
			if(partida != null && partida.getEstado().equals(Estado.JUGANDO)) {
				Pieza p = jugador.getPiezaObservada();
				if(p == null) {
					return false;
				}
				if(pieza == null) {
					return false;
				}
				if((p.getId() != pieza.getId())) {
					return false;
				}
					
				Location l = partida.getLocationDesdePieza(p);
				if(l == null) {
					return false;
				}
				double y = partida.getEsquina1().getY();
				Location nueva = l.clone().add(0.5,0,0.5);
				nueva.setY(y+1.35);
				
				if(nueva != null) {
					//Generar particulas
					int particles = 20;
			        float radius = (float) 1.5;
			        Location location1 = nueva.clone();
			        for (int i = 0; i < particles; i++) {
			            double angle, x, z;
			            angle = 2 * Math.PI * i / particles;
			            x = Math.cos(angle) * radius;
			            z = Math.sin(angle) * radius;
			            location1.add(x, 0, z);
			            Utilidades.generarParticula("FLAME", location1, 0.001F, 0.001F, 0.001F, 0.01F, 1, jugador.getJugador());
			            location1.subtract(x, 0, z);
			        } 
				}
				return true;
			}else {
				return false;
			}		
		}else {
			return false;
		}	
	}
	
	public void cooldownParticulaSeleccionada(Jugador j,Partida partida,Pieza pieza){
		this.jugador = j;
		this.partida = partida;
		this.pieza = pieza;
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
 	    taskID = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
		public void run(){
			if(!ejecutarCooldownParticulaSeleccionada()){
				Bukkit.getScheduler().cancelTask(taskID);
				return;
			}	
 		   }
 	   }, 0L, 5L);
	}
	
	protected boolean ejecutarCooldownParticulaSeleccionada() {
		if(jugador != null) {
			if(partida != null && partida.getEstado().equals(Estado.JUGANDO)) {
				Pieza p = jugador.getPiezaSeleccionada();
				if(p == null) {
					return false;
				}
				if(pieza == null) {
					return false;
				}
				if((p.getId() != pieza.getId())) {
					return false;
				}
				
				Location l = partida.getLocationDesdePieza(p);
				double y = partida.getEsquina1().getY();
				Location nueva = l.clone().add(0.5,0,0.5);
				nueva.setY(y+1.35);
				
				if(nueva != null) {
					//Generar particulas
					int particles = 20;
			        float radius = (float) 1.5;
			        Location location1 = nueva.clone();
			        for (int i = 0; i < particles; i++) {
			            double angle, x, z;
			            angle = 2 * Math.PI * i / particles;
			            x = Math.cos(angle) * radius;
			            z = Math.sin(angle) * radius;
			            location1.add(x, 0, z);
			            Utilidades.generarParticula("VILLAGER_HAPPY", location1, 0.001F, 0.001F, 0.001F, 0.001F, 1, jugador.getJugador());
			            location1.subtract(x, 0, z);
			        } 
				}
				return true;
			}else {
				return false;
			}		
		}else {
			return false;
		}	
	}
	
	public void cooldownParticulaMovimientosPosibles(Jugador j,Partida partida,Pieza pieza,final ArrayList<MovimientoPosible> locs){
		this.jugador = j;
		this.partida = partida;
		this.pieza = pieza;
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
 	    taskID = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
		public void run(){
			if(!ejecutarCooldownParticulaSeleccionada(locs)){
				Bukkit.getScheduler().cancelTask(taskID);
				return;
			}	
 		   }
 	   }, 0L, 5L);
	}
	
	protected boolean ejecutarCooldownParticulaSeleccionada(ArrayList<MovimientoPosible> locs) {
		if(jugador != null) {
			if(partida != null && partida.getEstado().equals(Estado.JUGANDO)) {
				Pieza p = jugador.getPiezaSeleccionada();
				if(p == null) {
					return false;
				}
				if(pieza == null) {
					return false;
				}
				if((p.getId() != pieza.getId())) {
					return false;
				}
				if(locs != null) {
					for(MovimientoPosible loc : locs) {
						Location nueva = loc.getLocation().clone().add(0.5,0,0.5);
						double y = partida.getEsquina1().getY();
						nueva.setY(y+1.35);
						
						Utilidades.generarParticula("VILLAGER_HAPPY", nueva.clone().add(0.5,0,0.5), 0.001F, 0.001F, 0.001F, 0.001F, 1, jugador.getJugador());
						Utilidades.generarParticula("VILLAGER_HAPPY", nueva.clone().add(0.5,0,-0.5), 0.001F, 0.001F, 0.001F, 0.001F, 1, jugador.getJugador());
						Utilidades.generarParticula("VILLAGER_HAPPY", nueva.clone().add(-0.5,0,0.5), 0.001F, 0.001F, 0.001F, 0.001F, 1, jugador.getJugador());
						Utilidades.generarParticula("VILLAGER_HAPPY", nueva.clone().add(-0.5,0,-0.5), 0.001F, 0.001F, 0.001F, 0.001F, 1, jugador.getJugador());
					}
				}
				
				return true;
			}else {
				return false;
			}		
		}else {
			return false;
		}	
	}
	
	public void cooldownParticulaObservarMovimiento(Jugador j,Partida partida,final Location loc){
		this.jugador = j;
		this.partida = partida;
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
 	    taskID = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
		public void run(){
			if(!ejecutarCooldownObservarMovimiento(loc)){
				Bukkit.getScheduler().cancelTask(taskID);
				return;
			}	
 		   }
 	   }, 0L, 5L);
	}
	
	protected boolean ejecutarCooldownObservarMovimiento(Location loc) {
		if(jugador != null) {
			if(partida != null && partida.getEstado().equals(Estado.JUGANDO)) {
				int[] celdaS = jugador.getCeldaObservada();
				if(celdaS == null) {
					return false;
				}
				
				if(loc != null) {
					Location nueva = loc.clone().add(0.5,0,0.5);
					double y = partida.getEsquina1().getY();
					nueva.setY(y+1.35);
					
					Utilidades.generarParticula("FLAME", nueva.clone().add(0.5,0,0.5), 0.001F, 0.001F, 0.001F, 0.01F, 1, jugador.getJugador());
					Utilidades.generarParticula("FLAME", nueva.clone().add(0.5,0,-0.5), 0.001F, 0.001F, 0.001F, 0.01F, 1, jugador.getJugador());
					Utilidades.generarParticula("FLAME", nueva.clone().add(-0.5,0,0.5), 0.001F, 0.001F, 0.001F, 0.01F, 1, jugador.getJugador());
					Utilidades.generarParticula("FLAME", nueva.clone().add(-0.5,0,-0.5), 0.001F, 0.001F, 0.001F, 0.01F, 1, jugador.getJugador());
				}
				
				return true;
			}else {
				return false;
			}		
		}else {
			return false;
		}	
	}
}
