package mc.ajneb97.api;

import mc.ajneb97.model.Arena;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class ArenaStartEvent extends Event{

	private Arena arena;
	private static final HandlerList handlers = new HandlerList();

	public ArenaStartEvent(Arena arena){
		this.arena = arena;
	}

	public Arena getArena() {
		return arena;
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
