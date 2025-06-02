package mc.ajneb97.config.model;

public class PiecesHologramsConfig {
    private boolean enabled;
    private PieceHologramsValues values;

    public PiecesHologramsConfig(boolean enabled, PieceHologramsValues values) {
        this.enabled = enabled;
        this.values = values;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public PieceHologramsValues getValues() {
        return values;
    }

    public void setValues(PieceHologramsValues values) {
        this.values = values;
    }
}
