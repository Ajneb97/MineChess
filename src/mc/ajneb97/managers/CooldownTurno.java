package mc.ajneb97.managers;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import mc.ajneb97.MineChess;
import mc.ajneb97.juego.Estado;
import mc.ajneb97.juego.Jugador;
import mc.ajneb97.juego.Partida;

public class CooldownTurno {

	private Partida partida;
	int taskID;
	int tiempo;
	private MineChess plugin;
	public CooldownTurno(MineChess plugin){		
		this.plugin = plugin;		
	}
	
	public void cooldown(Partida partida,int tiempo,final String accion){
		this.partida = partida;
		Jugador turno = partida.getTurno();
		this.tiempo = tiempo;	
		turno.setTiempo(tiempo);
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
 	    taskID = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
		public void run(){
			if(!ejecutarCooldown(accion)){
				Bukkit.getScheduler().cancelTask(taskID);
				return;
			}	
 		   }
 	   }, 0L, 20L);
	}

	protected boolean ejecutarCooldown(String accion) {
		if(partida != null && partida.getEstado().equals(Estado.JUGANDO)) {
			Jugador turno = partida.getTurno();
			if(turno.getTiempo() > 0) {
				turno.disminuirTiempo();
			}else {
				if(accion.equalsIgnoreCase("pass")) {
					PartidaManager.cambiarTurno(partida, plugin.getConfig(), plugin);
				}else {
					//mover
					PartidaManager.moverPiezaAleatoria(partida, plugin);
				}
				
			}
			return true;
		}else {
			return false;
		}
	}
}
