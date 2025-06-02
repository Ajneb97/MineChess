package mc.ajneb97.libs.actionbar;

import mc.ajneb97.MineChess;
import mc.ajneb97.manager.MessagesManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionBarAPI
{


    public static void sendActionBar(Player player, String message) {
        if(player == null){
            return;
        }
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(MessagesManager.getColoredMessage(message)));
    }
  
  public static void sendActionBar(final Player player, final String message, int duration, MineChess plugin) {
	  sendActionBar(player, message);
	  
      if (duration > 0) {
          // Sends empty message at the end of the duration. Allows messages shorter than 3 seconds, ensures precision.
          new BukkitRunnable() {
              @Override
              public void run() {
            	  sendActionBar(player, "");
              }
          }.runTaskLater(plugin, duration + 1);
      }

      // Re-sends the messages every 3 seconds so it doesn't go away from the player's screen.
      while (duration > 40) {
          duration -= 40;
          new BukkitRunnable() {
              @Override
              public void run() {
                  sendActionBar(player, message);
              }
          }.runTaskLater(plugin, (long) duration);
      }
  }

}
