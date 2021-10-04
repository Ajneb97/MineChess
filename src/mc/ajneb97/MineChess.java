package mc.ajneb97;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import mc.ajneb97.api.ChessAPI;
import mc.ajneb97.api.ExpansionMineChess;
import mc.ajneb97.juego.Estado;
import mc.ajneb97.juego.Jugador;
import mc.ajneb97.juego.Partida;
import mc.ajneb97.managers.CartelesListener;
import mc.ajneb97.managers.Entrar;
import mc.ajneb97.managers.InventarioCoronacion;
import mc.ajneb97.managers.PartidaListener;
import mc.ajneb97.managers.PartidaManager;
import mc.ajneb97.managers.ScoreboardAdmin;
import mc.ajneb97.managers.SignAdmin;
import mc.ajneb97.mysql.ConexionMySQL;
import mc.ajneb97.mysql.MySQL;

//JAQUE: Al realizar un movimiento, comprobar por todas las piezas del jugador en su turno y ver si alguna posicion
//       coincide con la del rey contrario.
//JAQUE: Si el rey esta en jaque, en su turno solo mostrar los movimientos posibles para salir de jaque.
//       Para esto hay que obtener TODOS los movimientos posibles del rey, y por cada uno de ellos comprobar si
//       el rey ya no esta en jaque. Los movimientos validos que permiten al rey salir de jaque seran los movimientos
//       finales.
//JAQUE: Si el rey esta en jaque, al seleccionar una pieza comprobar por cada movimiento posible si ese movimiento
//       saca del jaque al rey, y agregarlo a la lista. Para ver si el movimiento saca del jaque al rey, hay
//       que comprobar por todas las piezas contrarias si pueden comer al rey o no
//JAQUE: Eliminar los movimientos que harian que tu rey este en jaque

public class MineChess extends JavaPlugin {
  
	PluginDescriptionFile pdfFile = getDescription();
	public String version = pdfFile.getVersion();
	public String latestversion;
	private ArrayList<Partida> partidas;
	private FileConfiguration arenas = null;
	private File arenasFile = null;
	private FileConfiguration signs = null;
	private File signsFile = null;
	private FileConfiguration players = null;
	private File playersFile = null;
	private ArrayList<JugadorDatos> jugadores;
	public String rutaConfig;
	public static String prefix = ChatColor.translateAlternateColorCodes('&', "&8[&2&lMineChess&8] ");
	
	private ConexionMySQL conexionDatabase;
	
	public void onEnable(){
	   partidas = new ArrayList<Partida>();
	   jugadores = new ArrayList<JugadorDatos>();
	   registerEvents();
	   registerCommands();
	   registerConfig();
	   registerArenas();
	   registerPlayers();
	   cargarPartidas();
	   if(MySQL.isEnabled(getConfig())) {
		   conexionDatabase = new ConexionMySQL();
		   conexionDatabase.setupMySql(this, getConfig());
	   }
	   cargarJugadores();

	   SignAdmin signs = new SignAdmin(this);
	   signs.actualizarSigns();
	   ScoreboardAdmin scoreboards = new ScoreboardAdmin(this);
	   scoreboards.crearScoreboards();
	    
	   ChessAPI api = new ChessAPI(this);
	   if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
		   new ExpansionMineChess(this).register();
	   }
	   checkMessagesUpdate();
	   
	   
	   Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.YELLOW + "Has been enabled! " + ChatColor.WHITE + "Version: " + version);
	   Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.YELLOW + "Thanks for using my plugin!  " + ChatColor.WHITE + "~Ajneb97");
	   updateChecker();
	}
	  
	public void onDisable(){
		if(partidas != null) {
			for(int i=0;i<partidas.size();i++) {
				if(!partidas.get(i).getEstado().equals(Estado.DESACTIVADA)) {
					PartidaManager.finalizarPartida(partidas.get(i),null,this,true);
				}
			}
		}
		guardarPartidas();
		guardarJugadores();
		Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.YELLOW + "Has been disabled! " + ChatColor.WHITE + "Version: " + version);
	}
	public void registerCommands(){
		this.getCommand("chess").setExecutor(new Comando(this));
	}
	
	public void registerEvents(){
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PartidaListener(this), this);
		pm.registerEvents(new InventarioCoronacion(this), this);
		pm.registerEvents(new CartelesListener(this), this);
		pm.registerEvents(new Entrar(this), this);
	}
	
	public Partida getPartidaJugador(String jugador) {
		for(int i=0;i<partidas.size();i++) {
			ArrayList<Jugador> jugadores = partidas.get(i).getJugadores();
			for(int c=0;c<jugadores.size();c++) {
				if(jugadores.get(c).getJugador().getName().equals(jugador)) {
					return partidas.get(i);
				}
			}
		}
		return null;
	}
	
	public ArrayList<Partida> getPartidas() {
		return this.partidas;
	}
	
	public Partida getPartida(String nombre) {
		for(int i=0;i<partidas.size();i++) {
			if(partidas.get(i).getNombre().equals(nombre)) {
				return partidas.get(i);
			}
		}
		return null;
	}
	
	public void agregarPartida(Partida partida) {
		this.partidas.add(partida);
	}
	
	public void removerPartida(String nombre) {
		for(int i=0;i<partidas.size();i++) {
			if(partidas.get(i).getNombre().equals(nombre)) {
				partidas.remove(i);
			}
		}
	}
	
	public void registerConfig(){
		File config = new File(this.getDataFolder(),"config.yml");
		rutaConfig = config.getPath();
		if(!config.exists()){
			this.getConfig().options().copyDefaults(true);
			saveConfig();
		}
	}
	
	public void registerArenas(){
		  arenasFile = new File(this.getDataFolder(), "arenas.yml");
		  if(!arenasFile.exists()){
		    	this.getArenas().options().copyDefaults(true);
				saveArenas();
		    }
	  }
	  public void saveArenas() {
		 try {
			 arenas.save(arenasFile);
		 } catch (IOException e) {
			 e.printStackTrace();
	 	}
	 }
	  
	  public FileConfiguration getArenas() {
		    if (arenas == null) {
		        reloadArenas();
		    }
		    return arenas;
		}
	  
	  public void reloadArenas() {
		    if (arenas == null) {
		    	arenasFile = new File(getDataFolder(), "arenas.yml");
		    }
		    arenas = YamlConfiguration.loadConfiguration(arenasFile);

		    Reader defConfigStream;
			try {
				defConfigStream = new InputStreamReader(this.getResource("arenas.yml"), "UTF8");
				if (defConfigStream != null) {
			        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			        arenas.setDefaults(defConfig);
			    }
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}	    
		}
	  
	  public void cargarPartidas() {
		  this.partidas = new ArrayList<Partida>();
		  FileConfiguration arenas = getArenas();
		  if(arenas.contains("Arenas")) {
			  for(String key : arenas.getConfigurationSection("Arenas").getKeys(false)) {
				  Location lSpawn = null;
				  if(arenas.contains("Arenas."+key+".Spawn")) {
					  double xSpawn = Double.valueOf(arenas.getString("Arenas."+key+".Spawn.x"));
					  double ySpawn = Double.valueOf(arenas.getString("Arenas."+key+".Spawn.y"));
					  double zSpawn = Double.valueOf(arenas.getString("Arenas."+key+".Spawn.z"));
					  String worldSpawn = arenas.getString("Arenas."+key+".Spawn.world");
					  float pitchSpawn = Float.valueOf(arenas.getString("Arenas."+key+".Spawn.pitch"));
					  float yawSpawn = Float.valueOf(arenas.getString("Arenas."+key+".Spawn.yaw"));
					  lSpawn = new Location(Bukkit.getWorld(worldSpawn),xSpawn,ySpawn,zSpawn,yawSpawn,pitchSpawn);
				  }
				  Location lLocation = null;
				  if(arenas.contains("Arenas."+key+".Location")) {
					  double xLocation = Double.valueOf(arenas.getString("Arenas."+key+".Location.x"));
					  double yLocation = Double.valueOf(arenas.getString("Arenas."+key+".Location.y"));
					  double zLocation = Double.valueOf(arenas.getString("Arenas."+key+".Location.z"));
					  String worldLocation = arenas.getString("Arenas."+key+".Location.world");
					  lLocation = new Location(Bukkit.getWorld(worldLocation),xLocation,yLocation,zLocation);
				  }
				  
				  Partida partida = new Partida(key);
				  partida.setSpawn(lSpawn);
				  partida.setEsquina1(lLocation);
				  String enabled = arenas.getString("Arenas."+key+".enabled");
				  if(enabled.equals("true")) {
					  partida.setEstado(Estado.ESPERANDO);
				  }else {
					  partida.setEstado(Estado.DESACTIVADA);
				  }
				  
				  this.partidas.add(partida);
			  }
		  }
		  
	  }
	
	public void guardarPartidas() {
		  FileConfiguration arenas = getArenas();
		  arenas.set("Arenas", null);
		  for(Partida p : this.partidas) {
			  String nombre = p.getNombre();
			  Location lSpawn = p.getSpawn();
			  if(lSpawn != null) {
				  arenas.set("Arenas."+nombre+".Spawn.x", lSpawn.getX()+"");
				  arenas.set("Arenas."+nombre+".Spawn.y", lSpawn.getY()+"");
				  arenas.set("Arenas."+nombre+".Spawn.z", lSpawn.getZ()+"");
				  arenas.set("Arenas."+nombre+".Spawn.world", lSpawn.getWorld().getName());
				  arenas.set("Arenas."+nombre+".Spawn.pitch", lSpawn.getPitch());
				  arenas.set("Arenas."+nombre+".Spawn.yaw", lSpawn.getYaw());
			  }
			  
			  Location lEsquina = p.getEsquina1();
			  if(lEsquina != null) {
				  arenas.set("Arenas."+nombre+".Location.x", lEsquina.getX()+"");
				  arenas.set("Arenas."+nombre+".Location.y", lEsquina.getY()+"");
				  arenas.set("Arenas."+nombre+".Location.z", lEsquina.getZ()+"");
				  arenas.set("Arenas."+nombre+".Location.world", lEsquina.getWorld().getName());
			  }
			  
			  if(p.getEstado().equals(Estado.DESACTIVADA)) {
				  arenas.set("Arenas."+nombre+".enabled", "false");
			  }else {
				  arenas.set("Arenas."+nombre+".enabled", "true");
			  }
		  }
		  this.saveArenas();
	  }
	
	public void registerSigns(){
		signsFile = new File(this.getDataFolder(), "signs.yml");
		  if(!signsFile.exists()){
		    	this.getSigns().options().copyDefaults(true);
				saveSigns();
		    }
	  }
	  public void saveSigns() {
		 try {
			 signs.save(signsFile);
		 } catch (IOException e) {
			 e.printStackTrace();
	 	}
	 }
	  
	  public FileConfiguration getSigns() {
		    if (signs == null) {
		        reloadSigns();
		    }
		    return signs;
		}
	  
	  public void reloadSigns() {
		    if (signs == null) {
		    	signsFile = new File(getDataFolder(), "signs.yml");
		    }
		    signs = YamlConfiguration.loadConfiguration(signsFile);

		    Reader defConfigStream;
			try {
				defConfigStream = new InputStreamReader(this.getResource("signs.yml"), "UTF8");
				if (defConfigStream != null) {
			        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			        signs.setDefaults(defConfig);
			    }
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}	    
		}
	  
	  public void cargarJugadores() {
		  if(MySQL.isEnabled(getConfig())) {
			  this.jugadores = MySQL.getJugadores(this);
			  return;
		  } 
		  FileConfiguration config = getPlayers();
			 if(config.contains("Players")) {
					for(String key : config.getConfigurationSection("Players").getKeys(false)) {	
						String player = config.getString("Players."+key+".name");
						int wins = 0;
						int loses = 0;
						int ties = 0;
						long millis = 0;
						if(config.contains("Players."+key+".wins")){
							wins = Integer.valueOf(config.getString("Players."+key+".wins"));
						}
						if(config.contains("Players."+key+".loses")){
							loses = Integer.valueOf(config.getString("Players."+key+".loses"));
						}
						if(config.contains("Players."+key+".ties")){
							ties = Integer.valueOf(config.getString("Players."+key+".ties"));
						}
						if(config.contains("Players."+key+".played_time")) {
							millis = Long.valueOf(config.getString("Players."+key+".played_time"));
						}
						agregarJugadorDatos(new JugadorDatos(player,key,wins,loses,ties,millis));
					}
				}
	  }
	  
	  public void guardarJugadores() {
		  if(MySQL.isEnabled(getConfig())) {
			  return;
		  }
		  FileConfiguration players = getPlayers();
			for(JugadorDatos j : jugadores) {
				String uuid = j.getUUID();
				String name = j.getPlayer();
				int wins = j.getWins();
				int loses = j.getLoses();
				int ties = j.getTies();
				players.set("Players."+uuid+".name", name);
				players.set("Players."+uuid+".wins", wins);
				players.set("Players."+uuid+".loses", loses);
				players.set("Players."+uuid+".ties", ties);
				players.set("Players."+uuid+".played_time", j.getMillisJugados());
			}
			savePlayers();
	  }
	  
	  public void agregarJugadorDatos(JugadorDatos jugador) {
			jugadores.add(jugador);
		}
	  
	  public void removerJugadorDatos(String jugador) {
		  for(int i=0;i<jugadores.size();i++) {
			  if(jugadores.get(i).getPlayer().equals(jugador)) {
				  jugadores.remove(i);
			  }
		  }
	  }
		
		public JugadorDatos getJugador(String jugador) {
			for(JugadorDatos j : jugadores) {
				if(j.getPlayer().equals(jugador)) {
					return j;
				}
			}
			return null;
		}
		
		public ArrayList<JugadorDatos> getJugadores(){
			return this.jugadores;
		}
	  
	  public void updateChecker(){
		  
		  try {
			  HttpURLConnection con = (HttpURLConnection) new URL(
	                  "https://api.spigotmc.org/legacy/update.php?resource=74178").openConnection();
	          int timed_out = 1250;
	          con.setConnectTimeout(timed_out);
	          con.setReadTimeout(timed_out);
	          latestversion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
	          if (latestversion.length() <= 7) {
	        	  if(!version.equals(latestversion)){
	        		  Bukkit.getConsoleSender().sendMessage(ChatColor.RED +"There is a new version available. "+ChatColor.YELLOW+
	        				  "("+ChatColor.GRAY+latestversion+ChatColor.YELLOW+")");
	        		  Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"You can download it at: "+ChatColor.WHITE+"https://www.spigotmc.org/resources/74178/");  
	        	  }      	  
	          }
	      } catch (Exception ex) {
	    	  Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED +"Error while checking update.");
	      }
	  }
	  
	  public void registerPlayers(){
		  playersFile = new File(this.getDataFolder(), "players.yml");
		  if(!playersFile.exists()){
		    	this.getPlayers().options().copyDefaults(true);
				savePlayers();
		    }
	  }
	  public void savePlayers() {
		 try {
			 players.save(playersFile);
		 } catch (IOException e) {
			 e.printStackTrace();
	 	}
	 }
	  
	  public FileConfiguration getPlayers() {
		    if (players == null) {
		        reloadPlayers();
		    }
		    return players;
		}
	 
	  
	  public void reloadPlayers() {
		    if (players == null) {
		    playersFile = new File(getDataFolder(), "players.yml");
		    }
		    players = YamlConfiguration.loadConfiguration(playersFile);

		    Reader defConfigStream;
			try {
				
				defConfigStream = new InputStreamReader(this.getResource("players.yml"), "UTF8");
				if (defConfigStream != null) {
			        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			        players.setDefaults(defConfig);
			    }
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}	    
		}
	  
	  public void checkMessagesUpdate(){
		  Path archivo = Paths.get(rutaConfig);
		  try{
			  String texto = new String(Files.readAllBytes(archivo));
			  if(!texto.contains("click_distance:")){
					getConfig().set("Config.click_distance", 15);
					saveConfig();
			  }
			  if(!texto.contains("mysql_database:")){
					getConfig().set("Config.mysql_database.enabled", false);
					getConfig().set("Config.mysql_database.host", "localhost");
					getConfig().set("Config.mysql_database.port", 3306);
					getConfig().set("Config.mysql_database.username", "root");
					getConfig().set("Config.mysql_database.password", "root");
					getConfig().set("Config.mysql_database.database", "database");
					saveConfig();
			  }
			  if(!texto.contains("errorClearInventory:")){
					getConfig().set("Messages.errorClearInventory", "&c&lERROR! &7To join an arena clear your inventory first.");
					getConfig().set("Config.empty_inventory_to_join", false);
					saveConfig();
			  }
			  if(!texto.contains("noArenasAvailable:")){
					getConfig().set("Messages.noArenasAvailable", "&cThere are no arenas available.");
					saveConfig();
			  }
			  if(!texto.contains("losers_command_rewards:")){
				  List<String> lista = new ArrayList<String>();
					lista.add("msg %player% &aYou've lost! Here, take this compensation reward.");
					lista.add("eco give %player% %random_2*points-3*points%");
					getConfig().set("Config.losers_command_rewards", lista);
					saveConfig();
			  }
			  if(!texto.contains("minimum_time_for_rewards:")){
				  
					getConfig().set("Config.minimum_time_for_rewards", 240);
					saveConfig();
			  }
			  if(!texto.contains("teleport_last_location:")){
					getConfig().set("Config.teleport_last_location", false);
					saveConfig();
			  }
			  if(!texto.contains("rewards_executed_after_teleport:")){
					getConfig().set("Config.rewards_executed_after_teleport", true);
					saveConfig();
			  }
			  if(!texto.contains("time_in_each_turn:")){
					getConfig().set("Config.time_in_each_turn.enabled", true);
					getConfig().set("Config.time_in_each_turn.time", 60);
					getConfig().set("Config.time_in_each_turn.action_when_time_runs_out", "move");
					saveConfig();
			  }
			  if(!texto.contains("errorPlayerNotExists:")){
					getConfig().set("Messages.errorPlayerNotExists", "&7Player &e%player% &7doesn't have any stored stats.");
					List<String> lista = new ArrayList<String>();
					lista.add("&9&lStats of &e%player%");
					lista.add("&7Wins: &e%wins%");
					lista.add("&7Ties: &e%ties%");
					lista.add("&7Loses: &e%loses%");
					getConfig().set("Messages.commandPlayerStats", lista);
					saveConfig();
			  }
			  if(!texto.contains("commandSpectateErrorUse:")){
					getConfig().set("Messages.commandSpectateErrorUse", "&cYou need to use &7/chess spectate <arena>");
					getConfig().set("Messages.arenaMustHaveStarted", "&cThe arena must have started to spectate it!");
					saveConfig();
			  }
			  if(!texto.contains("titleKingInCheck:")){
					getConfig().set("Messages.titleKingInCheck", "&a%player%'s &cKing is in check!");
					getConfig().set("Messages.check", "&6➤ &a%player%'s &cKing is in check!");
					getConfig().set("Messages.titleKingInCheckmate", "&a%player%'s &cKing is in checkmate!");
					getConfig().set("Messages.checkmate", "&6➤ &a%player%'s &cKing is in checkmate!");
					getConfig().set("Config.soundCheck", "ENTITY_BLAZE_DEATH;10;0.5");
					saveConfig();
			  }
			  if(!texto.contains("per_arena_chat:")){
					getConfig().set("Config.per_arena_chat", true);
					saveConfig();
			  }
			  if(!texto.contains("piece_holograms_enabled:")){
					getConfig().set("Config.piece_holograms_enabled", true);
					saveConfig();
			  }
		  }catch(IOException e){
			  e.printStackTrace();
		  }
	  }
	  
	  public Connection getConnection() {
		  return this.conexionDatabase.getConnection();
	  }
	  
	  public void crearJugadorDatosSQL(String nombre,String uuid) {
		  MySQL.crearJugador(this, nombre, uuid);
	  }
}
