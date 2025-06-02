package mc.ajneb97.model.verify;

import mc.ajneb97.utils.JSONMessage;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MineChessInvalidParticle extends MineChessBaseError {

    private String particle;

    public MineChessInvalidParticle(String file, String errorText, boolean critical, String particle) {
        super(file, errorText, critical);
        this.particle = particle;
    }

    @Override
    public void sendMessage(Player player) {
        List<String> hover = new ArrayList<>();

        JSONMessage jsonMessage = new JSONMessage(player,prefix+"&7Particle &c"+particle+" &7on file &c"+file+" &7is not valid.");
        hover.add("&eTHIS IS A WARNING!");
        hover.add("&fThe particle &c"+particle+" &fdefined on");
        hover.add("&ffile &c"+file+" &fdoesn't exists for your");
        hover.add("&fminecraft version.");

        jsonMessage.hover(hover).send();
    }
}
