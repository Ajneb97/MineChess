package mc.ajneb97.model.verify;

import mc.ajneb97.utils.JSONMessage;
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

        JSONMessage jsonMessage = new JSONMessage(player,prefix+"&7Piece &c"+pieceName+" &7on file &c"+file+" &7is not valid.");
        hover.add("&eTHIS IS AN ERROR!");
        hover.add("&fThe piece &c"+pieceName+" &fdefined on");
        hover.add("&ffile &c"+file+" &fis not valid.");
        hover.add("&fUse one of the following: &7rook, bishop, knight, queen");

        jsonMessage.hover(hover).send();
    }
}
