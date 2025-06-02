package mc.ajneb97.api;

import mc.ajneb97.MineChess;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * This class will automatically register as a placeholder expansion 
 * when a jar including this class is added to the directory 
 * {@code /plugins/PlaceholderAPI/expansions} on your server.
 * <br>
 * <br>If you create such a class inside your own plugin, you have to
 * register it manually in your plugins {@code onEbale()} by using 
 * {@code new YourExpansionClass().register();}
 */
public class ExpansionMineChess extends PlaceholderExpansion {

    // We get an instance of the plugin later.
    private MineChess plugin;

    public ExpansionMineChess(MineChess plugin) {
    	this.plugin = plugin;
    }
    
    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist(){
        return true;
    }
    /**
     * Since this expansion requires api access to the plugin "SomePlugin" 
     * we must check if said plugin is on the server or not.
     *
     * @return true or false depending on if the required plugin is installed.
     */
    @Override
    public boolean canRegister(){
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * 
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor(){
        return "Ajneb97";
    }
 
    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest 
     * method to obtain a value if a placeholder starts with our 
     * identifier.
     * <br>This must be unique and can not contain % or _
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier(){
        return "minechess";
    }

    /**
     * This is the version of this expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier){

        if(player == null){
            return "";
        }

        return switch (identifier) {
            case "wins" ->
                // %minechess_wins%
                    MineChessAPI.getWins(player) + "";
            case "loses" ->
                // %minechess_loses%
                    MineChessAPI.getLoses(player) + "";
            case "ties" ->
                // %minechess_ties%
                    MineChessAPI.getTies(player) + "";
            case "time_played" ->
                // %minechess_time_played%
                    MineChessAPI.getTimePlayed(player);
            default ->
                // We return null if an invalid placeholder (f.e. %someplugin_placeholder3%)
                // was provided
                    null;
        };

    }
}
