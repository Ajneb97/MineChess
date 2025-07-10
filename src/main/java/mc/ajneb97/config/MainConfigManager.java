package mc.ajneb97.config;

import mc.ajneb97.MineChess;
import mc.ajneb97.config.model.*;
import mc.ajneb97.config.model.gameitems.GameItemConfig;
import mc.ajneb97.config.model.gameitems.GameItemsConfig;
import mc.ajneb97.config.model.gameitems.SoundConfig;
import mc.ajneb97.manager.CommonItemManager;
import mc.ajneb97.model.Arena;
import mc.ajneb97.model.ArenaEndTimeMode;
import mc.ajneb97.model.chess.PieceType;
import mc.ajneb97.utils.ServerVersion;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MainConfigManager {

    private MineChess plugin;
    private CommonConfig configFile;

    private int arenaStartingCooldown;
    private int arenaEndingPhaseCooldown;
    private boolean emptyInventoryToJoin;
    private int selectPieceMaxDistance;
    private int maxDistanceFromArenaLobby;
    private int playerDataSave;
    private boolean coloredArmor;
    private GameTimeLimitations gameTimeLimitations;
    private ArrayList<PieceStructure> pieceStructures;
    private GameItemsConfig gameItemsConfig;
    private List<String> commandsWhitelist;
    private PerArenaChatConfig perArenaChat;
    private PieceInteractions pieceInteractions;
    private GameActions gameActions;
    private Arena arenaDefaultValues;
    private PiecesHologramsConfig piecesHologramsConfig;
    private int maxConsecutiveMovementsWithoutProgress;
    private boolean isMySQL;
    private boolean updateNotify;


    public MainConfigManager(MineChess plugin){
        this.plugin = plugin;
        this.configFile = new CommonConfig("config.yml",plugin,null,false);
        configFile.registerConfig();

        checkUpdate();
    }

    public void configure() {
        FileConfiguration config = configFile.getConfig();
        updateNotify = config.getBoolean("update_notify");
        arenaStartingCooldown = config.getInt("arena_starting_cooldown");
        arenaEndingPhaseCooldown = config.getInt("arena_ending_phase_cooldown");
        emptyInventoryToJoin = config.getBoolean("empty_inventory_to_join");
        selectPieceMaxDistance = config.getInt("select_piece_max_distance");
        maxDistanceFromArenaLobby = config.getInt("max_distance_from_arena_lobby");
        playerDataSave = config.getInt("player_data_save");
        coloredArmor = config.getBoolean("colored_armor");

        gameTimeLimitations = new GameTimeLimitations(
                config.getInt("game_time_limitations.min_time"),
                config.getBoolean("game_time_limitations.limit_rewards"),
                config.getBoolean("game_time_limitations.limit_stats")
        );

        pieceStructures = new ArrayList<>();
        for(String key : config.getConfigurationSection("pieces").getKeys(false)){
            PieceType pieceType = PieceType.valueOf(key.toUpperCase());
            int points = config.getInt("pieces."+key+".points");
            List<String> blocksBlack = config.contains("pieces."+key+".black.blocks") ? config.getStringList("pieces."+key+".black.blocks") : null;
            List<String> blocksWhite = config.contains("pieces."+key+".white.blocks") ? config.getStringList("pieces."+key+".white.blocks") : null;
            PieceHologramsValues hologramBlack = configurePieceHologram(config,"pieces."+key+".black");
            PieceHologramsValues hologramWhite = configurePieceHologram(config,"pieces."+key+".white");

            PieceColorStructure pieceBlack = new PieceColorStructure(
                    blocksBlack,hologramBlack
            );
            PieceColorStructure pieceWhite = new PieceColorStructure(
                    blocksWhite,hologramWhite
            );

            pieceStructures.add(new PieceStructure(pieceType,points,pieceBlack,pieceWhite));
        }

        CommonItemManager commonItemManager = plugin.getCommonItemManager();
        GameItemConfig leaveItem = new GameItemConfig(
                config.getBoolean("game_items.leave_item.enabled"),
                commonItemManager.getCommonItemFromConfig(config,"game_items.leave_item.item")
        );
        GameItemConfig selectItem = new GameItemConfig(
                true, commonItemManager.getCommonItemFromConfig(config,"game_items.select_item.item")
        );
        GameItemConfig playAgainItem = new GameItemConfig(
                config.getBoolean("game_items.play_again_item.enabled"),
                commonItemManager.getCommonItemFromConfig(config,"game_items.play_again_item.item")
        );
        gameItemsConfig = new GameItemsConfig(leaveItem,selectItem,playAgainItem);

        commandsWhitelist = config.getStringList("commands_whitelist");
        perArenaChat = new PerArenaChatConfig(
                config.getBoolean("per_arena_chat.enabled"),
                config.getString("per_arena_chat.format")
        );

        pieceInteractions = new PieceInteractions(
                configurePieceInteraction(config,"piece_interactions.interact.see_cell"),
                configurePieceInteraction(config,"piece_interactions.interact.selected_piece"),
                configurePieceInteraction(config,"piece_interactions.interact.valid_movements"),
                configurePieceInteraction(config,"piece_interactions.interact.see_valid_movement_cell")
        );

        GameActionsGame gameActionsGame = new GameActionsGame(
                config.getStringList("actions.game.select_piece"),
                config.getStringList("actions.game.deselect_piece"),
                config.getStringList("actions.game.change_turn"),
                config.getStringList("actions.game.move_piece"),
                config.getStringList("actions.game.capture_piece"),
                config.getStringList("actions.game.capture_piece_enpassant"),
                config.getStringList("actions.game.king_in_check"),
                config.getStringList("actions.game.castling"),
                config.getStringList("actions.game.promotion")
        );
        GameActionsEndGame gameActionsEndGame = new GameActionsEndGame(
                config.getStringList("actions.end_game.end_by_time"),
                config.getStringList("actions.end_game.end_by_player_time"),
                config.getStringList("actions.end_game.end_by_time_tie"),
                config.getStringList("actions.end_game.end_by_stalemate_tie"),
                config.getStringList("actions.end_game.end_by_checkmate"),
                config.getStringList("actions.end_game.end_by_leave"),
                config.getStringList("actions.end_game.end_by_movements_without_progress_tie")
        );
        GameActionsRewards gameActionsRewards = new GameActionsRewards(
                config.getBoolean("actions.rewards.after_teleport"),
                config.getStringList("actions.rewards.end_by_time"),
                config.getStringList("actions.rewards.end_by_player_time"),
                config.getStringList("actions.rewards.end_by_time_tie"),
                config.getStringList("actions.rewards.end_by_stalemate_tie"),
                config.getStringList("actions.rewards.end_by_checkmate"),
                config.getStringList("actions.rewards.end_by_leave"),
                config.getStringList("actions.rewards.end_by_movements_without_progress_tie")
        );
        gameActions = new GameActions(gameActionsGame,gameActionsEndGame,gameActionsRewards);

        piecesHologramsConfig = new PiecesHologramsConfig(
                config.getBoolean("pieces_holograms.enabled"),
                new PieceHologramsValues(
                        config.getString("pieces_holograms.default_values.text"),
                        config.getDouble("pieces_holograms.default_values.offset_y")
                )
        );

        maxConsecutiveMovementsWithoutProgress = config.getInt("max_consecutive_movements_without_progress");

        isMySQL = config.getBoolean("mysql_database.enabled");

        configureArenaDefaultValues(config);
    }

    public boolean reloadConfig(){
        if(!configFile.reloadConfig()){
            return false;
        }
        configure();
        return true;
    }

    private void configureArenaDefaultValues(FileConfiguration config){
        arenaDefaultValues = new Arena("default");
        arenaDefaultValues.setEndTimeMode(ArenaEndTimeMode.valueOf(config.getString("arenas_default_values.end_time")));
        arenaDefaultValues.setMaxTime(config.getInt("arenas_default_values.gamemode_values.max_time"));
        arenaDefaultValues.setTurnTime(config.getInt("arenas_default_values.gamemode_values.turn_time"));
        arenaDefaultValues.setBoardBlackCellBlock(Material.valueOf(config.getString("arenas_default_values.board_black_cell_block")));
        arenaDefaultValues.setBoardWhiteCellBlock(Material.valueOf(config.getString("arenas_default_values.board_white_cell_block")));
    }

    private PieceHologramsValues configurePieceHologram(FileConfiguration config, String path){
        if(!config.contains(path+".hologram")){
            return null;
        }
        return new PieceHologramsValues(
                config.getString(path+".hologram.text"),
                config.getDouble(path+".hologram.offset_y"));
    }

    private PieceInteraction configurePieceInteraction(FileConfiguration config, String path){
        String value = config.getString(path+".value");
        PieceInteraction.ParticleFormType type = null;
        double size = 0;
        double offsetY = 0;
        if(config.contains(path+".type")){
            type = PieceInteraction.ParticleFormType.valueOf(config.getString(path+".type"));
        }
        if(config.contains(path+".size")){
            size = config.getDouble(path+".size");
        }
        if(config.contains(path+".offset_y")){
            offsetY = config.getDouble(path+".offset_y");
        }

        PieceInteraction pieceInteraction = new PieceInteraction(value);
        pieceInteraction.setType(type);
        pieceInteraction.setSize(size);
        pieceInteraction.setOffsetY(offsetY);

        return pieceInteraction;
    }

    private SoundConfig configureSound(FileConfiguration config, String path){
        String soundString = config.getString(path);
        if(soundString.equals("none")){
            return null;
        }
        String[] soundStringSep = soundString.split(";");
        return new SoundConfig(
                soundStringSep[0],Float.parseFloat(soundStringSep[1]),Float.parseFloat(soundStringSep[2])
        );
    }

    public void checkUpdate(){
        Path pathConfig = Paths.get(configFile.getRoute());
        try{
            String text = new String(Files.readAllBytes(pathConfig));
            FileConfiguration config = getConfig();
            if(!text.contains("max_consecutive_movements_without_progress:")){
                config.set("max_consecutive_movements_without_progress",50);
                List<String> list = new ArrayList<>();
                list.add("to_all: title: 10;80;10;&e&lFIFTY-MOVE RULE!;&eIt's a tie!");
                list.add("to_all: centered_message: &e&m                                               ");
                list.add("to_all: centered_message: ");
                list.add("to_all: centered_message: &e&l%max% MOVEMENTS WITHOUT PROGRESS!");
                list.add("to_all: centered_message: &eIt's a tie! No captures or pawns moves have occurred in");
                list.add("to_all: centered_message: &ethe last &6%max% &emovements!");
                list.add("to_all: centered_message: ");
                list.add("to_all: centered_message: &e&m                                               ");
                config.set("actions.end_game.end_by_movements_without_progress_tie",list);
                list = new ArrayList<>();
                list.add("to_all: console_command: eco give %player% 50");
                config.set("actions.rewards.end_by_movements_without_progress_tie",list);
                configFile.saveConfig();
            }
            if(!text.contains("verifyServerCertificate:")){
                config.set("mysql_database.pool.connectionTimeout",5000);
                config.set("mysql_database.advanced.verifyServerCertificate",false);
                config.set("mysql_database.advanced.useSSL",true);
                config.set("mysql_database.advanced.allowPublicKeyRetrieval",true);
                configFile.saveConfig();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public CommonConfig getConfigFile() {
        return configFile;
    }

    public FileConfiguration getConfig(){
        return configFile.getConfig();
    }

    public ArrayList<PieceStructure> getPieceStructures() {
        return pieceStructures;
    }

    public PieceStructure getPieceStructure(PieceType pieceType){
        for(PieceStructure pieceStructure : pieceStructures){
            if(pieceStructure.getPieceType().equals(pieceType)){
                return pieceStructure;
            }
        }
        return null;
    }

    public boolean isEmptyInventoryToJoin() {
        return emptyInventoryToJoin;
    }

    public GameItemsConfig getGameItemsConfig() {
        return gameItemsConfig;
    }

    public int getArenaStartingCooldown() {
        return arenaStartingCooldown;
    }

    public int getArenaEndingPhaseCooldown() {
        return arenaEndingPhaseCooldown;
    }

    public List<String> getCommandsWhitelist() {
        return commandsWhitelist;
    }

    public PerArenaChatConfig getPerArenaChat() {
        return perArenaChat;
    }

    public int getSelectPieceMaxDistance() {
        return selectPieceMaxDistance;
    }

    public PieceInteractions getPieceInteractions() {
        return pieceInteractions;
    }

    public Arena getArenaDefaultValues() {
        return arenaDefaultValues;
    }

    public GameActions getGameActions() {
        return gameActions;
    }

    public boolean isColoredArmor() {
        return coloredArmor;
    }

    public int getMaxDistanceFromArenaLobby() {
        return maxDistanceFromArenaLobby;
    }

    public int getPlayerDataSave() {
        return playerDataSave;
    }

    public boolean isMySQL() {
        return isMySQL;
    }

    public PiecesHologramsConfig getPiecesHologramsConfig() {
        return piecesHologramsConfig;
    }

    public boolean isUpdateNotify() {
        return updateNotify;
    }

    public GameTimeLimitations getGameTimeLimitations() {
        return gameTimeLimitations;
    }

    public int getMaxConsecutiveMovementsWithoutProgress() {
        return maxConsecutiveMovementsWithoutProgress;
    }
}
