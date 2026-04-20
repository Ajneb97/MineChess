package mc.ajneb97.api;

import mc.ajneb97.model.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class ArenaLeaveEvent extends Event{

	private Arena arena;
	private Player player;
	private static final HandlerList handlers = new HandlerList();

	public ArenaLeaveEvent(Arena arena, Player player){
		this.arena = arena;
		this.player = player;
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

}
