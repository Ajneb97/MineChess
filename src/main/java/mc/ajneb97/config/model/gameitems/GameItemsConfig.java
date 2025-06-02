package mc.ajneb97.config.model.gameitems;

public class GameItemsConfig {
    private GameItemConfig leaveItem;
    private GameItemConfig selectItem;
    private GameItemConfig playAgainItem;

    public GameItemsConfig(GameItemConfig leaveItem,GameItemConfig selectItem,GameItemConfig playAgainItem) {
        this.leaveItem = leaveItem;
        this.selectItem = selectItem;
        this.playAgainItem = playAgainItem;
    }


    public GameItemConfig getLeaveItem() {
        return leaveItem;
    }

    public GameItemConfig getSelectItem() {
        return selectItem;
    }

    public GameItemConfig getPlayAgainItem() {
        return playAgainItem;
    }
}
