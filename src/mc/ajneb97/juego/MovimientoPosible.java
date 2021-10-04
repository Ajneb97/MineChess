package mc.ajneb97.juego;

import org.bukkit.Location;

public class MovimientoPosible {

	private Location location;
	private Pieza pieza; //pieza original

	public MovimientoPosible(Location location, Pieza pieza) {
		this.location = location;
		this.pieza = pieza;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public Pieza getPieza() {
		return pieza;
	}
	public void setPieza(Pieza pieza) {
		this.pieza = pieza;
	}
	
	
}
