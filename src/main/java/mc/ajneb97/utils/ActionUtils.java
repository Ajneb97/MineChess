package mc.ajneb97.utils;

import mc.ajneb97.MineChess;
import mc.ajneb97.api.MineChessAPI;
import mc.ajneb97.config.model.gameitems.SoundConfig;
import mc.ajneb97.model.internal.VariablesProperties;
import mc.ajneb97.libs.titles.TitleAPI;
import mc.ajneb97.manager.MessagesManager;
import mc.ajneb97.model.Arena;
import mc.ajneb97.model.game.GamePlayer;
import mc.ajneb97.model.internal.CommonVariable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ActionUtils {

    public static void executeActions(GamePlayer gamePlayer, Arena arena, List<String> actions, MineChess plugin, ArrayList<CommonVariable> variables,
                                      boolean includeSpectators){
        for(String action : actions){
            if(action.startsWith("to_all: ")){
                action = action.replace("to_all: ","");
                for(GamePlayer g : arena.getGamePlayers(includeSpectators)){
                    if(g == null){
                        continue;
                    }
                    variables.add(new CommonVariable("%points%",g.getPoints()+""));
                    executeAction(g.getPlayer(),action,plugin,variables);
                }
            }else if(action.startsWith("to_opponent: ")){
                action = action.replace("to_opponent: ","");
                GamePlayer opponentPlayer = arena.getOpponentPlayer(gamePlayer);
                if(opponentPlayer != null){
                    executeAction(opponentPlayer.getPlayer(),action,plugin,variables);
                }
            }else if(action.startsWith("to_winner: ")){
                action = action.replace("to_winner: ","");
                executeAction(arena.getWinner().getPlayer(),action,plugin,variables);
            }else{
                executeAction(gamePlayer.getPlayer(),action,plugin,variables);
            }
        }
    }

    public static void executeAction(Player player, String actionText, MineChess plugin, ArrayList<CommonVariable> variables){
        if(player == null){
            return;
        }
        int indexFirst = actionText.indexOf(" ");
        String actionType = actionText.substring(0,indexFirst).replace(":","");
        String actionLine = actionText.substring(indexFirst+1);
        actionLine = VariablesUtils.replaceAllVariablesInLine(actionLine,new VariablesProperties(
                variables,player,plugin.getDependencyManager().isPlaceholderAPI()
        ));

        switch (actionType) {
            case "message" -> ActionUtils.message(player, actionLine);
            case "centered_message" -> ActionUtils.centeredMessage(player, actionLine);
            case "console_command" -> ActionUtils.consoleCommand(actionLine);
            case "player_command" -> ActionUtils.playerCommand(player, actionLine);
            case "playsound" -> ActionUtils.playSound(player, actionLine);
            case "title" -> ActionUtils.title(player, actionLine);
        }
    }

    public static void playSound(Player player, String soundLine){
        String[] sep = soundLine.split(";");
        Sound sound = null;
        float volume = 0;
        float pitch = 0;
        try {
            sound = getSoundByName(sep[0]);
            volume = Float.parseFloat(sep[1]);
            pitch = Float.parseFloat(sep[2]);
        }catch(Exception e ) {
            Bukkit.getConsoleSender().sendMessage(MineChess.prefix+
                    MessagesManager.getLegacyColoredMessage("&7Sound Name: &c"+sep[0]+" &7is not valid. Change it in the config!"));
            return;
        }

        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public static void playSoundFromConfig(Player player, SoundConfig soundConfig){
        if(soundConfig == null){
            return;
        }

        Sound sound;
        try {
            sound = getSoundByName(soundConfig.getSound());
        }catch(Exception e ) {
            Bukkit.getConsoleSender().sendMessage(MineChess.prefix+
                    MessagesManager.getLegacyColoredMessage("&7Sound Name: &c"+soundConfig.getSound()+" &7is not valid. Change it in the config!"));
            return;
        }

        player.playSound(player.getLocation(), sound, soundConfig.getVolume(), soundConfig.getPitch());
    }

    private static Sound getSoundByName(String name){
        try {
            Class<?> soundTypeClass = Class.forName("org.bukkit.Sound");
            Method valueOf = soundTypeClass.getMethod("valueOf", String.class);
            return (Sound) valueOf.invoke(null,name);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void consoleCommand(String actionLine){
        ConsoleCommandSender sender = Bukkit.getConsoleSender();
        Bukkit.dispatchCommand(sender, actionLine);
    }

    public static void playerCommand(Player player, String actionLine){
        player.performCommand(actionLine);
    }

    public static void message(Player player,String actionLine){
        if(MineChessAPI.getPlugin().getConfigsManager().getMainConfigManager().isUseMiniMessage()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(actionLine));
        }else{
            player.sendMessage(MessagesManager.getLegacyColoredMessage(actionLine));
        }
    }

    public static void centeredMessage(Player player,String actionLine){
        if(MineChessAPI.getPlugin().getConfigsManager().getMainConfigManager().isUseMiniMessage()){
            MiniMessage mm = MiniMessage.miniMessage();
            Component component = mm.deserialize(actionLine);
            String centeredTextLegacy = MessagesManager.getCenteredMessage(LegacyComponentSerializer.legacySection().serialize(component)); // to legacy
            Component centeredTextMiniMessage = LegacyComponentSerializer.legacySection().deserialize(centeredTextLegacy); // to minimessage
            player.sendMessage(centeredTextMiniMessage);
        }else{
            actionLine = MessagesManager.getLegacyColoredMessage(actionLine);
            player.sendMessage(MessagesManager.getCenteredMessage(actionLine));
        }
    }

    public static void title(Player player,String actionLine){
        String[] sep = actionLine.split(";");
        int fadeIn = Integer.parseInt(sep[0]);
        int stay = Integer.parseInt(sep[1]);
        int fadeOut = Integer.parseInt(sep[2]);

        String title = sep[3];
        String subtitle = sep[4];
        if(title.equals("none")) {
            title = "";
        }
        if(subtitle.equals("none")) {
            subtitle = "";
        }
        TitleAPI.sendTitle(player,fadeIn,stay,fadeOut,title,subtitle);
    }

    public static void firework(Player player, String actionLine, MineChess plugin){
        ArrayList<Color> colors = new ArrayList<Color>();
        FireworkEffect.Type type = null;
        ArrayList<Color> fadeColors = new ArrayList<Color>();
        int power = 0;

        String[] sep = actionLine.split(" ");
        for(String s : sep) {
            if(s.startsWith("colors:")) {
                s = s.replace("colors:", "");
                String[] colorsSep = s.split(",");
                for(String colorSep : colorsSep) {
                    colors.add(OtherUtils.getFireworkColorFromName(colorSep));
                }
            }else if(s.startsWith("type:")) {
                s = s.replace("type:", "");
                type = FireworkEffect.Type.valueOf(s);
            }else if(s.startsWith("fade:")) {
                s = s.replace("fade:", "");
                String[] colorsSep = s.split(",");
                for(String colorSep : colorsSep) {
                    fadeColors.add(OtherUtils.getFireworkColorFromName(colorSep));
                }
            }else if(s.startsWith("power:")) {
                s = s.replace("power:", "");
                power = Integer.parseInt(s);
            }
        }

        Location location = player.getLocation();

        ServerVersion serverVersion = MineChess.serverVersion;
        EntityType entityType = null;
        if(serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_20_R4)){
            entityType = EntityType.FIREWORK_ROCKET;
        }else{
            entityType = EntityType.valueOf("FIREWORK");
        }
        Firework firework = (Firework) location.getWorld().spawnEntity(location, entityType);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        FireworkEffect effect = FireworkEffect.builder().flicker(false)
                .withColor(colors)
                .with(type)
                .withFade(fadeColors)
                .build();
        fireworkMeta.addEffect(effect);
        fireworkMeta.setPower(power);
        firework.setFireworkMeta(fireworkMeta);
        firework.setMetadata("minechess", new FixedMetadataValue(plugin, "no_damage"));
    }
}
