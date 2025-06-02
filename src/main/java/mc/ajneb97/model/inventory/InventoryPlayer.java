package mc.ajneb97.model.inventory;

import mc.ajneb97.manager.inventory.InventoryType;
import org.bukkit.entity.Player;

public class InventoryPlayer {
    private Player player;
    private InventoryType inventoryType;

    public InventoryPlayer(Player player, InventoryType inventoryType) {
        this.player = player;
        this.inventoryType = inventoryType;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public InventoryType getInventoryType() {
        return inventoryType;
    }

    public void setInventoryType(InventoryType inventoryType) {
        this.inventoryType = inventoryType;
    }
}
