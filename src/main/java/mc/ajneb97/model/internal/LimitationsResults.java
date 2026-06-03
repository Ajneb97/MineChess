package mc.ajneb97.model.internal;

public class LimitationsResults {
    private boolean mustGiveStats;
    private boolean mustGiveRewards;

    public LimitationsResults(boolean mustGiveStats, boolean mustGiveRewards) {
        this.mustGiveStats = mustGiveStats;
        this.mustGiveRewards = mustGiveRewards;
    }

    public boolean isMustGiveStats() {
        return mustGiveStats;
    }

    public void setMustGiveStats(boolean mustGiveStats) {
        this.mustGiveStats = mustGiveStats;
    }

    public boolean isMustGiveRewards() {
        return mustGiveRewards;
    }

    public void setMustGiveRewards(boolean mustGiveRewards) {
        this.mustGiveRewards = mustGiveRewards;
    }
}
