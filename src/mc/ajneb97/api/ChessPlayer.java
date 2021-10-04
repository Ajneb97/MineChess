package mc.ajneb97.api;

public class ChessPlayer {

	private String player;
	private int wins;
	private int loses;
	private int ties;
	private long playedTime;
	
	public ChessPlayer(String player,int wins,int loses,int ties,long playedTime) {
		this.player = player;
		this.wins = wins;
		this.loses = loses;
		this.ties = ties;
		this.playedTime = playedTime;
	}

	public String getPlayer() {
		return player;
	}

	public int getWins() {
		return wins;
	}

	public int getLoses() {
		return loses;
	}

	public int getTies() {
		return ties;
	}

	public long getPlayedTime() {
		return playedTime;
	}
	
	
}
