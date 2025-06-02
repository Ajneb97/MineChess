package mc.ajneb97.api;

import mc.ajneb97.model.Arena;
import mc.ajneb97.model.game.GamePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class ArenaEndEvent extends Event{

	private Arena arena;
	private GamePlayer winner;
	private GamePlayer loser;
	private static final HandlerList handlers = new HandlerList();

	public ArenaEndEvent(Arena arena,GamePlayer winner,GamePlayer loser){
		this.arena = arena;
		this.winner = winner;
		this.loser = loser;
	}

	public Arena getArena() {
		return arena;
	}

	public GamePlayer getWinner() {
		return winner;
	}

	public GamePlayer getLoser() {
		return loser;
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
