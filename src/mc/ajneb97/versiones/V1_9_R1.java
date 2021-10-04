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

public class V1_9_R1 {
	
	public void generarParticula(String particle, Location loc, float xOffset, float yOffset, float zOffset, float speed, int count, Player player){
		net.minecraft.server.v1_9_R1.EnumParticle enumParticle = net.minecraft.server.v1_9_R1.EnumParticle.valueOf(particle);
		float x = (float)loc.getX();
	    float y = (float)loc.getY();
	    float z = (float)loc.getZ();
	    
	    net.minecraft.server.v1_9_R1.PacketPlayOutWorldParticles packet = new net.minecraft.server.v1_9_R1.PacketPlayOutWorldParticles(enumParticle, false, x, y, z, xOffset, 
	      yOffset, zOffset, speed, count, null);
	    
	    ((org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
	  }
	
	@SuppressWarnings("deprecation")
	public void setSkullBlock(Location locBloque,String id,String textura,int rot) {
		net.minecraft.server.v1_9_R1.BlockPosition locDestino = new net.minecraft.server.v1_9_R1.BlockPosition(locBloque.getX(),locBloque.getY(),locBloque.getZ());	
		locBloque.getBlock().setTypeIdAndData(144, (byte) 1, true);
		
		Skull skullBlock = (Skull) locBloque.getBlock().getState();
		skullBlock.setSkullType(SkullType.PLAYER);
		skullBlock.update();
		
		
		net.minecraft.server.v1_9_R1.TileEntity te = ((org.bukkit.craftbukkit.v1_9_R1.CraftWorld)locBloque.getWorld()).getHandle().getTileEntity(locDestino);
		net.minecraft.server.v1_9_R1.NBTTagCompound compound = new net.minecraft.server.v1_9_R1.NBTTagCompound();
		
		te.save(compound);
		net.minecraft.server.v1_9_R1.NBTTagCompound compoundSeleccionada = new net.minecraft.server.v1_9_R1.NBTTagCompound();
		net.minecraft.server.v1_9_R1.NBTTagCompound compoundOwner = new net.minecraft.server.v1_9_R1.NBTTagCompound();
		net.minecraft.server.v1_9_R1.NBTTagCompound compoundProperties = new net.minecraft.server.v1_9_R1.NBTTagCompound();
		net.minecraft.server.v1_9_R1.NBTTagList compoundTextures = new net.minecraft.server.v1_9_R1.NBTTagList();
		net.minecraft.server.v1_9_R1.NBTTagCompound compoundValue = new net.minecraft.server.v1_9_R1.NBTTagCompound();
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
		net.minecraft.server.v1_9_R1.ItemStack cabeza = org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack.asNMSCopy(item);		
		net.minecraft.server.v1_9_R1.NBTTagCompound tag = cabeza.hasTag() ? cabeza.getTag() : new net.minecraft.server.v1_9_R1.NBTTagCompound();
		net.minecraft.server.v1_9_R1.NBTTagCompound skullOwnerCompound = new net.minecraft.server.v1_9_R1.NBTTagCompound();
		net.minecraft.server.v1_9_R1.NBTTagCompound propiedades = new net.minecraft.server.v1_9_R1.NBTTagCompound();
		
		
		net.minecraft.server.v1_9_R1.NBTTagList texturas = new net.minecraft.server.v1_9_R1.NBTTagList();
		net.minecraft.server.v1_9_R1.NBTTagCompound texturasObjeto = new net.minecraft.server.v1_9_R1.NBTTagCompound();
		texturasObjeto.setString("Value", textura);
		texturas.add(texturasObjeto);
		propiedades.set("textures", texturas);
		skullOwnerCompound.set("Properties", propiedades);
		
		skullOwnerCompound.setString("Id", id);
		
		tag.set("SkullOwner", skullOwnerCompound);
		cabeza.setTag(tag);
		
		
		return org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack.asBukkitCopy(cabeza);
	}
}
