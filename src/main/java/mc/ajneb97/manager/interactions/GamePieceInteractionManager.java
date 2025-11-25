package mc.ajneb97.manager.interactions;

import mc.ajneb97.MineChess;
import mc.ajneb97.manager.BoardManager;
import mc.ajneb97.model.chess.MovementType;
import mc.ajneb97.model.chess.Piece;
import mc.ajneb97.model.game.GameEndsReason;
import mc.ajneb97.model.internal.MovePieceResult;
import mc.ajneb97.model.inventory.InventoryPlayer;
import mc.ajneb97.utils.ActionUtils;
import mc.ajneb97.config.MainConfigManager;
import mc.ajneb97.manager.inventory.InventoryType;
import mc.ajneb97.model.Arena;
import mc.ajneb97.model.PlayerColor;
import mc.ajneb97.model.chess.Movement;
import mc.ajneb97.model.chess.PieceType;
import mc.ajneb97.model.game.GamePlayer;
import mc.ajneb97.model.internal.CheckValidationResult;
import mc.ajneb97.model.internal.CommonVariable;
import mc.ajneb97.model.internal.CoordinatePiece;
import mc.ajneb97.utils.GameUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Arrays;

public class GamePieceInteractionManager {
    private MineChess plugin;
    public GamePieceInteractionManager(MineChess plugin){
        this.plugin = plugin;
    }

    public void selectPiece(GamePlayer gamePlayer, Arena arena, Piece piece){
        FileConfiguration messagesConfig = plugin.getMessagesConfig();
        MainConfigManager mainConfigManager = plugin.getConfigsManager().getMainConfigManager();

        int[] seeingPos = gamePlayer.getSeeingPos();
        int[] selectedPos = gamePlayer.getSelectedPos();

        PieceType pieceType = piece.getType();
        String pieceName = GameUtils.getPieceNameFromConfig(pieceType,messagesConfig);
        String coords = GameUtils.getRealCoords(seeingPos[0],seeingPos[1]);

        ArrayList<CommonVariable> variables = new ArrayList<>();
        variables.add(new CommonVariable("%player%",gamePlayer.getName()));
        variables.add(new CommonVariable("%piece%",pieceName));
        variables.add(new CommonVariable("%coords%",coords));

        if(selectedPos != null && Arrays.equals(selectedPos, seeingPos)){
            gamePlayer.setSelectedPos(null);
            gamePlayer.setSelectedPieceAvailableMovements(null);

            ActionUtils.executeActions(gamePlayer,arena,mainConfigManager.getGameActions().getGameActionsGame().getDeselectPiece(),plugin,variables,false);
        }else{
            gamePlayer.setSelectedPos(seeingPos);

            // Generate available movements
            gamePlayer.setSelectedPieceAvailableMovements(arena.getBoard().getPossibleMovements(seeingPos[0],seeingPos[1],true));

            ActionUtils.executeActions(gamePlayer,arena,mainConfigManager.getGameActions().getGameActionsGame().getSelectPiece(),plugin,variables,false);
        }

        // Update delay select piece
        gamePlayer.setDelaySelectPieceNextMillis(System.currentTimeMillis()+200);
    }

    public void movePieceAutomatically(GamePlayer gamePlayerTurn,Arena arena,int xFrom,int yFrom,Movement movement){
        int[] seeingPos = new int[]{movement.getX(), movement.getY()};
        int[] selectedPos = new int[]{xFrom,yFrom};

        movePiece(gamePlayerTurn,arena,seeingPos,selectedPos,movement);
    }

    public void movePieceManually(GamePlayer gamePlayer, Arena arena, Movement movement) {
        int[] seeingPos = gamePlayer.getSeeingPos();
        int[] selectedPos = gamePlayer.getSelectedPos();

        movePiece(gamePlayer,arena,seeingPos,selectedPos,movement);
    }

    private void movePiece(GamePlayer gamePlayer,Arena arena,int[] seeingPos,int[] selectedPos,Movement movement){
        FileConfiguration messagesConfig = plugin.getMessagesConfig();
        MainConfigManager mainConfigManager = plugin.getConfigsManager().getMainConfigManager();

        Piece selectedPiece = arena.getBoard().getPiece(selectedPos[0],selectedPos[1]);

        BoardManager boardManager = plugin.getBoardManager();
        MovePieceResult movePieceResult = arena.getBoard().move(selectedPos[0],selectedPos[1],movement);
        for(CoordinatePiece p : movePieceResult.getPiecesToUpdate()){
            boardManager.updateCell(p, arena);
        }

        String movedPieceName = GameUtils.getPieceNameFromConfig(selectedPiece.getType(),messagesConfig);
        String coordsString = GameUtils.getRealCoords(seeingPos[0],seeingPos[1]);
        GamePlayer opponentPlayer = arena.getOpponentPlayer(gamePlayer);

        ArrayList<CommonVariable> variables = new ArrayList<>();
        variables.add(new CommonVariable("%player%",gamePlayer.getName()));
        variables.add(new CommonVariable("%opponent_player%",opponentPlayer.getName()));

        boolean isCapture = movement.getType().equals(MovementType.CAPTURE) || movement.getType().equals(MovementType.EN_PASSANT)
                || movement.getType().equals(MovementType.PROMOTION_CAPTURE);
        if(isCapture){
            String capturedPieceName = GameUtils.getPieceNameFromConfig(movePieceResult.getCapturedPieceType(),messagesConfig);

            variables.add(new CommonVariable("%piece%",movedPieceName));
            variables.add(new CommonVariable("%coords%",coordsString));
            variables.add(new CommonVariable("%opponent_player_piece%",capturedPieceName));

            if(movement.getType().equals(MovementType.EN_PASSANT)){
                ActionUtils.executeActions(gamePlayer,arena,mainConfigManager.getGameActions().getGameActionsGame().getCapturePieceEnPassant(),plugin,variables,true);
            }else{
                ActionUtils.executeActions(gamePlayer,arena,mainConfigManager.getGameActions().getGameActionsGame().getCapturePiece(),plugin,variables,true);
            }

            // Points
            capturePoints(gamePlayer,movePieceResult.getCapturedPieceType(),mainConfigManager);
        }else if(movement.getType().name().startsWith("CASTLING")){
            // Castling
            String castlingCoords = null;
            if(movement.getType().equals(MovementType.CASTLING_LONG)){
                castlingCoords = GameUtils.getRealCoords(2,selectedPos[1]);
            }else{
                castlingCoords = GameUtils.getRealCoords(6,selectedPos[1]);
            }

            variables.add(new CommonVariable("%coords%",castlingCoords));
            ActionUtils.executeActions(gamePlayer,arena,mainConfigManager.getGameActions().getGameActionsGame().getCastling(),plugin,variables,true);
        }else{
            // Normal move
            variables.add(new CommonVariable("%piece%",movedPieceName));
            variables.add(new CommonVariable("%coords%",coordsString));
            ActionUtils.executeActions(gamePlayer,arena,mainConfigManager.getGameActions().getGameActionsGame().getMovePiece(),plugin,variables,true);
        }

        // Verify King in check
        CheckValidationResult result = check(arena,gamePlayer,opponentPlayer,mainConfigManager,variables);
        if(result.equals(CheckValidationResult.CHECKMATE) || result.equals(CheckValidationResult.STALEMATE)){
            return;
        }

        // Promotion
        if(movement.getType().name().startsWith("PROMOTION")){
            gamePlayer.setPromotionPos(new int[]{seeingPos[0],seeingPos[1]});

            plugin.getInventoryManager().openInventory(new InventoryPlayer(gamePlayer.getPlayer(), InventoryType.PROMOTION));
            return;
        }

        // Movements without progress (50 move rule)
        if(consecutiveMovementsWithoutProgressReached(arena,isCapture,selectedPiece.getType().equals(PieceType.PAWN),mainConfigManager)){
            return;
        }

        // Insufficient material
        if(insufficientMaterialReached(arena,mainConfigManager)){
            return;
        }

        plugin.getArenaManager().changeTurn(arena,!result.equals(CheckValidationResult.CHECK));
    }

    public void promotePawn(Arena arena, GamePlayer gamePlayer, PieceType pieceType){
        int[] promotionPos = gamePlayer.getPromotionPos();
        CoordinatePiece pieceToUpdate = arena.getBoard().promotePawn(promotionPos[0],promotionPos[1],pieceType,arena.getColor(gamePlayer));
        gamePlayer.setPromotionPos(null);
        gamePlayer.getPlayer().closeInventory();

        BoardManager boardManager = plugin.getBoardManager();
        boardManager.updateCell(pieceToUpdate,arena);

        FileConfiguration messagesConfig = plugin.getMessagesConfig();
        MainConfigManager mainConfigManager = plugin.getConfigsManager().getMainConfigManager();
        String movedPieceName = GameUtils.getPieceNameFromConfig(pieceType,messagesConfig);

        ArrayList<CommonVariable> variables = new ArrayList<>();
        GamePlayer opponentPlayer = arena.getOpponentPlayer(gamePlayer);
        variables.add(new CommonVariable("%player%",gamePlayer.getName()));
        variables.add(new CommonVariable("%piece%",movedPieceName));
        variables.add(new CommonVariable("%opponent_player%",opponentPlayer.getName()));
        ActionUtils.executeActions(gamePlayer,arena,mainConfigManager.getGameActions().getGameActionsGame().getPromotion(),plugin,variables,true);

        // Verify King in check
        CheckValidationResult result = check(arena,gamePlayer,opponentPlayer,mainConfigManager,variables);
        if(result.equals(CheckValidationResult.CHECKMATE) || result.equals(CheckValidationResult.STALEMATE)){
            return;
        }

        plugin.getArenaManager().changeTurn(arena,!result.equals(CheckValidationResult.CHECK));
    }

    // Returns true if checkmate
    private CheckValidationResult check(Arena arena, GamePlayer gamePlayer, GamePlayer opponentPlayer, MainConfigManager mainConfigManager, ArrayList<CommonVariable> variables){
        PlayerColor opponentColor = arena.getColor(opponentPlayer);
        int amountOfMovements = arena.getBoard().getAllPossibleMovements(opponentColor).size();
        if(arena.getBoard().verifyInCheck(opponentColor)) {
            // Verify checkmate
            if(amountOfMovements == 0){
                arena.setWinner(gamePlayer);
                plugin.getArenaManager().getGameEndManager().startEndingStage(arena, GameEndsReason.CHECKMATE);

                return CheckValidationResult.CHECKMATE;
            }

            ActionUtils.executeActions(gamePlayer,arena,mainConfigManager.getGameActions().getGameActionsGame().getKingInCheck(),plugin,variables,true);
            return CheckValidationResult.CHECK;
        }

        if(amountOfMovements == 0){
            plugin.getArenaManager().getGameEndManager().startEndingStage(arena,GameEndsReason.STALEMATE);

            return CheckValidationResult.STALEMATE;
        }

        return CheckValidationResult.NONE;
    }

    private boolean consecutiveMovementsWithoutProgressReached(Arena arena,boolean isCapture,boolean isPawnMovement,MainConfigManager mainConfigManager){
        if(mainConfigManager.getEnabledRulesConfig().isEndByMovementsWithoutProgressEnabled()){
            return false;
        }

        if(isCapture || isPawnMovement){
            arena.setMovementsWithoutProgress(0);
            return false;
        }

        arena.setMovementsWithoutProgress(arena.getMovementsWithoutProgress()+1);
        int maxMovements = mainConfigManager.getMaxConsecutiveMovementsWithoutProgress();
        if(arena.getMovementsWithoutProgress() >= maxMovements){
            plugin.getArenaManager().getGameEndManager().startEndingStage(arena,GameEndsReason.MOVEMENTS_WITHOUT_PROGRESS);
            return true;
        }
        return false;
    }

    private boolean insufficientMaterialReached(Arena arena,MainConfigManager mainConfigManager){
        if(mainConfigManager.getEnabledRulesConfig().isEndByInsufficientMaterialEnabled()){
            return false;
        }
        if(arena.getBoard().isInsufficientMaterial()){
            plugin.getArenaManager().getGameEndManager().startEndingStage(arena,GameEndsReason.INSUFFICIENT_MATERIAL);
            return true;
        }
        return false;
    }

    private void capturePoints(GamePlayer gamePlayer,PieceType pieceType,MainConfigManager mainConfigManager){
        int points = mainConfigManager.getPieceStructure(pieceType).getPoints();
        gamePlayer.addPoints(points);
    }

    public void updateInteractions(GamePlayer gamePlayer){
        gamePlayer.setSeeingPos(null);
        gamePlayer.setSelectedPos(null);
        gamePlayer.setSelectedPieceAvailableMovements(null);
    }
}
