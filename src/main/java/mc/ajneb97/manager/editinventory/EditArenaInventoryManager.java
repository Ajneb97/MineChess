package mc.ajneb97.manager.editinventory;

import mc.ajneb97.MineChess;
import mc.ajneb97.config.MainConfigManager;
import mc.ajneb97.manager.BoardManager;
import mc.ajneb97.model.ArenaEndTimeMode;
import mc.ajneb97.model.editinventory.EditInventoryPlayer;
import mc.ajneb97.utils.InventoryItem;
import mc.ajneb97.manager.MessagesManager;
import mc.ajneb97.model.Arena;
import mc.ajneb97.utils.MiniMessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EditArenaInventoryManager {

    private MineChess plugin;

    public EditArenaInventoryManager(MineChess plugin) {
        this.plugin = plugin;
    }

    public void openMainInventory(EditInventoryPlayer editInventoryPlayer) {
        editInventoryPlayer.setCurrentInventory(EditInventoryPlayer.InventoryType.ARENA);
        Arena arena = editInventoryPlayer.getArena();

        FileConfiguration messagesConfig = plugin.getMessagesConfig();
        MainConfigManager mainConfigManager = plugin.getConfigsManager().getMainConfigManager();
        Inventory inv;
        if(mainConfigManager.isUseMiniMessage()){
            inv = MiniMessageUtils.createInventory(54,messagesConfig.getString("inventoryEditingArenaTitle"));
        }else{
            inv = Bukkit.createInventory(null, 54, MessagesManager.getLegacyColoredMessage(messagesConfig.getString("inventoryEditingArenaTitle")));
        }

        // Decoration
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        for(int i=45;i<54;i++){
            inv.setItem(i, item);
        }

        // Locations
        List<String> lore = messagesConfig.getStringList("inventoryEditingArenaLocationLobbyItemLore");
        new InventoryItem(inv,10, Material.COMPASS).name(messagesConfig.getString("inventoryEditingArenaLocationLobbyItemName"))
                .lore(setLoreLocation(lore,messagesConfig,arena.getLobbyLocation())).ready();

        String colorWhite = messagesConfig.getString("pieceWhite");
        String colorBlack = messagesConfig.getString("pieceBlack");
        lore = messagesConfig.getStringList("inventoryEditingArenaLocationSpawnPlayerItemLore");
        for(int i=0;i<lore.size();i++){
            lore.set(i,lore.get(i).replace("%color%",colorWhite));
        }
        new InventoryItem(inv,19, Material.COMPASS).name(messagesConfig.getString("inventoryEditingArenaLocationSpawnPlayerItemName")
                        .replace("%color%",colorWhite))
                .lore(setLoreLocation(lore,messagesConfig,arena.getSpawnPlayer1Location())).ready();

        lore = messagesConfig.getStringList("inventoryEditingArenaLocationSpawnPlayerItemLore");
        for(int i=0;i<lore.size();i++){
            lore.set(i,lore.get(i).replace("%color%",colorBlack));
        }
        new InventoryItem(inv,28, Material.COMPASS).name(messagesConfig.getString("inventoryEditingArenaLocationSpawnPlayerItemName")
                        .replace("%color%",colorBlack))
                .lore(setLoreLocation(lore,messagesConfig,arena.getSpawnPlayer2Location())).ready();

        new InventoryItem(inv,37, Material.REDSTONE_BLOCK).name(messagesConfig.getString("inventoryEditingArenaLocationResetItemName"))
                .lore(messagesConfig.getStringList("inventoryEditingArenaLocationResetItemLore")).ready();


        // Gamemode
        lore = messagesConfig.getStringList("inventoryEditingArenaGameModeItemLore");
        replaceValueVariable(lore,"ARENA_TIME");
        new InventoryItem(inv,12, Material.REPEATER).name(messagesConfig.getString("inventoryEditingArenaGameModeItemName"))
                .lore(lore).ready();

        // Piece Type
        lore = messagesConfig.getStringList("inventoryEditingArenaPiecesTypeItemLore");
        replaceValueVariable(lore,"BLOCK");
        new InventoryItem(inv,30, Material.QUARTZ_BLOCK).name(messagesConfig.getString("inventoryEditingArenaPiecesTypeItemName"))
                .lore(lore).ready();


        // Max Time
        lore = messagesConfig.getStringList("inventoryEditingMaxTimeArenaTimeItemLore");
        replaceValueVariable(lore,arena.getMaxTime()+"");
        new InventoryItem(inv,23, Material.CLOCK).name(messagesConfig.getString("inventoryEditingMaxTimeArenaTimeItemName"))
                .lore(lore).ready();

        // Turn Time
        lore = messagesConfig.getStringList("inventoryEditingTurnTimeItemLore");
        replaceValueVariable(lore,arena.getTurnTime()+"");
        new InventoryItem(inv,24, Material.CLOCK).name(messagesConfig.getString("inventoryEditingTurnTimeItemName"))
                .lore(lore).ready();

        // End Mode
        lore = messagesConfig.getStringList("inventoryEditingEndModeItemLore");
        replaceValueVariable(lore,arena.getEndTimeMode().name());
        new InventoryItem(inv,25, Material.DISPENSER).name(messagesConfig.getString("inventoryEditingEndModeItemName"))
                .lore(lore).ready();

        editInventoryPlayer.getPlayer().openInventory(inv);
        plugin.getEditInventoryManager().getPlayers().add(editInventoryPlayer);
    }

    private void replaceValueVariable(List<String> lore,String value){
        for(int i=0;i<lore.size();i++){
            lore.set(i,lore.get(i).replace("%value%",value));
        }
    }

    private List<String> setLoreLocation(List<String> lore,FileConfiguration messages,Location location){
        List<String> finalLore = new ArrayList<>();
        for(String line : lore){
            if(line.equals("%location%")){
                List<String> locationLore = messages.getStringList("inventoryEditingArenaCommonLocationItemLore");
                for(int i=0;i<locationLore.size();i++){
                    locationLore.set(i,locationLore.get(i).replace("%world%",location.getWorld().getName())
                            .replace("%x%",location.getX()+"").replace("%y%",location.getBlockY()+"")
                            .replace("%z%",location.getBlockZ()+"").replace("%pitch%",location.getPitch()+"")
                            .replace("%yaw%",location.getYaw()+""));
                }
                finalLore.addAll(locationLore);
            }else{
                finalLore.add(line);
            }
        }
        return finalLore;
    }

    private void clickItemLocation(EditInventoryPlayer editInventoryPlayer, ClickType clickType, String type){
        Arena arena = editInventoryPlayer.getArena();
        Location location = editInventoryPlayer.getPlayer().getLocation();
        if(type.equals("lobby")){
            arena.setLobbyLocation(location.clone());
        }else if(type.equals("spawn_1")){
            arena.setSpawnPlayer1Location(location.clone());
        }else{
            arena.setSpawnPlayer2Location(location.clone());
        }

        FileConfiguration messages = plugin.getMessagesConfig();
        plugin.getMessagesManager().sendMessage(editInventoryPlayer.getPlayer(),messages.getString("inventoryEditingArenaCommonLocationSet"),true);
        plugin.getConfigsManager().getArenasConfigManager().saveArena(arena);
        openMainInventory(editInventoryPlayer);
    }

    private void clickItemResetLocations(EditInventoryPlayer editInventoryPlayer){
        Arena arena = editInventoryPlayer.getArena();
        Location boardStartLocation = arena.getBoardStartLocation();
        arena.configureLocations(boardStartLocation, BoardManager.CELL_SIZE);

        FileConfiguration messages = plugin.getMessagesConfig();
        plugin.getMessagesManager().sendMessage(editInventoryPlayer.getPlayer(),messages.getString("inventoryEditingArenaLocationsReset"),true);
        plugin.getConfigsManager().getArenasConfigManager().saveArena(arena);
        openMainInventory(editInventoryPlayer);
    }

    private void clickItemEndMode(EditInventoryPlayer editInventoryPlayer, ClickType clickType){
        Arena arena = editInventoryPlayer.getArena();

        if(arena.getEndTimeMode().equals(ArenaEndTimeMode.CHECK_POINTS)){
            arena.setEndTimeMode(ArenaEndTimeMode.ALWAYS_TIE);
        }else{
            arena.setEndTimeMode(ArenaEndTimeMode.CHECK_POINTS);
        }

        FileConfiguration messages = plugin.getMessagesConfig();
        plugin.getMessagesManager().sendMessage(editInventoryPlayer.getPlayer(),messages.getString("inventoryEditingEndModeChanged"),true);
        plugin.getConfigsManager().getArenasConfigManager().saveArena(arena);
        openMainInventory(editInventoryPlayer);
    }

    private void clickItemTime(EditInventoryPlayer editInventoryPlayer, ClickType clickType, String type){
        int value = 0;
        if(clickType.equals(ClickType.SHIFT_LEFT)){
            if(type.equals("turn_time")){
                value = 10;
            }else{
                value = 60;
            }
        }else if(clickType.equals(ClickType.SHIFT_RIGHT)){
            if(type.equals("turn_time")){
                value = -10;
            }else{
                value = -60;
            }
        }else if(clickType.equals(ClickType.LEFT)){
            if(type.equals("turn_time")){
                value = 5;
            }else{
                value = 10;
            }
        }else if(clickType.equals(ClickType.RIGHT)){
            if(type.equals("turn_time")){
                value = -5;
            }else{
                value = -10;
            }
        }

        Arena arena = editInventoryPlayer.getArena();
        int currentValue;
        if(type.equals("turn_time")){
            currentValue = arena.getTurnTime();
        }else{
            currentValue = arena.getMaxTime();
        }

        int newValue = currentValue+value;
        if(newValue < 0){
            return;
        }

        if(type.equals("turn_time")){
            arena.setTurnTime(newValue);
        }else{
            arena.setMaxTime(newValue);
        }

        plugin.getConfigsManager().getArenasConfigManager().saveArena(arena);
        openMainInventory(editInventoryPlayer);
    }



    public void onInventoryClick(EditInventoryPlayer editInventoryPlayer, int slot, ItemStack item, ClickType clickType){
        switch(slot){
            case 10 -> clickItemLocation(editInventoryPlayer,clickType,"lobby");
            case 19 -> clickItemLocation(editInventoryPlayer,clickType,"spawn_1");
            case 28 -> clickItemLocation(editInventoryPlayer,clickType,"spawn_2");
            case 37 -> clickItemResetLocations(editInventoryPlayer);
            case 25 -> clickItemEndMode(editInventoryPlayer,clickType);
            case 23 -> clickItemTime(editInventoryPlayer,clickType,"max_time");
            case 24 -> clickItemTime(editInventoryPlayer,clickType,"turn_time");
        }

    }
}
