package mc.ajneb97.utils;

import mc.ajneb97.manager.MessagesManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class InventoryItem {

	private Inventory inventory;
	private int slot;
	private ItemStack item;
	private ItemMeta meta;
	
	public InventoryItem(Inventory inventory, int slot, Material material) {
		this.inventory = inventory;
		this.item = new ItemStack(material);
		this.meta = item.getItemMeta();
		this.slot = slot;
	}
	
	@SuppressWarnings("deprecation")
	public InventoryItem dataValue(short datavalue) {
		item.setDurability(datavalue);
		return this;
	}
	
	public InventoryItem amount(int amount) {
		item.setAmount(amount);
		return this;
	}
	
	public InventoryItem name(String name) {
		meta.setDisplayName(MessagesManager.getColoredMessage(name));
		return this;
	}
	
	public InventoryItem lore(List<String> lore) {
		for(int i=0;i<lore.size();i++) {
			lore.set(i, MessagesManager.getColoredMessage(lore.get(i)));
		}
		meta.setLore(lore);
		return this;
	}
	
	public InventoryItem enchanted(boolean enchanted) {
		if(enchanted) {
			meta.addEnchant(Enchantment.FIRE_ASPECT, 1, true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		return this;
	}
	
	public void ready() {
		item.setItemMeta(meta);
		inventory.setItem(slot, item);
	}
}
