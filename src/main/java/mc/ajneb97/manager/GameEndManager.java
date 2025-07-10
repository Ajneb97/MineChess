package mc.ajneb97.manager;

import mc.ajneb97.model.game.GameLeaveReason;
import mc.ajneb97.MineChess;
import mc.ajneb97.api.ArenaEndEvent;
import mc.ajneb97.config.MainConfigManager;
import mc.ajneb97.config.model.GameActionsRewards;
import mc.ajneb97.config.model.GameTimeLimitations;
import mc.ajneb97.config.model.gameitems.GameItemConfig;
import mc.ajneb97.model.Arena;
import mc.ajneb97.model.ArenaEndTimeMode;
import mc.ajneb97.model.game.GameEndsReason;
import mc.ajneb97.model.game.GamePlayer;
import mc.ajneb97.model.game.GameStatus;
import mc.ajneb97.model.internal.CommonVariable;
import mc.ajneb97.utils.ActionUtils;
import mc.ajneb97.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GameEndManager {
    private MineChess plugin;
    public GameEndManager(MineChess plugin){
        this.plugin = plugin;
    }

    public void startEndingStage(Arena arena, GameEndsReason reason) {
        arena.stopCooldownTask();
        arena.setStatus(GameStatus.ENDING);
        arena.setEndReason(reason);

        MainConfigManager mainConfigManager = plugin.getConfigsManager().getMainConfigManager();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        GameItemConfig leaveItem = mainConfigManager.getGameItemsConfig().getLeaveItem();
        GameItemConfig playAgainItem = mainConfigManager.getGameItemsConfig().getPlayAgainItem();
        CommonItemManager commonItemManager = plugin.getCommonItemManager();

        // Set winner if time is up
        if(reason.equals(GameEndsReason.TIME)){
            ArenaEndTimeMode endTimeMode = arena.getEndTimeMode();
            int whitePoints = arena.getWhitePoints();
            int blackPoints = arena.getBlackPoints();
            if(endTimeMode.equals(ArenaEndTimeMode.CHECK_POINTS) && whitePoints != blackPoints){
                if(whitePoints > blackPoints){
                    arena.setWinner(arena.getPlayerWhite());
                }else{
                    arena.setWinner(arena.getPlayerBlack());
                }
            }
        }else if(reason.equals(GameEndsReason.PLAYER_TIME)){
            arena.setWinner(arena.getOpponentPlayer(arena.getPlayerTurn()));
        }

        GameTimeLimitations gameTimeLimitations = plugin.getConfigsManager().getMainConfigManager().getGameTimeLimitations();
        long millisPlayed = System.currentTimeMillis()-arena.getMillisStart();
        boolean lastsMoreThanAllowed = gameLastsMoreThanAllowed(millisPlayed);

        // Actions and Rewards
        List<String> actions = getActionsEndGameByReason(arena,reason,mainConfigManager);
        ArrayList<CommonVariable> variables = getEndGameVariablesByReason(arena,reason);
        ActionUtils.executeActions(null,arena,actions,plugin,variables,true);
        if(lastsMoreThanAllowed || !gameTimeLimitations.isLimitRewards()){
            giveRewards(arena,reason,mainConfigManager,false);
        }

        ArenaManager arenaManager = plugin.getArenaManager();
        GamePlayer winner = arena.getWinner();
        for(GamePlayer gamePlayer : arena.getGamePlayers()){
            arenaManager.getGamePieceInteractionManager().updateInteractions(gamePlayer);

            Player player = gamePlayer.getPlayer();

            //Update data
            if(lastsMoreThanAllowed || !gameTimeLimitations.isLimitStats()){
                playerDataManager.endGame(player,winner == gamePlayer,winner != gamePlayer,millisPlayed);
            }

            player.closeInventory();
            player.getInventory().clear();

            if(leaveItem.isEnabled()){
                ItemStack item = commonItemManager.createItemFromCommonItem(leaveItem.getItem());
                player.getInventory().setItem(8, ItemUtils.setTagStringItem(plugin,item,"minechess_item_type","leave"));
            }
            if(playAgainItem.isEnabled()){
                ItemStack item = commonItemManager.createItemFromCommonItem(playAgainItem.getItem());
                player.getInventory().setItem(7, ItemUtils.setTagStringItem(plugin,item,"minechess_item_type","play_again"));
            }
        }

        int time = plugin.getConfigsManager().getMainConfigManager().getArenaEndingPhaseCooldown();
        arena.startCooldownTask(plugin,time);

        //API
        GamePlayer loser = null;
        if(winner != null){
            loser = arena.getOpponentPlayer(winner);
        }
        plugin.getServer().getPluginManager().callEvent(new ArenaEndEvent(arena,winner,loser));
    }

    public List<String> getActionsEndGameByReason(Arena arena,GameEndsReason reason,MainConfigManager mainConfigManager){
        switch(reason){
            case TIME -> {
                if(arena.getWinner() == null){
                    return mainConfigManager.getGameActions().getGameActionsEndGame().getEndByTimeTieActions();
                }else{
                    return mainConfigManager.getGameActions().getGameActionsEndGame().getEndByTimeActions();
                }
            }
            case PLAYER_TIME -> {
                return mainConfigManager.getGameActions().getGameActionsEndGame().getEndByPlayerTimeActions();
            }
            case STALEMATE -> {
                return mainConfigManager.getGameActions().getGameActionsEndGame().getEndByStalemateTieActions();
            }
            case CHECKMATE -> {
                return mainConfigManager.getGameActions().getGameActionsEndGame().getEndByCheckmateActions();
            }
            case PLAYER_LEAVES -> {
                return mainConfigManager.getGameActions().getGameActionsEndGame().getEndByLeaveActions();
            }
            case MOVEMENTS_WITHOUT_PROGRESS -> {
                return mainConfigManager.getGameActions().getGameActionsEndGame().getEndByMovementsWithoutProgressTie();
            }
        }
        return null;
    }

    private List<String> getRewardsEndGameByReason(Arena arena,GameEndsReason reason,MainConfigManager mainConfigManager){
        switch(reason){
            case TIME -> {
                if(arena.getWinner() == null){
                    return mainConfigManager.getGameActions().getGameActionsRewards().getEndByTimeTieActions();
                }else{
                    return mainConfigManager.getGameActions().getGameActionsRewards().getEndByTimeActions();
                }
            }
            case PLAYER_TIME -> {
                return mainConfigManager.getGameActions().getGameActionsRewards().getEndByPlayerTimeActions();
            }
            case STALEMATE -> {
                return mainConfigManager.getGameActions().getGameActionsRewards().getEndByStalemateTieActions();
            }
            case CHECKMATE -> {
                return mainConfigManager.getGameActions().getGameActionsRewards().getEndByCheckmateActions();
            }
            case PLAYER_LEAVES -> {
                return mainConfigManager.getGameActions().getGameActionsRewards().getEndByLeaveActions();
            }
            case MOVEMENTS_WITHOUT_PROGRESS -> {
                return mainConfigManager.getGameActions().getGameActionsRewards().getEndByMovementsWithoutProgressTie();
            }
        }
        return null;
    }

    public ArrayList<CommonVariable> getEndGameVariablesByReason(Arena arena,GameEndsReason reason){
        ArrayList<CommonVariable> variables = new ArrayList<>();
        GamePlayer winner = arena.getWinner();
        switch(reason){
            case TIME -> {
                variables.add(new CommonVariable("%player_white%",arena.getPlayerWhite().getName()));
                variables.add(new CommonVariable("%player_black%",arena.getPlayerBlack().getName()));
                if(winner != null){
                    variables.add(new CommonVariable("%player_winner%",winner.getName()));
                    variables.add(new CommonVariable("%player_winner_points%",winner.getPoints()+""));
                }
            }
            case PLAYER_TIME -> {
                GamePlayer loser = arena.getOpponentPlayer(winner);
                variables.add(new CommonVariable("%player_winner%",winner.getName()));
                variables.add(new CommonVariable("%player_winner_points%",winner.getPoints()+""));
                variables.add(new CommonVariable("%player_loser%",loser.getName()));
                variables.add(new CommonVariable("%player_loser_points%",loser.getPoints()+""));
            }
            case STALEMATE -> {
                GamePlayer opponentPlayer = arena.getOpponentPlayer(arena.getPlayerTurn());
                variables.add(new CommonVariable("%player_stalemate%",opponentPlayer.getName()));
            }
            case CHECKMATE, PLAYER_LEAVES -> {
                variables.add(new CommonVariable("%player_winner%",winner.getName()));
                variables.add(new CommonVariable("%player_winner_points%",winner.getPoints()+""));
            }
            case MOVEMENTS_WITHOUT_PROGRESS -> {
                variables.add(new CommonVariable("%player_white%",arena.getPlayerWhite().getName()));
                variables.add(new CommonVariable("%player_black%",arena.getPlayerBlack().getName()));
                int maxMovements = plugin.getConfigsManager().getMainConfigManager().getMaxConsecutiveMovementsWithoutProgress();
                variables.add(new CommonVariable("%max%",maxMovements+""));
            }
        }
        return variables;
    }

    private boolean gameLastsMoreThanAllowed(long millisPlayed){
        long minMillis = plugin.getConfigsManager().getMainConfigManager().getGameTimeLimitations().getMinTime()*1000L;
        return millisPlayed >= minMillis;
    }

    private void giveRewards(Arena arena,GameEndsReason reason,MainConfigManager mainConfigManager,boolean givingAfterTeleport){
        GameActionsRewards gameActionsRewards = mainConfigManager.getGameActions().getGameActionsRewards();
        boolean mustGiveAfterTeleport = gameActionsRewards.isAfterTeleport();
        if((mustGiveAfterTeleport && givingAfterTeleport) || (!mustGiveAfterTeleport && !givingAfterTeleport)){
            ArrayList<CommonVariable> variables = getEndGameVariablesByReason(arena,reason);
            List<String> actions = getRewardsEndGameByReason(arena,reason,mainConfigManager);
            ActionUtils.executeActions(null,arena,actions,plugin,variables,false);
        }
    }

    public void endGame(Arena arena, GameLeaveReason reason) {
        ArenaManager arenaManager = plugin.getArenaManager();
        ArrayList<GamePlayer> playersCopy = new ArrayList<>(arena.getGamePlayers(true));

        Arena dummyArena = new Arena("dummy");
        if(arena.isInGame()){
            plugin.getBoardManager().removeBoardPieces(arena);
            arena.stopCooldownTask();

            dummyArena.setWinner(arena.getWinner());
            dummyArena.setPlayerBlack(arena.getPlayerBlack());
            dummyArena.setPlayerWhite(arena.getPlayerWhite());
            dummyArena.setMillisStart(arena.getMillisStart());
            dummyArena.setPlayerColorTurn(arena.getPlayerColorTurn());
        }

        for(GamePlayer gamePlayer : playersCopy){
            arenaManager.leaveArena(gamePlayer.getPlayer(),arena,reason);
        }

        if(arena.isInGame()){
            // Rewards after teleport
            long millisPlayed = System.currentTimeMillis()-arena.getMillisStart();
            boolean lastsMoreThanAllowed = gameLastsMoreThanAllowed(millisPlayed);
            GameTimeLimitations gameTimeLimitations = plugin.getConfigsManager().getMainConfigManager().getGameTimeLimitations();
            if(lastsMoreThanAllowed || !gameTimeLimitations.isLimitRewards()){
                giveRewards(dummyArena,arena.getEndReason(),plugin.getConfigsManager().getMainConfigManager(),true);
            }

        }

        if(!reason.equals(GameLeaveReason.ARENA_DISABLED)){
            arena.setStatus(GameStatus.WAITING);
        }

        arena.resetArena();
    }
}
