package mc.ajneb97.api;

import mc.ajneb97.model.Arena;
import mc.ajneb97.model.game.GamePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class ArenaJoinEvent extends Event{

	private Arena arena;
	private GamePlayer gamePlayer;
	private static final HandlerList handlers = new HandlerList();

	public ArenaJoinEvent(Arena arena, GamePlayer gamePlayer){
		this.arena = arena;
		this.gamePlayer = gamePlayer;
	}

	public Arena getArena() {
		return arena;
	}

	public GamePlayer getGamePlayer() {
		return gamePlayer;
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
