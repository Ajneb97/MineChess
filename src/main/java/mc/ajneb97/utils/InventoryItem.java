package mc.ajneb97.utils;

import mc.ajneb97.api.MineChessAPI;
import mc.ajneb97.manager.MessagesManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class InventoryItem {

	private Inventory inventory;
	private int slot;
	private ItemStack item;
	private ItemMeta meta;
	private boolean useMiniMessage;
	
	public InventoryItem(Inventory inventory, int slot, Material material) {
		this.inventory = inventory;
		this.item = new ItemStack(material);
		this.meta = item.getItemMeta();
		this.slot = slot;
		this.useMiniMessage = MineChessAPI.getPlugin().getConfigsManager().getMainConfigManager().isUseMiniMessage();
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
		if(useMiniMessage){
			meta.displayName(MiniMessage.miniMessage().deserialize(name).decoration(TextDecoration.ITALIC, false));
		}else{
			meta.setDisplayName(MessagesManager.getLegacyColoredMessage(name));
		}
		return this;
	}
	
	public InventoryItem lore(List<String> lore) {
		List<String> loreCopy = new ArrayList<>(lore);
		if(useMiniMessage){
			List<Component> loreComponent = new ArrayList<>();
			for(int i=0;i<loreCopy.size();i++) {
				loreComponent.add(MiniMessage.miniMessage().deserialize(loreCopy.get(i)).decoration(TextDecoration.ITALIC, false));
			}
			meta.lore(loreComponent);
		}else{
			for(int i=0;i<loreCopy.size();i++) {
				loreCopy.set(i, MessagesManager.getLegacyColoredMessage(loreCopy.get(i)));
			}
			meta.setLore(loreCopy);
		}
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
