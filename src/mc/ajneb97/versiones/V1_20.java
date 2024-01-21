package mc.ajneb97.versiones;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
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
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class V1_20 {
	
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
		
		UUID uuid = UUID.randomUUID();
		try {
			PlayerProfile profile = (PlayerProfile) Bukkit.class.getMethod("createPlayerProfile", UUID.class).invoke(null,uuid);
            PlayerTextures textures = profile.getTextures();
			String decoded = new String(Base64.getDecoder().decode(textura));
            String decodedFormatted = decoded.replaceAll("\\s", "");
            int firstIndex = decodedFormatted.indexOf("\"SKIN\":{\"url\":")+15;
            int lastIndex = decodedFormatted.indexOf("}",firstIndex+1);
            URL url = new URL(decodedFormatted.substring(firstIndex,lastIndex-1));
            textures.setSkin(url);
            profile.setTextures(textures);
            skullBlock.getClass().getMethod("setOwnerProfile", PlayerProfile.class).invoke(skullBlock, profile);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		skullBlock.update();
	}
	
	public ItemStack getCabeza(ItemStack item, String id,String textura) {
		if (textura.isEmpty()) return item;

		SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        UUID uuid = UUID.randomUUID();
		try {
			PlayerProfile profile = (PlayerProfile) Bukkit.class.getMethod("createPlayerProfile", UUID.class).invoke(null,uuid);
			PlayerTextures textures = profile.getTextures();
			String decoded = new String(Base64.getDecoder().decode(textura));
			String decodedFormatted = decoded.replaceAll("\\s", "");
			int firstIndex = decodedFormatted.indexOf("\"SKIN\":{\"url\":")+15;
			int lastIndex = decodedFormatted.indexOf("}",firstIndex+1);
			URL url = new URL(decodedFormatted.substring(firstIndex,lastIndex-1));
			textures.setSkin(url);
			profile.setTextures(textures);
			Method method = skullMeta.getClass().getMethod("setOwnerProfile", PlayerProfile.class);
			method.setAccessible(true);
			method.invoke(skullMeta, profile);
	        item.setItemMeta(skullMeta);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        return item;
	}
}
