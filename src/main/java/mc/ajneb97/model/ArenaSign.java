package mc.ajneb97.model;

import mc.ajneb97.model.game.GameStatus;
import org.bukkit.Location;

public class ArenaSign {

    private int id;
    private String arenaName;
    private Location location;

    private GameStatus previousGameStatus;
    private int previousNumberOfPlayers;

    public ArenaSign(int id, String arenaName, Location location) {
        this.id = id;
        this.arenaName = arenaName;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArenaName() {
        return arenaName;
    }

    public void setArenaName(String arenaName) {
        this.arenaName = arenaName;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public GameStatus getPreviousGameStatus() {
        return previousGameStatus;
    }

    public void setPreviousGameStatus(GameStatus previousGameStatus) {
        this.previousGameStatus = previousGameStatus;
    }

    public int getPreviousNumberOfPlayers() {
        return previousNumberOfPlayers;
    }

    public void setPreviousNumberOfPlayers(int previousNumberOfPlayers) {
        this.previousNumberOfPlayers = previousNumberOfPlayers;
    }
}
