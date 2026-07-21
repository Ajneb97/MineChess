package mc.ajneb97.model.verify;

import mc.ajneb97.api.MineChessAPI;
import mc.ajneb97.utils.JSONMessage;
import mc.ajneb97.utils.JSONMessageAdventure;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MineChessInvalidPieceType extends MineChessBaseError {

    private String pieceName;

    public MineChessInvalidPieceType(String file, String errorText, boolean critical, String pieceName) {
        super(file, errorText, critical);
        this.pieceName = pieceName;
    }

    @Override
    public void sendMessage(Player player) {
        List<String> hover = new ArrayList<>();

        boolean isPaper = MineChessAPI.getPlugin().getDependencyManager().isPaper();
        if(isPaper){
            JSONMessageAdventure jsonMessage = new JSONMessageAdventure(player,prefix+"<gray>Piece <red>"+pieceName+" <gray>on file <red>"+file+" <gray>is not valid.");
            hover.add("<yellow>THIS IS AN ERROR!");
            hover.add("<white>The piece <red>"+pieceName+" <white>defined on");
            hover.add("<white>file <red>"+file+" <white>is not valid.");
            hover.add("<white>Use one of the following: <gray>rook, bishop, knight, queen");

            jsonMessage.hover(hover).send();
        }else{
            JSONMessage jsonMessage = new JSONMessage(player,prefix+"&7Piece &c"+pieceName+" &7on file &c"+file+" &7is not valid.");
            hover.add("&eTHIS IS AN ERROR!");
            hover.add("&fThe piece &c"+pieceName+" &fdefined on");
            hover.add("&ffile &c"+file+" &fis not valid.");
            hover.add("&fUse one of the following: &7rook, bishop, knight, queen");

            jsonMessage.hover(hover).send();
        }

    }
}
