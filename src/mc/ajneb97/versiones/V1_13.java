package mc.ajneb97.versiones;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.SkullType;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class V1_13 {

	public void generarParticula(String particle, Location loc, float xOffset, float yOffset, float zOffset, float speed, int count, Player player){
		player.spawnParticle(Particle.valueOf(particle),loc,count,xOffset,yOffset,zOffset,speed);
	  }
	
	@SuppressWarnings("deprecation")
	public void setSkullBlock(Location locBloque,String id,String textura,int rot) {
		net.minecraft.server.v1_13_R1.BlockPosition locDestino = new net.minecraft.server.v1_13_R1.BlockPosition(locBloque.getX(),locBloque.getY(),locBloque.getZ());	
		locBloque.getBlock().setType(Material.valueOf("PLAYER_HEAD"));
		
		Skull skullBlock = (Skull) locBloque.getBlock().getState();
		if(rot == 8) {
			skullBlock.setRotation(BlockFace.NORTH);
		}
		skullBlock.update();
		
		
		net.minecraft.server.v1_13_R1.TileEntity te = ((org.bukkit.craftbukkit.v1_13_R1.CraftWorld)locBloque.getWorld()).getHandle().getTileEntity(locDestino);
		net.minecraft.server.v1_13_R1.NBTTagCompound compound = new net.minecraft.server.v1_13_R1.NBTTagCompound();
		
		te.save(compound);
		net.minecraft.server.v1_13_R1.NBTTagCompound compoundSeleccionada = new net.minecraft.server.v1_13_R1.NBTTagCompound();
		net.minecraft.server.v1_13_R1.NBTTagCompound compoundOwner = new net.minecraft.server.v1_13_R1.NBTTagCompound();
		net.minecraft.server.v1_13_R1.NBTTagCompound compoundProperties = new net.minecraft.server.v1_13_R1.NBTTagCompound();
		net.minecraft.server.v1_13_R1.NBTTagList compoundTextures = new net.minecraft.server.v1_13_R1.NBTTagList();
		net.minecraft.server.v1_13_R1.NBTTagCompound compoundValue = new net.minecraft.server.v1_13_R1.NBTTagCompound();
		compoundValue.setString("Value", textura);
		compoundTextures.add(compoundValue);
		compoundProperties.set("textures", compoundTextures);
		compoundOwner.set("Properties", compoundProperties);
		compoundOwner.setString("Id", id);
		compoundSeleccionada.setString("id", "Skull");
		compoundSeleccionada.set("Owner", compoundOwner);
		compoundSeleccionada.setInt("x", locDestino.getX());
		compoundSeleccionada.setInt("y", locDestino.getY());
		compoundSeleccionada.setInt("z", locDestino.getZ());
		compoundSeleccionada.setByte("SkullType", (byte) 3);
		te.load(compoundSeleccionada);
		te.update();
	}
	
	public ItemStack getCabeza(ItemStack item, String id,String textura) {
		net.minecraft.server.v1_13_R1.ItemStack cabeza = org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack.asNMSCopy(item);		
		net.minecraft.server.v1_13_R1.NBTTagCompound tag = cabeza.hasTag() ? cabeza.getTag() : new net.minecraft.server.v1_13_R1.NBTTagCompound();
		net.minecraft.server.v1_13_R1.NBTTagCompound skullOwnerCompound = new net.minecraft.server.v1_13_R1.NBTTagCompound();
		net.minecraft.server.v1_13_R1.NBTTagCompound propiedades = new net.minecraft.server.v1_13_R1.NBTTagCompound();
		
		
		net.minecraft.server.v1_13_R1.NBTTagList texturas = new net.minecraft.server.v1_13_R1.NBTTagList();
		net.minecraft.server.v1_13_R1.NBTTagCompound texturasObjeto = new net.minecraft.server.v1_13_R1.NBTTagCompound();
		texturasObjeto.setString("Value", textura);
		texturas.add(texturasObjeto);
		propiedades.set("textures", texturas);
		skullOwnerCompound.set("Properties", propiedades);
		
		skullOwnerCompound.setString("Id", id);
		
		tag.set("SkullOwner", skullOwnerCompound);
		cabeza.setTag(tag);
		
		
		return org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack.asBukkitCopy(cabeza);
	}
}
