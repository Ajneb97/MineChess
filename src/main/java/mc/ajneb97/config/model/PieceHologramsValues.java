package mc.ajneb97.config.model;

public class PieceHologramsValues {
    private String text;
    private double offsetY;

    public PieceHologramsValues(String text, double offsetY) {
        this.text = text;
        this.offsetY = offsetY;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }
}
