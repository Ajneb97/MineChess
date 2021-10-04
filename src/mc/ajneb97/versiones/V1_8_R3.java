package mc.ajneb97.versiones;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Skull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class V1_8_R3 {

	/*BARRIER
	BLOCK_CRACK
	BLOCK_DUST
	CLOUD
	CRIT
	CRIT_MAGIC
	DAMAGE_INDICATOR
	DRAGON_BREATH
	DRIP_LAVA
	DRIP_WATER
	ENCHANTMENT_TABLE
	END_ROD
	EXPLOSION_HUGE
	EXPLOSION_LARGE
	EXPLOSION_NORMAL
	FIREWORKS_SPARK
	FLAME
	FOOTSTEP
	HEART
	ITEM_CRACK
	ITEM_TAKE
	LAVA
	MOB_APPEARANCE
	NOTE
	PORTAL
	REDSTONE
	SLIME
	SMOKE_LARGE
	SMOKE_NORMAL
	SNOW_SHOVEL
	SNOWBALL
	SPELL
	SPELL_INSTANT
	SPELL_MOB
	SPELL_MOB_AMBIENT
	SPELL_WITCH
	SUSPENDED
	SUSPENDED_DEPTH
	SWEEP_ATTACK
	TOWN_AURA
	VILLAGER_ANGRY
	VILLAGER_HAPPY
	WATER_BUBBLE
	WATER_DROP
	WATER_SPLASH
	WATER_WAKE */

	public void generarParticula(String particle, Location loc, float xOffset, float yOffset, float zOffset, float speed, int count, Player player){
		net.minecraft.server.v1_8_R3.EnumParticle enumParticle = net.minecraft.server.v1_8_R3.EnumParticle.valueOf(particle);
		float x = (float)loc.getX();
	    float y = (float)loc.getY();
	    float z = (float)loc.getZ();
	    
	    net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles packet = new net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles(enumParticle, false, x, y, z, xOffset, 
	      yOffset, zOffset, speed, count, null);
	    
	    ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
	  }
	
	@SuppressWarnings("deprecation")
	public void setSkullBlock(Location locBloque,String id,String textura,int rot) {
		net.minecraft.server.v1_8_R3.BlockPosition locDestino = new net.minecraft.server.v1_8_R3.BlockPosition(locBloque.getX(),locBloque.getY(),locBloque.getZ());	
		locBloque.getBlock().setTypeIdAndData(144, (byte) 1, true);
		
		Skull skullBlock = (Skull) locBloque.getBlock().getState();
		skullBlock.setSkullType(SkullType.PLAYER);
		skullBlock.update();
		
		
		net.minecraft.server.v1_8_R3.TileEntity te = ((org.bukkit.craftbukkit.v1_8_R3.CraftWorld)locBloque.getWorld()).getHandle().getTileEntity(locDestino);
		net.minecraft.server.v1_8_R3.NBTTagCompound compound = new net.minecraft.server.v1_8_R3.NBTTagCompound();
		
		te.b(compound);
		net.minecraft.server.v1_8_R3.NBTTagCompound compoundSeleccionada = new net.minecraft.server.v1_8_R3.NBTTagCompound();
		net.minecraft.server.v1_8_R3.NBTTagCompound compoundOwner = new net.minecraft.server.v1_8_R3.NBTTagCompound();
		net.minecraft.server.v1_8_R3.NBTTagCompound compoundProperties = new net.minecraft.server.v1_8_R3.NBTTagCompound();
		net.minecraft.server.v1_8_R3.NBTTagList compoundTextures = new net.minecraft.server.v1_8_R3.NBTTagList();
		net.minecraft.server.v1_8_R3.NBTTagCompound compoundValue = new net.minecraft.server.v1_8_R3.NBTTagCompound();
		compoundValue.setString("Value", textura);
		compoundTextures.add(compoundValue);
		compoundProperties.set("textures", compoundTextures);
		compoundOwner.set("Properties", compoundProperties);
		compoundOwner.setString("Id", id);
		compoundSeleccionada.setString("id", "Skull");
		compoundSeleccionada.set("Owner", compoundOwner);
		compoundSeleccionada.setByte("Rot", (byte)rot);
		compoundSeleccionada.setInt("x", locDestino.getX());
		compoundSeleccionada.setInt("y", locDestino.getY());
		compoundSeleccionada.setInt("z", locDestino.getZ());
		compoundSeleccionada.setByte("SkullType", (byte) 3);
		te.a(compoundSeleccionada);
		te.update();
	}
	
	public ItemStack getCabeza(ItemStack item, String id,String textura) {
		net.minecraft.server.v1_8_R3.ItemStack cabeza = org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack.asNMSCopy(item);		
		net.minecraft.server.v1_8_R3.NBTTagCompound tag = cabeza.hasTag() ? cabeza.getTag() : new net.minecraft.server.v1_8_R3.NBTTagCompound();
		net.minecraft.server.v1_8_R3.NBTTagCompound skullOwnerCompound = new net.minecraft.server.v1_8_R3.NBTTagCompound();
		net.minecraft.server.v1_8_R3.NBTTagCompound propiedades = new net.minecraft.server.v1_8_R3.NBTTagCompound();
		
		
		net.minecraft.server.v1_8_R3.NBTTagList texturas = new net.minecraft.server.v1_8_R3.NBTTagList();
		net.minecraft.server.v1_8_R3.NBTTagCompound texturasObjeto = new net.minecraft.server.v1_8_R3.NBTTagCompound();
		texturasObjeto.setString("Value", textura);
		texturas.add(texturasObjeto);
		propiedades.set("textures", texturas);
		skullOwnerCompound.set("Properties", propiedades);
		
		skullOwnerCompound.setString("Id", id);
		
		tag.set("SkullOwner", skullOwnerCompound);
		cabeza.setTag(tag);
		
		
		return org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack.asBukkitCopy(cabeza);
	}
}
