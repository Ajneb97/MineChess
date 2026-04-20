package mc.ajneb97.api;

import mc.ajneb97.model.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class ArenaPreJoinEvent extends Event implements Cancellable {

	private Arena arena;
	private Player player;
	private boolean cancelled;
	private static final HandlerList handlers = new HandlerList();

	public ArenaPreJoinEvent(Arena arena, Player player){
		this.arena = arena;
		this.player = player;
		this.cancelled = false;
	}

	public Arena getArena() {
		return arena;
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
	    return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
