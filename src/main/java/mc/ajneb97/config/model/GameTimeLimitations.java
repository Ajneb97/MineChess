package mc.ajneb97.config.model;

public class GameTimeLimitations {
    private int minTime;
    private boolean limitRewards;
    private boolean limitStats;

    public GameTimeLimitations(int minTime, boolean limitRewards, boolean limitStats) {
        this.minTime = minTime;
        this.limitRewards = limitRewards;
        this.limitStats = limitStats;
    }

    public int getMinTime() {
        return minTime;
    }

    public void setMinTime(int minTime) {
        this.minTime = minTime;
    }

    public boolean isLimitRewards() {
        return limitRewards;
    }

    public void setLimitRewards(boolean limitRewards) {
        this.limitRewards = limitRewards;
    }

    public boolean isLimitStats() {
        return limitStats;
    }

    public void setLimitStats(boolean limitStats) {
        this.limitStats = limitStats;
    }
}
