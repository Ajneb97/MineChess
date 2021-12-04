package mc.ajneb97.managers;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import mc.ajneb97.MineChess;
import mc.ajneb97.juego.Jugador;
import mc.ajneb97.juego.Partida;
import mc.ajneb97.juego.Pieza;
import mc.ajneb97.juego.Tablero;
import mc.ajneb97.otros.Utilidades;
import net.md_5.bungee.api.ChatColor;

public class InventarioCoronacion implements Listener {

	private MineChess plugin;
	public InventarioCoronacion(MineChess plugin) {
		this.plugin = plugin;
	}
	
	public static void crearInventario(Jugador jugador,MineChess plugin) {
		FileConfiguration config = plugin.getConfig();
		Inventory inv = Bukkit.createInventory(null, 9, ChatColor.translateAlternateColorCodes('&', config.getString("Messages.pawnPromotionInventoryTitle")));
		
		ItemStack item = null;
		
		//Torre
		if(Bukkit.getVersion().contains("1.13") || Bukkit.getVersion().contains("1.14") || Bukkit.getVersion().contains("1.15")
				|| Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17") || Bukkit.getVersion().contains("1.18")) {
			item = new ItemStack(Material.valueOf("PLAYER_HEAD"),1);
		}else {
			item = new ItemStack(Material.SKULL_ITEM,1,(short) 3);
		}
		item = Utilidades.getCabeza(item, "5e193aa2-292e-43c6-b92b-e823f6e0cc1e", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjQ1NTlkNzU0NjRiMmU0MGE1MThlNGRlOGU2Y2YzMDg1ZjBhM2NhMGIxYjcwMTI2MTRjNGNkOTZmZWQ2MDM3OCJ9fX0=");
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("Messages.promoteItemName").replace("%piece%", config.getString("Messages.pieceRook"))));
		item.setItemMeta(meta);
		inv.setItem(1, item);
		
		//Caballo
		if(Bukkit.getVersion().contains("1.13") || Bukkit.getVersion().contains("1.14") || Bukkit.getVersion().contains("1.15")
				|| Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17") || Bukkit.getVersion().contains("1.18")) {
			item = new ItemStack(Material.valueOf("PLAYER_HEAD"),1);
		}else {
			item = new ItemStack(Material.SKULL_ITEM,1,(short) 3);
		}
		item = Utilidades.getCabeza(item, "022202fd-9546-4492-b8b6-b768e95701c2", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2JiNGIyODg5OTFlZmI4Y2EwNzQzYmVjY2VmMzEyNThiMzFkMzlmMjQ5NTFlZmIxYzljMThhNDE3YmE0OGY5In19fQ==");
		meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("Messages.promoteItemName").replace("%piece%", config.getString("Messages.pieceKnight"))));
		item.setItemMeta(meta);
		inv.setItem(3, item);		
		
		//Alfil
		if(Bukkit.getVersion().contains("1.13") || Bukkit.getVersion().contains("1.14") || Bukkit.getVersion().contains("1.15")
				|| Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17") || Bukkit.getVersion().contains("1.18")) {
			item = new ItemStack(Material.valueOf("PLAYER_HEAD"),1);
		}else {
			item = new ItemStack(Material.SKULL_ITEM,1,(short) 3);
		}
		if(jugador.getColor().equals("b")) {
			item = Utilidades.getCabeza(item, "eb1fc1a8-763e-442f-bf10-302b3beebb32", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTI2Yjc3MjMyOWNmMzJmODY0M2M0OTI4NjI2YjZhMzI1MjMzZmY2MWFhOWM3NzI1ODczYTRiZDY2ZGIzZDY5MiJ9fX0=");
		}else {
			item = Utilidades.getCabeza(item, "c7b9f611-64c6-4e9c-ac97-8dedf8b97e17", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjZjNWVjYWM5NDJjNzdiOTVhYjQ2MjBkZjViODVlMzgwNjRjOTc0ZjljNWM1NzZiODQzNjIyODA2YTQ1NTcifX19");
		}
		
		meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("Messages.promoteItemName").replace("%piece%", config.getString("Messages.pieceBishop"))));
		item.setItemMeta(meta);
		inv.setItem(5, item);		
		
		//Reina
		if(Bukkit.getVersion().contains("1.13") || Bukkit.getVersion().contains("1.14") || Bukkit.getVersion().contains("1.15")
				|| Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17") || Bukkit.getVersion().contains("1.18")) {
			item = new ItemStack(Material.valueOf("PLAYER_HEAD"),1);
		}else {
			item = new ItemStack(Material.SKULL_ITEM,1,(short) 3);
		}
		item = Utilidades.getCabeza(item, "fdea850d-ae8b-4e10-8b03-6883494ae266", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTRiZjg5M2ZjNmRlZmFkMjE4Zjc4MzZlZmVmYmU2MzZmMWMyY2MxYmI2NTBjODJmY2NkOTlmMmMxZWU2In19fQ==");
		meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("Messages.promoteItemName").replace("%piece%", config.getString("Messages.pieceQueen"))));
		item.setItemMeta(meta);
		inv.setItem(7, item);	
		
		jugador.getJugador().openInventory(inv);
	}
	
	@EventHandler
	public void clickearInventarioCoronacion(InventoryClickEvent event) {
		FileConfiguration config = plugin.getConfig();
		String pathInventory = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.pawnPromotionInventoryTitle"));
		String pathInventoryM = ChatColor.stripColor(pathInventory);
		if(ChatColor.stripColor(event.getView().getTitle()).equals(pathInventoryM)){
			if(event.getCurrentItem() == null){
				event.setCancelled(true);
				return;
			}
			if((event.getSlotType() == null)){
				event.setCancelled(true);
				return;
			}else{
				Player jugador = (Player) event.getWhoClicked();
				event.setCancelled(true);
				if(event.getClickedInventory().equals(jugador.getOpenInventory().getTopInventory())) {
					Partida partida = plugin.getPartidaJugador(jugador.getName());
					if(partida != null) {
						Jugador j = partida.getJugador(jugador.getName());
						if(j.estaCoronandoPeon()) {
							int slot = event.getSlot();
							if(slot == 1 || slot == 3 || slot == 5 || slot == 7) {
								coronacion(partida,j,slot,config,plugin);
							}
						}
					}
				}
			}
		}
	}
	
	public static void coronacion(Partida partida,Jugador j,int slot,FileConfiguration config,MineChess plugin) {
		Pieza seleccionada = j.getPiezaSeleccionada();
		Location l = partida.getLocationDesdePieza(seleccionada).clone();
		int[] posPieza = partida.getPosicionDesdeCoordenada(l);
		l.setY(partida.getEsquina1().getY());
		l.add(0,1,0);
		String prefix = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.prefix"));
		ArrayList<Jugador> jugadores = partida.getJugadores();
		if(slot == 1) {
			//torre
			Tablero.eliminarPieza(l);
			Location l1h = l.clone().add(0.5,0,0.5);
			Tablero.eliminarPiezaHolograma(plugin, l1h,config);
			
			Tablero.crearPieza(l, "torre", seleccionada.getColor());
			Location l2h = l.clone().add(0.5,4.7,0.5);
			Tablero.crearPiezaHolograma(plugin, l2h, "torre", config);
			
			Pieza nuevaPieza = new Pieza(seleccionada.getId(),"torre",seleccionada.getColor(),true,false);
			partida.reemplazarPieza(posPieza, nuevaPieza);
			for(Jugador jug : jugadores) {
				jug.getJugador().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.promotion")
						.replace("%player%", j.getJugador().getName()).replace("%piece%", config.getString("Messages.pieceRook"))));	
			}
		}else if(slot == 3) {
			//caballo
			Tablero.eliminarPieza(l);
			Location l1h = l.clone().add(0.5,0,0.5);
			Tablero.eliminarPiezaHolograma(plugin, l1h,config);
			
			Tablero.crearPieza(l, "caballo", seleccionada.getColor());
			Location l2h = l.clone().add(0.5,4.7,0.5);
			Tablero.crearPiezaHolograma(plugin, l2h, "caballo", config);
			
			Pieza nuevaPieza = new Pieza(seleccionada.getId(),"caballo",seleccionada.getColor(),true,false);
			partida.reemplazarPieza(posPieza, nuevaPieza);
			for(Jugador jug : jugadores) {
				jug.getJugador().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.promotion")
						.replace("%player%",  j.getJugador().getName()).replace("%piece%", config.getString("Messages.pieceKnight"))));	
			}
		}else if(slot == 5) {
			//alfil
			Tablero.eliminarPieza(l);
			Location l1h = l.clone().add(0.5,0,0.5);
			Tablero.eliminarPiezaHolograma(plugin, l1h,config);
			
			Tablero.crearPieza(l, "alfil", seleccionada.getColor());
			Location l2h = l.clone().add(0.5,4.7,0.5);
			Tablero.crearPiezaHolograma(plugin, l2h, "alfil", config);
			
			Pieza nuevaPieza = new Pieza(seleccionada.getId(),"alfil",seleccionada.getColor(),true,false);
			partida.reemplazarPieza(posPieza, nuevaPieza);
			for(Jugador jug : jugadores) {
				jug.getJugador().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.promotion")
						.replace("%player%",  j.getJugador().getName()).replace("%piece%", config.getString("Messages.pieceBishop"))));	
			}
		}else if(slot == 7) {
			//reina
			Tablero.eliminarPieza(l);
			Location l1h = l.clone().add(0.5,0,0.5);
			Tablero.eliminarPiezaHolograma(plugin, l1h,config);
			
			Tablero.crearPieza(l, "reina", seleccionada.getColor());
			Location l2h = l.clone().add(0.5,4.7,0.5);
			Tablero.crearPiezaHolograma(plugin, l2h, "reina", config);
			
			Pieza nuevaPieza = new Pieza(seleccionada.getId(),"reina",seleccionada.getColor(),true,false);
			partida.reemplazarPieza(posPieza, nuevaPieza);
			for(Jugador jug : jugadores) {
				jug.getJugador().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Messages.promotion")
						.replace("%player%",  j.getJugador().getName()).replace("%piece%", config.getString("Messages.pieceQueen"))));	
			}
		}
			
		String[] separados = config.getString("Config.soundPromotion").split(";");
		try {
			Sound sound = Sound.valueOf(separados[0]);
			for(Jugador jug : jugadores) {
				jug.getJugador().playSound(jug.getJugador().getLocation(), sound, Float.valueOf(separados[1]), Float.valueOf(separados[2]));
			}
		}catch(Exception e) {
			Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',"&7Sound Name: &c"+separados[0]+" &7is not valid. Change it in the config!"));
		}
		
		j.setCoronandoPeon(false);
		j.getJugador().closeInventory();
		
		j.setPiezaSeleccionada(null);
		
		PartidaManager.cambiarTurno(partida,config,plugin);
	}
}
