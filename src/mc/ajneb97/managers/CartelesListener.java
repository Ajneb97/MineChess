package mc.ajneb97.managers;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import mc.ajneb97.MineChess;
import mc.ajneb97.juego.Estado;
import mc.ajneb97.juego.Partida;
import mc.ajneb97.otros.Utilidades;

public class CartelesListener implements Listener{

	private MineChess plugin;
	public CartelesListener(MineChess plugin) {
		this.plugin = plugin;
	}
	
	//Al poner Cartel
		@EventHandler
		public void crearCartel(SignChangeEvent event ) {
			Player jugador = event.getPlayer();
			if(jugador.isOp() || jugador.hasPermission("chess.admin")) {
				if(event.getLine(0).equals("[Chess]")) {
					String arena = event.getLine(1);
					if(arena != null && plugin.getPartida(arena) != null) {
						FileConfiguration config = plugin.getConfig();
						Partida partida = plugin.getPartida(arena);
						String estado = "";
						if(partida.estaIniciada()) {
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
							event.setLine(i, ChatColor.translateAlternateColorCodes('&', lista.get(i).replace("%arena%", arena)
									.replace("%status%", estado).replace("%current_players%", partida.getCantidadActualJugadores()+"")
									.replace("%max_players%", "2")));
						}
						
						FileConfiguration signs = plugin.getSigns();
						signs.set("Signs."+arena+".x", event.getBlock().getX()+"");
						signs.set("Signs."+arena+".y", event.getBlock().getY()+"");
						signs.set("Signs."+arena+".z", event.getBlock().getZ()+"");
						signs.set("Signs."+arena+".world", event.getBlock().getWorld().getName()+"");
						plugin.saveSigns();
					}
				}
			}
		}
		
		@EventHandler
		public void eliminarCartel(BlockBreakEvent event ) {
			Player jugador = event.getPlayer();
			Block block = event.getBlock();
			if(jugador.isOp() || jugador.hasPermission("chess.admin")) {
				if(block.getType().name().contains("SIGN")) {
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
									if(block.getX() == x && block.getY() == y && block.getZ() == z && world.getName().equals(block.getWorld().getName())) {
										signs.set("Signs."+arena, null);
										plugin.saveSigns();
										break;
									}
								}
							}
						}
					}
					
				}
			}
		}
		
		@EventHandler
		public void entrarPartida(PlayerInteractEvent event) {
			final Player jugador = event.getPlayer();
			Block block = event.getClickedBlock();
			if(block != null && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				if(block.getType().name().contains("SIGN")) {
					FileConfiguration signs = plugin.getSigns();
					if(signs.contains("Signs")) {
						for(String arena : signs.getConfigurationSection("Signs").getKeys(false)) {
							final Partida partida = plugin.getPartida(arena);
							if(partida != null) {
								int x = Integer.valueOf(signs.getString("Signs."+arena+".x"));
								int y = Integer.valueOf(signs.getString("Signs."+arena+".y"));
								int z = Integer.valueOf(signs.getString("Signs."+arena+".z"));
								World world = Bukkit.getWorld(signs.getString("Signs."+arena+".world"));
								if(world != null) {
									if(block.getX() == x && block.getY() == y && block.getZ() == z && world.getName().equals(block.getWorld().getName())) {
										final FileConfiguration config = plugin.getConfig();
										final String prefix = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.prefix"));
										 if(partida.estaActivada()) {
											   if(plugin.getPartidaJugador(jugador.getName()) == null) {
												   if(!partida.estaIniciada()) {
													   if(!partida.estaLlena()) {
														   if(!Utilidades.pasaConfigInventario(jugador, config)) {
															   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.errorClearInventory"))); 
															   return;
														   }
														   PartidaManager.jugadorEntra(partida, jugador,plugin);
													   }else {
														   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.arenaIsFull"))); 
													   }
												   }else {
													   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.arenaAlreadyStarted"))); 
												   }
											   }else {
												   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.alreadyInArena"))); 
											   }
										   }else {
											   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.arenaDisabledError"))); 
										   }
										break;
									}
								}
							}
						}
					}
					
				}
			}
		}
}
