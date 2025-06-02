package mc.ajneb97.config.model;

public class GameActions {
    private GameActionsGame gameActionsGame;
    private GameActionsEndGame gameActionsEndGame;
    private GameActionsRewards gameActionsRewards;

    public GameActions(GameActionsGame gameActionsGame, GameActionsEndGame gameActionsEndGame, GameActionsRewards gameActionsRewards) {
        this.gameActionsGame = gameActionsGame;
        this.gameActionsEndGame = gameActionsEndGame;
        this.gameActionsRewards = gameActionsRewards;
    }

    public GameActionsGame getGameActionsGame() {
        return gameActionsGame;
    }

    public void setGameActionsGame(GameActionsGame gameActionsGame) {
        this.gameActionsGame = gameActionsGame;
    }

    public GameActionsEndGame getGameActionsEndGame() {
        return gameActionsEndGame;
    }

    public void setGameActionsEndGame(GameActionsEndGame gameActionsEndGame) {
        this.gameActionsEndGame = gameActionsEndGame;
    }

    public GameActionsRewards getGameActionsRewards() {
        return gameActionsRewards;
    }

    public void setGameActionsRewards(GameActionsRewards gameActionsRewards) {
        this.gameActionsRewards = gameActionsRewards;
    }
}
