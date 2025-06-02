package mc.ajneb97.model.data;

import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;

public class PlayerDataBackup {
    private ItemStack[] inventory;
    private GameMode gamemode;
    private float xp;
    private int level;
    private int food;
    private double health;
    private double maxHealth;
    private boolean allowFlight;
    private boolean isFlying;

    public PlayerDataBackup(ItemStack[] inventory, GameMode gamemode, float xp, int level, int food, double health, double maxHealth, boolean allowFlight, boolean isFlying) {
        this.inventory = inventory;
        this.gamemode = gamemode;
        this.xp = xp;
        this.level = level;
        this.food = food;
        this.health = health;
        this.maxHealth = maxHealth;
        this.allowFlight = allowFlight;
        this.isFlying = isFlying;
    }

    public ItemStack[] getInventory() {
        return inventory;
    }

    public void setInventory(ItemStack[] inventory) {
        this.inventory = inventory;
    }

    public GameMode getGamemode() {
        return gamemode;
    }

    public void setGamemode(GameMode gamemode) {
        this.gamemode = gamemode;
    }

    public float getXp() {
        return xp;
    }

    public void setXp(float xp) {
        this.xp = xp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getFood() {
        return food;
    }

    public void setFood(int food) {
        this.food = food;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
    }

    public boolean isAllowFlight() {
        return allowFlight;
    }

    public void setAllowFlight(boolean allowFlight) {
        this.allowFlight = allowFlight;
    }

    public boolean isFlying() {
        return isFlying;
    }

    public void setFlying(boolean flying) {
        isFlying = flying;
    }
}
