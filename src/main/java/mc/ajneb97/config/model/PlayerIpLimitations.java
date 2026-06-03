package mc.ajneb97.config.model;

public class PlayerIpLimitations {
    private boolean limitRewards;
    private boolean limitStats;

    public PlayerIpLimitations(boolean limitRewards, boolean limitStats) {
        this.limitRewards = limitRewards;
        this.limitStats = limitStats;
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
