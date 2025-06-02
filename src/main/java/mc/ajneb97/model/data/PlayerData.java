package mc.ajneb97.model.data;

import java.util.UUID;

public class PlayerData {
    private UUID uuid;
    private String name;
    private int wins;
    private int ties;
    private int loses;
    private long millisPlayed;
    private boolean modified;

    public PlayerData(UUID uuid,String name){
        this.uuid = uuid;
        this.name = name;
        this.modified = false;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getTies() {
        return ties;
    }

    public void setTies(int ties) {
        this.ties = ties;
    }

    public int getLoses() {
        return loses;
    }

    public void setLoses(int loses) {
        this.loses = loses;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public long getMillisPlayed() {
        return millisPlayed;
    }

    public void setMillisPlayed(long millisPlayed) {
        this.millisPlayed = millisPlayed;
    }

    public void addMillisPlayed(long value){
        this.millisPlayed = millisPlayed+value;
    }
}
