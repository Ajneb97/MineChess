package mc.ajneb97.listener;

import mc.ajneb97.MineChess;
import mc.ajneb97.manager.MessagesManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;

public class PlayerListener implements Listener {

    private MineChess plugin;
    public PlayerListener(MineChess plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        plugin.getPlayerDataManager().setJoinPlayerData(event.getPlayer());

        //Update notification
        Player player = event.getPlayer();
        String latestVersion = plugin.getUpdateCheckerManager().getLatestVersion();
        if(player.isOp() && plugin.getConfigsManager().getMainConfigManager().isUpdateNotify() && !plugin.version.equals(latestVersion)){
            player.sendMessage(MessagesManager.getColoredMessage(plugin.prefix+" &cThere is a new version available. &e(&7"+latestVersion+"&e)"));
            player.sendMessage(MessagesManager.getColoredMessage("&cYou can download it at: &fhttps://modrinth.com/plugin/minechess-minigame"));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLeave(PlayerQuitEvent event){
        plugin.getArenaManager().getGamePlayerManager().leaveServer(event.getPlayer());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        plugin.getArenaManager().getGamePlayerManager().interact(event.getPlayer(),event);
    }

    @EventHandler
    public void onUseCommand(PlayerCommandPreprocessEvent event){
        if(event.isCancelled()){
            return;
        }
        plugin.getArenaManager().getGamePlayerManager().useCommand(event.getPlayer(),event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event){
        if(event.isCancelled()){
            return;
        }
        plugin.getArenaManager().getGamePlayerManager().chat(event.getPlayer(),event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event){
        if(event.isCancelled()){
            return;
        }
        plugin.getArenaManager().getGamePlayerManager().breakBlock(event.getPlayer(),event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event){
        if(event.isCancelled()){
            return;
        }
        plugin.getArenaManager().getGamePlayerManager().placeBlock(event.getPlayer(),event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDropItem(PlayerDropItemEvent event){
        if(event.isCancelled()){
            return;
        }
        plugin.getArenaManager().getGamePlayerManager().dropItem(event.getPlayer(),event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamagesEntity(EntityDamageByEntityEvent event){
        if(event.isCancelled()){
            return;
        }

        Entity damager = event.getDamager();
        if(damager instanceof Player){
            plugin.getArenaManager().getGamePlayerManager().damageEntity((Player)damager,event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerReceivesDamage(EntityDamageEvent event){
        if(event.isCancelled()){
            return;
        }
        Entity damaged = event.getEntity();
        if(damaged instanceof Player){
            plugin.getArenaManager().getGamePlayerManager().receiveDamage((Player)damaged,event);
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event){
        plugin.getArenaManager().getGamePlayerManager().inventoryClick((Player)event.getWhoClicked(),event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClose(InventoryCloseEvent event){
        plugin.getArenaManager().getGamePlayerManager().inventoryClose((Player)event.getPlayer(),event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSwapHand(PlayerSwapHandItemsEvent event){
        plugin.getArenaManager().getGamePlayerManager().swapHand(event.getPlayer(),event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFoodLevelChange(FoodLevelChangeEvent event){
        plugin.getArenaManager().getGamePlayerManager().foodChange((Player)event.getEntity(),event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent event){
        if(event.isCancelled()){
            return;
        }
        plugin.getArenaManager().getGamePlayerManager().moveCamera(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityInteract(PlayerInteractAtEntityEvent event){
        plugin.getArenaManager().getGamePlayerManager().interactEntity(event.getPlayer(),event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignModify(SignChangeEvent event){
        if(event.isCancelled()){
            return;
        }
        plugin.getSignManager().createSign(event.getPlayer(), event);
    }
}
