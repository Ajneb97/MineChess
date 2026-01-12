package mc.ajneb97.manager;

import mc.ajneb97.MineChess;
import mc.ajneb97.config.model.*;
import mc.ajneb97.model.inventory.CommonInventory;
import mc.ajneb97.model.verify.*;
import mc.ajneb97.config.MainConfigManager;
import mc.ajneb97.manager.inventory.InventoryManager;
import mc.ajneb97.model.chess.PieceType;
import mc.ajneb97.model.inventory.CommonInventoryItem;
import mc.ajneb97.model.items.CommonItem;
import mc.ajneb97.utils.ItemUtils;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class VerifyManager {
    private MineChess plugin;
    private ArrayList<MineChessBaseError> errors;
    private boolean criticalErrors;
    public VerifyManager(MineChess plugin) {
        this.plugin = plugin;
        this.errors = new ArrayList<>();
        this.criticalErrors = false;
    }

    public void sendVerification(Player player) {
        player.sendMessage(MessagesManager.getLegacyColoredMessage("&f&l- - - - - - - - &6&lMINECHESS VERIFY &f&l- - - - - - - -"));
        player.sendMessage(MessagesManager.getLegacyColoredMessage(""));
        if(errors.isEmpty()) {
            player.sendMessage(MessagesManager.getLegacyColoredMessage("&aThere are no errors in the plugin ;)"));
        }else {
            player.sendMessage(MessagesManager.getLegacyColoredMessage("&e&oHover on the errors to see more information."));
            for(MineChessBaseError error : errors) {
                error.sendMessage(player);
            }
        }
        player.sendMessage(MessagesManager.getLegacyColoredMessage(""));
        player.sendMessage(MessagesManager.getLegacyColoredMessage("&f&l- - - - - - - - &6&lMINECHESS VERIFY &f&l- - - - - - - -"));
    }

    public void verify() {
        this.errors = new ArrayList<>();
        this.criticalErrors = false;

        // CHECK CONFIG
        verifyConfigOptions();

        // CHECK ARENAS
        verifyArenas();

        //CHECK INVENTORIES
        InventoryManager inventoryManager = plugin.getInventoryManager();
        ArrayList<CommonInventory> inventories = inventoryManager.getInventories();
        for(CommonInventory inventory : inventories){
            verifyInventory(inventory);
        }
    }

    public void verifyArenas(){
        MainConfigManager mainConfigManager = plugin.getConfigsManager().getMainConfigManager();

        ArrayList<PieceStructure> pieceStructure = mainConfigManager.getPieceStructures();
        for(PieceStructure piece : pieceStructure){
            PieceColorStructure pieceColorBlack = piece.getBlackPiece();
            PieceColorStructure pieceColorWhite = piece.getWhitePiece();
            if(pieceColorBlack.getBlocks() == null || pieceColorWhite.getBlocks() == null){
                errors.add(new MineChessNoPieceBlocks("config.yml", null, true, piece.getPieceType().name().toLowerCase()));
                criticalErrors = true;
            }
        }
    }

    public void verifyConfigOptions() {
        MainConfigManager mainConfigManager = plugin.getConfigsManager().getMainConfigManager();

        PieceInteractions pieceInteractions = mainConfigManager.getPieceInteractions();
        verifyParticleAndAddError("config.yml", pieceInteractions.getSeeCell().getValue());
        verifyParticleAndAddError("config.yml", pieceInteractions.getSelectedPiece().getValue());
        verifyParticleAndAddError("config.yml", pieceInteractions.getValidMovements().getValue());
        verifyParticleAndAddError("config.yml", pieceInteractions.getSeeValidMovementCell().getValue());

        GameActionsGame gameActionsGame = mainConfigManager.getGameActions().getGameActionsGame();
        verifyActions(gameActionsGame.getSelectPiece(),"game.select_piece","config.yml");
        verifyActions(gameActionsGame.getDeselectPiece(),"game.deselect_piece","config.yml");
        verifyActions(gameActionsGame.getChangeTurn(),"game.change_turn","config.yml");
        verifyActions(gameActionsGame.getMovePiece(),"game.move_piece","config.yml");
        verifyActions(gameActionsGame.getCapturePiece(),"game.capture_piece","config.yml");
        verifyActions(gameActionsGame.getCapturePieceEnPassant(),"game.capture_piece_enpassant","config.yml");
        verifyActions(gameActionsGame.getKingInCheck(),"game.king_in_check","config.yml");
        verifyActions(gameActionsGame.getCastling(),"game.castling","config.yml");
        verifyActions(gameActionsGame.getPromotion(),"game.promotion","config.yml");

        GameActionsRewards gameActionsRewards = mainConfigManager.getGameActions().getGameActionsRewards();
        verifyActions(gameActionsRewards.getEndByTimeActions(),"rewards.end_by_time","config.yml");
        verifyActions(gameActionsRewards.getEndByPlayerTimeActions(),"rewards.end_by_player_time","config.yml");
        verifyActions(gameActionsRewards.getEndByTimeTieActions(),"rewards.end_by_time_tie","config.yml");
        verifyActions(gameActionsRewards.getEndByStalemateTieActions(),"rewards.end_by_stalemate_tie","config.yml");
        verifyActions(gameActionsRewards.getEndByCheckmateActions(),"rewards.end_by_checkmate","config.yml");
        verifyActions(gameActionsRewards.getEndByLeaveActions(),"rewards.end_by_leave","config.yml");

        GameActionsEndGame gameActionsEndGame = mainConfigManager.getGameActions().getGameActionsEndGame();
        verifyActions(gameActionsEndGame.getEndByTimeActions(),"end_game.end_by_time","config.yml");
        verifyActions(gameActionsEndGame.getEndByPlayerTimeActions(),"end_game.end_by_player_time","config.yml");
        verifyActions(gameActionsEndGame.getEndByTimeTieActions(),"end_game.end_by_time_tie","config.yml");
        verifyActions(gameActionsEndGame.getEndByStalemateTieActions(),"end_game.end_by_stalemate_tie","config.yml");
        verifyActions(gameActionsEndGame.getEndByCheckmateActions(),"end_game.end_by_checkmate","config.yml");
        verifyActions(gameActionsEndGame.getEndByLeaveActions(),"end_game.end_by_leave","config.yml");
    }

    public void verifyActions(List<String> actions,String actionGroup,String fileName){
        if(actions == null){
            return;
        }
        for(int i=0;i<actions.size();i++){
            String actionOriginal = actions.get(i);
            String action = actionOriginal.replace("to_all: ","")
                    .replace("to_opponent: ","").replace("to_winner: ","")
                    .replace("to_loser: ","");
            String[] actionText = action.split(" ");
            String actionName = actionText[0];
            if(actionName.equals("console_command:") || actionName.equals("player_command:")
                    || actionName.equals("playsound:") || actionName.equals("message:")
                    || actionName.equals("centered_message:") || actionName.equals("title:")){
                continue;
            }
            errors.add(new MineChessActionError(fileName,actions.get(i),false,actionGroup,(i+1)+""));
        }
    }

    public void verifyInventory(CommonInventory inventory){
        List<CommonInventoryItem> items = inventory.getItems();
        int maxSlots = inventory.getSlots();
        for(CommonInventoryItem item : items){
           String type = item.getType();
           if(type != null){
               if(type.startsWith("piece: ")){
                   String pieceName = type.replace("piece: ","").toUpperCase();
                   try{
                       PieceType.valueOf(pieceName);
                   }catch(Exception e){
                       errors.add(new MineChessInvalidPieceType("inventory.yml",null,true,pieceName));
                       criticalErrors = true;
                   }
               }
           }

           //Items
           CommonItem commonItem = item.getItem();
           if(commonItem != null){
               verifyItemAndAddError("inventory.yml",commonItem.getId());
           }

           //Valid slots
           for(int slot : item.getSlots()){
               if(slot >= maxSlots){
                   errors.add(new MineChessInventoryInvalidSlotError("inventory.yml",null,true,slot,
                           inventory.getName(),maxSlots));
                   criticalErrors = true;
               }
           }
        }
    }

    public boolean isCriticalErrors() {
        return criticalErrors;
    }

    public void verifyItemAndAddError(String file,String material){
        try{
            ItemUtils.createItemFromID(material);
        }catch(Exception e){
            errors.add(new MineChessInvalidItem(file,null,true,material));
            criticalErrors = true;
        }
    }

    public void verifyParticleAndAddError(String file,String particle){
        particle = particle.split(";")[0];
        try{
            Particle.valueOf(particle);
        }catch(Exception e){
            errors.add(new MineChessInvalidParticle(file,null,false,particle));
        }
    }
}
