package mc.ajneb97.manager;

import mc.ajneb97.MineChess;
import mc.ajneb97.config.model.gameitems.GameItemConfig;
import mc.ajneb97.model.game.GameLeaveReason;
import mc.ajneb97.config.model.gameitems.GameItemsConfig;
import mc.ajneb97.model.Arena;
import mc.ajneb97.utils.ItemUtils;
import mc.ajneb97.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GameSpectatorManager {
    private MineChess plugin;
    public GameSpectatorManager(MineChess plugin){
        this.plugin = plugin;
    }

    public void joinArena(Player player, Arena arena){
        MessagesManager msgManager = plugin.getMessagesManager();
        FileConfiguration messagesConfig = plugin.getMessagesConfig();
        GamePlayerManager gamePlayerManager = plugin.getArenaManager().getGamePlayerManager();

        if(arena.isDisabled()){
            msgManager.sendMessage(player,messagesConfig.getString("arenaIsDisabled"),true);
            return;
        }
        if(!arena.isInGame()){
            msgManager.sendMessage(player,messagesConfig.getString("arenaIsNotInGame"),true);
            return;
        }
        if(gamePlayerManager.getArenaByPlayer(player,true) != null){
            msgManager.sendMessage(player,messagesConfig.getString("playerAlreadyPlaying"),true);
            return;
        }

        //Player inventory
        boolean emptyInventoryToJoin = plugin.getConfigsManager().getMainConfigManager().isEmptyInventoryToJoin();
        if(emptyInventoryToJoin && !PlayerUtils.isEmptyInventory(player)){
            msgManager.sendMessage(player,messagesConfig.getString("emptyInventoryToJoin"),true);
            return;
        }

        spectatorJoinsArena(player,arena,gamePlayerManager);
    }

    private void spectatorJoinsArena(Player player, Arena arena, GamePlayerManager gamePlayerManager){
        arena.addSpectator(player);

        player.teleport(arena.getLobbyLocation());
        gamePlayerManager.clearPlayer(player);

        // Vanish
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,99999999,0,false,false));
        for(Player p : Bukkit.getOnlinePlayers()){
            p.hidePlayer(plugin,player);
        }

        //Spectator Items
        Inventory inv = player.getInventory();
        GameItemsConfig gameItemsConfig = plugin.getConfigsManager().getMainConfigManager().getGameItemsConfig();
        CommonItemManager commonItemManager = plugin.getCommonItemManager();
        GameItemConfig leaveItem = gameItemsConfig.getLeaveItem();
        if(leaveItem.isEnabled()){
            ItemStack item = commonItemManager.createItemFromCommonItem(leaveItem.getItem());
            inv.setItem(8, ItemUtils.setTagStringItem(plugin,item,"minechess_item_type","leave"));
        }
    }

    public void leaveArena(Player player, Arena arena, GameLeaveReason reason){
        MessagesManager msgManager = plugin.getMessagesManager();
        FileConfiguration messagesConfig = plugin.getMessagesConfig();

        // Vanish
        for(Player p : Bukkit.getOnlinePlayers()){
            p.showPlayer(plugin,player);
        }

        plugin.getScoreboardManager().removeScoreboard(player);
        PlayerDataBackupManager backupManager = plugin.getPlayerDataManager().getPlayerDataBackupManager();
        backupManager.restorePlayerDataBackup(player,false);
        if(!reason.equals(GameLeaveReason.LEAVE_SERVER)){
            backupManager.clearPlayerDataBackup(player,!reason.equals(GameLeaveReason.SERVER_STOP));
        }

        arena.removeSpectator(player);

        if(reason.equals(GameLeaveReason.ARENA_DISABLED)){
            msgManager.sendMessage(player,messagesConfig.getString("arenaDisabledKickMessage"),true);
        }

        //Teleport back to lobby
        Location location = plugin.getArenaManager().getMainLobbyLocation();
        player.teleport(location);
    }
}
