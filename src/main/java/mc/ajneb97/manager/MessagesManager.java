package mc.ajneb97.manager;

import mc.ajneb97.api.MineChessAPI;
import mc.ajneb97.libs.centeredmessages.DefaultFontInfo;
import mc.ajneb97.model.game.GamePlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessagesManager {

	private String prefix;
	private String timeSeconds;
	private String timeMinutes;
	private String timeHours;
	private String timeDays;

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getTimeSeconds() {
		return timeSeconds;
	}

	public void setTimeSeconds(String timeSeconds) {
		this.timeSeconds = timeSeconds;
	}

	public String getTimeMinutes() {
		return timeMinutes;
	}

	public void setTimeMinutes(String timeMinutes) {
		this.timeMinutes = timeMinutes;
	}

	public String getTimeHours() {
		return timeHours;
	}

	public void setTimeHours(String timeHours) {
		this.timeHours = timeHours;
	}

	public String getTimeDays() {
		return timeDays;
	}

	public void setTimeDays(String timeDays) {
		this.timeDays = timeDays;
	}

	public void sendMessage(CommandSender sender, String message, boolean prefix){
		if(!message.isEmpty()){
			if(MineChessAPI.getPlugin().getConfigsManager().getMainConfigManager().isUseMiniMessage()){
				if(prefix){
					sender.sendMessage(MiniMessage.miniMessage().deserialize(this.prefix+message));
				}else{
					sender.sendMessage(MiniMessage.miniMessage().deserialize(message));
				}
			}else{
				if(prefix){
					sender.sendMessage(getLegacyColoredMessage(this.prefix+message));
				}else{
					sender.sendMessage(getLegacyColoredMessage(message));
				}
			}
		}
	}

	public void sendMessage(ArrayList<GamePlayer> gamePlayers, String message, boolean prefix){
		for(GamePlayer gamePlayer : gamePlayers){
			sendMessage(gamePlayer.getPlayer(),message,prefix);
		}
	}

	public static String getLegacyColoredMessage(String message) {
		Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
		Matcher match = pattern.matcher(message);

		while(match.find()) {
			String color = message.substring(match.start(),match.end());
			message = message.replace(color, ChatColor.of(color)+"");

			match = pattern.matcher(message);
		}

		message = ChatColor.translateAlternateColorCodes('&', message);
		return message;
	}

	public static String getCenteredMessage(String message){
		int CENTER_PX = 154;
		int messagePxSize = 0;
		boolean previousCode = false;
		boolean isBold = false;

		for(char c : message.toCharArray()){
			if(c == '§'){
				previousCode = true;
				continue;
			}else if(previousCode == true){
				previousCode = false;
				if(c == 'l' || c == 'L'){
					isBold = true;
					continue;
				}else isBold = false;
			}else{
				DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
				messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
				messagePxSize++;
			}
		}

		int halvedMessageSize = messagePxSize / 2;
		int toCompensate = CENTER_PX - halvedMessageSize;
		int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
		int compensated = 0;
		StringBuilder sb = new StringBuilder();
		while(compensated < toCompensate){
			sb.append(" ");
			compensated += spaceLength;
		}
		return (sb.toString() + message);
	}
}
