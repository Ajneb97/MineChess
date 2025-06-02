package mc.ajneb97.config;

import mc.ajneb97.MineChess;
import mc.ajneb97.config.model.CommonConfig;
import mc.ajneb97.manager.CommonItemManager;
import mc.ajneb97.manager.inventory.CustomItemsInventory;
import mc.ajneb97.manager.inventory.InventoryManager;
import mc.ajneb97.model.inventory.CommonInventory;
import mc.ajneb97.model.inventory.CommonInventoryItem;
import mc.ajneb97.model.items.CommonItem;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class InventoryConfigManager {

    private MineChess plugin;
    private CommonConfig configFile;


    public InventoryConfigManager(MineChess plugin){
        this.plugin = plugin;
        this.configFile = new CommonConfig("inventories.yml",plugin,null, false);
        this.configFile.registerConfig();
        checkUpdates();
    }

    public void configure(){
        FileConfiguration config = configFile.getConfig();
        InventoryManager inventoryManager = plugin.getInventoryManager();

        ArrayList<CommonInventory> inventories = new ArrayList<>();
        CommonItemManager commonItemManager = plugin.getCommonItemManager();
        if(config.contains("inventories")) {
            for(String key : config.getConfigurationSection("inventories").getKeys(false)) {
                int slots = config.getInt("inventories."+key+".slots");
                String title = config.getString("inventories."+key+".title");

                List<CommonInventoryItem> items = new ArrayList<>();
                for(String slotString : config.getConfigurationSection("inventories."+key).getKeys(false)) {
                    if(!slotString.equals("slots") && !slotString.equals("title")) {
                        String path = "inventories."+key+"."+slotString;
                        CommonItem item = null;
                        if(config.contains(path+".item")){
                            item = commonItemManager.getCommonItemFromConfig(config, path+".item");
                        }

                        String openInventory = config.contains(path+".open_inventory") ?
                                config.getString(path+".open_inventory") : null;

                        List<String> clickActions = config.contains(path+".click_actions") ?
                                config.getStringList(path+".click_actions") : null;

                        String type = config.contains(path+".type") ?
                                config.getString(path+".type") : null;

                        CommonInventoryItem inventoryItem = new CommonInventoryItem(slotString,item,openInventory,clickActions,type);
                        items.add(inventoryItem);
                    }
                }

                CommonInventory inv = new CommonInventory(key,slots,title,items);
                inventories.add(inv);
            }
        }
        inventoryManager.setInventories(inventories);

        CustomItemsInventory customItemsInventory = new CustomItemsInventory(
                commonItemManager.getCommonItemFromConfig(config, "custom_items.piece_rook.black"),
                commonItemManager.getCommonItemFromConfig(config, "custom_items.piece_rook.white"),
                commonItemManager.getCommonItemFromConfig(config, "custom_items.piece_bishop.black"),
                commonItemManager.getCommonItemFromConfig(config, "custom_items.piece_bishop.white"),
                commonItemManager.getCommonItemFromConfig(config, "custom_items.piece_knight.black"),
                commonItemManager.getCommonItemFromConfig(config, "custom_items.piece_knight.white"),
                commonItemManager.getCommonItemFromConfig(config, "custom_items.piece_queen.black"),
                commonItemManager.getCommonItemFromConfig(config, "custom_items.piece_queen.white")
        );
        inventoryManager.setCustomItems(customItemsInventory);
    }

    public boolean reloadConfig(){
        if(!configFile.reloadConfig()){
            return false;
        }
        configure();
        return true;
    }

    public void checkUpdates(){
        Path pathConfig = Paths.get(configFile.getRoute());
        try{
            String text = new String(Files.readAllBytes(pathConfig));
            FileConfiguration config = configFile.getConfig();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig(){
        return configFile.getConfig();
    }
}
