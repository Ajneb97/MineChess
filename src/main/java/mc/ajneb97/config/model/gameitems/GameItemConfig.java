package mc.ajneb97.config.model.gameitems;


import mc.ajneb97.model.items.CommonItem;

public class GameItemConfig {
    private boolean enabled;
    private CommonItem item;

    public GameItemConfig(boolean enabled, CommonItem item) {
        this.enabled = enabled;
        this.item = item;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public CommonItem getItem() {
        return item;
    }

    public void setItem(CommonItem item) {
        this.item = item;
    }
}
