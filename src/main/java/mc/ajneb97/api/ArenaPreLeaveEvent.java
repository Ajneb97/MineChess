package mc.ajneb97.api;

import mc.ajneb97.model.Arena;
import mc.ajneb97.model.game.GameLeaveReason;
import mc.ajneb97.model.game.GamePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class ArenaPreLeaveEvent extends Event{

	private Arena arena;
	private GamePlayer gamePlayer;
	private GameLeaveReason reason;
	private static final HandlerList handlers = new HandlerList();

	public ArenaPreLeaveEvent(Arena arena, GamePlayer gamePlayer, GameLeaveReason reason){
		this.arena = arena;
		this.gamePlayer = gamePlayer;
		this.reason = reason;
	}

	public Arena getArena() {
		return arena;
	}

	public GamePlayer getGamePlayer() {
		return gamePlayer;
	}

	public GameLeaveReason getReason() {
		return reason;
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
