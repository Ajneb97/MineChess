package mc.ajneb97.tasks;

import mc.ajneb97.manager.ArenaManager;
import mc.ajneb97.MineChess;
import mc.ajneb97.libs.fastboard.FastBoard;
import mc.ajneb97.manager.MessagesManager;
import mc.ajneb97.model.Arena;
import mc.ajneb97.model.game.GamePlayer;
import mc.ajneb97.model.game.GameStatus;
import mc.ajneb97.utils.MiniMessageUtils;
import mc.ajneb97.utils.OtherUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ScoreboardManager {
    private final Map<UUID, FastBoard> boards;
    private MineChess plugin;
    private boolean stop;

    public ScoreboardManager(MineChess plugin){
        this.plugin = plugin;
        this.boards = new HashMap<>();
        this.stop = false;
    }

    public void start(){
        new BukkitRunnable(){
            @Override
            public void run() {
                if(stop){
                    this.cancel();
                }else{
                    execute();
                }
            }
        }.runTaskTimerAsynchronously(plugin,0L,20L);
    }

    public void stop(){
        this.stop = true;
    }

    public void execute(){
        ArenaManager arenaManager = plugin.getArenaManager();
        ArrayList<Arena> arenas = arenaManager.getArenas();
        boolean isPlaceholderAPI = plugin.getDependencyManager().isPlaceholderAPI();

        FileConfiguration messagesConfig = plugin.getMessagesConfig();

        String scoreboardTitle1 = messagesConfig.getString("scoreboards.arenaTimeGameMode.title");
        List<String> scoreboardBody1 = messagesConfig.getStringList("scoreboards.arenaTimeGameMode.body");
        String statusWaiting = messagesConfig.getString("statusWaiting");
        String statusStarting = messagesConfig.getString("statusStarting");
        String statusIngame = messagesConfig.getString("statusIngame");
        String statusFinishing = messagesConfig.getString("statusFinishing");

        for(Arena arena : arenas){
            //Status
            String status = null;
            int time = arena.getCooldownTime();
            switch (arena.getStatus()) {
                case WAITING -> status = statusWaiting;
                case STARTING -> status = statusStarting.replace("%time%", OtherUtils.getTimeFormat1(time));
                case PLAYING -> status = statusIngame.replace("%time%", OtherUtils.getTimeFormat1(time));
                case ENDING -> status = statusFinishing.replace("%time%", OtherUtils.getTimeFormat1(time));
            }

            updateScoreboard(arena,scoreboardBody1,status,isPlaceholderAPI,scoreboardTitle1);
        }
    }

    private void updateScoreboard(Arena arena, List<String> gameScoreboardBody, String status, boolean isPlaceholderAPI, String gameScoreboardTitle) {
        ArrayList<GamePlayer> players = arena.getGamePlayers(true);
        int whitePoints = arena.getWhitePoints();
        int blackPoints = arena.getBlackPoints();

        String whiteTime = getPlayerTime(arena,arena.getPlayerWhite());
        String blackTime = getPlayerTime(arena,arena.getPlayerBlack());

        boolean isMiniMessage = plugin.getConfigsManager().getMainConfigManager().isUseMiniMessage();

        for (GamePlayer gamePlayer : players) {
            Player player = gamePlayer.getPlayer();
            if(player == null){
                continue;
            }

            FastBoard board = boards.get(player.getUniqueId());
            if (board == null) {
                board = new FastBoard(player);
                boards.put(player.getUniqueId(), board);
            }

            List<String> replacedScoreboardBody = new ArrayList<>();

            for (String line : gameScoreboardBody) {
                line = line.replace("%white_points%", whitePoints + "")
                        .replace("%black_points%", blackPoints + "").replace("%status%", status)
                        .replace("%white_time%",whiteTime).replace("%black_time%",blackTime);
                if (isPlaceholderAPI) {
                    line = PlaceholderAPI.setPlaceholders(player, line);
                }

                if(isMiniMessage){
                    line = MiniMessageUtils.miniMessageToLegacy(line);
                    replacedScoreboardBody.add(MessagesManager.getLegacyColoredMessage(line));
                }else{
                    replacedScoreboardBody.add(MessagesManager.getLegacyColoredMessage(line));
                }
            }

            if (!board.isDeleted()) {
                try {
                    if(isMiniMessage){
                        gameScoreboardTitle = MiniMessageUtils.miniMessageToLegacy(gameScoreboardTitle);
                        board.updateTitle(MessagesManager.getLegacyColoredMessage(gameScoreboardTitle));
                    }else{
                        board.updateTitle(MessagesManager.getLegacyColoredMessage(gameScoreboardTitle));
                    }

                    board.updateLines(replacedScoreboardBody);
                } catch (Exception ignored) {

                }
            }
        }
    }

    private String getPlayerTime(Arena arena,GamePlayer gamePlayer){
        int time;
        if(arena.getPlayerWhite() != null){
            if(arena.getStatus().equals(GameStatus.PLAYING)){
                time = gamePlayer.getTurnTime();
            }else{
                time = arena.getMaxTime();
            }
        }else{
            time = arena.getMaxTime();
        }
        return OtherUtils.getTimeFormat1(time);
    }

    public void removeScoreboard(Player player){
        FastBoard board = this.boards.remove(player.getUniqueId());
        if(board != null){
            board.delete();
        }
    }
}
