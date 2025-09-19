package mc.ajneb97.manager;

import mc.ajneb97.MineChess;
import mc.ajneb97.model.Arena;
import mc.ajneb97.model.chess.Movement;
import mc.ajneb97.model.chess.Piece;
import mc.ajneb97.model.game.GameLeaveReason;
import mc.ajneb97.model.game.GamePlayer;
import mc.ajneb97.utils.ItemUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public class GameItemManager {
    private MineChess plugin;
    public GameItemManager(MineChess plugin){
        this.plugin = plugin;
    }

    public void clickItem(Player player, ItemStack item, Arena arena, Action clickType){
        ArenaManager arenasManager = plugin.getArenaManager();
        String type = ItemUtils.getTagStringItem(plugin,item,"minechess_item_type");
        if(type == null){
            return;
        }

        GamePlayer gamePlayer = arena.getGamePlayer(player,true);
        if(gamePlayer.isOnPromotion()){
            return;
        }

        if(clickType.name().contains("RIGHT")){
            switch (type) {
                case "leave" -> arenasManager.leaveArena(player, arena, GameLeaveReason.ITEM);
                case "select_piece" -> rightClickSelectPiece(gamePlayer, arena);
                case "play_again" -> clickPlayAgainItem(gamePlayer, arena);
            }
        }else{
            switch(type){
                case "select_piece":
                    leftClickSelectPiece(gamePlayer,arena);
                    return;
            }
        }
    }

    public void leftClickSelectPiece(GamePlayer gamePlayer,Arena arena){
        if(!arena.isTurnPlayer(gamePlayer)){
            return;
        }

        if(!gamePlayer.isAfterDelaySelectPiece()){
            return;
        }

        int[] seeingPos = gamePlayer.getSeeingPos();

        if(seeingPos == null){
            // The player is not looking at a valid board position.
            return;
        }

        Piece piece = arena.getBoard().getPiece(seeingPos[0],seeingPos[1]);
        if(piece == null){
            // The position is valid, but the cell is empty, no piece on it.
            return;
        }

        if(!piece.getColor().equals(arena.getColor(gamePlayer))){
            // The piece is not of the player's color.
            return;
        }

        plugin.getArenaManager().getGamePieceInteractionManager().selectPiece(gamePlayer,arena,piece);
    }

    public void rightClickSelectPiece(GamePlayer gamePlayer,Arena arena) {
        if(!arena.isTurnPlayer(gamePlayer)){
            return;
        }

        int[] seeingPos = gamePlayer.getSeeingPos();
        int[] selectedPos = gamePlayer.getSelectedPos();

        if (seeingPos == null || selectedPos == null) {
            return;
        }

        Movement movement = gamePlayer.getPossibleMovementCell(seeingPos);
        if(movement == null){
            return;
        }

        // The seeing pos is a valid movement.
        plugin.getArenaManager().getGamePieceInteractionManager().movePieceManually(gamePlayer,arena,movement);
    }

    public void clickPlayAgainItem(GamePlayer gamePlayer,Arena arena){
        ArenaManager arenaManager = plugin.getArenaManager();
        MessagesManager msgManager = plugin.getMessagesManager();
        FileConfiguration messagesConfig = plugin.getMessagesConfig();
        Player player = gamePlayer.getPlayer();

        Arena availableArena = arenaManager.getAvailableArena();
        if(availableArena == null){
            msgManager.sendMessage(player,messagesConfig.getString("noAvailableArenas"),true);
            return;
        }

        arenaManager.leaveArena(player,arena,GameLeaveReason.ITEM);
        arenaManager.joinArena(player,availableArena);
    }
}
