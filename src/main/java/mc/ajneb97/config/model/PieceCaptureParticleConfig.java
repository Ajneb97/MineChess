package mc.ajneb97.config.model;

public class PieceCaptureParticleConfig {
    private boolean enabled;
    private String name;
    private double speed;
    private int amount;
    private double spreadX;
    private double spreadY;
    private double spreadZ;

    public PieceCaptureParticleConfig(boolean enabled, String name, double speed, int amount, double spreadX, double spreadY, double spreadZ) {
        this.enabled = enabled;
        this.name = name;
        this.speed = speed;
        this.amount = amount;
        this.spreadX = spreadX;
        this.spreadY = spreadY;
        this.spreadZ = spreadZ;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }

    public double getSpeed() {
        return speed;
    }

    public int getAmount() {
        return amount;
    }

    public double getSpreadX() {
        return spreadX;
    }

    public double getSpreadY() {
        return spreadY;
    }

    public double getSpreadZ() {
        return spreadZ;
    }
}
