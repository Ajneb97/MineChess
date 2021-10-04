package mc.ajneb97;




import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import mc.ajneb97.juego.Estado;
import mc.ajneb97.juego.Jugador;
import mc.ajneb97.juego.Partida;
import mc.ajneb97.juego.Tablero;
import mc.ajneb97.managers.PartidaManager;
import mc.ajneb97.mysql.MySQL;
import mc.ajneb97.mysql.MySQLJugadorCallback;
import mc.ajneb97.otros.Utilidades;
import net.md_5.bungee.api.ChatColor;



public class Comando implements CommandExecutor {
	
	private MineChess plugin;
	public Comando(MineChess plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
	   if (!(sender instanceof Player)){
		   FileConfiguration config = plugin.getConfig();
		   String prefix = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.prefix"));
		   if(args.length > 0) {
			   if(args[0].equalsIgnoreCase("reload")) {
				   plugin.reloadConfig();
				   sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.configReloaded")));
			   }
		   }
		   
		   return false;   	
	   }
	   Player jugador = (Player)sender;
	   FileConfiguration config = plugin.getConfig();
	   String prefix = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.prefix"));
	   if(args.length > 0) {
		   if(args[0].equalsIgnoreCase("create")) {
			   // /chess create <arena>
			   if(jugador.isOp() || jugador.hasPermission("chess.admin")) {
				   if(args.length >= 2) {
					   if(plugin.getPartida(args[1]) == null) {
						   if(!config.contains("MainLobby")) {
							   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.noMainLobby")));  
							   return true;
						   }
						   
						   Partida partida = new Partida(args[1]);
						   plugin.agregarPartida(partida);
						   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.arenaCreated").replace("%arena%", args[1]))); 
					   }else {
						   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.arenaAlreadyExists")));  
					   }
				   }else {
					   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.commandCreateErrorUse"))); 
				   }
			   }else {
				   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.noPermissions")));
			   }
		   }else if(args[0].equalsIgnoreCase("build")) {
			// /chess build <arena>
			   if(jugador.isOp() || jugador.hasPermission("chess.admin")) {
				   if(args.length >= 2) {
					   Partida partida = plugin.getPartida(args[1]);
					   if(partida != null) {
						   //161 8 9 -> 184 8 32
						   Tablero.construirTablero(jugador.getLocation());
						   Location esquina1 = jugador.getLocation().clone().add(0,-1,0);
						   Tablero.crearPiezas(esquina1);
						   partida.setEsquina1(new Location(esquina1.getWorld(),esquina1.getBlockX(),esquina1.getBlockY(),esquina1.getBlockZ()));
						   
						   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.arenaBuilt")));  
					   }else {
						   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.arenaDoesNotExists")));  
					   }
				   }else {
					   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.commandBuildErrorUse"))); 
				   }
			   }else {
				   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.noPermissions")));
			   }
		   }else if(args[0].equalsIgnoreCase("setspawn")) {
			// /chess setspawn <arena>
			   if(jugador.isOp() || jugador.hasPermission("chess.admin")) {
				   if(args.length >= 2) {
					   Partida partida = plugin.getPartida(args[1]);
					   if(partida != null) {
						   partida.setSpawn(jugador.getLocation().clone());
						   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.spawnSet").replace("%arena%", args[1])));  
					   }else {
						   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.arenaDoesNotExists")));  
					   }
				   }else {
					   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.commandSetSpawnErrorUse"))); 
				   }
			   }else {
				   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.noPermissions")));
			   }
		   }else if(args[0].equalsIgnoreCase("setmainlobby")) {
			   // /chess setmainlobby
			   if(jugador.isOp() || jugador.hasPermission("chess.admin")) {
				   Location l = jugador.getLocation();
				   config.set("MainLobby.x", l.getX()+"");
				   config.set("MainLobby.y", l.getY()+"");
				   config.set("MainLobby.z", l.getZ()+"");
				   config.set("MainLobby.world", l.getWorld().getName());
				   config.set("MainLobby.pitch", l.getPitch());
				   config.set("MainLobby.yaw", l.getYaw());
				   plugin.saveConfig();
				   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.mainLobbyDefined"))); 
			   }else {
				   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.noPermissions")));
			   }
		   }else if(args[0].equalsIgnoreCase("spectate")) {
			   // /chess spectate <arena> o <player>
			   if(args.length >= 2) {
				   Partida partida = plugin.getPartida(args[1]);
				   if(partida == null) {
					   partida = plugin.getPartidaJugador(args[1]);
					   if(partida == null) {
						   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.commandSpectateAllError"))); 
						   return true;
					   }
				   }
				   if(partida.estaActivada()) {
					   if(plugin.getPartidaJugador(jugador.getName()) == null) {
						   if(partida.estaIniciada()) {
							  PartidaManager.espectadorEntra(partida, jugador, plugin);
						   }else {
							   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.arenaMustHaveStarted"))); 
						   }
					   }else {
						   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.alreadyInArena"))); 
					   }
				   }else {
					   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.arenaDisabledError"))); 
				   }
				   
			   }else {
				   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.commandSpectateErrorUse"))); 
			   }
		   }
		   else if(args[0].equalsIgnoreCase("join")) {
			   // /chess join <arena>
			   if(args.length >= 2) {
				   Partida partida = plugin.getPartida(args[1]);
				   if(partida != null) {
					   if(partida.estaActivada()) {
						   if(plugin.getPartidaJugador(jugador.getName()) == null) {
							   if(!partida.estaIniciada()) {
								   if(!partida.estaLlena()) {
									   if(!Utilidades.pasaConfigInventario(jugador, config)) {
										   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.errorClearInventory"))); 
										   return true;
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
				   }else {
					   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.arenaDoesNotExists"))); 
				   }
			   }else {
				   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.commandJoinErrorUse"))); 
			   }
		   }else if(args[0].equalsIgnoreCase("joinrandom")) {
			   // /chess joinrandom
			   Partida partidaNueva = PartidaManager.getPartidaDisponible(plugin);
			   if(partidaNueva == null) {
					jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.noArenasAvailable")));
			   }else {
				   if(plugin.getPartidaJugador(jugador.getName()) == null) {
					   PartidaManager.jugadorEntra(partidaNueva, jugador,plugin);
				   }else {
					   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.alreadyInArena"))); 
				   }
			   }
			   
		   }
		   else if(args[0].equalsIgnoreCase("leave")) {
			   // /chess leave
			   Partida partida = plugin.getPartidaJugador(jugador.getName());
			   if(partida != null) {  
				   Jugador j = partida.getJugador(jugador.getName());
				   if(j.esEspectador()) {
					   PartidaManager.espectadorSale(partida, jugador, plugin);
				   }else {
					   PartidaManager.jugadorSale(partida, jugador, false, plugin, false); 
				   }	   
			   }else {
				   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.notInAGame"))); 
			   }
		   }else if(args[0].equalsIgnoreCase("enable")) {
			   // /chess enable <arena>
			   //Para activar una arena todo debe estar definido
			   if(jugador.isOp() || jugador.hasPermission("chess.admin")) {
				   if(args.length >= 2) {
					   Partida partida = plugin.getPartida(args[1]);
					   if(partida != null) {
						   if(partida.estaActivada()) {
							   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.arenaAlreadyEnabled"))); 
						   }else {
							   if(partida.getEsquina1() == null) {
								   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.enableArenaBuildError"))); 
								   return true;
							   }
							   if(partida.getSpawn() == null) {
								   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.enableArenaSpawnError"))); 
								   return true;
							   }
							   
							   partida.setEstado(Estado.ESPERANDO);
							   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.arenaEnabled").replace("%arena%", args[1]))); 
						   }
					   }else {
						   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.arenaDoesNotExists"))); 
					   }
				   }else {
					   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.commandEnableErrorUse"))); 
				   }
			   }else {
				   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.noPermissions")));
			   }
		   }else if(args[0].equalsIgnoreCase("disable")) {
			   // /chess disable <arena>
			   if(jugador.isOp() || jugador.hasPermission("chess.admin")) {
				   if(args.length >= 2) {
					   Partida partida = plugin.getPartida(args[1]);
					   if(partida != null) {
						   if(!partida.estaActivada()) {
							   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.arenaAlreadyDisabled"))); 
						   }else {
							   partida.setEstado(Estado.DESACTIVADA);
							   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.arenaDisabled").replace("%arena%", args[1]))); 
						   }
					   }else {
						   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.arenaDoesNotExists"))); 
					   }
				   }else {
					   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.commandDisableErrorUse"))); 
				   }
			   }else {
				   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.noPermissions")));
			   }
		   }else if(args[0].equalsIgnoreCase("delete")) {
			   // /chess delete <nombre>
			   if(jugador.isOp() || jugador.hasPermission("chess.admin")) {
				   if(args.length >= 2) {
					   Partida partida = plugin.getPartida(args[1]);
					   if(partida != null) {
						   Location l = partida.getEsquina1();
						   Location l2 = l.clone().add(24,5,24);
						   for(int x=l.getBlockX();x<l2.getBlockX();x++) {
							   for(int y=l.getBlockY();y<l2.getBlockY();y++) {
								   for(int z=l.getBlockZ();z<l2.getBlockZ();z++) {
									   Location nueva = new Location(l.getWorld(),x,y,z);
									   if(nueva != null) {
										   Block b = nueva.getBlock();
										   if(b != null) {
											   b.setType(Material.AIR); 
										   } 
									   }
								   }
							   }  
						   }
						   
						   plugin.removerPartida(args[1]);
						   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.arenaDeleted").replace("%arena%", args[1]))); 
					   }else {
						   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.arenaDoesNotExists")));  
					   }
				   }else {
					   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.commandDeleteErrorUse"))); 
				   }
			   }else {
				   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.noPermissions")));
			   }
		   }else if(args[0].equalsIgnoreCase("stats")) {
			   comandoStats(args,jugador,config,prefix);
		   }
		   else if(args[0].equalsIgnoreCase("reload")) {
			   if(jugador.isOp() || jugador.hasPermission("chess.admin")) {
				   plugin.reloadConfig();
				   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.configReloaded")));
			   }else {
				   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.noPermissions")));
			   }
		   }
		   else {
			   if(jugador.isOp() || jugador.hasPermission("chess.admin")) {
				   enviarAyuda(jugador); 
			   }else {
				   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.noPermissions")));
			   }
			  
		   }
	   }else {
		   if(jugador.isOp() || jugador.hasPermission("chess.admin")) {
			   enviarAyuda(jugador); 
		   }else {
			   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.noPermissions")));
		   }
	   }
	   
	   return true;
	   
	}
	
	public void comandoStats(String[] args, final Player jugador, final FileConfiguration config, final String prefix) {
		// /chess stats -> Stats del jugador
		// /chess stats <usuario> -> Stats de ese usuario
		JugadorDatos j = null;
		String nombreJugador = "";
		if (args.length == 1) {
			if (jugador.isOp() || jugador.hasPermission("chess.stats")) {
				nombreJugador = jugador.getName();
				j = plugin.getJugador(nombreJugador);
			} else {
				jugador.sendMessage(prefix
						+ ChatColor.translateAlternateColorCodes('&', config.getString("Messages.noPermissions")));
				return;
			}
		} else {
			if (jugador.isOp() || jugador.hasPermission("chess.stats.others")) {
				nombreJugador = args[1];
				j = plugin.getJugador(nombreJugador);
			} else {
				jugador.sendMessage(prefix
						+ ChatColor.translateAlternateColorCodes('&', config.getString("Messages.noPermissions")));
				return;
			}
		}
		
		if (j != null) {
			List<String> msg = config.getStringList("Messages.commandPlayerStats");
			for (int i = 0; i < msg.size(); i++) {
				jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',
						msg.get(i).replace("%player%", j.getPlayer()).replace("%wins%", j.getWins() + "")
								.replace("%ties%", j.getTies() + "").replace("%loses%", j.getLoses() + "")
								.replace("%time%", Utilidades.getTiempoJugado(j.getMillisJugados()))));
			}
		} else {
			jugador.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
					config.getString("Messages.errorPlayerNotExists").replace("%player%", nombreJugador)));
		}
	}
	
	public void enviarAyuda(Player jugador) {
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7[ [ &8[&9MineChess&8] &7] ]"));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',""));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6/chess create <arena> &8Creates a new arena."));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6/chess delete <arena> &8Deletes an arena."));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6/chess join <arena> &8Joins an arena."));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6/chess joinrandom &8Joins a random arena."));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6/chess leave &8Leaves from the arena."));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6/chess spectate <arena>/<player> &8Spectates an arena or player."));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6/chess setmainlobby &8Defines the minigame main lobby."));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6/chess enable <arena> &8Enables an arena."));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6/chess disable <arena> &8Disables an arena."));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6/chess build <arena> &8Builds the arena in your current position."));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6/chess setspawn <arena> &8Sets the arena spawn."));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6/chess reload &8Reloads the configuration files."));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6/chess stats <player> &8Check stats of player."));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',""));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7[ [ &8[&9MineChess&8] &7] ]"));
	}
}
