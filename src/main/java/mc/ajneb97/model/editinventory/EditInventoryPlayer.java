package mc.ajneb97.model.editinventory;

import mc.ajneb97.model.Arena;
import org.bukkit.entity.Player;

public class EditInventoryPlayer {

	private Player player;
	private InventoryType currentInventory;
	private Arena arena;
	private int page;

	public EditInventoryPlayer(Player player) {
		this.player = player;
		this.page = 1;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public InventoryType getCurrentInventory() {
		return currentInventory;
	}

	public void setCurrentInventory(InventoryType currentInventory) {
		this.currentInventory = currentInventory;
	}

	public Arena getArena() {
		return arena;
	}

	public void setArena(Arena arena) {
		this.arena = arena;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public enum InventoryType {
		ARENA
	}
}
