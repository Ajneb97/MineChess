package mc.ajneb97.model.inventory;

import java.util.ArrayList;
import java.util.List;

public class CommonInventory {

    private String name;
    private int slots;
    private String title;
    private List<CommonInventoryItem> items;

    public CommonInventory(String name, int slots, String title, List<CommonInventoryItem> items) {
        this.name = name;
        this.slots = slots;
        this.title = title;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<CommonInventoryItem> getItems() {
        return items;
    }

    public void setItems(List<CommonInventoryItem> items) {
        this.items = items;
    }

    //Returns the amount of arena slots on a page from this inventory
    public int getTotalArenaSlots() {
        int total = 0;
        for(CommonInventoryItem item : items) {
            int size = item.getSlots().size();
            if(item.getType() != null && item.getType().equals("arena")) {
                total = total+size;
            }
        }
        return total;
    }

    public List<Integer> getArenaSlots(){
        List<Integer> slots = new ArrayList<>();
        for(CommonInventoryItem item : items) {
            if(item.getType() != null && item.getType().equals("arena")) {
                slots.addAll(item.getSlots());
            }
        }
        return slots;
    }
}
