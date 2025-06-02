package mc.ajneb97.model;

import mc.ajneb97.MineChess;
import mc.ajneb97.model.chess.Board;
import mc.ajneb97.model.game.GameEndsReason;
import mc.ajneb97.model.game.GameStatus;
import mc.ajneb97.model.game.GamePlayer;
import mc.ajneb97.tasks.ArenaCooldownManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Arena {
    private String name;
    private Board board;
    private Material boardBlackCellBlock;
    private Material boardWhiteCellBlock;
    private int maxTime;
    private int turnTime;
    private ArenaEndTimeMode endTimeMode;
    private Location lobbyLocation;
    private Location spawnPlayer1Location;
    private Location spawnPlayer2Location;
    private Location boardStartLocation;

    private GameStatus status;
    private GamePlayer playerBlack;
    private GamePlayer playerWhite;
    private GamePlayer winner;
    private PlayerColor playerColorTurn;
    private GameEndsReason endReason;
    private long millisStart;
    private ArrayList<GamePlayer> spectators;

    private ArenaCooldownManager arenaCooldownManager;

    public Arena(String name){
        this.name = name;
        this.status = GameStatus.DISABLED;
        this.board = new Board();
        this.board.createInitialBoard();
        this.spectators = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Material getBoardBlackCellBlock() {
        return boardBlackCellBlock;
    }

    public void setBoardBlackCellBlock(Material boardBlackCellBlock) {
        this.boardBlackCellBlock = boardBlackCellBlock;
    }

    public Material getBoardWhiteCellBlock() {
        return boardWhiteCellBlock;
    }

    public void setBoardWhiteCellBlock(Material boardWhiteCellBlock) {
        this.boardWhiteCellBlock = boardWhiteCellBlock;
    }

    public Location getLobbyLocation() {
        return lobbyLocation;
    }

    public void setLobbyLocation(Location lobbyLocation) {
        this.lobbyLocation = lobbyLocation;
    }

    public Location getSpawnPlayer1Location() {
        return spawnPlayer1Location;
    }

    public void setSpawnPlayer1Location(Location spawnPlayer1Location) {
        this.spawnPlayer1Location = spawnPlayer1Location;
    }

    public Location getSpawnPlayer2Location() {
        return spawnPlayer2Location;
    }

    public void setSpawnPlayer2Location(Location spawnPlayer2Location) {
        this.spawnPlayer2Location = spawnPlayer2Location;
    }

    public Location getBoardStartLocation() {
        return boardStartLocation;
    }

    public void setBoardStartLocation(Location boardStartLocation) {
        this.boardStartLocation = boardStartLocation;
    }

    public boolean isDisabled(){
        return status.equals(GameStatus.DISABLED);
    }

    public void configureLocations(Location boardStartLocation,int cellSize){
        this.boardStartLocation = boardStartLocation;

        this.lobbyLocation = boardStartLocation.clone().add(cellSize*4,cellSize*1.5,cellSize*4);
        this.spawnPlayer1Location = boardStartLocation.clone().add(cellSize*4,cellSize*2,cellSize);
        this.spawnPlayer1Location.setYaw(0);this.spawnPlayer1Location.setPitch(40);
        this.spawnPlayer2Location = boardStartLocation.clone().add(cellSize*4,cellSize*2,cellSize*7);
        this.spawnPlayer2Location.setYaw(180);this.spawnPlayer2Location.setPitch(40);
    }

    public boolean locationsAreMissing(){
        return lobbyLocation == null || spawnPlayer1Location == null || spawnPlayer2Location == null;
    }

    public void enable(){
        status = GameStatus.WAITING;
    }

    public void disable(){
        status = GameStatus.DISABLED;
    }

    public boolean isInGame(){
        return status.equals(GameStatus.PLAYING) || status.equals(GameStatus.ENDING);
    }

    public boolean isEnding(){
        return status.equals(GameStatus.ENDING);
    }

    public GamePlayer getPlayerBlack() {
        return playerBlack;
    }

    public void setPlayerBlack(GamePlayer playerBlack) {
        this.playerBlack = playerBlack;
    }

    public GamePlayer getPlayerWhite() {
        return playerWhite;
    }

    public void setPlayerWhite(GamePlayer playerWhite) {
        this.playerWhite = playerWhite;
    }

    public ArrayList<GamePlayer> getGamePlayers(){
        ArrayList<GamePlayer> players = new ArrayList<>();
        if(playerBlack != null){
            players.add(playerBlack);
        }
        if(playerWhite != null){
            players.add(playerWhite);
        }
        return players;
    }

    public ArrayList<GamePlayer> getGamePlayers(boolean includeSpectators){
        ArrayList<GamePlayer> players = getGamePlayers();
        if(includeSpectators){
            players.addAll(spectators);
        }
        return players;
    }

    public boolean isFull(){
        return playerBlack != null && playerWhite != null;
    }

    public GamePlayer getGamePlayer(Player player){
        if(playerWhite != null && playerWhite.getPlayer() != null && playerWhite.getPlayer().equals(player)){
            return playerWhite;
        }
        if(playerBlack != null && playerBlack.getPlayer() != null && playerBlack.getPlayer().equals(player)){
            return playerBlack;
        }
        return null;
    }

    public GamePlayer getGamePlayer(Player player,boolean includeSpectator){
        GamePlayer gamePlayer = getGamePlayer(player);
        if(gamePlayer != null){
            return gamePlayer;
        }
        if(includeSpectator){
            for(GamePlayer g : spectators){
                if(g.getPlayer().equals(player)){
                    return g;
                }
            }
        }
        return null;
    }

    public void addPlayer(GamePlayer gamePlayer){
        if(playerWhite == null){
            playerWhite = gamePlayer;
            return;
        }
        if(playerBlack == null){
            playerBlack = gamePlayer;
        }
    }

    public void startCooldownTask(MineChess plugin, int time){
        stopCooldownTask();
        arenaCooldownManager = new ArenaCooldownManager(plugin,this,time);
        arenaCooldownManager.start();
    }

    public void stopCooldownTask(){
        if(arenaCooldownManager != null){
            arenaCooldownManager.stop();
            arenaCooldownManager = null;
        }
    }

    public int getCooldownTime(){
        if(arenaCooldownManager != null){
            return arenaCooldownManager.getTime();
        }
        return 0;
    }

    public int getWhitePoints(){
        if(playerWhite != null){
            return playerWhite.getPoints();
        }
        return 0;
    }

    public int getBlackPoints(){
        if(playerBlack != null){
            return playerBlack.getPoints();
        }
        return 0;
    }

    public void removeGamePlayer(Player player){
        if(playerWhite != null && playerWhite.getPlayer().equals(player)){
            playerWhite = null;
            return;
        }
        if(playerBlack != null && playerBlack.getPlayer().equals(player)){
            playerBlack = null;
        }
    }

    public GamePlayer getWinner() {
        return winner;
    }

    public void setWinner(GamePlayer winner) {
        this.winner = winner;
    }

    public void resetArena(){
        winner = null;
        playerColorTurn = null;
        this.board = new Board();
        this.board.createInitialBoard();
        millisStart = 0;
        endReason = null;
    }

    public PlayerColor getColor(GamePlayer gamePlayer){
        if(playerWhite != null && playerWhite.equals(gamePlayer)){
            return PlayerColor.WHITE;
        }
        if(playerBlack != null && playerBlack.equals(gamePlayer)){
            return PlayerColor.BLACK;
        }
        return null;
    }

    public GamePlayer getPlayerByColor(PlayerColor playerColor){
        if(playerColor.equals(PlayerColor.WHITE)){
            return playerWhite;
        }
        return playerBlack;
    }

    public PlayerColor getPlayerColorTurn() {
        return playerColorTurn;
    }

    public GamePlayer getPlayerTurn(){
        return getPlayerByColor(playerColorTurn);
    }

    public GamePlayer getOpponentPlayer(GamePlayer gamePlayer){
        if(playerWhite == gamePlayer){
            return playerBlack;
        }
        return playerWhite;
    }

    public void setPlayerColorTurn(PlayerColor playerColorTurn) {
        this.playerColorTurn = playerColorTurn;
    }

    public void changeTurn(){
        playerBlack.setTurnTime(turnTime);
        playerWhite.setTurnTime(turnTime);

        if(playerColorTurn == null){
            playerColorTurn = PlayerColor.WHITE;
            return;
        }

        if(playerColorTurn.equals(PlayerColor.WHITE)){
            playerColorTurn = PlayerColor.BLACK;
        }else{
            playerColorTurn = PlayerColor.WHITE;
        }
    }

    public int getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    public int getTurnTime() {
        return turnTime;
    }

    public void setTurnTime(int turnTime) {
        this.turnTime = turnTime;
    }

    public ArenaEndTimeMode getEndTimeMode() {
        return endTimeMode;
    }

    public void setEndTimeMode(ArenaEndTimeMode endTimeMode) {
        this.endTimeMode = endTimeMode;
    }

    public void setDefaults(Arena defaultArena){
        endTimeMode = defaultArena.getEndTimeMode();
        maxTime = defaultArena.getMaxTime();
        turnTime = defaultArena.getTurnTime();
        boardBlackCellBlock = defaultArena.getBoardBlackCellBlock();
        boardWhiteCellBlock = defaultArena.getBoardWhiteCellBlock();
    }

    public boolean isTurnPlayer(GamePlayer gamePlayer){
        return getPlayerTurn() == gamePlayer;
    }

    public long getMillisStart() {
        return millisStart;
    }

    public void setMillisStart(long millisStart) {
        this.millisStart = millisStart;
    }

    public GameEndsReason getEndReason() {
        return endReason;
    }

    public void setEndReason(GameEndsReason endReason) {
        this.endReason = endReason;
    }

    public ArrayList<GamePlayer> getSpectators() {
        return spectators;
    }

    public void setSpectators(ArrayList<GamePlayer> spectators) {
        this.spectators = spectators;
    }

    public void addSpectator(Player player){
        GamePlayer gamePlayer = new GamePlayer(player);
        gamePlayer.setSpectator(true);
        this.spectators.add(gamePlayer);
    }

    public void removeSpectator(Player player){
        this.spectators.removeIf(g -> g.getPlayer().equals(player));
    }

}
