package mc.ajneb97.juego;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Partida {

	private Jugador jugador1;
	private Jugador jugador2;
	private Pieza[][] tablero = new Pieza[8][8];
	private Estado estado;
	private String nombre;
	private Location esquina1;
	private int tiempoMaximo;
	private int tiempo;
	private Location spawn;
	private Jugador turno;
	private ArrayList<Jugador> espectadores;
	private int tiempoEnTerminar;
	
	public Partida(String nombre) {
		this.nombre = nombre;
		this.estado = Estado.DESACTIVADA;
		this.espectadores = new ArrayList<Jugador>();
		this.tiempo = 0;
		this.tiempoEnTerminar = 0;
		reiniciarPiezas();
	}
	
	public int getTiempoEnTerminar() {
		return this.tiempoEnTerminar;
	}
	
	public void setTiempoEnTerminar(int tiempo) {
		this.tiempoEnTerminar = tiempo;
	}
	
	public void agregarEspectador(Player espectador) {
		espectadores.add(new Jugador(espectador,true));
	}
	
	public void removerEspectador(String nombre) {
		for(int i=0;i<espectadores.size();i++) {
			if(espectadores.get(i).getJugador().getName().equals(nombre)) {
				espectadores.remove(i);
				break;
			}
		}
	}
	
	public int getTiempo() {
		return tiempo;
	}

	public void setTiempo(int tiempo) {
		this.tiempo = tiempo;
	}
	
	public void disminuirTiempo() {
		this.tiempo--;
	}

	public Jugador getTurno() {
		return turno;
	}

	public void setTurno(Jugador turno) {
		this.turno = turno;
	}

	public Location getSpawn() {
		return spawn;
	}

	public void setSpawn(Location spawn) {
		this.spawn = spawn;
	}

	public int getTiempoMaximo() {
		return tiempoMaximo;
	}

	public void setTiempoMaximo(int tiempoMaximo) {
		this.tiempoMaximo = tiempoMaximo;
	}

	public Location getEsquina1() {
		return esquina1;
	}

	public void setEsquina1(Location esquina1) {
		this.esquina1 = esquina1;
	}

	public ArrayList<Jugador> getJugadores(){
		ArrayList<Jugador> jugadores = new ArrayList<Jugador>();
		if(jugador1 != null) {
			jugadores.add(jugador1);
		}
		if(jugador2 != null) {
			jugadores.add(jugador2);
		}
		for(int i=0;i<espectadores.size();i++) {
			jugadores.add(espectadores.get(i));
		}
		
		return jugadores;
	}
	
	public void reiniciarPiezas() {
		for(int i=0;i<tablero.length;i++) {
			for(int c=0;c<tablero.length;c++) {
				tablero[i][c] = null;
			}
		}
		
		tablero[0][0] = new Pieza(1,"torre","b",false,false);
		tablero[0][1] = new Pieza(2,"caballo","b",false,false);
		tablero[0][2] = new Pieza(3,"alfil","b",false,false);
		tablero[0][3] = new Pieza(4,"rey","b",false,false);
		tablero[0][4] = new Pieza(5,"reina","b",false,false);
		tablero[0][5] = new Pieza(6,"alfil","b",false,false);
		tablero[0][6] = new Pieza(7,"caballo","b",false,false);
		tablero[0][7] = new Pieza(8,"torre","b",false,false);
		tablero[1][0] = new Pieza(9,"peon","b",false,false);
		tablero[1][1] = new Pieza(10,"peon","b",false,false);
		tablero[1][2] = new Pieza(11,"peon","b",false,false);
		tablero[1][3] = new Pieza(12,"peon","b",false,false);
		tablero[1][4] = new Pieza(13,"peon","b",false,false);
		tablero[1][5] = new Pieza(14,"peon","b",false,false);
		tablero[1][6] = new Pieza(15,"peon","b",false,false);
		tablero[1][7] = new Pieza(16,"peon","b",false,false);
		
		tablero[7][0] = new Pieza(17,"torre","n",false,false);
		tablero[7][1] = new Pieza(18,"caballo","n",false,false);
		tablero[7][2] = new Pieza(19,"alfil","n",false,false);
		tablero[7][3] = new Pieza(20,"rey","n",false,false);
		tablero[7][4] = new Pieza(21,"reina","n",false,false);
		tablero[7][5] = new Pieza(22,"alfil","n",false,false);
		tablero[7][6] = new Pieza(23,"caballo","n",false,false);
		tablero[7][7] = new Pieza(24,"torre","n",false,false);
		tablero[6][0] = new Pieza(25,"peon","n",false,false);
		tablero[6][1] = new Pieza(26,"peon","n",false,false);
		tablero[6][2] = new Pieza(27,"peon","n",false,false);
		tablero[6][3] = new Pieza(28,"peon","n",false,false);
		tablero[6][4] = new Pieza(29,"peon","n",false,false);
		tablero[6][5] = new Pieza(30,"peon","n",false,false);
		tablero[6][6] = new Pieza(31,"peon","n",false,false);
		tablero[6][7] = new Pieza(32,"peon","n",false,false);
	}
	
	public void moverPieza(int[] cInicial, int[] cFinal, boolean isEnPassant) {
		Pieza aux = tablero[cInicial[1]][cInicial[0]];
		Pieza nueva = new Pieza(aux.getId(),aux.getTipo(),aux.getColor(),true,isEnPassant);
		tablero[cFinal[1]][cFinal[0]] = nueva;
		tablero[cInicial[1]][cInicial[0]] = null;
	}
	
	public void reemplazarPieza(int[] coordenadas,Pieza nueva) {
		tablero[coordenadas[1]][coordenadas[0]] = nueva;
	}
	
	public void mostrarTablero() {
		for(int i=0;i<tablero.length;i++) {
			String linea = "";
			for(int c=0;c<tablero.length;c++) {
				if(tablero[i][c] == null) {
					linea = linea+"0 ";
				}else {
					linea = linea+"1 ";
				}
				
			}
			Bukkit.getConsoleSender().sendMessage(linea);
		}
	}

	public Jugador getJugador1() {
		return jugador1;
	}

	public void setJugador1(Jugador jugador1) {
		this.jugador1 = jugador1;
	}

	public Jugador getJugador2() {
		return jugador2;
	}

	public void setJugador2(Jugador jugador2) {
		this.jugador2 = jugador2;
	}

	public Pieza[][] getTablero() {
		return tablero;
	}

	public void setTablero(Pieza[][] tablero) {
		this.tablero = tablero;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public int[] getPosicionDesdeCoordenada(Location l) {
		// Si esquina es 231,3,63
		
		// Fix sera 232,3,64 (sumar de 3 en 3)
		// Si la location es 247,4,67
		Location fix = this.esquina1.clone().add(1,0,1);
		Location aux = fix.clone();
		for(int i=0;i<tablero.length;i++) {
			for(int c=0;c<tablero.length;c++) {
				if(l.getWorld().equals(aux.getWorld()) && l.getX() == aux.getX() && l.getZ() == aux.getZ()) {
					int[] nuevo = {c,i};
					return nuevo;
				}
				aux.add(3, 0, 0);
			}
			aux.add(-24,0,3);
		}	
		return null;
	}
	
	public Pieza getPiezaDesdeCoordenada(Location l) {
		// Si esquina es 231,3,63
		
		// Fix sera 232,3,64 (sumar de 3 en 3)
		// Si la location es 247,4,67
		Location fix = this.esquina1.clone().add(1,0,1);
		Location aux = fix.clone();
		for(int i=0;i<tablero.length;i++) {
			for(int c=0;c<tablero.length;c++) {
				if(l.getWorld().equals(aux.getWorld()) && l.getX() == aux.getX() && l.getZ() == aux.getZ()) {
					return this.tablero[i][c];
				}
				aux.add(3, 0, 0);
			}
			aux.add(-24,0,3);
		}	
		return null;
	}
	
	public Location getLocationRey(String color) {
		for(int i=0;i<tablero.length;i++) {
			for(int c=0;c<tablero.length;c++) {
				if(tablero[c][i] != null && tablero[c][i].getColor().equals(color) && tablero[c][i].getTipo().equals("rey")) {
					Pieza p = tablero[c][i];
					return getLocationDesdePieza(p);
				}
			}
		}
		return null;
	}
	
	public Location getLocationDesdeTablero(int x,int y) {
		Location fix = this.esquina1.clone().add(1,0,1);
		// Si Fix es 215,3,47
		// Y x=4 y y=1
		// Location nueva es 215+4*3,3,47+1*3 = 227,3,50
		int xNueva = fix.getBlockX()+(x*3);
		int zNueva = fix.getBlockZ()+(y*3);
		Location nueva = new Location(fix.getWorld(),xNueva,fix.getBlockY(),zNueva);
		return nueva;
	}
	
	public Location getLocationDesdePieza(Pieza pieza) {
		int[] pos = getPosicion(pieza);
		if(pos == null) {
			return null;
		}else {
			return getLocationDesdeTablero(pos[0],pos[1]);
		}
	}
	
	public int[] getPosicion(Pieza pieza) {
		for(int i=0;i<tablero.length;i++) {
			for(int c=0;c<tablero.length;c++) {
				if(tablero[c][i] != null && pieza.getId() == tablero[c][i].getId()) {
					int[] nuevo = {i,c};
					return nuevo;
				}
			}
		}
		return null;
	}
	
	public void agregarJugador(Jugador j) {
		if(this.jugador1 == null) {
			this.jugador1 = j;
		}else {
			this.jugador2 = j;
		}
	}
	
	public Jugador getJugador(String name) {
		if(jugador1 != null && jugador1.getJugador().getName().equals(name)) {
			return jugador1;
		}
		if(jugador2 != null && jugador2.getJugador().getName().equals(name)) {
			return jugador2;
		}
		for(int i=0;i<espectadores.size();i++) {
			if(espectadores.get(i).getJugador().getName().equals(name)) {
				return espectadores.get(i);
			}
		}
		return null;
	}
	
	public void removerJugador(String name) {
		if(jugador1 != null && jugador1.getJugador().getName().equals(name)) {
			jugador1 = null;
		}
		if(jugador2 != null && jugador2.getJugador().getName().equals(name)) {
			jugador2 = null;
		}
	}
	
	public int getCantidadActualJugadores() {
		int cantidad = 0;
		if(this.jugador1 != null) {
			cantidad++;
		}
		if(this.jugador2 != null) {
			cantidad++;
		}
		return cantidad;
	}
	
	public boolean estaLlena() {
		if(jugador1 != null && jugador2 != null) {
			return true;
		}else {
			return false;
		}
	}
	
	public boolean estaActivada() {
		if(!estado.equals(Estado.DESACTIVADA)) {
			return true;
		}else {
			return false;
		}
	}
	
	public boolean estaIniciada() {
		if(estado.equals(Estado.JUGANDO) || estado.equals(Estado.TERMINANDO)) {
			return true;
		}else {
			return false;
		}
	}
	
	public Jugador getGanador() {
		if(jugador1 == null) {
			return jugador2;
		}
		if(jugador2 == null) {
			return jugador1;
		}
		
		int puntos1 = jugador1.getPuntos();
		int puntos2 = jugador2.getPuntos();
		if(puntos1 > puntos2) {
			return jugador1;
		}else if(puntos2 > puntos1) {
			return jugador2;
		}else {
			return null; //empate
		}	
	}
	
}
