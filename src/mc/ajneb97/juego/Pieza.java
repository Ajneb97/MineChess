package mc.ajneb97.juego;

import org.bukkit.Location;

public class Pieza {

	private int id;
	private String tipo;
	private String color;
	private boolean movida;
	private boolean enPassant;
	
	public Pieza(int id,String tipo, String color, boolean movida, boolean enPassant) {
		this.tipo = tipo;
		this.color = color;
		this.id = id;
		this.movida = movida;
		this.enPassant = enPassant;
	}
	public void setEnPassant(boolean enPassant) {
		this.enPassant = enPassant;
	}
	public boolean isEnPassant() {
		return this.enPassant;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public boolean seHaMovido() {
		return movida;
	}
	public void setMovida(boolean movida) {
		this.movida = movida;
	}
	
}
