package mc.ajneb97.utils;

import mc.ajneb97.model.PlayerColor;
import mc.ajneb97.model.chess.PieceType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class GameUtils {
    public static String getPieceNameFromConfig(PieceType pieceType, FileConfiguration messagesConfig){
        String name = pieceType.name();
        String capitalized = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        return messagesConfig.getString("piece"+capitalized);
    }

    public static String getColorFromConfig(PlayerColor playerColor, FileConfiguration messagesConfig){
        String name = playerColor.name();
        String capitalized = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        return messagesConfig.getString("piece"+capitalized);
    }

    public static String getRealCoords(int x,int z){
        String[] letters = {"A","B","C","D","E","F","G","H"};
        return letters[x]+","+(z+1);
    }

    public static void setColoredArmor(Player player, PlayerColor playerColor){
        EntityEquipment equipment = player.getEquipment();
        Color color = playerColor.equals(PlayerColor.WHITE) ? Color.WHITE : Color.BLACK;

        ItemStack item = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(color);
        item.setItemMeta(meta);
        equipment.setHelmet(item);

        item = new ItemStack(Material.LEATHER_CHESTPLATE);
        meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(color);
        item.setItemMeta(meta);
        equipment.setChestplate(item);

        item = new ItemStack(Material.LEATHER_LEGGINGS);
        meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(color);
        item.setItemMeta(meta);
        equipment.setLeggings(item);

        item = new ItemStack(Material.LEATHER_BOOTS);
        meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(color);
        item.setItemMeta(meta);
        equipment.setBoots(item);
    }
}
