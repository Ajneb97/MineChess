package mc.ajneb97.manager.editinventory;

import mc.ajneb97.MineChess;
import mc.ajneb97.model.Arena;
import mc.ajneb97.model.editinventory.EditInventoryPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class EditInventoryManager {
    private MineChess plugin;
    private ArrayList<EditInventoryPlayer> players;
    private EditArenaInventoryManager arenaInventoryManager;

    public EditInventoryManager(MineChess plugin) {
        this.plugin = plugin;
        this.players = new ArrayList<>();
        this.arenaInventoryManager = new EditArenaInventoryManager(plugin);
    }

    public ArrayList<EditInventoryPlayer> getPlayers() {
        return players;
    }

    public EditInventoryPlayer getEditInventoryPlayer(Player player) {
        for(EditInventoryPlayer p : players) {
            if(player.getName().equals(p.getPlayer().getName())) {
                return p;
            }
        }
        return null;
    }

    public void removeEditInventoryPlayer(Player player) {
        players.removeIf(p -> p.getPlayer().equals(player));
    }

    public void openArenaInventory(Player player, Arena arena){
        EditInventoryPlayer editInventoryPlayer = new EditInventoryPlayer(player);
        editInventoryPlayer.setArena(arena);
        arenaInventoryManager.openMainInventory(editInventoryPlayer);
    }

    public void onClick(EditInventoryPlayer editInventoryPlayer, int slot, ItemStack item, ClickType clickType){
        EditInventoryPlayer.InventoryType inventoryType = editInventoryPlayer.getCurrentInventory();
        switch(inventoryType){
            case ARENA:
                arenaInventoryManager.onInventoryClick(editInventoryPlayer,slot,item,clickType);
                break;
        }
    }
}
