package mc.ajneb97.tasks;

import mc.ajneb97.config.model.PieceInteraction;
import mc.ajneb97.manager.ArenaManager;
import mc.ajneb97.model.chess.Piece;
import mc.ajneb97.MineChess;
import mc.ajneb97.config.MainConfigManager;
import mc.ajneb97.manager.BoardManager;
import mc.ajneb97.model.Arena;
import mc.ajneb97.model.chess.Movement;
import mc.ajneb97.model.game.GamePlayer;
import mc.ajneb97.utils.ParticleUtils;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ArenaParticlesTask {
    private MineChess plugin;
    public ArenaParticlesTask(MineChess plugin){
        this.plugin = plugin;
    }

    public void start(){
        new BukkitRunnable(){
            @Override
            public void run() {
                execute();
            }
        }.runTaskTimerAsynchronously(plugin,0L,5L);
    }

    public void execute(){
        ArenaManager arenaManager = plugin.getArenaManager();
        BoardManager boardManager = plugin.getBoardManager();
        MainConfigManager mainConfigManager = plugin.getConfigsManager().getMainConfigManager();

        PieceInteraction seePieceInteraction = mainConfigManager.getPieceInteractions().getSeeCell();
        PieceInteraction selectedPieceInteraction = mainConfigManager.getPieceInteractions().getSelectedPiece();
        PieceInteraction validMovementsInteraction = mainConfigManager.getPieceInteractions().getValidMovements();
        PieceInteraction seeValidMovementsCellInteraction = mainConfigManager.getPieceInteractions().getSeeValidMovementCell();
        PieceInteraction invalidCheckMovementsInteraction = mainConfigManager.getPieceInteractions().getInvalidCheckMovements();

        for(Arena arena : arenaManager.getArenas()){
            for(GamePlayer gamePlayer : arena.getGamePlayers()){
                int[] seeingPos = gamePlayer.getSeeingPos();
                int[] selectedPos = gamePlayer.getSelectedPos();
                ArrayList<Movement> movementsPos = gamePlayer.getSelectedPieceAvailableMovements();

                if(selectedPos != null){
                    particlesAtPos(gamePlayer,arena,selectedPos,selectedPieceInteraction,boardManager);
                }
                boolean alreadySeeing = false;
                if(movementsPos != null){
                    for(Movement movement : movementsPos){
                        if(movement.isPutsInCheck()){
                            particlesAtPos(gamePlayer,arena,new int[]{movement.getX(), movement.getY()},invalidCheckMovementsInteraction,boardManager);
                        }else{
                            particlesAtPos(gamePlayer,arena,new int[]{movement.getX(), movement.getY()},validMovementsInteraction,boardManager);
                            if(seeingPos != null && seeingPos[0] == movement.getX() && seeingPos[1] == movement.getY()){
                                particlesAtPos(gamePlayer,arena,new int[]{movement.getX(), movement.getY()},seeValidMovementsCellInteraction,boardManager);
                                alreadySeeing = true;
                            }
                        }
                    }
                }
                if(!alreadySeeing && seeingPos != null){
                    Piece piece = arena.getBoard().getPiece(seeingPos[0],seeingPos[1]);
                    if((piece != null && piece.getColor().equals(arena.getColor(gamePlayer))) || gamePlayer.isPossibleMovementCell(seeingPos)){
                        particlesAtPos(gamePlayer,arena,seeingPos,seePieceInteraction,boardManager);
                    }
                }
            }
        }
    }

    private void particlesAtPos(GamePlayer gamePlayer, Arena arena, int[] pos, PieceInteraction pieceInteraction, BoardManager boardManager){
        if(!pieceInteraction.isEnabled()){
            return;
        }

        Location l = boardManager.getCellLocationFromPositionCentered(pos,arena);
        l.add(0,1.5+pieceInteraction.getOffsetY(),0);

        String particle = pieceInteraction.getValue();
        double size = pieceInteraction.getSize();

        if(pieceInteraction.getType().equals(PieceInteraction.ParticleFormType.CIRCLE)){
            ParticleUtils.spawnCircleParticle(gamePlayer.getPlayer(),particle,l,size);
        }else{
            ParticleUtils.spawnSquareParticle(gamePlayer.getPlayer(),particle,l,size);
        }
    }
}
