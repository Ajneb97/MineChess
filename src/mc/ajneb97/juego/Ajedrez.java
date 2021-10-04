package mc.ajneb97.juego;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Ajedrez {

	public static ArrayList<MovimientoPosible> getMovimientosPosibles(Partida partida,Pieza[][] tablero,int x,int y,boolean enroqueBloqueado){
		Pieza pieza = tablero[y][x];
		if(pieza.getTipo().equals("caballo")) {
			return getMovimientosPosiblesCaballo(partida,tablero,x,y);
		}else if(pieza.getTipo().equals("peon")) {
			return getMovimientosPosiblesPeon(partida,tablero,x,y);
		}else if(pieza.getTipo().equals("torre")) {
			return getMovimientosPosiblesTorre(partida,tablero,x,y);
		}else if(pieza.getTipo().equals("alfil")) {
			return getMovimientosPosiblesAlfil(partida,tablero,x,y);
		}else if(pieza.getTipo().equals("rey")) {
			return getMovimientosPosiblesRey(partida,tablero,x,y,enroqueBloqueado);
		}else if(pieza.getTipo().equals("reina")) {
			return getMovimientosPosiblesReina(partida,tablero,x,y);
		}
		else {
			return null;
		}
	}
	
	public static ArrayList<MovimientoPosible> getMovimientosPosiblesCaballo(Partida partida,Pieza[][] tablero,int x,int y){
		ArrayList<MovimientoPosible> locations = new ArrayList<MovimientoPosible>();
		Pieza piezaOriginal = tablero[y][x];
		int xNueva = x+1;
		int yNueva = y+2;
		if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
			Pieza posicionNueva = tablero[yNueva][xNueva];
			if(posicionNueva == null || !posicionNueva.getColor().equals(piezaOriginal.getColor())) {
				locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
			}
		}
		
		xNueva = x-1;
		yNueva = y+2;
		if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
			Pieza posicionNueva = tablero[yNueva][xNueva];
			if(posicionNueva == null || !posicionNueva.getColor().equals(piezaOriginal.getColor())) {
				locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
			}
		}
		
		xNueva = x+2;
		yNueva = y+1;
		if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
			Pieza posicionNueva = tablero[yNueva][xNueva];
			if(posicionNueva == null || !posicionNueva.getColor().equals(piezaOriginal.getColor())) {
				locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
			}
		}
		
		xNueva = x+2;
		yNueva = y-1;
		if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
			Pieza posicionNueva = tablero[yNueva][xNueva];
			if(posicionNueva == null || !posicionNueva.getColor().equals(piezaOriginal.getColor())) {
				locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
			}
		}
		
		xNueva = x-2;
		yNueva = y+1;
		if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
			Pieza posicionNueva = tablero[yNueva][xNueva];
			if(posicionNueva == null || !posicionNueva.getColor().equals(piezaOriginal.getColor())) {
				locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
			}
		}
		
		xNueva = x-2;
		yNueva = y-1;
		if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
			Pieza posicionNueva = tablero[yNueva][xNueva];
			if(posicionNueva == null || !posicionNueva.getColor().equals(piezaOriginal.getColor())) {
				locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
			}
		}
		
		xNueva = x-1;
		yNueva = y-2;
		if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
			Pieza posicionNueva = tablero[yNueva][xNueva];
			if(posicionNueva == null || !posicionNueva.getColor().equals(piezaOriginal.getColor())) {
				locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
			}
		}
		
		xNueva = x+1;
		yNueva = y-2;
		if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
			Pieza posicionNueva = tablero[yNueva][xNueva];
			if(posicionNueva == null || !posicionNueva.getColor().equals(piezaOriginal.getColor())) {
				locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
			}
		}
		
		return locations;
	}
	
	public static ArrayList<MovimientoPosible> getMovimientosPosiblesPeon(Partida partida,Pieza[][] tablero,int x,int y){
		ArrayList<MovimientoPosible> locations = new ArrayList<MovimientoPosible>();
		Pieza piezaOriginal = tablero[y][x];
		if(piezaOriginal.getColor().equals("b")) {
			//En-passant (comprueba a los lados)
			int xNueva = x-1;
			int yNueva = y;
			if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
				Pieza posicionNueva = tablero[yNueva][xNueva];
				if(posicionNueva != null && posicionNueva.getTipo().equals("peon") && !posicionNueva.getColor().equals(piezaOriginal.getColor())
						&& posicionNueva.isEnPassant()) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva+1),piezaOriginal));
				}
			}
			xNueva = x+1;
			yNueva = y;
			if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
				Pieza posicionNueva = tablero[yNueva][xNueva];
				if(posicionNueva != null && posicionNueva.getTipo().equals("peon") && !posicionNueva.getColor().equals(piezaOriginal.getColor())
						&& posicionNueva.isEnPassant()) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva+1),piezaOriginal));
				}
			}
			
			
			xNueva = x;
			yNueva = y+1;
			boolean bloqueadoAdelante = false;
			
			if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
				Pieza posicionNueva = tablero[yNueva][xNueva];
				if(posicionNueva == null) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
				}else {
					bloqueadoAdelante = true;
				}
			}
			if(!bloqueadoAdelante) {
				xNueva = x;
				yNueva = y+2;
				if(y == 1 && xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
					Pieza posicionNueva = tablero[yNueva][xNueva];
					if(posicionNueva == null) {
						locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
					}
				}
			}
			
			xNueva = x+1;
			yNueva = y+1;
			if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
				Pieza posicionNueva = tablero[yNueva][xNueva];
				if(posicionNueva != null && !posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
				}
			}
			xNueva = x-1;
			yNueva = y+1;
			if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
				Pieza posicionNueva = tablero[yNueva][xNueva];
				if(posicionNueva != null && !posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
				}
			}
		}else {
			//En-passant (comprueba a los lados)
			int xNueva = x-1;
			int yNueva = y;
			if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
				Pieza posicionNueva = tablero[yNueva][xNueva];
				if(posicionNueva != null && posicionNueva.getTipo().equals("peon") && !posicionNueva.getColor().equals(piezaOriginal.getColor())
						&& posicionNueva.isEnPassant()) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva-1),piezaOriginal));
				}
			}
			xNueva = x+1;
			yNueva = y;
			if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
				Pieza posicionNueva = tablero[yNueva][xNueva];
				if(posicionNueva != null && posicionNueva.getTipo().equals("peon") && !posicionNueva.getColor().equals(piezaOriginal.getColor())
						&& posicionNueva.isEnPassant()) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva-1),piezaOriginal));
				}
			}
			
			xNueva = x;
			yNueva = y-1;
			boolean bloqueadoAdelante = false;
			if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
				Pieza posicionNueva = tablero[yNueva][xNueva];
				if(posicionNueva == null) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
				}else {
					bloqueadoAdelante = true;
				}
			}
			if(!bloqueadoAdelante) {
				xNueva = x;
				yNueva = y-2;
				if(y == 6 && xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
					Pieza posicionNueva = tablero[yNueva][xNueva];
					if(posicionNueva == null) {
						locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
					}
				}
			}
			xNueva = x+1;
			yNueva = y-1;
			if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
				Pieza posicionNueva = tablero[yNueva][xNueva];
				if(posicionNueva != null && !posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
				}
			}
			xNueva = x-1;
			yNueva = y-1;
			if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
				Pieza posicionNueva = tablero[yNueva][xNueva];
				if(posicionNueva != null && !posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
				}
			}
		}
		return locations;
	}
	
	public static ArrayList<MovimientoPosible> getMovimientosPosiblesTorre(Partida partida,Pieza[][] tablero,int x,int y){
		ArrayList<MovimientoPosible> locations = new ArrayList<MovimientoPosible>();
		Pieza piezaOriginal = tablero[y][x];
		for(int i=1;i<8;i++) {
			int xNueva = x+i;
			if(xNueva <= tablero.length-1 && xNueva >= 0) {
				Pieza posicionNueva = tablero[y][xNueva];
				if(posicionNueva == null) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, y),piezaOriginal));
				}else if(!posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, y),piezaOriginal));
					break;
				}else if(posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					break;
				}
			}
		}
		for(int i=1;i<8;i++) {
			int xNueva = x-i;
			if(xNueva <= tablero.length-1 && xNueva >= 0) {
				Pieza posicionNueva = tablero[y][xNueva];
				if(posicionNueva == null) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, y),piezaOriginal));
				}else if(!posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, y),piezaOriginal));
					break;
				}else if(posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					break;
				}
			}
		}
		for(int i=1;i<8;i++) {
			int yNueva = y+i;
			if(yNueva <= tablero.length-1 && yNueva >= 0) {
				Pieza posicionNueva = tablero[yNueva][x];
				if(posicionNueva == null) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(x, yNueva),piezaOriginal));
				}else if(!posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(x, yNueva),piezaOriginal));
					break;
				}else if(posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					break;
				}
			}
		}
		for(int i=1;i<8;i++) {
			int yNueva = y-i;
			if(yNueva <= tablero.length-1 && yNueva >= 0) {
				Pieza posicionNueva = tablero[yNueva][x];
				if(posicionNueva == null) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(x, yNueva),piezaOriginal));
				}else if(!posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(x, yNueva),piezaOriginal));
					break;
				}else if(posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					break;
				}
			}
		}
		return locations;
	}
	
	public static ArrayList<MovimientoPosible> getMovimientosPosiblesAlfil(Partida partida,Pieza[][] tablero,int x,int y){
		ArrayList<MovimientoPosible> locations = new ArrayList<MovimientoPosible>();
		Pieza piezaOriginal = tablero[y][x];
		for(int i=1;i<8;i++) {
			int xNueva = x+i;
			int yNueva = y+i;
			if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
				Pieza posicionNueva = tablero[yNueva][xNueva];
				if(posicionNueva == null) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
				}else if(!posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
					break;
				}else if(posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					break;
				}
			}
		}
		for(int i=1;i<8;i++) {
			int xNueva = x-i;
			int yNueva = y+i;
			if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
				Pieza posicionNueva = tablero[yNueva][xNueva];
				if(posicionNueva == null) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
				}else if(!posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
					break;
				}else if(posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					break;
				}
			}
		}
		for(int i=1;i<8;i++) {
			int xNueva = x+i;
			int yNueva = y-i;
			if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
				Pieza posicionNueva = tablero[yNueva][xNueva];
				if(posicionNueva == null) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
				}else if(!posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
					break;
				}else if(posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					break;
				}
			}
		}
		for(int i=1;i<8;i++) {
			int xNueva = x-i;
			int yNueva = y-i;
			if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
				Pieza posicionNueva = tablero[yNueva][xNueva];
				if(posicionNueva == null) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
				}else if(!posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
					break;
				}else if(posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					break;
				}
			}
		}
		return locations;
	}
	
	public static ArrayList<MovimientoPosible> getMovimientosPosiblesRey(Partida partida,Pieza[][] tablero,int x,int y, boolean enroqueBloqueado){
		ArrayList<MovimientoPosible> locations = new ArrayList<MovimientoPosible>();
		Pieza piezaOriginal = tablero[y][x];
		int xNueva = x;
		int yNueva = y-1;
		if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
			Pieza posicionNueva = tablero[yNueva][xNueva];
			if(posicionNueva == null || !posicionNueva.getColor().equals(piezaOriginal.getColor())) {
				locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
			}
		}
		xNueva = x+1;
		yNueva = y;
		if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
			Pieza posicionNueva = tablero[yNueva][xNueva];
			if(posicionNueva == null || !posicionNueva.getColor().equals(piezaOriginal.getColor())) {
				locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
			}
		}
		xNueva = x-1;
		yNueva = y;
		if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
			Pieza posicionNueva = tablero[yNueva][xNueva];
			if(posicionNueva == null || !posicionNueva.getColor().equals(piezaOriginal.getColor())) {
				locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
			}
		}
		xNueva = x;
		yNueva = y+1;
		if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
			Pieza posicionNueva = tablero[yNueva][xNueva];
			if(posicionNueva == null || !posicionNueva.getColor().equals(piezaOriginal.getColor())) {
				locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
			}
		}
		xNueva = x+1;
		yNueva = y+1;
		if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
			Pieza posicionNueva = tablero[yNueva][xNueva];
			if(posicionNueva == null || !posicionNueva.getColor().equals(piezaOriginal.getColor())) {
				locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
			}
		}
		xNueva = x+1;
		yNueva = y-1;
		if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
			Pieza posicionNueva = tablero[yNueva][xNueva];
			if(posicionNueva == null || !posicionNueva.getColor().equals(piezaOriginal.getColor())) {
				locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
			}
		}
		xNueva = x-1;
		yNueva = y+1;
		if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
			Pieza posicionNueva = tablero[yNueva][xNueva];
			if(posicionNueva == null || !posicionNueva.getColor().equals(piezaOriginal.getColor())) {
				locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
			}
		}
		xNueva = x-1;
		yNueva = y-1;
		if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
			Pieza posicionNueva = tablero[yNueva][xNueva];
			if(posicionNueva == null || !posicionNueva.getColor().equals(piezaOriginal.getColor())) {
				locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
			}
		}
		
		//ENROQUE
		if(!enroqueBloqueado) {
			if(!piezaOriginal.seHaMovido()) {
				//CORTO
				for(int i=1;i<=3;i++) {
					xNueva = x-i;
					yNueva = y;
					if(i == 3) {
						Pieza posicionNueva = tablero[yNueva][xNueva];
						if(posicionNueva != null && posicionNueva.getTipo().equals("torre") && posicionNueva.getColor().equals(piezaOriginal.getColor())
								&& !posicionNueva.seHaMovido()) {
							locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
						}
					}else {
						Pieza posicionNueva = tablero[yNueva][xNueva];
						if(posicionNueva != null) {
							break;
						}
					}
				}
				
				//LARGO
				for(int i=1;i<=4;i++) {
					xNueva = x+i;
					yNueva = y;
					if(i == 4) {
						Pieza posicionNueva = tablero[yNueva][xNueva];
						if(posicionNueva != null && posicionNueva.getTipo().equals("torre") && posicionNueva.getColor().equals(piezaOriginal.getColor())
								&& !posicionNueva.seHaMovido()) {
							locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
						}
					}else {
						Pieza posicionNueva = tablero[yNueva][xNueva];
						if(posicionNueva != null) {
							break;
						}
					}
				}
				
				
			}
		}
		
		
		return locations;
	}
	
	public static ArrayList<MovimientoPosible> getMovimientosPosiblesReina(Partida partida,Pieza[][] tablero,int x,int y){
		ArrayList<MovimientoPosible> locations = new ArrayList<MovimientoPosible>();
		Pieza piezaOriginal = tablero[y][x];
		for(int i=1;i<8;i++) {
			int xNueva = x+i;
			int yNueva = y+i;
			if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
				Pieza posicionNueva = tablero[yNueva][xNueva];
				if(posicionNueva == null) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
				}else if(!posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
					break;
				}else if(posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					break;
				}
			}
		}
		for(int i=1;i<8;i++) {
			int xNueva = x-i;
			int yNueva = y+i;
			if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
				Pieza posicionNueva = tablero[yNueva][xNueva];
				if(posicionNueva == null) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
				}else if(!posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
					break;
				}else if(posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					break;
				}
			}
		}
		for(int i=1;i<8;i++) {
			int xNueva = x+i;
			int yNueva = y-i;
			if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
				Pieza posicionNueva = tablero[yNueva][xNueva];
				if(posicionNueva == null) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
				}else if(!posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
					break;
				}else if(posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					break;
				}
			}
		}
		for(int i=1;i<8;i++) {
			int xNueva = x-i;
			int yNueva = y-i;
			if(xNueva <= tablero.length-1 && yNueva <= tablero.length-1 && xNueva >= 0 && yNueva >= 0) {
				Pieza posicionNueva = tablero[yNueva][xNueva];
				if(posicionNueva == null) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
				}else if(!posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, yNueva),piezaOriginal));
					break;
				}else if(posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					break;
				}
			}
		}
		for(int i=1;i<8;i++) {
			int xNueva = x+i;
			if(xNueva <= tablero.length-1 && xNueva >= 0) {
				Pieza posicionNueva = tablero[y][xNueva];
				if(posicionNueva == null) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, y),piezaOriginal));
				}else if(!posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, y),piezaOriginal));
					break;
				}else if(posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					break;
				}
			}
		}
		for(int i=1;i<8;i++) {
			int xNueva = x-i;
			if(xNueva <= tablero.length-1 && xNueva >= 0) {
				Pieza posicionNueva = tablero[y][xNueva];
				if(posicionNueva == null) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, y),piezaOriginal));
				}else if(!posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(xNueva, y),piezaOriginal));
					break;
				}else if(posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					break;
				}
			}
		}
		for(int i=1;i<8;i++) {
			int yNueva = y+i;
			if(yNueva <= tablero.length-1 && yNueva >= 0) {
				Pieza posicionNueva = tablero[yNueva][x];
				if(posicionNueva == null) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(x, yNueva),piezaOriginal));
				}else if(!posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(x, yNueva),piezaOriginal));
					break;
				}else if(posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					break;
				}
			}
		}
		for(int i=1;i<8;i++) {
			int yNueva = y-i;
			if(yNueva <= tablero.length-1 && yNueva >= 0) {
				Pieza posicionNueva = tablero[yNueva][x];
				if(posicionNueva == null) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(x, yNueva),piezaOriginal));
				}else if(!posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					locations.add(new MovimientoPosible(partida.getLocationDesdeTablero(x, yNueva),piezaOriginal));
					break;
				}else if(posicionNueva.getColor().equals(piezaOriginal.getColor())) {
					break;
				}
			}
		}
		return locations;
	}
}
