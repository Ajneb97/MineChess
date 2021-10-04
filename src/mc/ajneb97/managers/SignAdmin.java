package mc.ajneb97.managers;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import mc.ajneb97.MineChess;
import mc.ajneb97.juego.Estado;
import mc.ajneb97.juego.Partida;

public class SignAdmin {

	int taskID;
	private MineChess plugin;
	public SignAdmin(MineChess plugin){		
		this.plugin = plugin;		
	}
	
	public void actualizarSigns() {
	    BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
	    final FileConfiguration config = plugin.getConfig();
 	    taskID = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() { 
            	ejecutarActualizarSigns(config);
            }
        },0, 40L);
	}

	protected void ejecutarActualizarSigns(FileConfiguration config) {
		FileConfiguration signs = plugin.getSigns();
		if(signs.contains("Signs")) {
			for(String arena : signs.getConfigurationSection("Signs").getKeys(false)) {
				Partida partida = plugin.getPartida(arena);
				if(partida != null) {
					int x = Integer.valueOf(signs.getString("Signs."+arena+".x"));
					int y = Integer.valueOf(signs.getString("Signs."+arena+".y"));
					int z = Integer.valueOf(signs.getString("Signs."+arena+".z"));
					World world = Bukkit.getWorld(signs.getString("Signs."+arena+".world"));
					if(world != null) {
						int chunkX = x >> 4;
						int chunkZ = z >> 4;
						if(world.isChunkLoaded(chunkX, chunkZ)) {
							Block block = world.getBlockAt(x,y,z);
							if(block.getType().name().contains("SIGN")) {
								
								Sign sign = (Sign) block.getState();
								String estado = "";
								if(partida.estaIniciada() && !partida.getEstado().equals(Estado.TERMINANDO)) {
									estado = config.getString("Messages.signStatusIngame");
								}else if(partida.getEstado().equals(Estado.COMENZANDO)) {
									estado = config.getString("Messages.signStatusStarting");
								}else if(partida.getEstado().equals(Estado.ESPERANDO)) {
									estado = config.getString("Messages.signStatusWaiting");
								}else if(partida.getEstado().equals(Estado.DESACTIVADA)) {
									estado = config.getString("Messages.signStatusDisabled");
								}else if(partida.getEstado().equals(Estado.TERMINANDO)) {
									estado = config.getString("Messages.signStatusFinishing");
								}
								
								List<String> lista = config.getStringList("Messages.signFormat");
								for(int i=0;i<lista.size();i++) {
									sign.setLine(i, ChatColor.translateAlternateColorCodes('&', lista.get(i).replace("%arena%", arena)
											.replace("%status%", estado).replace("%current_players%", partida.getCantidadActualJugadores()+"")
											.replace("%max_players%", "2")));
								}
								sign.update();
							}
						}
					}
				}
			}
		}
		
	}
}
