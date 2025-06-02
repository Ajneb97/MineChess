package mc.ajneb97.manager;

import mc.ajneb97.MineChess;
import mc.ajneb97.api.ArenaStartEvent;
import mc.ajneb97.config.MainConfigManager;
import mc.ajneb97.config.model.gameitems.GameItemConfig;
import mc.ajneb97.config.model.gameitems.GameItemsConfig;
import mc.ajneb97.manager.interactions.GamePieceInteractionManager;
import mc.ajneb97.model.Arena;
import mc.ajneb97.model.chess.PieceType;
import mc.ajneb97.model.game.GameEndsReason;
import mc.ajneb97.model.game.GameLeaveReason;
import mc.ajneb97.model.game.GamePlayer;
import mc.ajneb97.model.game.GameStatus;
import mc.ajneb97.model.internal.CommonVariable;
import mc.ajneb97.model.internal.CoordinateMovement;
import mc.ajneb97.utils.ActionUtils;
import mc.ajneb97.utils.GameUtils;
import mc.ajneb97.utils.ItemUtils;
import mc.ajneb97.utils.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ArenaManager {

    private MineChess plugin;
    private ArrayList<Arena> arenas;
    private Location mainLobbyLocation;

    private GamePlayerManager gamePlayerManager;
    private GameItemManager gameItemManager;
    private GamePieceInteractionManager gamePieceInteractionManager;
    private GameEndManager gameEndManager;
    private GameSpectatorManager gameSpectatorManager;

    public ArenaManager(MineChess plugin){
        this.plugin = plugin;
        this.arenas = new ArrayList<>();
        this.gamePlayerManager = new GamePlayerManager(plugin);
        this.gameItemManager = new GameItemManager(plugin);
        this.gamePieceInteractionManager = new GamePieceInteractionManager(plugin);
        this.gameEndManager = new GameEndManager(plugin);
        this.gameSpectatorManager = new GameSpectatorManager(plugin);
    }

    public Arena getArenaByName(String name){
        for(Arena arena : arenas){
            if(arena.getName().equals(name)){
                return arena;
            }
        }
        return null;
    }

    public ArrayList<Arena> getArenas() {
        return arenas;
    }

    public void setArenas(ArrayList<Arena> arenas) {
        this.arenas = arenas;
    }

    public Location getMainLobbyLocation() {
        return mainLobbyLocation;
    }

    public void setMainLobbyLocation(Location mainLobbyLocation) {
        this.mainLobbyLocation = mainLobbyLocation;
    }

    public String arenaIsValid(Arena arena, FileConfiguration messagesConfig){
        if(mainLobbyLocation == null){
            return messagesConfig.getString("arenaMainLobbyError");
        }
        if(arena.locationsAreMissing()){
            return messagesConfig.getString("arenaLocationsMissing");
        }
        return null;
    }

    public void addArena(String name){
        Arena arena = new Arena(name);

        MainConfigManager mainConfigManager = plugin.getConfigsManager().getMainConfigManager();
        arena.setDefaults(mainConfigManager.getArenaDefaultValues());

        arenas.add(arena);
        plugin.getConfigsManager().getArenasConfigManager().saveArena(arena);
        plugin.getVerifyManager().verify();
    }

    public void removeArena(String name){
        BoardManager boardManager = plugin.getBoardManager();
        for(int i=0;i<arenas.size();i++){
            if(arenas.get(i).getName().equals(name)){
                arenas.get(i).stopCooldownTask();
                boardManager.removeBoardFloor(arenas.get(i));
                arenas.remove(i);
                plugin.getConfigsManager().getArenasConfigManager().deleteArena(name);
                plugin.getVerifyManager().verify();
                return;
            }
        }
    }

    public void joinArena(Player player, Arena arena){
        MessagesManager msgManager = plugin.getMessagesManager();
        FileConfiguration messagesConfig = plugin.getMessagesConfig();

        if(plugin.getVerifyManager().isCriticalErrors()){
            msgManager.sendMessage(player,messagesConfig.getString("pluginCriticalErrors"),true);
            return;
        }
        if(arena.isDisabled()){
            msgManager.sendMessage(player,messagesConfig.getString("arenaIsDisabled"),true);
            return;
        }
        if(arena.isInGame()){
            msgManager.sendMessage(player,messagesConfig.getString("arenaIsInGame"),true);
            return;
        }
        if(arena.isFull()){
            msgManager.sendMessage(player,messagesConfig.getString("arenaIsFull"),true);
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

        playerJoinsArena(player,arena,messagesConfig,msgManager);
    }

    public void joinRandomArena(Player player){
        MessagesManager msgManager = plugin.getMessagesManager();
        FileConfiguration messagesConfig = plugin.getMessagesConfig();

        if(plugin.getVerifyManager().isCriticalErrors()){
            msgManager.sendMessage(player,messagesConfig.getString("pluginCriticalErrors"),true);
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

        Arena arena = getAvailableArena();
        if(arena == null){
            msgManager.sendMessage(player,messagesConfig.getString("noAvailableArenas"),true);
            return;
        }

        playerJoinsArena(player,arena,messagesConfig,msgManager);
    }

    public Arena getAvailableArena(){
        Arena arena = null;
        for(Arena a : arenas){
            if(a.isDisabled()){
                continue;
            }
            if(a.isInGame()){
                continue;
            }
            if(a.isFull()){
                continue;
            }

            if(arena == null){
                arena = a;
            }else{
                if(a.getGamePlayers().size() > arena.getGamePlayers().size()){
                    arena = a;
                }
            }
        }
        return arena;
    }

    private void playerJoinsArena(Player player, Arena arena, FileConfiguration messagesConfig, MessagesManager msgManager){
        GamePlayer gamePlayer = new GamePlayer(player);
        arena.addPlayer(gamePlayer);

        player.teleport(arena.getLobbyLocation());
        gamePlayerManager.clearPlayer(player);

        //Message
        int maxPlayers = 2;
        int currentPlayers = arena.getGamePlayers().size();
        for(GamePlayer p : arena.getGamePlayers()){
            msgManager.sendMessage(p.getPlayer(),messagesConfig.getString("playerJoin")
                    .replace("%current_players%",currentPlayers+"")
                    .replace("%max_players%",maxPlayers+"")
                    .replace("%player%",player.getName()),true);
        }

        //Pre game items
        Inventory inv = player.getInventory();
        GameItemsConfig gameItemsConfig = plugin.getConfigsManager().getMainConfigManager().getGameItemsConfig();
        CommonItemManager commonItemManager = plugin.getCommonItemManager();
        GameItemConfig leaveItem = gameItemsConfig.getLeaveItem();
        if(leaveItem.isEnabled()){
            ItemStack item = commonItemManager.createItemFromCommonItem(leaveItem.getItem());
            inv.setItem(8, ItemUtils.setTagStringItem(plugin,item,"minechess_item_type","leave"));
        }

        //Check if arena must start
        int minPlayers = 2;
        if(currentPlayers >= minPlayers && arena.getStatus().equals(GameStatus.WAITING)){
            startStartingStage(arena,messagesConfig,msgManager);
        }
    }

    private void startStartingStage(Arena arena, FileConfiguration messagesConfig, MessagesManager msgManager){
        arena.setStatus(GameStatus.STARTING);

        int time = plugin.getConfigsManager().getMainConfigManager().getArenaStartingCooldown();
        for(GamePlayer p : arena.getGamePlayers()){
            msgManager.sendMessage(p.getPlayer(),messagesConfig.getString("gameStarting")
                    .replace("%time%",time+""),true);
        }
        arena.startCooldownTask(plugin,time);
    }

    public void startPlayingStage(Arena arena) {
        arena.stopCooldownTask();
        arena.setStatus(GameStatus.PLAYING);
        arena.setMillisStart(System.currentTimeMillis());

        MessagesManager msgManager = plugin.getMessagesManager();
        FileConfiguration messagesConfig = plugin.getMessagesConfig();
        MainConfigManager mainConfigManager = plugin.getConfigsManager().getMainConfigManager();

        // Randomize players
        int num = new Random().nextInt(2);
        GamePlayer backup1 = arena.getPlayerBlack();
        GamePlayer backup2 = arena.getPlayerWhite();
        if(num == 0){
            arena.setPlayerBlack(backup2);
            arena.setPlayerWhite(backup1);
        }

        msgManager.sendMessage(arena.getPlayerBlack().getPlayer(), messagesConfig.getString("gameStarted")
                .replace("%piece_color%",messagesConfig.getString("pieceBlack")),true);
        msgManager.sendMessage(arena.getPlayerWhite().getPlayer(), messagesConfig.getString("gameStarted")
                .replace("%piece_color%",messagesConfig.getString("pieceWhite")),true);

        // Teleport
        arena.getPlayerBlack().getPlayer().teleport(arena.getSpawnPlayer1Location());
        arena.getPlayerWhite().getPlayer().teleport(arena.getSpawnPlayer2Location());

        plugin.getBoardManager().buildBoardPieces(arena);
        changeTurn(arena,true);

        for(GamePlayer gamePlayer : arena.getGamePlayers()){
            // Set Items
            Player player = gamePlayer.getPlayer();
            Inventory inv = player.getInventory();
            GameItemsConfig gameItemsConfig = mainConfigManager.getGameItemsConfig();
            CommonItemManager commonItemManager = plugin.getCommonItemManager();
            GameItemConfig leaveItem = gameItemsConfig.getSelectItem();
            ItemStack item = commonItemManager.createItemFromCommonItem(leaveItem.getItem());
            inv.setItem(0, ItemUtils.setTagStringItem(plugin,item,"minechess_item_type","select_piece"));

            if(mainConfigManager.isColoredArmor()){
                GameUtils.setColoredArmor(player,arena.getColor(gamePlayer));
            }
        }

        int time = arena.getMaxTime();
        arena.startCooldownTask(plugin,time);

        //API
        plugin.getServer().getPluginManager().callEvent(new ArenaStartEvent(arena));
    }

    public void changeTurn(Arena arena,boolean sendActions){
        MainConfigManager mainConfigManager = plugin.getConfigsManager().getMainConfigManager();

        arena.changeTurn();

        GamePlayer gamePlayer = arena.getPlayerTurn();
        ArrayList<CommonVariable> variables = new ArrayList<>();
        variables.add(new CommonVariable("%player%",gamePlayer.getName()));
        if(sendActions){
            ActionUtils.executeActions(gamePlayer,arena,mainConfigManager.getGameActions().getGameActionsGame().getChangeTurn(),plugin,variables,true);
        }

        for(GamePlayer g : arena.getGamePlayers()){
            gamePieceInteractionManager.updateInteractions(g);
        }
    }

    public void automaticMove(Arena arena){
        GamePlayer gamePlayer = arena.getPlayerTurn();
        if(gamePlayer.isOnPromotion()){
            List<PieceType> pieces = Arrays.asList(PieceType.BISHOP,PieceType.KNIGHT,PieceType.QUEEN,PieceType.ROOK);
            PieceType piece = pieces.get(new Random().nextInt(pieces.size()));
            gamePieceInteractionManager.promotePawn(arena,gamePlayer,piece);
        }else{
            ArrayList<CoordinateMovement> allPossibleMovements = arena.getBoard().getAllPossibleMovements(arena.getPlayerColorTurn());
            CoordinateMovement m = allPossibleMovements.get(new Random().nextInt(allPossibleMovements.size()));
            gamePieceInteractionManager.movePieceAutomatically(gamePlayer,arena,m.getFromX(),m.getFromY(),m.getMovement());
        }
    }

    public void leaveArena(Player player, Arena arena, GameLeaveReason reason){
        MessagesManager msgManager = plugin.getMessagesManager();
        FileConfiguration messagesConfig = plugin.getMessagesConfig();
        GamePlayer gamePlayer = arena.getGamePlayer(player,true);
        if(gamePlayer.isSpectator()){
            gameSpectatorManager.leaveArena(player,arena,reason);
            return;
        }

        plugin.getScoreboardManager().removeScoreboard(player);
        gamePieceInteractionManager.updateInteractions(gamePlayer);

        //Check if arena must end
        int minPlayers = 2;
        int currentPlayers = arena.getGamePlayers().size()-1;
        if(!reason.equals(GameLeaveReason.SERVER_STOP) && !reason.equals(GameLeaveReason.ARENA_DISABLED)){
            if(currentPlayers < minPlayers && arena.getStatus().equals(GameStatus.STARTING)){
                arena.setStatus(GameStatus.WAITING);
                arena.stopCooldownTask();
                for(GamePlayer p : arena.getGamePlayers()){
                    msgManager.sendMessage(p.getPlayer(),messagesConfig.getString("gameStartingCancelled"),true);
                }
            }else if(arena.getStatus().equals(GameStatus.PLAYING) && currentPlayers < minPlayers){
                // Set winner
                if(arena.getPlayerWhite() != null && arena.getPlayerWhite().equals(gamePlayer)){
                    arena.setWinner(arena.getPlayerBlack());
                }else if(arena.getPlayerBlack() != null && arena.getPlayerBlack().equals(gamePlayer)){
                    arena.setWinner(arena.getPlayerWhite());
                }
                gameEndManager.startEndingStage(arena, GameEndsReason.PLAYER_LEAVES);
            }
        }

        //Teleport back to lobby
        Location location = plugin.getArenaManager().getMainLobbyLocation();
        player.teleport(location);

        //Restore backup
        plugin.getPlayerDataManager().getPlayerDataBackupManager().restorePlayerDataBackup(player,false,!reason.equals(GameLeaveReason.SERVER_STOP));
        arena.removeGamePlayer(player);

        if(reason.equals(GameLeaveReason.ARENA_DISABLED)){
            msgManager.sendMessage(player,messagesConfig.getString("arenaDisabledKickMessage"),true);
        }

        //Message
        int maxPlayers = 2;
        if(!reason.equals(GameLeaveReason.END_GAME)){
            for(GamePlayer p : arena.getGamePlayers(true)){
                msgManager.sendMessage(p.getPlayer(),messagesConfig.getString("playerLeave")
                        .replace("%current_players%",currentPlayers+"")
                        .replace("%max_players%",maxPlayers+"")
                        .replace("%player%",player.getName()),true);
            }
        }
    }



    public void disableArena(Arena arena){
        gameEndManager.endGame(arena,GameLeaveReason.ARENA_DISABLED);
        arena.disable();
    }

    private void kickPlayersFromArena(Arena arena,GameLeaveReason reason){
        ArrayList<GamePlayer> playersCopy = new ArrayList<>(arena.getGamePlayers());
        for(GamePlayer p : playersCopy){
            leaveArena(p.getPlayer(),arena,reason);
        }
    }

    public void endAllArenasFromServerStop(){
        for(Arena arena : arenas){
            gameEndManager.endGame(arena,GameLeaveReason.SERVER_STOP);
        }
    }

    public GamePlayerManager getGamePlayerManager() {
        return gamePlayerManager;
    }

    public void winnersFireworks(Arena arena){
        new BukkitRunnable(){
            @Override
            public void run() {
                GamePlayer winner = arena.getWinner();
                if(winner == null){
                    return;
                }

                Player player = winner.getPlayer();
                if(player == null){
                    return;
                }

                Arena arenaPlayer = plugin.getArenaManager().getGamePlayerManager().getArenaByPlayer(player);
                if(arenaPlayer != null && arenaPlayer.equals(arena)){
                    String actionLine = "colors:RED type:BALL fade:AQUA power:2";
                    ActionUtils.firework(arena.getWinner().getPlayer(),actionLine,plugin);
                }
            }
        }.runTask(plugin);
    }

    public GameItemManager getGameItemManager() {
        return gameItemManager;
    }

    public GamePieceInteractionManager getGamePieceInteractionManager() {
        return gamePieceInteractionManager;
    }

    public GameEndManager getGameEndManager() {
        return gameEndManager;
    }

    public GameSpectatorManager getGameSpectatorManager() {
        return gameSpectatorManager;
    }

}
