package mc.ajneb97.command;

import mc.ajneb97.MineChess;
import mc.ajneb97.manager.ArenaManager;
import mc.ajneb97.manager.BoardManager;
import mc.ajneb97.manager.MessagesManager;
import mc.ajneb97.model.Arena;
import mc.ajneb97.model.game.GameLeaveReason;
import mc.ajneb97.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MainCommand implements CommandExecutor, TabCompleter {

    private MineChess plugin;
    public MainCommand(MineChess plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        MessagesManager msgManager = plugin.getMessagesManager();
        FileConfiguration messagesConfig = plugin.getMessagesConfig();

        if (!(sender instanceof Player player)) {
            if (args.length >= 1) {
                String arg = args[0].toLowerCase();
                switch (arg) {
                    case "reload" -> reload(sender,msgManager,messagesConfig);
                    case "enable" -> enable(sender, args, msgManager, messagesConfig);
                    case "disable" -> disable(sender, args, msgManager, messagesConfig);
                }
            }
            return true;
        }

        if(args.length >= 1){
            String arg = args[0].toLowerCase();
            switch (arg) {
                case "reload" -> reload(sender,msgManager,messagesConfig);
                case "create" -> create(player, args, msgManager, messagesConfig);
                case "build" -> build(player, args, msgManager, messagesConfig);
                case "delete" -> delete(player, args, msgManager, messagesConfig);
                case "setmainlobby" -> setMainLobby(player, msgManager, messagesConfig);
                case "enable" -> enable(player, args, msgManager, messagesConfig);
                case "disable" -> disable(player, args, msgManager, messagesConfig);
                case "join" -> join(player, args, msgManager, messagesConfig);
                case "joinrandom" -> joinrandom(player, msgManager, messagesConfig);
                case "leave" -> leave(player, msgManager, messagesConfig);
                case "edit" -> edit(player, args, msgManager, messagesConfig);
                case "spectate" -> spectate(player, args, msgManager, messagesConfig);
                case "verify" -> verify(player,msgManager,messagesConfig);
                default -> help(sender);
            }
        }else{
            help(sender);
        }

        return true;
    }

    public void help(CommandSender sender){
        if(PlayerUtils.isMineChessAdmin(sender)) {
            sender.sendMessage(MessagesManager.getColoredMessage("&7[ [ &6[&fMineChess&6] &7] ]"));
            sender.sendMessage(MessagesManager.getColoredMessage(""));
            sender.sendMessage(MessagesManager.getColoredMessage("&6/minechess create <name> &8Creates a new arena."));
            sender.sendMessage(MessagesManager.getColoredMessage("&6/minechess build <name> &8Builds an arena on your current position."));
            sender.sendMessage(MessagesManager.getColoredMessage("&6/minechess edit <name> &8Opens the editing GUI for an arena."));
            sender.sendMessage(MessagesManager.getColoredMessage("&6/minechess delete <name> &8Deletes an arena."));
            sender.sendMessage(MessagesManager.getColoredMessage("&6/minechess join <name> &8Joins an arena."));
            sender.sendMessage(MessagesManager.getColoredMessage("&6/minechess joinrandom &8Joins a random arena."));
            sender.sendMessage(MessagesManager.getColoredMessage("&6/minechess leave &8Leaves the Arena."));
            sender.sendMessage(MessagesManager.getColoredMessage("&6/minechess spectate <arena/player> &8Spectates an arena or a player."));
            sender.sendMessage(MessagesManager.getColoredMessage("&6/minechess setmainlobby &8Sets the Main Lobby."));
            sender.sendMessage(MessagesManager.getColoredMessage("&6/minechess enable <name> &8Enables an arena."));
            sender.sendMessage(MessagesManager.getColoredMessage("&6/minechess disable <name> &8Disables an arena."));
            sender.sendMessage(MessagesManager.getColoredMessage("&6/minechess verify &8Checks the plugin for errors."));
            sender.sendMessage(MessagesManager.getColoredMessage("&6/minechess reload &8Reloads the config."));
            sender.sendMessage(MessagesManager.getColoredMessage(""));
            sender.sendMessage(MessagesManager.getColoredMessage("&7[ [ &6[&fMineChess&6] &7] ]"));
        }
    }

    public void reload(CommandSender sender,MessagesManager msgManager,FileConfiguration messagesConfig){
        if(!PlayerUtils.isMineChessAdmin(sender)){
            msgManager.sendMessage(sender,messagesConfig.getString("noPermissions"),true);
            return;
        }

        if(!plugin.getConfigsManager().reload()){
            sender.sendMessage(MineChess.prefix+MessagesManager.getColoredMessage("&cThere was an error reloading the config, check the console."));
            return;
        }
        msgManager.sendMessage(sender,messagesConfig.getString("configReloaded"),true);
    }

    public void create(Player player,String[] args,MessagesManager msgManager,FileConfiguration messagesConfig){
        // /minechess create <arena>
        if(!PlayerUtils.isMineChessAdmin(player)){
            msgManager.sendMessage(player,messagesConfig.getString("noPermissions"),true);
            return;
        }

        if(args.length <= 1){
            msgManager.sendMessage(player,messagesConfig.getString("commandCreateError"),true);
            return;
        }

        String name = args[1];
        ArenaManager arenaManager = plugin.getArenaManager();
        if(arenaManager.getArenaByName(name) != null){
            msgManager.sendMessage(player,messagesConfig.getString("arenaAlreadyExist").replace("%arena%",name),true);
            return;
        }

        arenaManager.addArena(name);
        msgManager.sendMessage(player,messagesConfig.getString("arenaCreated").replace("%arena%",name),true);
    }

    public void build(Player player,String[] args,MessagesManager msgManager,FileConfiguration messagesConfig){
        // /minechess build <arena>
        if(!PlayerUtils.isMineChessAdmin(player)){
            msgManager.sendMessage(player,messagesConfig.getString("noPermissions"),true);
            return;
        }

        if(plugin.getVerifyManager().isCriticalErrors()){
            msgManager.sendMessage(player,messagesConfig.getString("pluginCriticalErrors"),true);
            return;
        }

        if(args.length <= 1){
            msgManager.sendMessage(player,messagesConfig.getString("commandBuildError"),true);
            return;
        }

        String name = args[1];
        ArenaManager arenaManager = plugin.getArenaManager();
        Arena arena = arenaManager.getArenaByName(name);
        if(arena == null){
            msgManager.sendMessage(player,messagesConfig.getString("arenaDoesNotExist").replace("%arena%",name),true);
            return;
        }

        BoardManager boardManager = plugin.getBoardManager();
        boardManager.removeBoardFloor(arena);
        boardManager.buildBoardFloor(arena,player.getLocation());
        msgManager.sendMessage(player,messagesConfig.getString("arenaBuilt").replace("%arena%",name),true);
    }

    public void delete(Player player,String[] args,MessagesManager msgManager,FileConfiguration messagesConfig) {
        // /minechess delete <arena>
        if (!PlayerUtils.isMineChessAdmin(player)) {
            msgManager.sendMessage(player, messagesConfig.getString("noPermissions"), true);
            return;
        }

        if (args.length <= 1) {
            msgManager.sendMessage(player, messagesConfig.getString("commandDeleteError"), true);
            return;
        }

        ArenaManager arenaManager = plugin.getArenaManager();
        Arena arena = arenaManager.getArenaByName(args[1]);
        if(arena == null){
            msgManager.sendMessage(player,messagesConfig.getString("arenaDoesNotExist").replace("%arena%",args[1]),true);
            return;
        }

        if(!arena.isDisabled()){
            msgManager.sendMessage(player,messagesConfig.getString("arenaMustBeDisabled"),true);
            return;
        }

        arenaManager.removeArena(args[1]);
        msgManager.sendMessage(player,messagesConfig.getString("arenaDeleted").replace("%arena%",args[1]),true);
    }

    public void setMainLobby(Player player,MessagesManager msgManager,FileConfiguration messagesConfig) {
        // /minechess setmainlobby
        if (!PlayerUtils.isMineChessAdmin(player)) {
            msgManager.sendMessage(player, messagesConfig.getString("noPermissions"), true);
            return;
        }

        Location l = player.getLocation().clone();
        plugin.getArenaManager().setMainLobbyLocation(l);
        plugin.getConfigsManager().getArenasConfigManager().saveMainLobby(l);
        msgManager.sendMessage(player,messagesConfig.getString("mainLobbySet"),true);
    }

    public void enable(CommandSender sender,String[] args,MessagesManager msgManager,FileConfiguration messagesConfig) {
        // /minechess enable <arena>
        if (!PlayerUtils.isMineChessAdmin(sender)) {
            msgManager.sendMessage(sender, messagesConfig.getString("noPermissions"), true);
            return;
        }

        if (args.length <= 1) {
            msgManager.sendMessage(sender, messagesConfig.getString("commandEnableError"), true);
            return;
        }

        ArenaManager arenasManager = plugin.getArenaManager();
        Arena arena = arenasManager.getArenaByName(args[1]);
        if(arena == null){
            msgManager.sendMessage(sender,messagesConfig.getString("arenaDoesNotExist").replace("%arena%",args[1]),true);
            return;
        }

        if(!arena.isDisabled()){
            msgManager.sendMessage(sender,messagesConfig.getString("arenaAlreadyEnabled").replace("%arena%",args[1]),true);
            return;
        }

        //Checks
        String message = arenasManager.arenaIsValid(arena,messagesConfig);
        if(message != null){
            msgManager.sendMessage(sender,message,true);
            return;
        }

        arena.enable();
        plugin.getConfigsManager().getArenasConfigManager().saveArena(arena);

        msgManager.sendMessage(sender,messagesConfig.getString("arenaEnabled").replace("%arena%",args[1]),true);
    }

    public void disable(CommandSender sender,String[] args,MessagesManager msgManager,FileConfiguration messagesConfig) {
        // /minechess disable <arena>
        if (!PlayerUtils.isMineChessAdmin(sender)) {
            msgManager.sendMessage(sender, messagesConfig.getString("noPermissions"), true);
            return;
        }

        if (args.length <= 1) {
            msgManager.sendMessage(sender, messagesConfig.getString("commandDisableError"), true);
            return;
        }

        ArenaManager arenasManager = plugin.getArenaManager();
        Arena arena = arenasManager.getArenaByName(args[1]);
        if(arena == null){
            msgManager.sendMessage(sender,messagesConfig.getString("arenaDoesNotExist").replace("%arena%",args[1]),true);
            return;
        }

        if(arena.isDisabled()){
            msgManager.sendMessage(sender,messagesConfig.getString("arenaAlreadyDisabled").replace("%arena%",args[1]),true);
            return;
        }

        arenasManager.disableArena(arena);
        plugin.getConfigsManager().getArenasConfigManager().saveArena(arena);

        msgManager.sendMessage(sender,messagesConfig.getString("arenaDisabled").replace("%arena%",args[1]),true);
    }

    public void join(Player player,String[] args,MessagesManager msgManager,FileConfiguration messagesConfig) {
        // /minechess join <arena>
        if (!PlayerUtils.isMineChessAdmin(player) && !player.hasPermission("minechess.join")) {
            msgManager.sendMessage(player, messagesConfig.getString("noPermissions"), true);
            return;
        }

        if (args.length <= 1) {
            msgManager.sendMessage(player, messagesConfig.getString("commandJoinError"), true);
            return;
        }

        ArenaManager arenasManager = plugin.getArenaManager();
        Arena arena = arenasManager.getArenaByName(args[1]);
        if(arena == null){
            msgManager.sendMessage(player,messagesConfig.getString("arenaDoesNotExist").replace("%arena%",args[1]),true);
            return;
        }

        arenasManager.joinArena(player,arena);
    }

    public void joinrandom(Player player,MessagesManager msgManager,FileConfiguration messagesConfig) {
        // /minechess joinrandom
        if (!PlayerUtils.isMineChessAdmin(player) && !player.hasPermission("minechess.join")) {
            msgManager.sendMessage(player, messagesConfig.getString("noPermissions"), true);
            return;
        }

        ArenaManager arenasManager = plugin.getArenaManager();

        arenasManager.joinRandomArena(player);
    }

    public void leave(Player player,MessagesManager msgManager,FileConfiguration messagesConfig) {
        // /minechess leave
        if (!PlayerUtils.isMineChessAdmin(player) && !player.hasPermission("minechess.leave")) {
            msgManager.sendMessage(player, messagesConfig.getString("noPermissions"), true);
            return;
        }

        Arena arena = plugin.getArenaManager().getGamePlayerManager().getArenaByPlayer(player,true);
        if(arena == null){
            msgManager.sendMessage(player,messagesConfig.getString("playerNotPlaying"),true);
            return;
        }

        plugin.getArenaManager().leaveArena(player,arena, GameLeaveReason.COMMAND);
    }

    public void edit(Player player,String[] args,MessagesManager msgManager,FileConfiguration messagesConfig) {
        // /minechess edit <arena>
        if (!PlayerUtils.isMineChessAdmin(player)) {
            msgManager.sendMessage(player, messagesConfig.getString("noPermissions"), true);
            return;
        }

        if (args.length <= 1) {
            msgManager.sendMessage(player, messagesConfig.getString("commandEditError"), true);
            return;
        }

        ArenaManager arenaManager = plugin.getArenaManager();
        Arena arena = arenaManager.getArenaByName(args[1]);
        if(arena == null){
            msgManager.sendMessage(player,messagesConfig.getString("arenaDoesNotExist").replace("%arena%",args[1]),true);
            return;
        }

        if(!arena.isDisabled()){
            msgManager.sendMessage(player,messagesConfig.getString("arenaMustBeDisabled"),true);
            return;
        }

        String message = plugin.getArenaManager().arenaIsValid(arena,messagesConfig);
        if(message != null){
            msgManager.sendMessage(player,message,true);
            return;
        }

        plugin.getEditInventoryManager().openArenaInventory(player,arena);
    }

    public void spectate(Player player,String[] args,MessagesManager msgManager,FileConfiguration messagesConfig) {
        // /minechess spectate <arena/player>
        if (!PlayerUtils.isMineChessAdmin(player) && !player.hasPermission("minechess.spectate")) {
            msgManager.sendMessage(player, messagesConfig.getString("noPermissions"), true);
            return;
        }

        if (args.length <= 1) {
            msgManager.sendMessage(player, messagesConfig.getString("commandSpectateError"), true);
            return;
        }

        ArenaManager arenasManager = plugin.getArenaManager();
        Arena arena = arenasManager.getArenaByName(args[1]);
        if(arena == null){
            // Check for spectate player
            Player p = Bukkit.getPlayer(args[1]);
            if(p != null){
                arena = arenasManager.getGamePlayerManager().getArenaByPlayer(p);
                if(arena != null){
                    arenasManager.getGameSpectatorManager().joinArena(player,arena);
                }else{
                    msgManager.sendMessage(player,messagesConfig.getString("playerSpectateNotPlaying"),true);
                }
                return;
            }

            msgManager.sendMessage(player,messagesConfig.getString("arenaDoesNotExist").replace("%arena%",args[1]),true);
            return;
        }

        arenasManager.getGameSpectatorManager().joinArena(player,arena);
    }

    public void verify(Player player,MessagesManager msgManager,FileConfiguration messagesConfig){
        // /minechess verify
        if(!PlayerUtils.isMineChessAdmin(player)){
            msgManager.sendMessage(player,messagesConfig.getString("noPermissions"),true);
            return;
        }
        plugin.getVerifyManager().sendVerification(player);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = getArgCommands(sender,args.length);

        if(args.length == 1) {
            for(String c : commands) {
                if(args[0].isEmpty() || c.startsWith(args[0].toLowerCase())) {
                    completions.add(c);
                }
            }
            return completions;
        }else {
            if(args.length == 2) {
                for(String c : commands) {
                    if(args[0].equalsIgnoreCase(c)){
                        if(c.equals("spectate")){
                            List<String> arenaCompletions = getArenaCompletions(args);
                            if(arenaCompletions != null){
                                completions.addAll(arenaCompletions);
                            }
                            List<String> playerCompletions = getPlayerCompletions(args);
                            if(playerCompletions != null){
                                completions.addAll(playerCompletions);
                            }
                            return completions;
                        }
                        return getArenaCompletions(args);
                    }
                }
            }
        }

        return null;
    }

    private List<String> getArgCommands(CommandSender sender,int args){
        args = args-1;
        List<String> commands = new ArrayList<>();
        if(sender.hasPermission("minechess.leave") && args == 0){
            commands.add("leave");
        }
        if(sender.hasPermission("minechess.join") && args == 0){
            commands.add("joinrandom");
        }
        if(sender.hasPermission("minechess.join") && args <= 1){
            commands.add("join");
        }
        if(sender.hasPermission("minechess.spectate") && args <= 1){
            commands.add("spectate");
        }

        if(PlayerUtils.isMineChessAdmin(sender)){
            if(args == 0){
                commands.add("reload");
                commands.add("create");
                commands.add("setmainlobby");
                commands.add("verify");
            }
            if(args <= 1){
                commands.add("delete");commands.add("build");
                commands.add("edit");commands.add("enable");commands.add("disable");
            }
        }
        return commands;
    }

    private List<String> getArenaCompletions(String[] args){
        List<String> completions = new ArrayList<>();
        String argArena = args[1];

        ArrayList<Arena> arenas = plugin.getArenaManager().getArenas();
        for(Arena arena : arenas) {
            if(argArena.isEmpty() || arena.getName().toLowerCase().startsWith(argArena.toLowerCase())) {
                completions.add(arena.getName());
            }
        }

        if(completions.isEmpty()){
            return null;
        }
        return completions;
    }

    private List<String> getPlayerCompletions(String[] args){
        List<String> completions = new ArrayList<>();
        String argPlayer = args[1];

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(argPlayer.isEmpty() || player.getName().toLowerCase().startsWith(argPlayer.toLowerCase())) {
                completions.add(player.getName());
            }
        }

        if(completions.isEmpty()){
            return null;
        }
        return completions;
    }
}
