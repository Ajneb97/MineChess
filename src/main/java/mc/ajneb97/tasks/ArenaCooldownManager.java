package mc.ajneb97.tasks;

import mc.ajneb97.MineChess;
import mc.ajneb97.manager.MessagesManager;
import mc.ajneb97.model.Arena;
import mc.ajneb97.model.game.GameEndsReason;
import mc.ajneb97.model.game.GameLeaveReason;
import mc.ajneb97.model.game.GamePlayer;
import mc.ajneb97.model.game.GameStatus;
import mc.ajneb97.manager.GamePlayerManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class ArenaCooldownManager {

    private MineChess plugin;
    private Arena arena;
    private boolean stop;
    private int time;
    public ArenaCooldownManager(MineChess plugin, Arena arena, int time){
        this.plugin = plugin;
        this.arena = arena;
        this.stop = false;
        this.time = time;
    }

    public boolean isStop() {
        return stop;
    }

    public int getTime() {
        return time;
    }

    public void stop(){
        this.stop = true;
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
        }.runTaskTimer(plugin,0L,20L);
    }

    public void execute(){
        switch (arena.getStatus()) {
            case WAITING, DISABLED -> {
            }
            case STARTING -> executeStarting();
            case PLAYING -> executePlaying();
            case ENDING -> executeEnding();
        }
        if(time > 0){
            time--;
        }
    }

    private void executeStarting(){
        if(time > 0 && time <= 5){
            MessagesManager msgManager = plugin.getMessagesManager();
            FileConfiguration messagesConfig = plugin.getMessagesConfig();
            for(GamePlayer p : arena.getGamePlayers()){
                msgManager.sendMessage(p.getPlayer(),messagesConfig.getString("gameStarting")
                        .replace("%time%",time+""),true);
            }
        }else if(time == 0){
            plugin.getArenaManager().startPlayingStage(arena);
        }
    }

    private void executePlaying() {
        GamePlayerManager gamePlayerManager = plugin.getArenaManager().getGamePlayerManager();
        if (time == 0) {
            plugin.getArenaManager().getGameEndManager().startEndingStage(arena, GameEndsReason.TIME);
            return;
        } else {
            GamePlayer playerTurn = arena.getPlayerTurn();
            if (playerTurn.getTurnTime() <= 0) {
                // Automatic move
                plugin.getArenaManager().automaticMove(arena);
            } else {
                arena.getPlayerTurn().reduceTurnTime();
            }
        }

        gamePlayerManager.showActionbar(arena);
        gamePlayerManager.checkMustTeleportArenaLobby(arena);
    }

    private void executeEnding(){
        if(time == 0){
            plugin.getArenaManager().getGameEndManager().endGame(arena, GameLeaveReason.END_GAME);
        }else{
            plugin.getArenaManager().winnersFireworks(arena);
        }
    }
}
