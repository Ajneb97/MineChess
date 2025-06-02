package mc.ajneb97.manager;

import mc.ajneb97.MineChess;
import mc.ajneb97.libs.actionbar.ActionBarAPI;
import mc.ajneb97.manager.inventory.InventoryManager;
import mc.ajneb97.manager.inventory.InventoryType;
import mc.ajneb97.model.Arena;
import mc.ajneb97.model.game.GameLeaveReason;
import mc.ajneb97.model.game.GamePlayer;
import mc.ajneb97.model.game.GameStatus;
import mc.ajneb97.model.inventory.InventoryPlayer;
import mc.ajneb97.utils.*;
import mc.ajneb97.config.model.PerArenaChatConfig;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GamePlayerManager {
    private MineChess plugin;
    public GamePlayerManager(MineChess plugin){
        this.plugin = plugin;
    }

    public Arena getArenaByPlayer(Player player){
        ArrayList<Arena> arenas = plugin.getArenaManager().getArenas();
        for(Arena arena : arenas){
            if(arena.getGamePlayer(player) != null){
                return arena;
            }
        }
        return null;
    }

    public Arena getArenaByPlayer(Player player,boolean checkSpectator){
        ArrayList<Arena> arenas = plugin.getArenaManager().getArenas();
        for(Arena arena : arenas){
            if(arena.getGamePlayer(player,checkSpectator) != null){
                return arena;
            }
        }
        return null;
    }

    public GamePlayer getGamePlayer(Player player){
        ArrayList<Arena> arenas = plugin.getArenaManager().getArenas();
        for(Arena arena : arenas){
            GamePlayer gamePlayer = arena.getGamePlayer(player);
            if(gamePlayer != null){
                return gamePlayer;
            }
        }
        return null;
    }

    public GamePlayer getGamePlayer(Player player,boolean checkSpectator){
        ArrayList<Arena> arenas = plugin.getArenaManager().getArenas();
        for(Arena arena : arenas){
            GamePlayer gamePlayer = arena.getGamePlayer(player,checkSpectator);
            if(gamePlayer != null){
                return gamePlayer;
            }
        }
        return null;
    }

    public void clearPlayer(Player player){
        plugin.getPlayerDataManager().getPlayerDataBackupManager().createPlayerDataBackup(player);
        player.getInventory().clear();
        player.getEquipment().clear();
        player.setGameMode(GameMode.SURVIVAL);
        player.setFoodLevel(20);

        ServerVersion serverVersion = MineChess.serverVersion;
        if(serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_21_R2)){
            player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20);
        }else{
            player.getAttribute(ItemUtils.getAttributeByName("GENERIC_MAX_HEALTH")).setBaseValue(20);
        }

        player.setHealth(20);
        player.setLevel(0);
        player.setExp(0);
        player.setAllowFlight(true);
        player.setFlying(true);
        for(PotionEffect p : player.getActivePotionEffects()) {
            player.removePotionEffect(p.getType());
        }
    }

    public void swapHand(Player player, PlayerSwapHandItemsEvent event) {
        if(getGamePlayer(player,true) != null){
            event.setCancelled(true);
        }
    }

    public void breakBlock(Player player, BlockBreakEvent event){
        plugin.getSignManager().breakSign(player,event);
        if(getGamePlayer(player,true) != null){
            event.setCancelled(true);
        }
    }

    public void placeBlock(Player player, BlockPlaceEvent event){
        if(getGamePlayer(player,true) != null){
            event.setCancelled(true);
        }
    }

    public void dropItem(Player player, PlayerDropItemEvent event){
        if(getGamePlayer(player,true) != null){
            event.setCancelled(true);
        }
    }

    public void foodChange(Player player, FoodLevelChangeEvent event){
        if(getGamePlayer(player,true) != null){
            event.setCancelled(true);
        }
    }

    public void interactEntity(Player player, PlayerInteractAtEntityEvent event){
        if(getGamePlayer(player,true) != null){
            event.setCancelled(true);
        }
    }

    public void useCommand(Player player, PlayerCommandPreprocessEvent event){
        GamePlayer gamePlayer = getGamePlayer(player,true);
        if(gamePlayer == null){
            return;
        }
        if(PlayerUtils.isMineChessAdmin(player)){
            return;
        }

        List<String> commandWhitelist = plugin.getConfigsManager().getMainConfigManager().getCommandsWhitelist();
        String command = event.getMessage();
        for(String c : commandWhitelist) {
            if(c.equalsIgnoreCase(command)){
                return;
            }
        }

        MessagesManager msgManager = plugin.getMessagesManager();
        FileConfiguration messagesConfig = plugin.getMessagesConfig();
        msgManager.sendMessage(player,messagesConfig.getString("commandBlocked"),true);
        event.setCancelled(true);
    }

    public void chat(Player player, AsyncPlayerChatEvent event){
        Arena arena = getArenaByPlayer(player);
        PerArenaChatConfig config = plugin.getConfigsManager().getMainConfigManager().getPerArenaChat();
        if(!config.isEnabled()){
            return;
        }
        if(arena == null){
            // Prevent players inside arenas see message of non-playing players.
            for(Player p : Bukkit.getOnlinePlayers()) {
                if(getArenaByPlayer(p) != null){
                    event.getRecipients().remove(p);
                }
            }
        }else{
            event.setCancelled(true);
            String format = config.getFormat();
            String message = event.getMessage();

            for(GamePlayer p : arena.getGamePlayers()){
                String msg = MessagesManager.getColoredMessage(format.replace("%player%",player.getName())
                        .replace("%message%",message));
                p.getPlayer().sendMessage(msg);
            }
        }
    }

    public void interact(Player player, PlayerInteractEvent event){
        plugin.getSignManager().clickSign(player,event);
        Arena arena = getArenaByPlayer(player,true);
        if(arena == null){
            return;
        }

        event.setCancelled(true);
        ItemStack item = event.getItem();
        Action action = event.getAction();
        if(action.name().contains("CLICK") && item != null){
            plugin.getArenaManager().getGameItemManager().clickItem(player,item,arena,action);
        }
    }
    public void damageEntity(Player player, EntityDamageByEntityEvent event) {
        if (getGamePlayer(player,true) != null) {
            event.setCancelled(true);
        }
    }

    public void receiveDamage(Player player, EntityDamageEvent event) {
        if (getGamePlayer(player,true) != null) {
            event.setCancelled(true);
        }
    }

    public void leaveServer(Player player){
        Arena arena = getArenaByPlayer(player,true);
        if(arena != null){
            plugin.getArenaManager().leaveArena(player,arena, GameLeaveReason.LEAVE_SERVER);
        }
    }

    public void moveCamera(Player player){
        Arena arena = getArenaByPlayer(player);
        if(arena == null){
            return;
        }

        if(!arena.getStatus().equals(GameStatus.PLAYING)){
            return;
        }

        GamePlayer gamePlayer = arena.getGamePlayer(player);

        if(!arena.isTurnPlayer(gamePlayer)){
            return;
        }

        // Update previous pos variable to the one seeing before.
        gamePlayer.setPreviousPos(gamePlayer.getSeeingPos());

        Block b;
        try{
            b = player.getTargetBlock(null, plugin.getConfigsManager().getMainConfigManager().getSelectPieceMaxDistance());
            if(b.getType().isAir()){
                // If the player is not looking at a block, nothing to show.
                gamePlayer.setSeeingPos(null);
                return;
            }
        }catch(Exception e){
            return;
        }

        int[] pos = plugin.getBoardManager().getPiecePositionFromLocation(b.getLocation(),arena);
        if(pos == null){
            // If the block is not part of the board, nothing to show.
            gamePlayer.setSeeingPos(null);
            return;
        }

        int[] currentSeeingPos = gamePlayer.getSeeingPos();
        if(currentSeeingPos != null && Arrays.equals(pos,currentSeeingPos)){
            // The current looking pos is the same as before, don't change anything.
            return;
        }

        gamePlayer.setSeeingPos(pos);
    }

    public void showActionbar(Arena arena){
        GamePlayer playerTurn = arena.getPlayerTurn();
        GamePlayer opponentPlayer = arena.getOpponentPlayer(playerTurn);

        String time = OtherUtils.getTimeFormat1(playerTurn.getTurnTime());

        FileConfiguration messagesConfig = plugin.getMessagesConfig();
        new BukkitRunnable(){
            @Override
            public void run() {
                ActionBarAPI.sendActionBar(playerTurn.getPlayer(),messagesConfig.getString("actionbarYourTurn")
                        .replace("%time%",time));
                String globalMessage = messagesConfig.getString("actionbarPlayerTurn")
                        .replace("%time%",time)
                        .replace("%player%",playerTurn.getName());
                ActionBarAPI.sendActionBar(opponentPlayer.getPlayer(),globalMessage);
                for(GamePlayer g : arena.getSpectators()){
                    ActionBarAPI.sendActionBar(g.getPlayer(),globalMessage);
                }

            }
        }.runTaskAsynchronously(plugin);
    }

    public void checkMustTeleportArenaLobby(Arena arena){
        ArrayList<GamePlayer> players = arena.getGamePlayers(true);
        int maxDistance = plugin.getConfigsManager().getMainConfigManager().getMaxDistanceFromArenaLobby();
        Location lobbyLocation = arena.getLobbyLocation();
        for(GamePlayer gamePlayer : players){
            Player player = gamePlayer.getPlayer();
            if(player == null){
                continue;
            }
            if(!lobbyLocation.getWorld().equals(player.getLocation().getWorld())){
                continue;
            }
            double distance = lobbyLocation.distance(player.getLocation());
            if(distance >= maxDistance){
                player.teleport(lobbyLocation);
            }
        }
    }

    public void inventoryClick(Player player, InventoryClickEvent event) {
        Arena arena = getArenaByPlayer(player,true);
        if(arena != null){
            event.setCancelled(true);
        }

        InventoryManager invManager = plugin.getInventoryManager();
        InventoryPlayer inventoryPlayer = invManager.getInventoryPlayer(player);
        if(inventoryPlayer != null) {
            event.setCancelled(true);
            if(event.getCurrentItem() == null || event.getClickedInventory() == null){
                return;
            }

            if(event.getClickedInventory().equals(InventoryUtils.getTopInventory(player))) {
                plugin.getInventoryManager().clickInventory(inventoryPlayer,event.getCurrentItem(),event.getClick());
            }
        }
    }

    public void inventoryClose(Player player, InventoryCloseEvent event) {
        InventoryManager invManager = plugin.getInventoryManager();
        InventoryPlayer inventoryPlayer = invManager.getInventoryPlayer(player);
        if(inventoryPlayer != null){
            if(inventoryPlayer.getInventoryType().equals(InventoryType.PROMOTION) ){
                GamePlayer gamePlayer = getGamePlayer(player,true);
                if (gamePlayer != null && gamePlayer.isOnPromotion()) {
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            player.openInventory(event.getInventory());
                        }
                    }.runTaskLater(plugin,1L);
                }
            }else{
                plugin.getInventoryManager().removeInventoryPlayer(player);
            }
        }
    }
}
