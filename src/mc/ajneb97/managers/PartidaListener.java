package mc.ajneb97.managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import mc.ajneb97.JugadorDatos;
import mc.ajneb97.MineChess;
import mc.ajneb97.juego.Ajedrez;
import mc.ajneb97.juego.Estado;
import mc.ajneb97.juego.Jugador;
import mc.ajneb97.juego.MovimientoPosible;
import mc.ajneb97.juego.Partida;
import mc.ajneb97.juego.Pieza;
import mc.ajneb97.mysql.MySQL;
import mc.ajneb97.mysql.MySQLJugadorCallback;
import mc.ajneb97.otros.Utilidades;

public class PartidaListener implements Listener{

	private MineChess plugin;
	public PartidaListener(MineChess plugin) {
		this.plugin = plugin;
	}
	
	//Para seleccionar una pieza a mover se debe hacer click izquierdo mirando a una pieza. Aqui detectar
	//de alguna manera que pieza es
	
	@EventHandler
	public void alSalir(PlayerQuitEvent event) {
		Player jugador = event.getPlayer();
		Partida partida = plugin.getPartidaJugador(jugador.getName());
		FileConfiguration config = plugin.getConfig();
		if(partida != null) {
			Jugador j = partida.getJugador(jugador.getName());
			if(j.esEspectador()) {
				PartidaManager.espectadorSale(partida, jugador,plugin);
			}else {
				if(partida.getEstado().equals(Estado.JUGANDO)) {
					JugadorDatos jDatos = plugin.getJugador(j.getJugador().getName());
					if(jDatos == null) {
						jDatos = new JugadorDatos(j.getJugador().getName(), j.getJugador().getUniqueId().toString(),0,0,0,0);
						plugin.agregarJugadorDatos(jDatos);
					}
					jDatos.aumentarLoses(config,plugin);
				}
				PartidaManager.jugadorSale(partida, jugador,false,plugin,false);
			}
		}
	}
	
	@EventHandler
	public void alEntrar(PlayerJoinEvent event) {
		final Player jugador = event.getPlayer();
		FileConfiguration config = plugin.getConfig();
		if(MySQL.isEnabled(config)) {
			MySQL.getJugador(jugador.getName(), plugin, new MySQLJugadorCallback() {
				@Override
				public void alTerminar(JugadorDatos j) {
					plugin.removerJugadorDatos(jugador.getName());
					if(j != null) {
						plugin.agregarJugadorDatos(j);
					}else {
						//Lo crea si no existe
						plugin.crearJugadorDatosSQL(jugador.getName(), jugador.getUniqueId().toString());
						plugin.agregarJugadorDatos(new JugadorDatos(jugador.getName(),jugador.getUniqueId().toString(),0,0,0,0));
					}
				}
			});
		}
	}
	
	@EventHandler
	public void clickearItemSalir(PlayerInteractEvent event) {
		Player jugador = event.getPlayer();
		Partida partida = plugin.getPartidaJugador(jugador.getName());
		if(partida != null) {
			Jugador j = partida.getJugador(jugador.getName());
			if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				if(event.getItem() != null) {
					FileConfiguration config = plugin.getConfig();
					ItemStack item = Utilidades.crearItem(config, "Config.leave_item");
					if(event.getItem().isSimilar(item)) {
						event.setCancelled(true);
						if(j.esEspectador()) {
							PartidaManager.espectadorSale(partida, jugador,plugin);
						}else {
							if(partida.getEstado().equals(Estado.JUGANDO)) {
								JugadorDatos jDatos = plugin.getJugador(j.getJugador().getName());
								if(jDatos == null) {
									jDatos = new JugadorDatos(j.getJugador().getName(), j.getJugador().getUniqueId().toString(),0,0,0,0);
									plugin.agregarJugadorDatos(jDatos);
								}
								jDatos.aumentarLoses(config,plugin);
							}
							PartidaManager.jugadorSale(partida, jugador,false,plugin,false);
						}
						
					}
				}
			}
		}
	}
	
	@EventHandler
	public void clickearItemPieza(PlayerInteractEvent event) {
		Player jugador = event.getPlayer();
		Partida partida = plugin.getPartidaJugador(jugador.getName());
		if(partida != null) {
			if(event.getItem() != null) {
				FileConfiguration config = plugin.getConfig();
				int clickDistance = config.getInt("Config.click_distance");
				ItemStack item = Utilidades.crearItem(config, "Config.select_item");
				if(event.getItem().isSimilar(item)) {
					event.setCancelled(true);
					if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
						//Mover
						
						Block b = jugador.getTargetBlock(null, clickDistance);
						
						if(b != null) {
							Pieza p = partida.getPiezaDesdeCoordenada(b.getLocation());
							Jugador j = partida.getJugador(jugador.getName());
							if(p == null || !p.getColor().equals(j.getColor())) {
								Pieza piezaS = j.getPiezaSeleccionada();
								if(piezaS != null) {
									int[] posPieza = partida.getPosicion(piezaS);
									ArrayList<MovimientoPosible> movimientosPosibles = j.getMovimientos();
									if(movimientosPosibles != null) {
										for(MovimientoPosible l : movimientosPosibles) {
											if(l.getLocation().getBlockX() == b.getLocation().getBlockX() && l.getLocation().getBlockZ() == b.getLocation().getBlockZ()) {
												//MOVER
												Location locTo = b.getLocation();
												int[] posPiezaNueva = partida.getPosicionDesdeCoordenada(locTo);
												j.setEnJaque(false);
												PartidaManager.moverPieza(plugin, partida, j, posPieza, posPiezaNueva, locTo, false);
												return;
											}
										}
									}
								}
							}else if(p != null && p.getColor().equals(j.getColor())) {
								Pieza piezaS = j.getPiezaSeleccionada();
								if(piezaS != null && piezaS.getTipo().equals("rey")) {
									int[] posPieza = partida.getPosicion(piezaS);
									ArrayList<MovimientoPosible> movimientosPosibles = j.getMovimientos();
									if(movimientosPosibles != null) {
										for(MovimientoPosible l : movimientosPosibles) {
											if(l.getLocation().getBlockX() == b.getLocation().getBlockX() && l.getLocation().getBlockZ() == b.getLocation().getBlockZ()) {
												//ENROQUE
												Location locTo = b.getLocation();
												int[] posPiezaNueva = partida.getPosicionDesdeCoordenada(locTo);
												PartidaManager.enroque(plugin, partida, j, posPieza, posPiezaNueva, locTo);
												return;
											}
										}
									}
								}
							}
						}
					}else if(event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
						//Seleccionar
						Block b = jugador.getTargetBlock(null, clickDistance);
						String prefix = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.prefix"));
						if(b != null) {
							Pieza p = partida.getPiezaDesdeCoordenada(b.getLocation());
							if(p != null) {
								Jugador j = partida.getJugador(jugador.getName());
								if(p.getColor().equals(j.getColor())) {
									if(partida.getTurno() != null && partida.getTurno().getJugador().getName().equals(jugador.getName())) {
										if(j.getPiezaSeleccionada() == null || j.getPiezaSeleccionada().getId() != p.getId()) {
											j.setPiezaSeleccionada(p);
											j.setCeldaObservada(null);
											CooldownParticulaPieza cooldown = new CooldownParticulaPieza(plugin);
											cooldown.cooldownParticulaSeleccionada(j,partida,p);
											
											double y = partida.getEsquina1().getY();
											Location nueva = b.getLocation().clone().add(0.5,0,0.5);
											nueva.setY(y+1.35);
											int[] posPieza = partida.getPosicion(p);
											ArrayList<MovimientoPosible> movimientosPosibles = null;
											if(partida.getTurno().enJaque()) {
												movimientosPosibles = Ajedrez.getMovimientosPosibles(partida, partida.getTablero(), posPieza[0], posPieza[1],true);
											}else {
												movimientosPosibles = Ajedrez.getMovimientosPosibles(partida, partida.getTablero(), posPieza[0], posPieza[1],false);
											}
											ArrayList<MovimientoPosible> movimientosAEliminar = new ArrayList<MovimientoPosible>();
											Location locationRey = partida.getLocationRey(partida.getTurno().getColor());
											if(movimientosPosibles != null) {
//												jugador.sendMessage("movimientos posibles: "+movimientosPosibles.size());
												for(MovimientoPosible l : movimientosPosibles) {
													if(p.getTipo().equals("rey")) {
														locationRey = l.getLocation();
													}
													//jugador.sendMessage("probando por posicion posible");
													//Verificar por cada location, si la pieza en esa posicion evita que se coman al rey
													//l es la nueva posicion de la pieza
													Pieza[][] tablero = partida.getTablero().clone();
													int[] cFinal = partida.getPosicionDesdeCoordenada(l.getLocation()).clone();
													
													Pieza aux = partida.getPiezaDesdeCoordenada(b.getLocation());
													Pieza nuevaPieza = new Pieza(aux.getId(),aux.getTipo(),aux.getColor(),aux.seHaMovido(),aux.isEnPassant());
													Pieza auxComida = partida.getPiezaDesdeCoordenada(l.getLocation());
													Pieza piezaComida = null;
													if(auxComida != null) {
														piezaComida = new Pieza(auxComida.getId(),auxComida.getTipo(),auxComida.getColor(),auxComida.seHaMovido(),auxComida.isEnPassant());
													}
													tablero[cFinal[1]][cFinal[0]] = nuevaPieza;
													tablero[posPieza[1]][posPieza[0]] = null;
//													jugador.sendMessage("Pos Inicial: "+posPieza[1]+","+posPieza[0]);
//													jugador.sendMessage("Pos Nueva: "+cFinal[1]+","+cFinal[0]);
													for(int i=0;i<tablero.length;i++) {
														for(int c=0;c<tablero.length;c++) {
															if(tablero[c][i] != null && !tablero[c][i].getColor().equals(partida.getTurno().getColor())) {
																//jugador.sendMessage(tablero[c][i].getTipo()+" "+tablero[c][i].isEnPassant()+"");
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
											
											j.setMovimientos(movimientosPosibles);
											
											CooldownParticulaPieza cooldown2 = new CooldownParticulaPieza(plugin);
											cooldown2.cooldownParticulaMovimientosPosibles(j, partida, p, movimientosPosibles);
											
											j.getJugador().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.pieceSelected").replace("%piece%", PartidaManager.getNombrePieza(config, p.getTipo()))));
											String[] separados = config.getString("Config.soundSelectPiece").split(";");
											try {
												Sound sound = Sound.valueOf(separados[0]);
												j.getJugador().playSound(j.getJugador().getLocation(), sound, Float.valueOf(separados[1]), Float.valueOf(separados[2]));
											}catch(Exception e) {
												Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',"&7Sound Name: &c"+separados[0]+" &7is not valid. Change it in the config!"));
											}
										}
										
										
									}else {
										jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.notYourTurn")));
									}
								}else {
									jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.notYourPiece")));
								}
								
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	//Para evitar lag, que en la clase Jugador se guarden los posibles movimientos de la pieza seleccionada
	public void mirarPieza(PlayerMoveEvent event) {
		Player jugador = event.getPlayer();
		Partida partida = plugin.getPartidaJugador(jugador.getName());
		if(partida != null) {
			FileConfiguration config = plugin.getConfig();
			int clickDistance = config.getInt("Config.click_distance");
			Block b = jugador.getTargetBlock(null, clickDistance);
			if(b != null) {
				Jugador j = partida.getJugador(jugador.getName());
				if(partida.getTurno() != null && partida.getTurno().equals(j)) {
					Pieza p = partida.getPiezaDesdeCoordenada(b.getLocation());				
					if(p != null) {
						Pieza piezaS = j.getPiezaSeleccionada();
						if(!p.getColor().equals(j.getColor()) && piezaS != null) {
							int[] posPieza = partida.getPosicion(piezaS);
							if(j.getCeldaObservada() == null || (posPieza[0] != j.getCeldaObservada()[0] && posPieza[1] != j.getCeldaObservada()[1])) {
								//ArrayList<Location> movimientosPosibles = Ajedrez.getMovimientosPosibles(partida, partida.getTablero(), posPieza[0], posPieza[1]);
								ArrayList<MovimientoPosible> movimientosPosibles = j.getMovimientos();
								if(movimientosPosibles != null) {
									for(MovimientoPosible l : movimientosPosibles) {
										if(l.getLocation().getBlockX() == b.getLocation().getBlockX() && l.getLocation().getBlockZ() == b.getLocation().getBlockZ()) {
											j.setCeldaObservada(posPieza);
											CooldownParticulaPieza c = new CooldownParticulaPieza(plugin);
											c.cooldownParticulaObservarMovimiento(j, partida, l.getLocation());
											return;
										}
									}
								}
								
								return;
							}else {
								j.setCeldaObservada(null);
								return;
							}
						}
						if(p.getColor().equals(j.getColor()) && piezaS != null) {
							if(p.getTipo().equals("torre") && piezaS.getTipo().equals("rey")) {
								int[] posPieza = partida.getPosicion(piezaS);
								if(j.getCeldaObservada() == null || (posPieza[0] != j.getCeldaObservada()[0] && posPieza[1] != j.getCeldaObservada()[1])) {
									//ArrayList<Location> movimientosPosibles = Ajedrez.getMovimientosPosibles(partida, partida.getTablero(), posPieza[0], posPieza[1]);
									ArrayList<MovimientoPosible> movimientosPosibles = j.getMovimientos();
									if(movimientosPosibles != null) {
										for(MovimientoPosible l : movimientosPosibles) {
											if(l.getLocation().getBlockX() == b.getLocation().getBlockX() && l.getLocation().getBlockZ() == b.getLocation().getBlockZ()) {
												j.setCeldaObservada(posPieza);
												CooldownParticulaPieza c = new CooldownParticulaPieza(plugin);
												c.cooldownParticulaObservarMovimiento(j, partida, l.getLocation());
												return;
											}
										}
									}
								}else {
									return;
								}
							}
							
						}
						if(!p.getColor().equals(j.getColor())){
							return;
						}
						if(j.getPiezaObservada() == null || j.getPiezaObservada().getId() != p.getId()) {
							j.setPiezaObservada(p);
							CooldownParticulaPieza c = new CooldownParticulaPieza(plugin);
							c.cooldownParticula(j,partida,p);
						}
					}else {
						j.setPiezaObservada(null);
						Pieza piezaS = j.getPiezaSeleccionada();
						if(piezaS != null) {
							int[] posPieza = partida.getPosicion(piezaS);
							if(j.getCeldaObservada() == null || (posPieza[0] != j.getCeldaObservada()[0] && posPieza[1] != j.getCeldaObservada()[1])) {
								//ArrayList<Location> movimientosPosibles = Ajedrez.getMovimientosPosibles(partida, partida.getTablero(), posPieza[0], posPieza[1]);
								ArrayList<MovimientoPosible> movimientosPosibles = j.getMovimientos();
								if(movimientosPosibles != null) {
									for(MovimientoPosible l : movimientosPosibles) {
										if(l.getLocation().getBlockX() == b.getLocation().getBlockX() && l.getLocation().getBlockZ() == b.getLocation().getBlockZ()) {
											j.setCeldaObservada(posPieza);
											CooldownParticulaPieza c = new CooldownParticulaPieza(plugin);
											c.cooldownParticulaObservarMovimiento(j, partida, l.getLocation());
											return;
										}
									}
								}
								
								return;
							}else {
								j.setCeldaObservada(null);
								return;
							}
							
						}
						j.setCeldaObservada(null);
					}
				}else {
					j.setCeldaObservada(null);
				}
			}
		}
	}
	
	@EventHandler
	public void alUsarComando(PlayerCommandPreprocessEvent event) {
		String comando = event.getMessage().toLowerCase();
		Player jugador = event.getPlayer();
		Partida partida = plugin.getPartidaJugador(jugador.getName());
		if(partida != null && !jugador.isOp() && !jugador.hasPermission("chess.admin")) {
			FileConfiguration config = plugin.getConfig();
			List<String> comandos = config.getStringList("Config.commands_whitelist");
			for(int i=0;i<comandos.size();i++) {
				if(comando.toLowerCase().startsWith(comandos.get(i))) {
					return;
				}
			}
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void cerrarInventario(InventoryCloseEvent event) {
		Player jugador = (Player)event.getPlayer();
		Partida partida = plugin.getPartidaJugador(jugador.getName());
		if(partida != null) {
			final Jugador j = partida.getJugador(jugador.getName());
			if(j.estaCoronandoPeon()) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						InventarioCoronacion.crearInventario(j,plugin);
					}
				}, 2L);
			}
		}
	}
	
	@EventHandler
	public void romperBloques(BlockBreakEvent event) {
		Player jugador = event.getPlayer();
		Partida partida = plugin.getPartidaJugador(jugador.getName());
		if(partida != null) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void dropearItem(PlayerDropItemEvent event) {
		Player jugador = event.getPlayer();
		Partida partida = plugin.getPartidaJugador(jugador.getName());
		if(partida != null) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void interactuarInventario(InventoryClickEvent event){
		Player jugador = (Player) event.getWhoClicked();
		Partida partida = plugin.getPartidaJugador(jugador.getName());
		if(partida != null) {
			if((event.getInventory().getType().equals(InventoryType.PLAYER) || 
					event.getInventory().getType().equals(InventoryType.CRAFTING)) 
					&& event.getSlotType() != null && event.getCurrentItem() != null){
				event.setCancelled(true);	
			}
		}
	}
	
	@EventHandler
	public void ponerBloques(BlockPlaceEvent event) {
		Player jugador = event.getPlayer();
		Partida partida = plugin.getPartidaJugador(jugador.getName());
		if(partida != null) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void alDañar(EntityDamageEvent event) {
		Entity entidad = event.getEntity();
		if(entidad instanceof Player) {
			Player jugador = (Player) entidad;
			Partida partida = plugin.getPartidaJugador(jugador.getName());
			if(partida != null) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void nivelDeComida(FoodLevelChangeEvent event) {
		Player jugador = (Player) event.getEntity();
		Partida partida = plugin.getPartidaJugador(jugador.getName());
		if(partida != null) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void alChatear(AsyncPlayerChatEvent event) {
		Player jugador = event.getPlayer();
		if(!event.isCancelled()) {
			FileConfiguration config = plugin.getConfig();
			if(config.getString("Config.per_arena_chat").equals("true")) {
				Partida partida = plugin.getPartidaJugador(jugador.getName());
				if(partida != null) {
					for(Player p : Bukkit.getOnlinePlayers()) {
						Partida otra = plugin.getPartidaJugador(p.getName());
						if(otra == null || !otra.equals(partida)) {
							event.getRecipients().remove(p);
						}
					}
				}else {
					for(Player p : Bukkit.getOnlinePlayers()) {
						if(plugin.getPartidaJugador(p.getName()) != null) {
							event.getRecipients().remove(p);
						}
					}
				}
			}

		}
	}
}
