package mc.ajneb97.model.verify;

import mc.ajneb97.api.MineChessAPI;
import mc.ajneb97.utils.JSONMessage;
import mc.ajneb97.utils.JSONMessageAdventure;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MineChessActionError extends MineChessBaseError {

    private String actionGroup;
    private String actionId;

    public MineChessActionError(String file, String errorText, boolean critical, String actionGroup, String actionId) {
        super(file, errorText, critical);
        this.actionId = actionId;
        this.actionGroup = actionGroup;
    }

    @Override
    public void sendMessage(Player player) {
        List<String> hover = new ArrayList<String>();

        boolean isPaper = MineChessAPI.getPlugin().getDependencyManager().isPaper();
        if(isPaper){
            JSONMessageAdventure jsonMessage = new JSONMessageAdventure(player,prefix+"<gray>Action (<red>"+actionGroup+"<gray>,<red>"+actionId+"<gray>)" +
                    " <gray>on file <red>"+file+" <gray>is not valid.");
            hover.add("<yellow>THIS IS A WARNING!");
            hover.add("<white>The action defined is probably");
            hover.add("<white>not formatted correctly:");
            for(String m : getFixedErrorText()) {
                hover.add("<red>"+m);
            }
            hover.add(" ");
            hover.add("<white>Remember to use a valid action from this list:");
            hover.add("<green>https://ajneb97.gitbook.io/minechess/actions");

            jsonMessage.hover(hover).send();
        }else{
            JSONMessage jsonMessage = new JSONMessage(player,prefix+"&7Action (&c"+actionGroup+"&7,&c"+actionId+"&7)" +
                    " &7on file &c"+file+" &7is not valid.");
            hover.add("&eTHIS IS A WARNING!");
            hover.add("&fThe action defined is probably");
            hover.add("&fnot formatted correctly:");
            for(String m : getFixedErrorText()) {
                hover.add("&c"+m);
            }
            hover.add(" ");
            hover.add("&fRemember to use a valid action from this list:");
            hover.add("&ahttps://ajneb97.gitbook.io/minechess/actions");

            jsonMessage.hover(hover).send();
        }

    }
}
