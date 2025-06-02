package mc.ajneb97.utils;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerUtils {
    public static boolean isMineChessAdmin(CommandSender sender){
        return sender.hasPermission("minechess.admin");
    }

    public static boolean isEmptyInventory(Player player){
        ItemStack[] contents = player.getInventory().getContents();
        int usedSlots = 0;
        for (ItemStack content : contents) {
            if (content != null && !content.getType().equals(Material.AIR)) {
                usedSlots++;
            }
        }
        return usedSlots == 0;
    }
}
