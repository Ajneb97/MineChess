package mc.ajneb97.managers;



import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import mc.ajneb97.MineChess;
import mc.ajneb97.juego.Estado;
import mc.ajneb97.juego.Jugador;
import mc.ajneb97.juego.Partida;
import mc.ajneb97.lib.fastboard.FastBoard;
import mc.ajneb97.otros.Utilidades;
import me.clip.placeholderapi.PlaceholderAPI;

public class ScoreboardAdmin {
	
	int taskID;
	private MineChess plugin;
	private final Map<UUID, FastBoard> boards = new HashMap<>();
	public ScoreboardAdmin(MineChess plugin){		
		this.plugin = plugin;		
	}
	
	public void crearScoreboards() {
	    BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
 	    taskID = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() { 
            	for(Player player : Bukkit.getOnlinePlayers()) {
            		actualizarScoreboard(player);
                }
            }
        },0, 20L);
	}
	
	protected void actualizarScoreboard(final Player player) {
		FileConfiguration config = plugin.getConfig();
		Partida partida = plugin.getPartidaJugador(player.getName());
		FastBoard board = boards.get(player.getUniqueId());
		if(partida != null) {
			if(board == null) {
				board = new FastBoard(player);
				board.updateTitle(ChatColor.translateAlternateColorCodes('&',config.getString("Messages.gameScoreboardTitle")));
				boards.put(player.getUniqueId(), board);
			}

			List<String> lista = config.getStringList("Messages.gameScoreboardBody");
			Jugador j1 = partida.getJugador1();
			Jugador j2 = partida.getJugador2();
			String nombre1 = "";
			String nombre2 = "";
			int puntos1 = 0;
			int puntos2 = 0;
			if(j1 != null) {
				nombre1 = j1.getJugador().getName();
				puntos1 = j1.getPuntos();
			}else {
				nombre1 = config.getString("Messages.gameScoreboardWaitingPlayer");
			}
			if(j2 != null) {
				nombre2 = j2.getJugador().getName();
				puntos2 = j2.getPuntos();
			}else {
				nombre2 = config.getString("Messages.gameScoreboardWaitingPlayer");
			}
			for(int i=0;i<lista.size();i++) {
				
				String message = ChatColor.translateAlternateColorCodes('&', lista.get(i).replace("%status%", getEstado(partida,config)).replace("%player_1%", nombre1)
						.replace("%player_2%", nombre2).replace("%player_1_points%", puntos1+"").replace("%player_2_points%", puntos2+""));
				if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI")!= null){
					message = PlaceholderAPI.setPlaceholders(player, message);
				}
				
				board.updateLine(i, message);
			}
		}else {
			if(board != null) {
				boards.remove(player.getUniqueId());
				board.delete();
			}
		}
	}
	
	private String getEstado(Partida partida,FileConfiguration config) {
		//Remplazar variables del %time%
		if(partida.getEstado().equals(Estado.ESPERANDO)) {
			return config.getString("Messages.statusWaiting");
		}else if(partida.getEstado().equals(Estado.COMENZANDO)) {
			int tiempo = partida.getTiempo();
			return config.getString("Messages.statusStarting").replace("%time%", Utilidades.getTiempo(tiempo));
		}else if(partida.getEstado().equals(Estado.TERMINANDO)) {
			int tiempo = partida.getTiempo();
			return config.getString("Messages.statusFinishing").replace("%time%", Utilidades.getTiempo(tiempo));
		}else {
			int tiempo = partida.getTiempo();
			return config.getString("Messages.statusIngame").replace("%time%", Utilidades.getTiempo(tiempo));
		}
	}

}
