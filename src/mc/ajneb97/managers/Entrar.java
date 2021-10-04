package mc.ajneb97.managers;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import mc.ajneb97.MineChess;

public class Entrar implements Listener{
	private MineChess plugin;
	public Entrar(MineChess plugin){		
		this.plugin = plugin;		
	}
	@EventHandler
	public void Join(PlayerJoinEvent event){
		Player jugador = event.getPlayer();	
		if(jugador.isOp() && !(plugin.version.equals(plugin.latestversion))){
			jugador.sendMessage(plugin.prefix + ChatColor.RED +" There is a new version available. "+ChatColor.YELLOW+
	  				  "("+ChatColor.GRAY+plugin.latestversion+ChatColor.YELLOW+")");
	  		    jugador.sendMessage(ChatColor.RED+"You can download it at: "+ChatColor.GREEN+"https://www.spigotmc.org/resources/74178/");			 
		}
	}
}
