package mc.ajneb97.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import mc.ajneb97.JugadorDatos;
import mc.ajneb97.MineChess;
import mc.ajneb97.juego.Ajedrez;
import mc.ajneb97.juego.Estado;
import mc.ajneb97.juego.Jugador;
import mc.ajneb97.juego.MovimientoPosible;
import mc.ajneb97.juego.Partida;
import mc.ajneb97.juego.Pieza;
import mc.ajneb97.juego.Tablero;
import mc.ajneb97.lib.titleapi.TitleAPI;
import mc.ajneb97.mysql.MySQL;
import mc.ajneb97.otros.Utilidades;

public class PartidaManager {

	@SuppressWarnings("deprecation")
	public static void jugadorEntra(Partida partida,Player jugador,MineChess plugin) {
		Jugador jugadorChess = new Jugador(jugador,false);
		FileConfiguration config = plugin.getConfig();
		String prefix = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.prefix"));
		partida.agregarJugador(jugadorChess);
		ArrayList<Jugador> jugadores = partida.getJugadores();
		for(int i=0;i<jugadores.size();i++) {
			jugadores.get(i).getJugador().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.playerJoin").replace("%player%", jugador.getName())
					.replace("%current_players%", partida.getCantidadActualJugadores()+"")));
		}
		
		jugador.getInventory().clear();
		jugador.getEquipment().clear();
		jugador.getEquipment().setArmorContents(null);
		jugador.updateInventory();
		
		jugador.setGameMode(GameMode.SURVIVAL);
		jugador.setExp(0);
		jugador.setLevel(0);
		jugador.setFoodLevel(20);
		jugador.setMaxHealth(20);
		jugador.setHealth(20);
		jugador.setAllowFlight(true);
		jugador.setFlying(true);
		for(PotionEffect p : jugador.getActivePotionEffects()) {
			jugador.removePotionEffect(p.getType());
		}
		
		jugador.teleport(partida.getSpawn());
		
		if(config.getString("Config.leave_item_enabled").equals("true")) {
			ItemStack item = Utilidades.crearItem(config, "Config.leave_item");
			jugador.getInventory().setItem(8, item);
		}
		
		if(partida.getCantidadActualJugadores() == 2 && partida.getEstado().equals(Estado.ESPERANDO)) {
			cooldownIniciarPartida(partida,plugin);
		}
	}
	
	@SuppressWarnings("deprecation")
	//Tiene que haber empezado la partida para espectearla
	public static void espectadorEntra(Partida partida,Player jugador,MineChess plugin) {
		partida.agregarEspectador(jugador);
		FileConfiguration config = plugin.getConfig();
		
		jugador.getInventory().clear();
		jugador.getEquipment().clear();
		jugador.getEquipment().setArmorContents(null);
		jugador.updateInventory();
		
		jugador.setGameMode(GameMode.SURVIVAL);
		jugador.setExp(0);
		jugador.setLevel(0);
		jugador.setFoodLevel(20);
		jugador.setMaxHealth(20);
		jugador.setHealth(20);
		jugador.setAllowFlight(true);
		jugador.setFlying(true);
		for(PotionEffect p : jugador.getActivePotionEffects()) {
			jugador.removePotionEffect(p.getType());
		}
		PotionEffect efecto = new PotionEffect(PotionEffectType.INVISIBILITY,999999,1,false,false);
		jugador.addPotionEffect(efecto);
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.hidePlayer(jugador);
			
		}
		
		jugador.teleport(partida.getSpawn());
		
		if(config.getString("Config.leave_item_enabled").equals("true")) {
			ItemStack item = Utilidades.crearItem(config, "Config.leave_item");
			jugador.getInventory().setItem(8, item);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void jugadorSale(Partida partida,Player jugador,boolean finalizaPartida,
			MineChess plugin,boolean cerrandoServer) {
		Jugador jugadorChess = partida.getJugador(jugador.getName());
		FileConfiguration config = plugin.getConfig();
		String prefix = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.prefix"));
		ItemStack[] inventarioGuardado = jugadorChess.getGuardados().getInventarioGuardado();
		ItemStack[] equipamientoGuardado = jugadorChess.getGuardados().getEquipamientoGuardado();
		GameMode gamemodeGuardado = jugadorChess.getGuardados().getGamemodeGuardado();	
		float xpGuardada = jugadorChess.getGuardados().getXPGuardada();
		int levelGuardado = jugadorChess.getGuardados().getLevelGuardado();
		int hambreGuardada = jugadorChess.getGuardados().getHambreGuardada();
		double vidaGuardada = jugadorChess.getGuardados().getVidaGuardada();
		double maxVidaGuardada = jugadorChess.getGuardados().getMaxVidaGuardada();
		boolean allowFligth = jugadorChess.getGuardados().isAllowFligth();
		boolean isFlying = jugadorChess.getGuardados().isFlying();
		Location lastLocation = jugadorChess.getGuardados().getLastLocation();
		
		JugadorDatos jDatos = plugin.getJugador(jugadorChess.getJugador().getName());
		if(jDatos == null) {
			jDatos = new JugadorDatos(jugadorChess.getJugador().getName(), jugadorChess.getJugador().getUniqueId().toString(),0,0,0,0);
			plugin.agregarJugadorDatos(jDatos);
		}
		long millisJugados = System.currentTimeMillis()-jugadorChess.getMillisAntes();
		jDatos.aumentarMillisJugados(millisJugados,config,plugin);

		partida.removerJugador(jugador.getName());
		
		if(!finalizaPartida) {
			ArrayList<Jugador> jugadores = partida.getJugadores();
			for(int i=0;i<jugadores.size();i++) {
				jugadores.get(i).getJugador().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.playerLeave").replace("%player%", jugador.getName())
						.replace("%current_players%", partida.getCantidadActualJugadores()+"")));
			}
		}
		
		
		if(config.getString("Config.teleport_last_location").equals("true")) {
			jugador.teleport(lastLocation);
		}else {
			double x = Double.valueOf(config.getString("MainLobby.x"));
			double y = Double.valueOf(config.getString("MainLobby.y"));
			double z = Double.valueOf(config.getString("MainLobby.z"));
			String world = config.getString("MainLobby.world");
			float yaw = Float.valueOf(config.getString("MainLobby.yaw"));
			float pitch = Float.valueOf(config.getString("MainLobby.pitch"));
			Location mainLobby = new Location(Bukkit.getWorld(world),x,y,z,yaw,pitch);
			jugador.teleport(mainLobby);
		}
		
		
		jugador.getInventory().setContents(inventarioGuardado);
		jugador.getEquipment().setArmorContents(equipamientoGuardado);
		jugador.setGameMode(gamemodeGuardado);
		jugador.setLevel(levelGuardado);
		jugador.setExp(xpGuardada);
		jugador.setFoodLevel(hambreGuardada);
		jugador.setMaxHealth(maxVidaGuardada);
		jugador.setHealth(vidaGuardada);
		for(PotionEffect p : jugador.getActivePotionEffects()) {
			jugador.removePotionEffect(p.getType());
		}
		jugador.updateInventory();

		jugador.setAllowFlight(allowFligth);
		jugador.setFlying(isFlying);
		
		
		if(!cerrandoServer) {
			if(partida.getCantidadActualJugadores() <= 1 && partida.getEstado().equals(Estado.COMENZANDO)){
				partida.setEstado(Estado.ESPERANDO);
			}else if(partida.getCantidadActualJugadores() <= 1 && partida.getEstado().equals(Estado.JUGANDO)) {
				//fase finalizacion
				PartidaManager.iniciarFaseFinalizacion(partida, plugin, partida.getGanador());
			}
		}
	}
	
	public static void espectadorSale(Partida partida,Player jugador,MineChess plugin) {
		Jugador jugadorChess = partida.getJugador(jugador.getName());
		FileConfiguration config = plugin.getConfig();
		ItemStack[] inventarioGuardado = jugadorChess.getGuardados().getInventarioGuardado();
		ItemStack[] equipamientoGuardado = jugadorChess.getGuardados().getEquipamientoGuardado();
		GameMode gamemodeGuardado = jugadorChess.getGuardados().getGamemodeGuardado();	
		float xpGuardada = jugadorChess.getGuardados().getXPGuardada();
		int levelGuardado = jugadorChess.getGuardados().getLevelGuardado();
		int hambreGuardada = jugadorChess.getGuardados().getHambreGuardada();
		double vidaGuardada = jugadorChess.getGuardados().getVidaGuardada();
		double maxVidaGuardada = jugadorChess.getGuardados().getMaxVidaGuardada();
		boolean allowFligth = jugadorChess.getGuardados().isAllowFligth();
		boolean isFlying = jugadorChess.getGuardados().isFlying();
		Location lastLocation = jugadorChess.getGuardados().getLastLocation();

		partida.removerEspectador(jugador.getName());

		if(config.getString("Config.teleport_last_location").equals("true")) {
			jugador.teleport(lastLocation);
		}else {
			double x = Double.valueOf(config.getString("MainLobby.x"));
			double y = Double.valueOf(config.getString("MainLobby.y"));
			double z = Double.valueOf(config.getString("MainLobby.z"));
			String world = config.getString("MainLobby.world");
			float yaw = Float.valueOf(config.getString("MainLobby.yaw"));
			float pitch = Float.valueOf(config.getString("MainLobby.pitch"));
			Location mainLobby = new Location(Bukkit.getWorld(world),x,y,z,yaw,pitch);
			jugador.teleport(mainLobby);
		}
		
		jugador.getInventory().setContents(inventarioGuardado);
		jugador.getEquipment().setArmorContents(equipamientoGuardado);
		jugador.setGameMode(gamemodeGuardado);
		jugador.setLevel(levelGuardado);
		jugador.setExp(xpGuardada);
		jugador.setFoodLevel(hambreGuardada);
		jugador.setMaxHealth(maxVidaGuardada);
		jugador.setHealth(vidaGuardada);
		for(PotionEffect p : jugador.getActivePotionEffects()) {
			jugador.removePotionEffect(p.getType());
		}
		jugador.updateInventory();

		jugador.setAllowFlight(allowFligth);
		jugador.setFlying(isFlying);
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.showPlayer(jugador);
		}
	}
	
	public static void cooldownIniciarPartida(Partida partida,MineChess plugin) {
		partida.setEstado(Estado.COMENZANDO);
		
		int time = Integer.valueOf(plugin.getConfig().getString("Config.arena_starting_cooldown"));
		
		CooldownManager cooldown = new CooldownManager(plugin);
		cooldown.cooldownComenzarJuego(partida,time);
	}
	
	public static void iniciarPartida(Partida partida,MineChess plugin) {
		partida.setEstado(Estado.JUGANDO);
		partida.getJugador1().setMillisAntes();
		partida.getJugador2().setMillisAntes();
		FileConfiguration config = plugin.getConfig();
		String prefix = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.prefix"));
		
		Random r = new Random();
		int num = r.nextInt(10);
		if(num >= 5) {
			partida.getJugador1().setColor("n");
			partida.getJugador2().setColor("b");
			partida.setTurno(partida.getJugador2());
		}else {
			partida.getJugador1().setColor("b");
			partida.getJugador2().setColor("n");
			partida.setTurno(partida.getJugador1());
		}

		darItems(partida,plugin.getConfig());
		
		ArrayList<Jugador> jugadores = partida.getJugadores();
		for(int i=0;i<jugadores.size();i++) {
			if(!jugadores.get(i).esEspectador()) {
				jugadores.get(i).getJugador().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.gameStarted")));
				if(jugadores.get(i).getColor().equals("b")) {
					jugadores.get(i).getJugador().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.whitePiecesInfo")));
				}else {
					jugadores.get(i).getJugador().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.blackPiecesInfo")));
				}
				jugadores.get(i).getJugador().closeInventory();
			}
		}
		
		for(int i=0;i<jugadores.size();i++) {
			jugadores.get(i).getJugador().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.changeTurn").replace("%player%", partida.getTurno().getJugador().getName())));
		}
		
		int time = Integer.valueOf(config.getString("Config.arena_time"));
		
		CooldownManager cooldown = new CooldownManager(plugin);
		cooldown.cooldownJuego(partida,time);
		CooldownManager cooldown2 = new CooldownManager(plugin);
		cooldown2.cooldownActionbar(partida,config);
		if(config.getString("Config.time_in_each_turn.enabled").equals("true")) {
			CooldownTurno cooldown3 = new CooldownTurno(plugin);
			String accion = config.getString("Config.time_in_each_turn.action_when_time_runs_out");
			cooldown3.cooldown(partida, Integer.valueOf(config.getString("Config.time_in_each_turn.time")),accion);
		}
		
		
		//Agregar Hologramas
		if(Bukkit.getServer().getPluginManager().getPlugin("HolographicDisplays") != null && config.getString("Config.piece_holograms_enabled").equals("true")) {
			Tablero.crearHologramasPiezas(partida.getEsquina1(), plugin);
		}
	}
	
	public static void cambiarTurno(Partida partida,FileConfiguration config,MineChess plugin) {
		Jugador ganador = null;
		if(partida.getTurno().equals(partida.getJugador1())) {
			partida.setTurno(partida.getJugador2());
			ganador = partida.getJugador1();
			//reiniciar en-passant jugador 2
			reiniciarEnPassant(partida,partida.getJugador2());
		}else {
			partida.setTurno(partida.getJugador1());
			ganador = partida.getJugador2();
			//reiniciar en-passant jugador 1
			reiniciarEnPassant(partida,partida.getJugador1());
		}
		
		//Revisar si hay jaque mate o no
		//Si el jugador turno no tiene movimientos posibles es jaque mate
		String prefix = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.prefix"));
		boolean enJaque = comprobarCheck(partida);
		if(enJaque) {
			partida.getTurno().setEnJaque(true);
		}
		int cantidadMovimientos = getMovimientosPosibles(partida,partida.getTurno().getColor()).size();
		if(cantidadMovimientos != 0 && enJaque) {
			//Sonido
			String[] separados = config.getString("Config.soundCheck").split(";");
			try {
				Sound sound = Sound.valueOf(separados[0]);
				for(Jugador j : partida.getJugadores()) {
					j.getJugador().playSound(j.getJugador().getLocation(), sound, Float.valueOf(separados[1]), Float.valueOf(separados[2]));
				}
			}catch(Exception e) {
				Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',"&7Sound Name: &c"+separados[0]+" &7is not valid. Change it in the config!"));
			}		
			for(Jugador j : partida.getJugadores()) {
				j.getJugador().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.check")
						.replace("%player%", partida.getTurno().getJugador().getName())));
				TitleAPI.sendTitle(j.getJugador(), 10, 30, 10, "", config.getString("Messages.titleKingInCheck").replace("%player%", partida.getTurno().getJugador().getName()));
			}
		}
		if(cantidadMovimientos == 0) {
			String[] separados = config.getString("Config.soundCheck").split(";");
			try {
				Sound sound = Sound.valueOf(separados[0]);
				for(Jugador j : partida.getJugadores()) {
					j.getJugador().playSound(j.getJugador().getLocation(), sound, Float.valueOf(separados[1]), Float.valueOf(separados[2]));
				}
			}catch(Exception e) {
				Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',"&7Sound Name: &c"+separados[0]+" &7is not valid. Change it in the config!"));
			}
			for(Jugador j : partida.getJugadores()) {
				j.getJugador().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.checkmate")
						.replace("%player%", partida.getTurno().getJugador().getName())));
				TitleAPI.sendTitle(j.getJugador(), 10, 30, 10, "", config.getString("Messages.titleKingInCheckmate").replace("%player%", partida.getTurno().getJugador().getName()));
			}
			iniciarFaseFinalizacion(partida,plugin,ganador);
			return;
		}

		ArrayList<Jugador> jugadores = partida.getJugadores();
		for(int i=0;i<jugadores.size();i++) {
			jugadores.get(i).getJugador().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.changeTurn").replace("%player%", partida.getTurno().getJugador().getName())));
		}
		if(config.getString("Config.time_in_each_turn.enabled").equals("true")) {
			partida.getTurno().setTiempo(Integer.valueOf(config.getString("Config.time_in_each_turn.time")));
		}
	}
	
	public static void reiniciarEnPassant(Partida partida,Jugador jugador) {
		String color = jugador.getColor();
		Pieza[][] tablero = partida.getTablero();
		for(int i=0;i<tablero.length;i++) {
			for(int c=0;c<tablero.length;c++) {
				if(tablero[i][c] != null && tablero[i][c].getColor().equals(color) && tablero[i][c].isEnPassant()) {
					tablero[i][c].setEnPassant(false);
				}
			}
		}
	}
	
	public static void darItems(Partida partida,FileConfiguration config) {
		ArrayList<Jugador> jugadores = partida.getJugadores();
		for(Jugador j : jugadores) {
			if(!j.esEspectador()) {
				Player p = j.getJugador();
				if(j.getColor().equals("b")) {
					darEquipamientoJugador(p,16777215);
				}else {
					darEquipamientoJugador(p,0);
				}
				
				ItemStack item = Utilidades.crearItem(config, "Config.select_item");
				p.getInventory().setItem(0, item);
			}
		}
	}
	
	public static void darEquipamientoJugador(Player jugador,int color) {
		ItemStack item = new ItemStack(Material.LEATHER_HELMET,1);
		LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
		meta.setColor(Color.fromRGB(color));
		item.setItemMeta(meta);
		jugador.getInventory().setHelmet(item);
		
		item = new ItemStack(Material.LEATHER_CHESTPLATE,1);
		meta = (LeatherArmorMeta) item.getItemMeta();
		meta.setColor(Color.fromRGB(color));
		item.setItemMeta(meta);
		jugador.getInventory().setChestplate(item);
		
		item = new ItemStack(Material.LEATHER_LEGGINGS,1);
		meta = (LeatherArmorMeta) item.getItemMeta();
		meta.setColor(Color.fromRGB(color));
		item.setItemMeta(meta);
		jugador.getInventory().setLeggings(item);
		
		item = new ItemStack(Material.LEATHER_BOOTS,1);
		meta = (LeatherArmorMeta) item.getItemMeta();
		meta.setColor(Color.fromRGB(color));
		item.setItemMeta(meta);
		jugador.getInventory().setBoots(item);
	}
	
	public static void lanzarFuegos(Jugador j) {
		Firework fw = (Firework) j.getJugador().getWorld().spawnEntity(j.getJugador().getLocation(), EntityType.FIREWORK);
		FireworkMeta fwm = fw.getFireworkMeta();
        Type type = Type.BALL;
        Color c1 = Color.RED;
        Color c2 = Color.AQUA;
        FireworkEffect efecto = FireworkEffect.builder().withColor(c1).withFade(c2).with(type).build();
        fwm.addEffect(efecto);
        fwm.setPower(2);
        fw.setFireworkMeta(fwm);	
	}
	
	public static void iniciarFaseFinalizacion(Partida partida,MineChess plugin,Jugador ganador) {
		partida.setEstado(Estado.TERMINANDO);
		FileConfiguration config = plugin.getConfig();
		int tiempoEnTerminar = partida.getTiempoMaximo()-partida.getTiempo();
		partida.setTiempoEnTerminar(tiempoEnTerminar);
		
		List<String> msg = null;
		if(ganador == null) {
			//empate
			msg = config.getStringList("Messages.gameFinishedTie");
		}else {
			msg = config.getStringList("Messages.gameFinished");
		}	
		ArrayList<Jugador> jugadores = partida.getJugadores();
		for(Jugador j : jugadores) {
			if(ganador == null) {
				for(int i=0;i<msg.size();i++) {
						j.getJugador().sendMessage(ChatColor.translateAlternateColorCodes('&', msg.get(i)));
				}
			}else {
				for(int i=0;i<msg.size();i++) {
					j.getJugador().sendMessage(ChatColor.translateAlternateColorCodes('&', msg.get(i).replace("%player%", ganador.getJugador().getName()).replace("%points%", ganador.getPuntos()+"")));
				}
			}
			j.getJugador().closeInventory();
			j.getJugador().getInventory().clear();
			if(config.getString("Config.leave_item_enabled").equals("true")) {
				ItemStack item = Utilidades.crearItem(config, "Config.leave_item");
				j.getJugador().getInventory().setItem(8, item);
			}
			
			if(!j.esEspectador()) {
				JugadorDatos jDatos = plugin.getJugador(j.getJugador().getName());
				if(jDatos == null) {
					jDatos = new JugadorDatos(j.getJugador().getName(), j.getJugador().getUniqueId().toString(),0,0,0,0);	
					plugin.agregarJugadorDatos(jDatos);
				}
				if(ganador != null && j.equals(ganador)) {
					jDatos.aumentarWins(config,plugin);
				}else if(ganador == null) {
					jDatos.aumentarTies(config,plugin);
				}else {
					jDatos.aumentarLoses(config,plugin);
				}
				
				if(config.getString("Config.rewards_executed_after_teleport").equals("false")) {
					int minimumTime = Integer.valueOf(config.getString("Config.minimum_time_for_rewards"));
					if(ganador != null && j.equals(ganador)) {
						if(tiempoEnTerminar >= minimumTime) {
							List<String> commands = config.getStringList("Config.winners_command_rewards");
							ejecutarComandosRewards(commands,j);
						}
					}else if(ganador != null && !j.equals(ganador)){
						if(tiempoEnTerminar >= minimumTime) {
							List<String> commands = config.getStringList("Config.losers_command_rewards");
							ejecutarComandosRewards(commands,j);
						}
					}
				}
			}
			
		}
		
		int time = Integer.valueOf(config.getString("Config.arena_ending_phase_cooldown"));
		CooldownManager c = new CooldownManager(plugin);
		c.cooldownFaseFinalizacion(partida,time,ganador);
	}
	
	public static void ejecutarComandosRewards(List<String> commands,Jugador j) {
		CommandSender console = Bukkit.getServer().getConsoleSender();
		for(int i=0;i<commands.size();i++){	
			if(commands.get(i).startsWith("msg %player%")) {
				String mensaje = commands.get(i).replace("msg %player% ", "");
				j.getJugador().sendMessage(ChatColor.translateAlternateColorCodes('&', mensaje));
			}else {
				String comandoAEnviar = commands.get(i).replaceAll("%player%", j.getJugador().getName()); 
				if(comandoAEnviar.contains("%random")) {
					int pos = comandoAEnviar.indexOf("%random");
					int nextPos = comandoAEnviar.indexOf("%", pos+1);
					String variableCompleta = comandoAEnviar.substring(pos,nextPos+1);
					String variable = variableCompleta.replace("%random_", "").replace("%", "");
					String[] sep = variable.split("-");
					int cantidadMinima = 0;
					int cantidadMaxima = 0;
				    try {
				    	cantidadMinima = (int) Utilidades.eval(sep[0].replace("points", j.getPuntos()+""));
				    	cantidadMaxima = (int) Utilidades.eval(sep[1].replace("points", j.getPuntos()+""));
					} catch (Exception e) {
						
					}
				    int num = Utilidades.getNumeroAleatorio(cantidadMinima, cantidadMaxima);
				    comandoAEnviar = comandoAEnviar.replace(variableCompleta, num+"");
				}
				Bukkit.dispatchCommand(console, comandoAEnviar);	
			}
		}
	}
	
	public static void finalizarPartida(Partida partida,Jugador ganador,MineChess plugin,boolean cerrandoServer) {
		ArrayList<Jugador> jugadores = partida.getJugadores();
		
		FileConfiguration config = plugin.getConfig();
		for(Jugador j : jugadores) {
			boolean esJugadorGanador = false;
			Player jugador = j.getJugador();
			if(ganador != null) {
				if(ganador.getJugador().getName().equals(jugador.getName()) && !cerrandoServer) {
					esJugadorGanador = true;
				}
			}
			
			if(j.esEspectador()) {
				espectadorSale(partida, j.getJugador(),plugin);
			}else {
				jugadorSale(partida, j.getJugador(),true,plugin,cerrandoServer);
			}
			
			if(!j.esEspectador()) {
				if(config.getString("Config.rewards_executed_after_teleport").equals("true")) {
					int tiempoEnTerminar = partida.getTiempoEnTerminar();
					int minimumTime = Integer.valueOf(config.getString("Config.minimum_time_for_rewards"));
					if(esJugadorGanador) {
						if(tiempoEnTerminar >= minimumTime) {
							List<String> commands = config.getStringList("Config.winners_command_rewards");
							ejecutarComandosRewards(commands,j);
						}
					}else {
						if(tiempoEnTerminar >= minimumTime) {
							List<String> commands = config.getStringList("Config.losers_command_rewards");
							ejecutarComandosRewards(commands,j);
						}
					}
				}
			}
			
			
		}
		Tablero.eliminarPiezas(partida.getEsquina1());
		Tablero.eliminarPiezasHologramas(plugin, partida.getEsquina1(),plugin.getConfig(),cerrandoServer);
		Tablero.crearPiezas(partida.getEsquina1());
		partida.reiniciarPiezas();
		partida.setTurno(null);
		
		partida.setEstado(Estado.ESPERANDO);
	}
	
	@SuppressWarnings("deprecation")
	public static void moverPieza(MineChess plugin, Partida partida,Jugador jugador,int[] posPiezaSeleccionada,int[] posNueva,Location locTo,boolean aleatorio) {
		Pieza pieza = jugador.getPiezaSeleccionada();
		Location lAntes = partida.getLocationDesdePieza(pieza);
		//Bukkit.getConsoleSender().sendMessage("se mueve pieza desde "+posPiezaSeleccionada[0]+" "+posPiezaSeleccionada[1]+" hasta "+posNueva[0]+" "+posNueva[1]);
		Location l1 = locTo.clone();
		l1.setY(partida.getEsquina1().getY());
		l1.add(0,1,0);
		
		FileConfiguration config = plugin.getConfig();
		Pieza piezaCapturada = partida.getPiezaDesdeCoordenada(l1);
		
		
		
		boolean esCapturada = false;
		Player jugadorContrincante = null;
		if(partida.getJugador1().equals(jugador)) {
			jugadorContrincante = partida.getJugador2().getJugador();
		}else {
			jugadorContrincante = partida.getJugador1().getJugador();
		}
		String piezaContrincante = "";
		if(piezaCapturada != null) {
			esCapturada = true;
			piezaContrincante = getNombrePieza(config,piezaCapturada.getTipo());
			capturaPieza(partida,jugador,config,piezaCapturada.getTipo(),l1,jugadorContrincante);
		}else {
			//En-passant
			//Si se come en diagonal y no hay pieza significa que es en-passant
			if(pieza.getTipo().equals("peon")) {
				if(pieza.getColor().equals("b")) {
					if(posPiezaSeleccionada[1]+1 == posNueva[1] && posPiezaSeleccionada[0] != posNueva[0]) {
						esCapturada = true;
						piezaContrincante = getNombrePieza(config,"peon");
						Location l3 = l1.clone().add(0,0,-3);
						Tablero.eliminarPieza(l3);
						Location l3h = l3.clone().add(0.5,0,0.5);
						Tablero.eliminarPiezaHolograma(plugin, l3h,config);
						capturaPieza(partida,jugador,config,"peon",l3,jugadorContrincante);
						partida.reemplazarPieza(partida.getPosicionDesdeCoordenada(l3), null);
					}
				}else {
					if(posPiezaSeleccionada[1]-1 == posNueva[1] && posPiezaSeleccionada[0] != posNueva[0]) {
						esCapturada = true;
						piezaContrincante = getNombrePieza(config,"peon");
						Location l3 = l1.clone().add(0,0,3);
						Tablero.eliminarPieza(l3);
						Location l3h = l3.clone().add(0.5,0,0.5);
						Tablero.eliminarPiezaHolograma(plugin, l3h,config);
						capturaPieza(partida,jugador,config,"peon",l3,jugadorContrincante);
						partida.reemplazarPieza(partida.getPosicionDesdeCoordenada(l3), null);
					}
				}
			}
		}
		
		Tablero.eliminarPieza(l1);
		Location l1h = l1.clone().add(0.5,0,0.5);
		Tablero.eliminarPiezaHolograma(plugin, l1h,config);
		
		Tablero.crearPieza(l1, pieza.getTipo(), pieza.getColor());
		Location l2h = l1.clone().add(0.5,3,0.5);
		if(!pieza.getTipo().equals("peon")) {
			l2h.add(0,1.7,0);
		}
		Tablero.crearPiezaHolograma(plugin, l2h, pieza.getTipo(), config);
		
		Tablero.eliminarPieza(lAntes.clone().add(0,1,0));
		Location lAntesh = lAntes.clone().add(0.5,0,0.5);
		Tablero.eliminarPiezaHolograma(plugin, lAntesh,config);
		
		//partida.mostrarTablero();
		//Bukkit.getConsoleSender().sendMessage("");
		
		//en-passant (mover las 2 casillas)
//		Bukkit.getConsoleSender().sendMessage("MOVIENDO PIEZA: "+pieza.getTipo()+" "+pieza.getColor()+" a "+posNueva[0]+","+posNueva[1]);
		boolean isEnPassant = false;
		if(piezaCapturada == null) {
			if(pieza.getTipo().equals("peon") && pieza.getColor().equals("b")) {
				if(posNueva[1] == 3 && posPiezaSeleccionada[1] == 1) {
					pieza.setEnPassant(true);
					isEnPassant = true;
//					Bukkit.getConsoleSender().sendMessage("SETEANDO EN PASSANT");
				}
			}else if(pieza.getTipo().equals("peon") && pieza.getColor().equals("n")) {
				if(posNueva[1] == 4 && posPiezaSeleccionada[1] == 6) {
					pieza.setEnPassant(true);
					isEnPassant = true;
//					Bukkit.getConsoleSender().sendMessage("SETEANDO EN PASSANT");
				}
			}
		}
		partida.moverPieza(posPiezaSeleccionada, posNueva,isEnPassant);
		//partida.mostrarTablero();
		//Bukkit.getConsoleSender().sendMessage("");
		
		int coord2 = posNueva[1]+1;
		String coord1 = getCoord1(posNueva[0]);
		String coordsFinal = coord1+coord2;
	
		String prefix = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.prefix"));
		ArrayList<Jugador> jugadores = partida.getJugadores();
		String nombrePieza = getNombrePieza(config,pieza.getTipo());
		for(Jugador j : jugadores) {
			if(esCapturada) {
				j.getJugador().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.pieceCaptured")
						.replace("%player%", jugador.getJugador().getName()).replace("%piece%", nombrePieza).replace("%coords%", coordsFinal)
						.replace("%player2%", jugadorContrincante.getName()).replace("%player2_piece%", piezaContrincante)));
			}else {
				j.getJugador().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.pieceMove")
						.replace("%player%", jugador.getJugador().getName()).replace("%piece%", nombrePieza).replace("%coords%", coordsFinal)));
			}	
		}	
		
		if(piezaCapturada == null) {
			String[] separados = config.getString("Config.soundMovePiece").split(";");
			try {
				Sound sound = Sound.valueOf(separados[0]);
				for(Jugador j : jugadores) {
					j.getJugador().playSound(j.getJugador().getLocation(), sound, Float.valueOf(separados[1]), Float.valueOf(separados[2]));
				}
			}catch(Exception e) {
				Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',"&7Sound Name: &c"+separados[0]+" &7is not valid. Change it in the config!"));
			}
		}
		

		
		//coronacion del peon (pawn promotion)
		//Bukkit.getConsoleSender().sendMessage(posNueva[1]+"");
		if(pieza.getTipo().equals("peon") && pieza.getColor().equals("b") && posNueva[1] == 7) {
			//Bukkit.getConsoleSender().sendMessage("coronacion blanca");
			if(piezaCapturada == null || !piezaCapturada.getTipo().equals("rey")) {
				jugador.setCoronandoPeon(true);
				InventarioCoronacion.crearInventario(jugador, plugin);
				if(aleatorio) {
					int slot = 0;
					Random r = new Random();
					int num = r.nextInt(4);
					if(num == 0) {
						slot = 1;
					}else if(num == 1) {
						slot = 3;
					}else if(num == 2) {
						slot = 5;
					}else {
						slot = 7;
					}
					InventarioCoronacion.coronacion(partida,jugador,slot,config,plugin);
				}
				return;
			}
			
		}else if(pieza.getTipo().equals("peon") && pieza.getColor().equals("n") && posNueva[1] == 0) {
			//Bukkit.getConsoleSender().sendMessage("coronacion negra");
			if(piezaCapturada == null || !piezaCapturada.getTipo().equals("rey")) {
				jugador.setCoronandoPeon(true);
				InventarioCoronacion.crearInventario(jugador, plugin);
				if(aleatorio) {
					int slot = 0;
					Random r = new Random();
					int num = r.nextInt(4);
					if(num == 0) {
						slot = 1;
					}else if(num == 1) {
						slot = 3;
					}else if(num == 2) {
						slot = 5;
					}else {
						slot = 7;
					}
					InventarioCoronacion.coronacion(partida,jugador,slot,config,plugin);
				}
				return;
			}
		}
		
		jugador.setPiezaSeleccionada(null);
		
		if(piezaCapturada != null && piezaCapturada.getTipo().equals("rey")) {
			//Se termina la partida
			iniciarFaseFinalizacion(partida,plugin,jugador);
			return;
		}

//		Bukkit.getConsoleSender().sendMessage("en passant: "+p2.isEnPassant());
		
		cambiarTurno(partida,config,plugin);
	}
	
	public static boolean comprobarCheck(Partida partida) {
		Jugador turno = partida.getTurno();
		Location rey = partida.getLocationRey(turno.getColor());
		
		Pieza[][] tablero = partida.getTablero();
		for(int i=0;i<tablero.length;i++) {
			for(int c=0;c<tablero.length;c++) {
				//Por cada pieza se hace esto
				//Comprobar si uno de los movimientos posibles coincide con la location del rey
				if(tablero[c][i] != null && !tablero[c][i].getColor().equals(turno.getColor())) {
					ArrayList<MovimientoPosible> movimientosPosibles = Ajedrez.getMovimientosPosibles(partida, tablero, i, c, false);
					if(movimientosPosibles != null) {
						for(MovimientoPosible l : movimientosPosibles) {
							if(l.getLocation().getWorld().getName().equals(rey.getWorld().getName()) &&
									l.getLocation().getBlockX() == rey.getBlockX() && l.getLocation().getBlockZ() == rey.getBlockZ()) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	public static void capturaPieza(Partida partida,Jugador jugador,FileConfiguration config,String tipo,Location l,Player jugadorContrincante) {
		String prefix = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.prefix"));
		aumentarPuntos(tipo,jugador,config);
		
		//Efecto Captura
		if(Bukkit.getVersion().contains("1.8")) {
			l.getWorld().playEffect(l, Effect.EXPLOSION_LARGE, 2);
		}else {
			l.getWorld().spawnParticle(Particle.EXPLOSION_LARGE,l,2);
		}
		
		String[] separados = config.getString("Config.soundCapturePiece").split(";");
		try {
			Sound sound = Sound.valueOf(separados[0]);
			jugador.getJugador().playSound(jugador.getJugador().getLocation(), sound, Float.valueOf(separados[1]), Float.valueOf(separados[2]));
		}catch(Exception e) {
			Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',"&7Sound Name: &c"+separados[0]+" &7is not valid. Change it in the config!"));
		}
		
		separados = config.getString("Config.soundLosePiece").split(";");
		try {
			Sound sound = Sound.valueOf(separados[0]);
			jugadorContrincante.playSound(jugadorContrincante.getLocation(), sound, Float.valueOf(separados[1]), Float.valueOf(separados[2]));
		}catch(Exception e) {
			Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',"&7Sound Name: &c"+separados[0]+" &7is not valid. Change it in the config!"));
		}
	}
	
	private static String getCoord1(int i) {
		if(i==7) {
			return "A";
		}else if(i==6) {
			return "B";
		}else if(i==5) {
			return "C";
		}else if(i==4) {
			return "D";
		}else if(i==3) {
			return "E";
		}else if(i==2) {
			return "F";
		}else if(i==1) {
			return "G";
		}else {
			return "H";
		}
	}

	@SuppressWarnings("deprecation")
	public static void enroque(MineChess plugin, Partida partida,Jugador jugador,int[] posPiezaSeleccionada,int[] posNueva,Location locTo) {
		Pieza pieza = jugador.getPiezaSeleccionada();
		Location lAntes = partida.getLocationDesdePieza(pieza).clone().add(0,1,0);
		//Bukkit.getConsoleSender().sendMessage("enroque "+posPiezaSeleccionada[0]+" "+posPiezaSeleccionada[1]+" hasta "+posNueva[0]+" "+posNueva[1]);
		Location l1 = locTo.clone();
		l1.setY(partida.getEsquina1().getY());
		l1.add(0,1,0);
		
		FileConfiguration config = plugin.getConfig();
		Pieza piezaCapturada = partida.getPiezaDesdeCoordenada(l1);
		//Bukkit.getConsoleSender().sendMessage(piezaCapturada.getId()+"");
		if(piezaCapturada.getId() == 1 || piezaCapturada.getId() == 17) {
			Location lNuevaRey = l1.clone().add(3,0,0);
			Location lNuevaTorre = l1.clone().add(6,0,0);
			//Bukkit.getConsoleSender().sendMessage("eliminando: "+l1.getBlockX()+" "+l1.getBlockY()+" "+l1.getBlockZ());
			//Bukkit.getConsoleSender().sendMessage("location nueva rey: "+lNuevaRey.getBlockX()+" "+lNuevaRey.getBlockZ());
			//Bukkit.getConsoleSender().sendMessage("location nueva torre: "+lNuevaTorre.getBlockX()+" "+lNuevaTorre.getBlockZ());
			Tablero.eliminarPieza(l1);
			Location l1h = l1.clone().add(0.5,0,0.5);
			Tablero.eliminarPiezaHolograma(plugin, l1h,config);
			
			Tablero.eliminarPieza(lAntes);
			Location lAntesh = lAntes.clone().add(0.5,0,0.5);
			Tablero.eliminarPiezaHolograma(plugin, lAntesh,config);
			
			Tablero.crearPieza(lNuevaRey, pieza.getTipo(), pieza.getColor());
			Location l2h = lNuevaRey.clone().add(0.5,3,0.5);
			if(!pieza.getTipo().equals("peon")) {
				l2h.add(0,1.7,0);
			}
			Tablero.crearPiezaHolograma(plugin, l2h, pieza.getTipo(), config);
			
			Tablero.crearPieza(lNuevaTorre, piezaCapturada.getTipo(), piezaCapturada.getColor());
			Location l3h = lNuevaTorre.clone().add(0.5,3,0.5);
			if(!piezaCapturada.getTipo().equals("peon")) {
				l3h.add(0,1.7,0);
			}
			Tablero.crearPiezaHolograma(plugin, l3h, piezaCapturada.getTipo(), config);
			//partida.mostrarTablero();
			//Bukkit.getConsoleSender().sendMessage("");
			int[] posPiezaSeleccionadaNueva = {posPiezaSeleccionada[0]-2,posPiezaSeleccionada[1]};
			partida.moverPieza(posPiezaSeleccionada, posPiezaSeleccionadaNueva,false);
			int[] posPiezaCapturadaNueva = {posNueva[0]+2,posNueva[1]};
			partida.moverPieza(posNueva, posPiezaCapturadaNueva,false);
			//fpartida.mostrarTablero();
			//Bukkit.getConsoleSender().sendMessage("");
		}else if(piezaCapturada.getId() == 8 || piezaCapturada.getId() == 24) {
			Location lNuevaRey = l1.clone().add(-6,0,0);
			Location lNuevaTorre = l1.clone().add(-9,0,0);
			//Bukkit.getConsoleSender().sendMessage("location nueva rey: "+lNuevaRey.getBlockX()+" "+lNuevaRey.getBlockZ());
			//Bukkit.getConsoleSender().sendMessage("location nueva torre: "+lNuevaTorre.getBlockX()+" "+lNuevaTorre.getBlockZ());
			Tablero.eliminarPieza(l1);
			Location l1h = l1.clone().add(0.5,0,0.5);
			Tablero.eliminarPiezaHolograma(plugin, l1h,config);
			
			Tablero.eliminarPieza(lAntes);
			Location lAntesh = lAntes.clone().add(0.5,0,0.5);
			Tablero.eliminarPiezaHolograma(plugin, lAntesh,config);
			
			Tablero.crearPieza(lNuevaRey, pieza.getTipo(), pieza.getColor());
			Location l2h = lNuevaRey.clone().add(0.5,3,0.5);
			if(!pieza.getTipo().equals("peon")) {
				l2h.add(0,1.7,0);
			}
			Tablero.crearPiezaHolograma(plugin, l2h, pieza.getTipo(), config);
			
			Tablero.crearPieza(lNuevaTorre, piezaCapturada.getTipo(), piezaCapturada.getColor());
			Location l3h = lNuevaTorre.clone().add(0.5,3,0.5);
			if(!piezaCapturada.getTipo().equals("peon")) {
				l3h.add(0,1.7,0);
			}
			Tablero.crearPiezaHolograma(plugin, l3h, piezaCapturada.getTipo(), config);
			//partida.mostrarTablero();
			//Bukkit.getConsoleSender().sendMessage("");
			int[] posPiezaSeleccionadaNueva = {posPiezaSeleccionada[0]+2,posPiezaSeleccionada[1]};
			partida.moverPieza(posPiezaSeleccionada, posPiezaSeleccionadaNueva,false);
			int[] posPiezaCapturadaNueva = {posNueva[0]-3,posNueva[1]};
			partida.moverPieza(posNueva, posPiezaCapturadaNueva,false);
			//partida.mostrarTablero();
			//Bukkit.getConsoleSender().sendMessage("");
		}
		
		int coord2 = posNueva[1]+1;
		String coord1 = getCoord1(posNueva[0]);
		String coordsFinal = coord1+coord2;
		
		String prefix = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.prefix"));
		ArrayList<Jugador> jugadores = partida.getJugadores();
		for(Jugador j : jugadores) {
			j.getJugador().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.castling")
					.replace("%player%", jugador.getJugador().getName()).replace("%coords%", coordsFinal)));	
		}	
		
		String[] separados = config.getString("Config.soundCastling").split(";");
		try {
			Sound sound = Sound.valueOf(separados[0]);
			for(Jugador j : jugadores) {
				j.getJugador().playSound(j.getJugador().getLocation(), sound, Float.valueOf(separados[1]), Float.valueOf(separados[2]));
			}
		}catch(Exception e) {
			Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',"&7Sound Name: &c"+separados[0]+" &7is not valid. Change it in the config!"));
		}
		
		jugador.setPiezaSeleccionada(null);
		
		cambiarTurno(partida,config,plugin);
	}

	public static String getNombrePieza(FileConfiguration config,String pieza) {
		if(pieza.equals("peon")) {
			return config.getString("Messages.piecePawn");
		}else if(pieza.equals("torre")) {
			return config.getString("Messages.pieceRook");
		}else if(pieza.equals("rey")) {
			return config.getString("Messages.pieceKing");
		}else if(pieza.equals("reina")) {
			return config.getString("Messages.pieceQueen");
		}else if(pieza.equals("alfil")) {
			return config.getString("Messages.pieceBishop");
		}else {
			return config.getString("Messages.pieceKnight");
		}
	}
	
	public static void aumentarPuntos(String pieza,Jugador jugador,FileConfiguration config) {
		if(pieza.equals("peon")) {
			jugador.aumentarPuntos(Integer.valueOf(config.getString("Config.pointsPawn")));
		}else if(pieza.equals("torre")) {
			jugador.aumentarPuntos(Integer.valueOf(config.getString("Config.pointsRook")));
		}else if(pieza.equals("rey")) {
			jugador.aumentarPuntos(Integer.valueOf(config.getString("Config.pointsKing")));
		}else if(pieza.equals("reina")) {
			jugador.aumentarPuntos(Integer.valueOf(config.getString("Config.pointsQueen")));
		}else if(pieza.equals("alfil")) {
			jugador.aumentarPuntos(Integer.valueOf(config.getString("Config.pointsBishop")));
		}else {
			jugador.aumentarPuntos(Integer.valueOf(config.getString("Config.pointsKnight")));
		}
	}
	
	public static ArrayList<MovimientoPosible> getMovimientosPosibles(Partida partida,String color){
		ArrayList<MovimientoPosible> movimientosFinales = new ArrayList<MovimientoPosible>();
		//int cantidadMovimientos = 0;
		for(int x=0;x<partida.getTablero().length;x++) {
			for(int y=0;y<partida.getTablero().length;y++) {
				if(partida.getTablero()[y][x] != null && partida.getTablero()[y][x].getColor().equals(color)) {
					ArrayList<MovimientoPosible> movimientosAEliminar = new ArrayList<MovimientoPosible>();
					ArrayList<MovimientoPosible> movimientosPosibles = null;
					Pieza p = partida.getTablero()[y][x];
					int[] posPieza = partida.getPosicion(p);
					if(partida.getTurno().enJaque()) {
						movimientosPosibles = Ajedrez.getMovimientosPosibles(partida, partida.getTablero(), posPieza[0], posPieza[1],true);
					}else {
						movimientosPosibles = Ajedrez.getMovimientosPosibles(partida, partida.getTablero(), posPieza[0], posPieza[1],false);
					}
					Location locationRey = partida.getLocationRey(partida.getTurno().getColor());
					
					if(movimientosPosibles != null) {
						for(MovimientoPosible l : movimientosPosibles) {
							if(p.getTipo().equals("rey")) {
								locationRey = l.getLocation();
							}
							
							//En esta parte de aqui hay que hacer la siguiente comprobacion:
							//El movimiento del enroque ver la posicion FINAL del rey y ver si queda o no en jaque
							//El l.getLocation() seria la posicion de la torre. Comprobar si la location seleccionada
							//corresponde a una torre, y el rey esta seleccionado. Luego de esto determinar la nueva
							//posicion del rey y la torre
							
							Pieza[][] tablero = partida.getTablero().clone();
							int[] cFinal = partida.getPosicionDesdeCoordenada(l.getLocation()).clone();
							
							Pieza aux = partida.getTablero()[y][x];
							Pieza nuevaPieza = new Pieza(aux.getId(),aux.getTipo(),aux.getColor(),aux.seHaMovido(),aux.isEnPassant());
							Pieza auxComida = partida.getPiezaDesdeCoordenada(l.getLocation());
							Pieza piezaComida = null;
							if(auxComida != null) {
								piezaComida = new Pieza(auxComida.getId(),auxComida.getTipo(),auxComida.getColor(),auxComida.seHaMovido(),auxComida.isEnPassant());
							}
							tablero[cFinal[1]][cFinal[0]] = nuevaPieza;
							tablero[posPieza[1]][posPieza[0]] = null;
							//jugador.sendMessage("Pos Inicial: "+posPieza[1]+","+posPieza[0]);
							//jugador.sendMessage("Pos Nueva: "+cFinal[1]+","+cFinal[0]);
							for(int i=0;i<tablero.length;i++) {
								for(int c=0;c<tablero.length;c++) {
									if(tablero[c][i] != null && !tablero[c][i].getColor().equals(partida.getTurno().getColor())) {
										//jugador.sendMessage("probando por pieza contraria");
										ArrayList<MovimientoPosible> movimientosPosiblesPieza = Ajedrez.getMovimientosPosibles(partida, tablero, i, c, false);
										if(movimientosPosiblesPieza != null) {
											for(MovimientoPosible l2 : movimientosPosiblesPieza) {
												if(l2.getLocation().getWorld().getName().equals(locationRey.getWorld().getName()) &&
														l2.getLocation().getBlockX() == locationRey.getBlockX() && l2.getLocation().getBlockZ() == locationRey.getBlockZ()) {
													//Se pueden comer al rey asi que habria que eliminar la Location l de la lista
													//aun puede haber otra posicion que si sirva
													//jugador.sendMessage("se elimina posicion ya que "+tablero[c][i].getTipo()+" puede comerse al rey");
													movimientosAEliminar.add(l);
													break;
												}		
											}
										}
									}
								}
							}
							//Volver a poner la pieza en su lugar original
							Pieza aux2 = new Pieza(nuevaPieza.getId(),nuevaPieza.getTipo(),nuevaPieza.getColor(),nuevaPieza.seHaMovido(),nuevaPieza.isEnPassant());
							tablero[posPieza[1]][posPieza[0]] = aux2;
							tablero[cFinal[1]][cFinal[0]] = piezaComida;
						}
					}
					movimientosPosibles.removeAll(movimientosAEliminar);
					movimientosFinales.addAll(movimientosPosibles);
					//cantidadMovimientos = cantidadMovimientos+movimientosPosibles.size();
				}
			}
		}
		
		
		
		return movimientosFinales;
	}
	
	public static void moverPiezaAleatoria(Partida partida,MineChess plugin) {
		Jugador turno = partida.getTurno();
		//Los movimientos posibles de alguna manera deberian guardar de que pieza exactamente es ese movimiento
		ArrayList<MovimientoPosible> movimientos = PartidaManager.getMovimientosPosibles(partida, turno.getColor());
		Random r = new Random();
		int num = r.nextInt(movimientos.size());
		MovimientoPosible lAleatoria = movimientos.get(num);
		int[] posPiezaSeleccionada = partida.getPosicion(lAleatoria.getPieza());
		int[] posNueva = partida.getPosicionDesdeCoordenada(lAleatoria.getLocation());
		//seleccionarle la pieza al jugador
		turno.setPiezaSeleccionada(lAleatoria.getPieza());
		
		PartidaManager.moverPieza(plugin, partida, turno, posPiezaSeleccionada, posNueva, lAleatoria.getLocation(),true);
	}
	
	public static Partida getPartidaDisponible(MineChess plugin) {
		ArrayList<Partida> partidas = plugin.getPartidas();
		ArrayList<Partida> disponibles = new ArrayList<Partida>();
		for(int i=0;i<partidas.size();i++) {
			if(partidas.get(i).getEstado().equals(Estado.ESPERANDO)) {
				if(!partidas.get(i).estaLlena()) {
					disponibles.add(partidas.get(i));
				}
			}
		}
		
		if(disponibles.isEmpty()) {
			return null;
		}
		
		//Ordenar
		for(int i=0;i<disponibles.size();i++) {
			for(int c=i+1;c<disponibles.size();c++) {
				if(disponibles.get(i).getCantidadActualJugadores() < disponibles.get(c).getCantidadActualJugadores()) {
					Partida p = disponibles.get(i);
					disponibles.set(i, disponibles.get(c));
					disponibles.set(c, p);
				}
			}
		}
		return disponibles.get(0);
	}

}
