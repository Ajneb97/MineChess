package mc.ajneb97.model.internal;

import org.bukkit.entity.Player;

import java.util.ArrayList;

public class VariablesProperties {
    private ArrayList<CommonVariable> savedVariables;
    private Player player;
    private boolean isPlaceholderAPI;

    public VariablesProperties(ArrayList<CommonVariable> savedVariables, Player player, boolean isPlaceholderAPI) {
        this.savedVariables = savedVariables;
        this.player = player;
        this.isPlaceholderAPI = isPlaceholderAPI;
    }

    public ArrayList<CommonVariable> getSavedVariables() {
        return savedVariables;
    }

    public void setSavedVariables(ArrayList<CommonVariable> savedVariables) {
        this.savedVariables = savedVariables;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isPlaceholderAPI() {
        return isPlaceholderAPI;
    }

    public void setPlaceholderAPI(boolean placeholderAPI) {
        isPlaceholderAPI = placeholderAPI;
    }

}
