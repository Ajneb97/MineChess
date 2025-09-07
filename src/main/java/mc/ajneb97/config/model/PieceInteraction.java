package mc.ajneb97.config.model;

public class PieceInteraction {

    private String value;
    private ParticleFormType type;
    private double size;
    private double offsetY;
    private boolean enabled;

    public PieceInteraction(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ParticleFormType getType() {
        return type;
    }

    public void setType(ParticleFormType type) {
        this.type = type;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public enum ParticleFormType{
        CIRCLE,
        SQUARE
    }
}
