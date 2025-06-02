package mc.ajneb97.model.game;

import mc.ajneb97.model.chess.Movement;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class GamePlayer {
    private Player player;
    private int[] previousPos;
    private int[] seeingPos;
    private int[] selectedPos;
    private ArrayList<Movement> selectedPieceAvailableMovements;
    private int points;
    private int turnTime;
    private int[] promotionPos;
    private long delaySelectPieceNextMillis;
    private boolean isSpectator;


    public GamePlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void addPoints(int value){
        this.points = this.points+value;
    }

    public int[] getSeeingPos() {
        return seeingPos;
    }

    public void setSeeingPos(int[] seeingPos) {
        this.seeingPos = seeingPos;
    }

    public int[] getPreviousPos() {
        return previousPos;
    }

    public void setPreviousPos(int[] previousPos) {
        this.previousPos = previousPos;
    }

    public int[] getSelectedPos() {
        return selectedPos;
    }

    public void setSelectedPos(int[] selectedPos) {
        this.selectedPos = selectedPos;
    }

    public ArrayList<Movement> getSelectedPieceAvailableMovements() {
        return selectedPieceAvailableMovements;
    }

    public void setSelectedPieceAvailableMovements(ArrayList<Movement> selectedPieceAvailableMovements) {
        this.selectedPieceAvailableMovements = selectedPieceAvailableMovements;
    }

    public boolean isPossibleMovementCell(int[] pos){
        if(selectedPieceAvailableMovements != null){
            for(Movement m : selectedPieceAvailableMovements){
                if(pos[0] == m.getX() && pos[1] == m.getY()){
                    return true;
                }
            }
        }
        return false;
    }

    public Movement getPossibleMovementCell(int[] pos){
        if(selectedPieceAvailableMovements != null){
            for(Movement m : selectedPieceAvailableMovements){
                if(pos[0] == m.getX() && pos[1] == m.getY()){
                    return m;
                }
            }
        }
        return null;
    }

    public int getTurnTime() {
        return turnTime;
    }

    public void setTurnTime(int turnTime) {
        this.turnTime = turnTime;
    }

    public void reduceTurnTime(){
        this.turnTime--;
    }

    public boolean isOnPromotion() {
        return promotionPos != null;
    }

    public int[] getPromotionPos() {
        return promotionPos;
    }

    public void setPromotionPos(int[] promotionPos) {
        this.promotionPos = promotionPos;
    }

    public long getDelaySelectPieceNextMillis() {
        return delaySelectPieceNextMillis;
    }

    public void setDelaySelectPieceNextMillis(long delaySelectPieceNextMillis) {
        this.delaySelectPieceNextMillis = delaySelectPieceNextMillis;
    }

    public boolean isAfterDelaySelectPiece(){
        return System.currentTimeMillis() >= this.delaySelectPieceNextMillis;
    }

    public boolean isSpectator() {
        return isSpectator;
    }

    public void setSpectator(boolean spectator) {
        isSpectator = spectator;
    }

    public String getName(){
        return player.getName();
    }
}
