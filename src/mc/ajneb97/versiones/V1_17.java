package mc.ajneb97.versiones;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.SkullType;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class V1_17 {
	
	public void generarParticula(String particle, Location loc, float xOffset, float yOffset, float zOffset, float speed, int count, Player player){
		player.spawnParticle(Particle.valueOf(particle),loc,count,xOffset,yOffset,zOffset,speed);
	  }
	
	
	@SuppressWarnings("deprecation")
	public void setSkullBlock(Location locBloque,String id,String textura,int rot) {	
		locBloque.getBlock().setType(Material.valueOf("PLAYER_HEAD"));
		Skull skullBlock = (Skull) locBloque.getBlock().getState();
		//skullBlock.setSkullType(SkullType.PLAYER);
		if(rot == 8) {
			skullBlock.setRotation(BlockFace.NORTH);
		}
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", textura));

        try {
            Field profileField = skullBlock.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullBlock, profile);
        }catch (NoSuchFieldException | IllegalAccessException e) { e.printStackTrace(); }
		
		skullBlock.update();
	}
	
	public ItemStack getCabeza(ItemStack item, String id,String textura) {
		if (textura.isEmpty()) return item;

        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", textura));

        try {
            Method mtd = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
            mtd.setAccessible(true);
            mtd.invoke(skullMeta, profile);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }

        item.setItemMeta(skullMeta);
        return item;
	}
}
