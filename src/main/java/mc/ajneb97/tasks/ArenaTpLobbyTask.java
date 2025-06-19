package mc.ajneb97.tasks;

import mc.ajneb97.MineChess;
import mc.ajneb97.manager.ArenaManager;
import mc.ajneb97.manager.GamePlayerManager;
import mc.ajneb97.model.Arena;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ArenaTpLobbyTask {
    private MineChess plugin;

    public ArenaTpLobbyTask(MineChess plugin){
        this.plugin = plugin;
    }

    public void start(){
        new BukkitRunnable(){
            @Override
            public void run() {
                execute();
            }
        }.runTaskTimer(plugin,0L,20L);
    }

    private void execute(){
        ArenaManager arenaManager = plugin.getArenaManager();
        ArrayList<Arena> arenas = arenaManager.getArenas();
        GamePlayerManager gamePlayerManager = arenaManager.getGamePlayerManager();
        for(Arena arena : arenas){
            gamePlayerManager.checkMustTeleportArenaLobby(arena);
        }
    }
}
