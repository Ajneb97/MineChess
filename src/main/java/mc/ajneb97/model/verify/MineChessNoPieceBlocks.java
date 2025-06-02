package mc.ajneb97.model.verify;

import mc.ajneb97.utils.JSONMessage;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MineChessNoPieceBlocks extends MineChessBaseError {

    private String pieceName;

    public MineChessNoPieceBlocks(String file, String errorText, boolean critical, String pieceName) {
        super(file, errorText, critical);
        this.pieceName = pieceName;
    }

    @Override
    public void sendMessage(Player player) {
        List<String> hover = new ArrayList<>();

        JSONMessage jsonMessage = new JSONMessage(player,prefix+"&7Piece &c"+pieceName+" &7on file &c"+file+" &7has no blocks structure.");
        hover.add("&eTHIS IS AN ERROR!");
        hover.add("&fThe piece &c"+pieceName+" &fdefined on");
        hover.add("&ffile &c"+file+" &fhas no blocks structure.");
        hover.add("&fYou have arenas that use piece blocks structures,");
        hover.add("&fso the block configuration for this piece");
        hover.add("&fmust be added.");

        jsonMessage.hover(hover).send();
    }
}
