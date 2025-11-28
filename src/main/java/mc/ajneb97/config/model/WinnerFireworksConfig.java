package mc.ajneb97.config.model;

import java.util.List;

public class WinnerFireworksConfig {
    private List<String> colors;
    private List<String> fade;
    private String type;
    private int power;
    private boolean flicker;
    private boolean trail;

    public WinnerFireworksConfig(List<String> colors, List<String> fade, String type, int power, boolean flicker, boolean trail) {
        this.colors = colors;
        this.fade = fade;
        this.type = type;
        this.power = power;
        this.flicker = flicker;
        this.trail = trail;
    }

    public List<String> getColors() {
        return colors;
    }

    public List<String> getFade() {
        return fade;
    }

    public String getType() {
        return type;
    }

    public int getPower() {
        return power;
    }

    public boolean isFlicker() {
        return flicker;
    }

    public boolean isTrail() {
        return trail;
    }
}
