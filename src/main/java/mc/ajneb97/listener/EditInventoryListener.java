package mc.ajneb97.listener;

import mc.ajneb97.MineChess;
import mc.ajneb97.manager.editinventory.EditInventoryManager;
import mc.ajneb97.model.editinventory.EditInventoryPlayer;
import mc.ajneb97.utils.InventoryUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class EditInventoryListener implements Listener {

    private MineChess plugin;
    public EditInventoryListener(MineChess plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void closeInventory(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        plugin.getEditInventoryManager().removeEditInventoryPlayer(player);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        EditInventoryManager invManager = plugin.getEditInventoryManager();
        EditInventoryPlayer inventoryPlayer = invManager.getEditInventoryPlayer(player);
        if(inventoryPlayer != null) {
            event.setCancelled(true);
            if(event.getCurrentItem() == null || event.getClickedInventory() == null){
                return;
            }

            if(event.getClickedInventory().equals(InventoryUtils.getTopInventory(player))) {
                int slot = event.getSlot();
                invManager.onClick(inventoryPlayer,slot,event.getCurrentItem(),event.getClick());
            }
        }
    }
}
