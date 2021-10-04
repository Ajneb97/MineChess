package mc.ajneb97.juego;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;


public class Jugador {

	private Player jugador;
	private int puntos;
	private String color;
	private ElementosGuardados guardados;
	private Pieza piezaObservada;
	private Pieza piezaSeleccionada;
	private ArrayList<MovimientoPosible> movimientosPiezaSeleccionada;
	private int[] celdaObservada;
	private boolean coronandoPeon;
	private boolean enJaque;
	private boolean espectador;
	private int tiempo;
	private long millisAntes; //Tiempo al iniciar la partida
	
	public Jugador(Player jugador,boolean espectador) {
		this.jugador = jugador;
		this.tiempo = 0;
		this.espectador = espectador;
		this.guardados = new ElementosGuardados(jugador.getLocation().clone(),jugador.getInventory().getContents().clone(),jugador.getEquipment().getArmorContents().clone(),jugador.getGameMode()
				,jugador.getExp(),jugador.getLevel(),jugador.getFoodLevel(),jugador.getHealth(),jugador.getMaxHealth(),jugador.getAllowFlight(),jugador.isFlying());
	}
	
	public long getMillisAntes() {
		return this.millisAntes;
	}
	
	public void setMillisAntes() {
		this.millisAntes = System.currentTimeMillis();
	}
	
	public int getTiempo() {
		return this.tiempo;
	}
	
	public void setTiempo(int tiempo) {
		this.tiempo = tiempo;
	}
	
	public void disminuirTiempo() {
		this.tiempo--;
	}
	
	public boolean esEspectador() {
		return this.espectador;
	}
	
	public void setEspectador(boolean espectador) {
		this.espectador = espectador;
	}
	
	public void setMovimientos(ArrayList<MovimientoPosible> movimientos) {
		this.movimientosPiezaSeleccionada = movimientos;
	}
	
	public ArrayList<MovimientoPosible> getMovimientos(){
		return this.movimientosPiezaSeleccionada;
	}
	
	public boolean enJaque() {
		return this.enJaque;
	}
	
	public void setEnJaque(boolean enJaque) {
		this.enJaque = enJaque;
	}
	
	public boolean estaCoronandoPeon() {
		return this.coronandoPeon;
	}
	
	public void setCoronandoPeon(boolean coronando) {
		this.coronandoPeon = coronando;
	}

	public int[] getCeldaObservada() {
		return celdaObservada;
	}

	public void setCeldaObservada(int[] celdaObservada) {
		this.celdaObservada = celdaObservada;
	}

	public Pieza getPiezaSeleccionada() {
		return piezaSeleccionada;
	}
	public void setPiezaSeleccionada(Pieza piezaSeleccionada) {
		this.piezaSeleccionada = piezaSeleccionada;
	}
	public void setPiezaObservada(Pieza piezaObservada) {
		this.piezaObservada = piezaObservada;
	}
	public Pieza getPiezaObservada() {
		return this.piezaObservada;
	}
	public Player getJugador() {
		return jugador;
	}
	public void setJugador(Player jugador) {
		this.jugador = jugador;
	}
	public int getPuntos() {
		return puntos;
	}
	public void setPuntos(int puntos) {
		this.puntos = puntos;
	}
	public void aumentarPuntos(int puntos) {
		this.puntos = this.puntos+puntos;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public ElementosGuardados getGuardados() {
		return guardados;
	}
	public void setGuardados(ElementosGuardados guardados) {
		this.guardados = guardados;
	}
	
	
}
