package mc.ajneb97.config.model;

public class PerArenaChatConfig {
    private boolean enabled;
    private String format;

    public PerArenaChatConfig(boolean enabled, String format) {
        this.enabled = enabled;
        this.format = format;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
