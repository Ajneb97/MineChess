package mc.ajneb97.model.verify;

import mc.ajneb97.utils.JSONMessage;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MineChessInventoryInvalidSlotError extends MineChessBaseError {

    private String inventoryName;
    private int slot;
    private int maxSlots;

    public MineChessInventoryInvalidSlotError(String file, String errorText, boolean critical, int slot, String inventoryName, int maxSlots) {
        super(file, errorText, critical);
        this.inventoryName = inventoryName;
        this.slot = slot;
        this.maxSlots = maxSlots;
    }

    @Override
    public void sendMessage(Player player) {
        List<String> hover = new ArrayList<String>();

        JSONMessage jsonMessage = new JSONMessage(player,prefix+"&7Inventory &c"+inventoryName+" &7has an item on an invalid slot");
        hover.add("&eTHIS IS AN ERROR!");
        hover.add("&fSlot &c"+slot+" &fon inventory &c"+inventoryName);
        hover.add("&fis out of range. Use a range");
        hover.add("&fbetween 0 and "+(maxSlots-1)+".");

        jsonMessage.hover(hover).send();
    }
}
