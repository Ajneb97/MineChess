package mc.ajneb97.listener;

import mc.ajneb97.MineChess;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSignOpenEvent;

// 1.20.1+
public class OtherListenerNew implements Listener {

    private MineChess plugin;
    public OtherListenerNew(MineChess plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignModify(PlayerSignOpenEvent event){
        if(event.isCancelled()){
            return;
        }
        plugin.getSignManager().openSign(event);
    }
}
