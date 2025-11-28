package mc.ajneb97.utils;

import mc.ajneb97.MineChess;
import mc.ajneb97.manager.MessagesManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class OtherUtils {

    public static boolean isNew() {
        ServerVersion serverVersion = MineChess.serverVersion;
        if(serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_16_R1)){
            return true;
        }
        return false;
    }

    public static boolean isLegacy() {
        ServerVersion serverVersion = MineChess.serverVersion;
        if(serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_13_R1)) {
            return false;
        }
        return true;
    }

    // 1.20+
    public static boolean isTrimNew() {
        ServerVersion serverVersion = MineChess.serverVersion;
        if(serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_20_R1)) {
            return true;
        }else {
            return false;
        }
    }

    public static String replaceGlobalVariables(String text, Player player, MineChess plugin) {
        if(player == null){
            return text;
        }
        text = text.replace("%player%",player.getName());
        if(plugin.getDependencyManager().isPlaceholderAPI()) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }

        return text;
    }

    public static String getTimeFormat1(int seconds) {
        // 360 -> 06:00
        // 90 -> 01:30
        // 150 -> 02:30
        int minutes = seconds/60;
        int secondsRes = seconds - (minutes*60);
        String secondsMsg = "";
        String minutesMsg = "";
        if(secondsRes >= 0 && secondsRes <= 9) {
            secondsMsg = "0"+secondsRes;
        }else {
            secondsMsg = secondsRes+"";
        }

        if(minutes >= 0 && minutes <= 9) {
            minutesMsg = "0"+minutes;
        }else {
            minutesMsg = minutes+"";
        }

        return minutesMsg+":"+secondsMsg;
    }

    public static String getTimeFormat2(long seconds, MessagesManager msgManager) {
        long totalMinWait = seconds/60;
        long totalHourWait = totalMinWait/60;
        long totalDayWait = totalHourWait/24;
        String time = "";
        if(seconds > 59){
            seconds = seconds - 60*totalMinWait;
        }
        if(seconds > 0) {
            time = seconds+msgManager.getTimeSeconds();
        }
        if(totalMinWait > 59){
            totalMinWait = totalMinWait - 60*totalHourWait;
        }
        if(totalMinWait > 0){
            time = totalMinWait+msgManager.getTimeMinutes()+" "+time;
        }
        if(totalHourWait > 23) {
            totalHourWait = totalHourWait - 24*totalDayWait;
        }
        if(totalHourWait > 0){
            time = totalHourWait+msgManager.getTimeHours()+" " + time;
        }
        if(totalDayWait > 0) {
            time = totalDayWait+msgManager.getTimeDays()+" " + time;
        }

        if(time.endsWith(" ")) {
            time = time.substring(0, time.length()-1);
        }

        return time;
    }

    public static Color getFireworkColorFromName(String colorName) {
        if(colorName.startsWith("#")){
            int rgbValue = Integer.parseInt(colorName.substring(1), 16);
            return Color.fromRGB(rgbValue);
        }
        try {
            return (Color) Color.class.getDeclaredField(colorName).get(Color.class);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isChunkLoaded(Location location) {
        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;
        if(location.getWorld().isChunkLoaded(chunkX, chunkZ)) {
            return true;
        }
        return false;
    }
}
