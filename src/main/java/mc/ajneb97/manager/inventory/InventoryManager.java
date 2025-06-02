package mc.ajneb97.manager.inventory;

import mc.ajneb97.MineChess;
import mc.ajneb97.manager.CommonItemManager;
import mc.ajneb97.model.chess.PieceType;
import mc.ajneb97.model.inventory.CommonInventory;
import mc.ajneb97.model.inventory.CommonInventoryItem;
import mc.ajneb97.model.inventory.InventoryPlayer;
import mc.ajneb97.model.items.CommonItem;
import mc.ajneb97.utils.ActionUtils;
import mc.ajneb97.manager.MessagesManager;
import mc.ajneb97.model.Arena;
import mc.ajneb97.model.PlayerColor;
import mc.ajneb97.model.game.GamePlayer;
import mc.ajneb97.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InventoryManager {

    private MineChess plugin;
    private ArrayList<CommonInventory> inventories;
    private CustomItemsInventory customItems;
    private ArrayList<InventoryPlayer> players;

    public InventoryManager(MineChess plugin){
        this.plugin = plugin;
        this.inventories = new ArrayList<>();
        this.players = new ArrayList<>();
    }

    public ArrayList<CommonInventory> getInventories() {
        return inventories;
    }

    public void setInventories(ArrayList<CommonInventory> inventories) {
        this.inventories = inventories;
    }

    public CustomItemsInventory getCustomItems() {
        return customItems;
    }

    public void setCustomItems(CustomItemsInventory customItems) {
        this.customItems = customItems;
    }

    public ArrayList<InventoryPlayer> getPlayers() {
        return players;
    }

    public CommonInventory getInventory(InventoryType inventoryType){
        for(CommonInventory inventory : inventories){
            if(inventory.getName().equals(inventoryType.name().toLowerCase())){
                return inventory;
            }
        }
        return null;
    }

    public InventoryPlayer getInventoryPlayer(Player player){
        for(InventoryPlayer inventoryPlayer : players){
            if(inventoryPlayer.getPlayer().equals(player)){
                return inventoryPlayer;
            }
        }
        return null;
    }

    public void removeInventoryPlayer(Player player){
        players.removeIf(p -> p.getPlayer().equals(player));
    }

    public void openInventory(InventoryPlayer inventoryPlayer){
        CommonInventory inventory = getInventory(inventoryPlayer.getInventoryType());

        String title = inventory.getTitle();
        Inventory inv = Bukkit.createInventory(null,inventory.getSlots(), MessagesManager.getColoredMessage(title));

        List<CommonInventoryItem> items = inventory.getItems();
        CommonItemManager commonItemManager = plugin.getCommonItemManager();

        //Add items for all inventories
        for(CommonInventoryItem itemInventory : items){
            List<Integer> slots = itemInventory.getSlots();
            String type = itemInventory.getType();

            for(int slot : slots){
                if(type != null){
                    if(type.startsWith("piece: ")){
                        ItemStack item = setPiece(type.replace("piece: ",""),inventoryPlayer);
                        item = setItemActions(itemInventory,item);
                        inv.setItem(slot,item);
                        continue;
                    }
                }

                ItemStack item = commonItemManager.createItemFromCommonItem(itemInventory.getItem());

                String openInventory = itemInventory.getOpenInventory();
                if(openInventory != null) {
                    item = ItemUtils.setTagStringItem(plugin,item, "minechess_open_inventory", openInventory);
                }
                if(type != null){
                    item = ItemUtils.setTagStringItem(plugin,item, "minechess_item_type", type);
                }
                item = setItemActions(itemInventory,item);

                inv.setItem(slot,item);
            }
        }

        inventoryPlayer.getPlayer().openInventory(inv);
        players.add(inventoryPlayer);
    }

    private ItemStack setItemActions(CommonInventoryItem commonItem, ItemStack item) {
        List<String> clickActions = commonItem.getClickActions();
        if(clickActions != null && !clickActions.isEmpty()) {
            String actionsList = "";
            for(int i=0;i<clickActions.size();i++) {
                if(i==clickActions.size()-1) {
                    actionsList=actionsList+clickActions.get(i);
                }else {
                    actionsList=actionsList+clickActions.get(i)+"|";
                }
            }
            item = ItemUtils.setTagStringItem(plugin, item, "minechess_item_actions", actionsList);
        }
        return item;
    }

    private void clickActionsItem(InventoryPlayer inventoryPlayer,String itemCommands){
        String[] sep = itemCommands.split("\\|");
        for(String action : sep){
            ActionUtils.executeAction(inventoryPlayer.getPlayer(),action,plugin,new ArrayList<>());
        }
    }

    public void clickInventory(InventoryPlayer inventoryPlayer, ItemStack item, ClickType clickType){
        String itemActions = ItemUtils.getTagStringItem(plugin,item,"minechess_item_actions");
        if(itemActions != null){
            clickActionsItem(inventoryPlayer,itemActions);
        }

        if(inventoryPlayer.getInventoryType().equals(InventoryType.PROMOTION)){
            clickInventoryPromotion(inventoryPlayer,item,clickType);
        }
    }

    public void clickInventoryPromotion(InventoryPlayer inventoryPlayer, ItemStack item, ClickType clickType){
        String pieceName = ItemUtils.getTagStringItem(plugin,item,"minechess_piece");
        if(pieceName != null){
            Arena arena = plugin.getArenaManager().getGamePlayerManager().getArenaByPlayer(inventoryPlayer.getPlayer());
            GamePlayer gamePlayer = arena.getGamePlayer(inventoryPlayer.getPlayer());
            if(gamePlayer.isOnPromotion()){
                plugin.getArenaManager().getGamePieceInteractionManager().promotePawn(arena,gamePlayer, PieceType.valueOf(pieceName));
            }
        }
    }

    public ItemStack setPiece(String pieceName,InventoryPlayer inventoryPlayer){
        Arena arena = plugin.getArenaManager().getGamePlayerManager().getArenaByPlayer(inventoryPlayer.getPlayer());
        PlayerColor color = arena.getColor(arena.getGamePlayer(inventoryPlayer.getPlayer()));

        CommonItem pieceItem = null;
        switch (pieceName) {
            case "rook" -> {
                if (color.equals(PlayerColor.WHITE)) {
                    pieceItem = customItems.getPieceRookBlack();
                } else {
                    pieceItem = customItems.getPieceRookWhite();
                }
            }
            case "bishop" -> {
                if (color.equals(PlayerColor.WHITE)) {
                    pieceItem = customItems.getPieceBishopBlack();
                } else {
                    pieceItem = customItems.getPieceBishopWhite();
                }
            }
            case "knight" -> {
                if (color.equals(PlayerColor.WHITE)) {
                    pieceItem = customItems.getPieceKnightBlack();
                } else {
                    pieceItem = customItems.getPieceKnightWhite();
                }
            }
            case "queen" -> {
                if (color.equals(PlayerColor.WHITE)) {
                    pieceItem = customItems.getPieceQueenBlack();
                } else {
                    pieceItem = customItems.getPieceQueenWhite();
                }
            }
            default -> {
                return null;
            }
        }

        ItemStack item = plugin.getCommonItemManager().createItemFromCommonItem(pieceItem);
        item = ItemUtils.setTagStringItem(plugin,item,"minechess_piece",pieceName.toUpperCase());
        return item;
    }
}
