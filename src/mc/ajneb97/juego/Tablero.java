package mc.ajneb97.juego;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.VisibilityManager;

import mc.ajneb97.MineChess;
import mc.ajneb97.otros.Utilidades;

public class Tablero {

	@SuppressWarnings("deprecation")
	public static void construirCelda(Location l,String color) {
		Material m = null;
		if(color.equals("b")) {
			if(Bukkit.getVersion().contains("1.13") || Bukkit.getVersion().contains("1.14") || Bukkit.getVersion().contains("1.15")
					|| Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17")) {
				m = Material.valueOf("WHITE_WOOL");
			}else {
				m = Material.WOOL;
			}
			
			l.getBlock().setType(m);
			l.clone().add(1,0,0).getBlock().setType(m);
			l.clone().add(2,0,0).getBlock().setType(m);
			l.clone().add(0,0,1).getBlock().setType(m);
			l.clone().add(1,0,1).getBlock().setType(m);
			l.clone().add(2,0,1).getBlock().setType(m);
			l.clone().add(0,0,2).getBlock().setType(m);
			l.clone().add(1,0,2).getBlock().setType(m);
			l.clone().add(2,0,2).getBlock().setType(m);
		}else {
			if(Bukkit.getVersion().contains("1.13") || Bukkit.getVersion().contains("1.14") || Bukkit.getVersion().contains("1.15")
					|| Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17")) {
				m = Material.valueOf("GRAY_WOOL");
				l.getBlock().setType(m);
				l.clone().add(1,0,0).getBlock().setType(m);
				l.clone().add(2,0,0).getBlock().setType(m);
				l.clone().add(0,0,1).getBlock().setType(m);
				l.clone().add(1,0,1).getBlock().setType(m);
				l.clone().add(2,0,1).getBlock().setType(m);
				l.clone().add(0,0,2).getBlock().setType(m);
				l.clone().add(1,0,2).getBlock().setType(m);
				l.clone().add(2,0,2).getBlock().setType(m);
			}else {
				m = Material.valueOf("WOOL");
				l.getBlock().setType(m);
				l.getBlock().setData((byte) 8);
				Location l2 = l.clone().add(1,0,0);
				l2.getBlock().setType(m);
				l2.getBlock().setData((byte) 8);
				Location l3 = l.clone().add(2,0,0);
				l3.getBlock().setType(m);
				l3.getBlock().setData((byte) 8);
				Location l4 = l.clone().add(0,0,1);
				l4.getBlock().setType(m);
				l4.getBlock().setData((byte) 8);
				Location l5 = l.clone().add(1,0,1);
				l5.getBlock().setType(m);
				l5.getBlock().setData((byte) 8);
				Location l6 = l.clone().add(2,0,1);
				l6.getBlock().setType(m);
				l6.getBlock().setData((byte) 8);
				Location l7 = l.clone().add(0,0,2);
				l7.getBlock().setType(m);
				l7.getBlock().setData((byte) 8);
				Location l8 = l.clone().add(1,0,2);
				l8.getBlock().setType(m);
				l8.getBlock().setData((byte) 8);
				Location l9 = l.clone().add(2,0,2);
				l9.getBlock().setType(m);
				l9.getBlock().setData((byte) 8);
			}
		}
	}
	
	public static void construirTablero(Location l) {
		Location actual = l.clone().add(0,-1,0);
		String color = "b";
		for(int i=0;i<8;i++) {
			for(int c=0;c<8;c++) {
				construirCelda(actual,color);
				if(c != 7) {
					if(color.equals("b")) {
						color = "n";
					}else {
						color = "b";
					}
				}
				actual.add(3,0,0);
			}
			actual.add(-24,0,3);
		}
	}
	
	public static void crearPiezas(Location esquina) {
		Location nueva = esquina.clone().add(1,1,1);
		for(int i=0;i<8;i++) {
			if(i==0 || i==7) {
				crearPieza(nueva,"torre","b");
			}else if(i==1 || i==6) {
				crearPieza(nueva,"caballo","b");
			}else if(i==2 || i==5) {
				crearPieza(nueva,"alfil","b");
			}else if(i==3) {
				crearPieza(nueva,"rey","b");
			}else {
				crearPieza(nueva,"reina","b");
			}
			nueva.add(3,0,0);
		}
		
		nueva = esquina.clone().add(1,1,4);
		for(int i=0;i<8;i++) {
			crearPieza(nueva,"peon","b");
			nueva.add(3,0,0);
		}
		
		nueva = esquina.clone().add(1,1,19);
		for(int i=0;i<8;i++) {
			crearPieza(nueva,"peon","n");
			nueva.add(3,0,0);
		}
		
		nueva = esquina.clone().add(1,1,22);
		for(int i=0;i<8;i++) {
			if(i==0 || i==7) {
				crearPieza(nueva,"torre","n");
			}else if(i==1 || i==6) {
				crearPieza(nueva,"caballo","n");
			}else if(i==2 || i==5) {
				crearPieza(nueva,"alfil","n");
			}else if(i==3) {
				crearPieza(nueva,"rey","n");
			}else {
				crearPieza(nueva,"reina","n");
			}
			nueva.add(3,0,0);
		}
	}
	
	public static void crearPieza(Location l,String tipo,String color) {
		Material m = null;
		int rot = 0;
		if(color.equals("b")) {
			m = Material.IRON_BLOCK;
			rot = 8;
		}else {
			m = Material.COAL_BLOCK;
		}
		
		if(tipo.equals("torre")) {
			l.getBlock().setType(m);
			l.clone().add(0,1,0).getBlock().setType(m);
			Location l2 = l.clone().add(0,2,0);
			Utilidades.setSkullBlock(l2, "5e193aa2-292e-43c6-b92b-e823f6e0cc1e", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjQ1NTlkNzU0NjRiMmU0MGE1MThlNGRlOGU2Y2YzMDg1ZjBhM2NhMGIxYjcwMTI2MTRjNGNkOTZmZWQ2MDM3OCJ9fX0=", rot);
		}else if(tipo.equals("caballo")) {
			l.getBlock().setType(m);
			l.clone().add(0,1,0).getBlock().setType(m);
			Location l2 = l.clone().add(0,2,0);
			Utilidades.setSkullBlock(l2, "022202fd-9546-4492-b8b6-b768e95701c2", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2JiNGIyODg5OTFlZmI4Y2EwNzQzYmVjY2VmMzEyNThiMzFkMzlmMjQ5NTFlZmIxYzljMThhNDE3YmE0OGY5In19fQ==", rot);
		}else if(tipo.equals("alfil")) {
			l.getBlock().setType(m);
			l.clone().add(0,1,0).getBlock().setType(m);
			Location l2 = l.clone().add(0,2,0);
			if(color.equals("b")) {
				Utilidades.setSkullBlock(l2, "eb1fc1a8-763e-442f-bf10-302b3beebb32", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTI2Yjc3MjMyOWNmMzJmODY0M2M0OTI4NjI2YjZhMzI1MjMzZmY2MWFhOWM3NzI1ODczYTRiZDY2ZGIzZDY5MiJ9fX0=", rot);
			}else {
				Utilidades.setSkullBlock(l2, "c766a367-5d10-4b8f-a2fe-d3796bfbfcc1", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDdmNTc2NmQyOTI4ZGMwZGYxYjM0MDRjM2JkMDczYzk0NzZkMjZjODA1NzNiMDMzMmU3Y2NlNzNkZjE1NDgyYSJ9fX0=", rot);
			}
		}else if(tipo.equals("rey")) {
			l.getBlock().setType(m);
			l.clone().add(0,1,0).getBlock().setType(m);
			l.clone().add(0,2,0).getBlock().setType(m);
			l.clone().add(0,3,0).getBlock().setType(Material.DIAMOND_BLOCK);
		}else if(tipo.equals("reina")) {
			l.getBlock().setType(m);
			l.clone().add(0,1,0).getBlock().setType(m);
			l.clone().add(0,2,0).getBlock().setType(m);
			Location l2 = l.clone().add(0,3,0);
			Utilidades.setSkullBlock(l2, "fdea850d-ae8b-4e10-8b03-6883494ae266", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTRiZjg5M2ZjNmRlZmFkMjE4Zjc4MzZlZmVmYmU2MzZmMWMyY2MxYmI2NTBjODJmY2NkOTlmMmMxZWU2In19fQ==", rot);
		}else if(tipo.equals("peon")) {
			l.getBlock().setType(m);
			Location l2 = l.clone().add(0,1,0);
			if(color.equals("b")) {
				Utilidades.setSkullBlock(l2, "eb1fc1a8-763e-442f-bf10-302b3beebb32", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTI2Yjc3MjMyOWNmMzJmODY0M2M0OTI4NjI2YjZhMzI1MjMzZmY2MWFhOWM3NzI1ODczYTRiZDY2ZGIzZDY5MiJ9fX0=", rot);
			}else {
				Utilidades.setSkullBlock(l2, "c766a367-5d10-4b8f-a2fe-d3796bfbfcc1", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDdmNTc2NmQyOTI4ZGMwZGYxYjM0MDRjM2JkMDczYzk0NzZkMjZjODA1NzNiMDMzMmU3Y2NlNzNkZjE1NDgyYSJ9fX0=", rot);
			}
		}
	}
	
	public static void eliminarPieza(Location l) {
		l.getBlock().setType(Material.AIR);
		l.clone().add(0,1,0).getBlock().setType(Material.AIR);
		l.clone().add(0,2,0).getBlock().setType(Material.AIR);
		l.clone().add(0,3,0).getBlock().setType(Material.AIR);
	}
	
	public static void eliminarPiezas(Location esquina) {
		Location nueva = esquina.clone().add(1,1,1);
		for(int x=0;x<8;x++) {
			for(int y=0;y<8;y++) {
				nueva.getBlock().setType(Material.AIR);
				nueva.clone().add(0,1,0).getBlock().setType(Material.AIR);
				nueva.clone().add(0,2,0).getBlock().setType(Material.AIR);
				nueva.clone().add(0,3,0).getBlock().setType(Material.AIR);
				nueva.add(3,0,0);
			}
			nueva.add(-24,0,3);
			//Bukkit.getConsoleSender().sendMessage("eliminando en: "+nueva.getX()+" "+nueva.getY()+" "+nueva.getZ());
		}
	}
	
	public static void crearHologramasPiezas(Location esquina,MineChess plugin) {
		Location nueva = esquina.clone().add(1.5,5.7,1.5);
		FileConfiguration config = plugin.getConfig();
		for(int i=0;i<8;i++) {
			if(i==0 || i==7) {
				crearPiezaHolograma(plugin,nueva,"torre",config);
			}else if(i==1 || i==6) {
				crearPiezaHolograma(plugin,nueva,"caballo",config);
			}else if(i==2 || i==5) {
				crearPiezaHolograma(plugin,nueva,"alfil",config);
			}else if(i==3) {
				crearPiezaHolograma(plugin,nueva,"rey",config);
			}else {
				crearPiezaHolograma(plugin,nueva,"reina",config);
			}
			nueva.add(3,0,0);
		}
		
		nueva = esquina.clone().add(1.5,4,4.5);
		for(int i=0;i<8;i++) {
			crearPiezaHolograma(plugin,nueva,"peon",config);
			nueva.add(3,0,0);
		}
		
		nueva = esquina.clone().add(1.5,4,19.5);
		for(int i=0;i<8;i++) {
			crearPiezaHolograma(plugin,nueva,"peon",config);
			nueva.add(3,0,0);
		}
		
		nueva = esquina.clone().add(1.5,5.7,22.5);
		for(int i=0;i<8;i++) {
			if(i==0 || i==7) {
				crearPiezaHolograma(plugin,nueva,"torre",config);
			}else if(i==1 || i==6) {
				crearPiezaHolograma(plugin,nueva,"caballo",config);
			}else if(i==2 || i==5) {
				crearPiezaHolograma(plugin,nueva,"alfil",config);
			}else if(i==3) {
				crearPiezaHolograma(plugin,nueva,"rey",config);
			}else {
				crearPiezaHolograma(plugin,nueva,"reina",config);
			}
			nueva.add(3,0,0);
		}
	}
	
	public static void crearPiezaHolograma(MineChess plugin,Location l,String tipo,FileConfiguration config) {
		if(Bukkit.getServer().getPluginManager().getPlugin("HolographicDisplays") != null && config.getString("Config.piece_holograms_enabled").equals("true")) {
			Hologram hologram = HologramsAPI.createHologram(plugin, l);
			VisibilityManager visibilityManager = hologram.getVisibilityManager();
			visibilityManager.setVisibleByDefault(false);
			if(tipo.equals("torre")) {
				hologram.insertTextLine(0, ChatColor.translateAlternateColorCodes('&',config.getString("Messages.pieceRook")));
			}else if(tipo.equals("caballo")) {
				hologram.insertTextLine(0, ChatColor.translateAlternateColorCodes('&',config.getString("Messages.pieceKnight")));
			}else if(tipo.equals("alfil")) {
				hologram.insertTextLine(0, ChatColor.translateAlternateColorCodes('&',config.getString("Messages.pieceBishop")));
			}else if(tipo.equals("rey")) {
				hologram.insertTextLine(0, ChatColor.translateAlternateColorCodes('&',config.getString("Messages.pieceKing")));
			}else if(tipo.equals("reina")) {
				hologram.insertTextLine(0, ChatColor.translateAlternateColorCodes('&',config.getString("Messages.pieceQueen")));
			}else if(tipo.equals("peon")) {
				hologram.insertTextLine(0, ChatColor.translateAlternateColorCodes('&',config.getString("Messages.piecePawn")));
			}
			visibilityManager.setVisibleByDefault(true);
		}
		
	}
	
	public static void eliminarPiezaHolograma(MineChess plugin,Location l,FileConfiguration config) {
		if(Bukkit.getServer().getPluginManager().getPlugin("HolographicDisplays") != null && config.getString("Config.piece_holograms_enabled").equals("true")) {
			for(Hologram h : HologramsAPI.getHolograms(plugin)) {
				if(h.getX() == l.getX() && h.getZ() == l.getZ() && h.getWorld().getName().equals(l.getWorld().getName())) {
					h.delete();
				}
			}
		}
		
	}
	
	public static void eliminarPiezasHologramas(MineChess plugin,Location esquina,FileConfiguration config,boolean cerrandoServer) {
		if(Bukkit.getServer().getPluginManager().getPlugin("HolographicDisplays") != null && config.getString("Config.piece_holograms_enabled").equals("true")
				&& !cerrandoServer) {
			Location nueva = esquina.clone().add(1.5,1,1.5);
			for(int x=0;x<8;x++) {
				for(int y=0;y<8;y++) {
					for(Hologram h : HologramsAPI.getHolograms(plugin)) {
						if(h.getX() == nueva.getX() && h.getZ() == nueva.getZ() && h.getWorld().getName().equals(nueva.getWorld().getName())) {
							h.delete();
						}
					}
					nueva.add(3,0,0);
				}
				nueva.add(-24,0,3);
				//Bukkit.getConsoleSender().sendMessage("eliminando en: "+nueva.getX()+" "+nueva.getY()+" "+nueva.getZ());
			}
		}
		
	}
}

