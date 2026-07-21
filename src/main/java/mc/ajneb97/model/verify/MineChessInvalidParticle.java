package mc.ajneb97.model.verify;

import mc.ajneb97.api.MineChessAPI;
import mc.ajneb97.utils.JSONMessage;
import mc.ajneb97.utils.JSONMessageAdventure;
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

        boolean isPaper = MineChessAPI.getPlugin().getDependencyManager().isPaper();
        if(isPaper){
            JSONMessageAdventure jsonMessage = new JSONMessageAdventure(player,prefix+"<gray>Particle <red>"+particle+" <gray>on file <red>"+file+" <gray>is not valid.");
            hover.add("<yellow>THIS IS A WARNING!");
            hover.add("<white>The particle <red>"+particle+" <white>defined on");
            hover.add("<white>file <red>"+file+" <white>doesn't exists for your");
            hover.add("<white>minecraft version.");

            jsonMessage.hover(hover).send();
        }else{
            JSONMessage jsonMessage = new JSONMessage(player,prefix+"&7Particle &c"+particle+" &7on file &c"+file+" &7is not valid.");
            hover.add("&eTHIS IS A WARNING!");
            hover.add("&fThe particle &c"+particle+" &fdefined on");
            hover.add("&ffile &c"+file+" &fdoesn't exists for your");
            hover.add("&fminecraft version.");

            jsonMessage.hover(hover).send();
        }

    }
}
