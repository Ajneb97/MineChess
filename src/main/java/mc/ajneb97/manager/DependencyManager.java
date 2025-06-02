package mc.ajneb97.manager;

import mc.ajneb97.MineChess;
import org.bukkit.Bukkit;

public class DependencyManager {

    private MineChess plugin;

    private boolean isPlaceholderAPI;
    private boolean isPaper;

    public DependencyManager(MineChess plugin){
        this.plugin = plugin;

        if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            isPlaceholderAPI = true;
        }
        try{
            Class.forName("com.destroystokyo.paper.ParticleBuilder");
            isPaper = true;
        }catch(Exception e){

        }
    }

    public boolean isPlaceholderAPI() {
        return isPlaceholderAPI;
    }

    public boolean isPaper() {
        return isPaper;
    }
}
