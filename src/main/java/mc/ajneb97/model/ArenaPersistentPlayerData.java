package mc.ajneb97.model;

public class ArenaPersistentPlayerData {
    private String playerBlackIp;
    private String playerWhiteIp;

    public String getPlayerBlackIp() {
        return playerBlackIp;
    }

    public void setPlayerBlackIp(String playerBlackIp) {
        this.playerBlackIp = playerBlackIp;
    }

    public String getPlayerWhiteIp() {
        return playerWhiteIp;
    }

    public void setPlayerWhiteIp(String playerWhiteIp) {
        this.playerWhiteIp = playerWhiteIp;
    }

    public void reset(){
        this.playerBlackIp = null;
        this.playerWhiteIp = null;
    }
}
