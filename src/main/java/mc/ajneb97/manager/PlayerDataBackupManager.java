package mc.ajneb97.manager;

import mc.ajneb97.MineChess;
import mc.ajneb97.model.data.PlayerDataBackup;
import mc.ajneb97.utils.ItemUtils;
import mc.ajneb97.utils.ServerVersion;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class PlayerDataBackupManager {
    private MineChess plugin;
    private Map<UUID, PlayerDataBackup> players;

    public PlayerDataBackupManager(MineChess plugin){
        this.plugin = plugin;
    }

    public Map<UUID, PlayerDataBackup> getPlayers() {
        return players;
    }

    public void setPlayers(Map<UUID, PlayerDataBackup> players) {
        this.players = players;
    }

    public PlayerDataBackup getPlayer(Player player){
        return players.get(player.getUniqueId());
    }

    public void restorePlayerDataBackup(Player player, boolean teleportToLobby, boolean async){
        PlayerDataBackup playerDataBackup = getPlayer(player);
        if(playerDataBackup == null){
            return;
        }

        player.getInventory().setContents(playerDataBackup.getInventory());
        player.setGameMode(playerDataBackup.getGamemode());
        player.setLevel(playerDataBackup.getLevel());
        player.setExp(playerDataBackup.getXp());
        player.setFoodLevel(playerDataBackup.getFood());

        ServerVersion serverVersion = MineChess.serverVersion;
        if(serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_21_R2)){
            player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(playerDataBackup.getMaxHealth());
        }else{
            player.getAttribute(ItemUtils.getAttributeByName("GENERIC_MAX_HEALTH")).setBaseValue(playerDataBackup.getMaxHealth());
        }

        player.setHealth(playerDataBackup.getHealth());
        for(PotionEffect p : player.getActivePotionEffects()) {
            player.removePotionEffect(p.getType());
        }

        player.setAllowFlight(playerDataBackup.isAllowFlight());
        player.setFlying(playerDataBackup.isFlying());

        if(teleportToLobby){
            Location location = plugin.getArenaManager().getMainLobbyLocation();
            player.teleport(location);
        }

        players.remove(player.getUniqueId());

        if(async){
            new BukkitRunnable(){
                @Override
                public void run() {
                    plugin.getConfigsManager().getPlayersConfigManager().saveBackupConfig(player.getUniqueId().toString(),null);
                }
            }.runTaskAsynchronously(plugin);
        }else{
            plugin.getConfigsManager().getPlayersConfigManager().saveBackupConfig(player.getUniqueId().toString(),null);
        }
    }

    public void createPlayerDataBackup(Player player){
        ServerVersion serverVersion = MineChess.serverVersion;
        double maxHealth;
        if(serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_21_R2)){
            maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getBaseValue();
        }else{
            maxHealth = player.getAttribute(ItemUtils.getAttributeByName("GENERIC_MAX_HEALTH")).getBaseValue();
        }

        PlayerDataBackup playerDataBackup = new PlayerDataBackup(player.getInventory().getContents().clone(),player.getGameMode()
                ,player.getExp(),player.getLevel(),player.getFoodLevel(),player.getHealth(),maxHealth,player.getAllowFlight(),player.isFlying());
        players.put(player.getUniqueId(),playerDataBackup);
        new BukkitRunnable(){
            @Override
            public void run() {
                plugin.getConfigsManager().getPlayersConfigManager().saveBackupConfig(player.getUniqueId().toString(),playerDataBackup);
            }
        }.runTaskAsynchronously(plugin);
    }
}
