package mc.ajneb97.api;

import mc.ajneb97.MineChess;
import mc.ajneb97.model.data.PlayerData;
import org.bukkit.entity.Player;

public class MineChessAPI {

    private static MineChess plugin;
    public MineChessAPI(MineChess plugin){
        this.plugin = plugin;
    }

    public static MineChess getPlugin() {
        return plugin;
    }

    public static PlayerData getPlayerData(Player player){
        return plugin.getPlayerDataManager().getPlayer(player,false);
    }

    public static PlayerData getPlayerData(String playerName){
        return plugin.getPlayerDataManager().getPlayerByName(playerName);
    }

    public static int getWins(Player player){
        return plugin.getPlayerDataManager().getWins(player);
    }

    public static int getLoses(Player player){
        return plugin.getPlayerDataManager().getLoses(player);
    }

    public static int getTies(Player player){
        return plugin.getPlayerDataManager().getTies(player);
    }

    public static String getTimePlayed(Player player){
        return plugin.getPlayerDataManager().getTimePlayed(player);
    }
}
