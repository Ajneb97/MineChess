package mc.ajneb97.tasks;

import mc.ajneb97.MineChess;
import mc.ajneb97.manager.ArenaManager;
import mc.ajneb97.utils.MiniMessageUtils;
import mc.ajneb97.utils.OtherUtils;
import mc.ajneb97.manager.MessagesManager;
import mc.ajneb97.model.Arena;
import mc.ajneb97.model.ArenaSign;
import mc.ajneb97.model.game.GameStatus;
import mc.ajneb97.utils.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSignOpenEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class SignManager {
    private MineChess plugin;
    private ArrayList<ArenaSign> arenaSigns;
    private boolean stop;
    public SignManager(MineChess plugin){
        this.plugin = plugin;
        this.arenaSigns = new ArrayList<>();
        this.stop = false;
    }

    public void setArenaSigns(ArrayList<ArenaSign> arenaSigns) {
        this.arenaSigns = arenaSigns;
    }

    public ArenaSign getSignByLocation(Location location){
        String world = location.getWorld().getName();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        for(ArenaSign sign : arenaSigns) {
            Location lS = sign.getLocation();
            String worldS = lS.getWorld().getName();
            int xS = lS.getBlockX();
            int yS = lS.getBlockY();
            int zS = lS.getBlockZ();

            if(world.equals(worldS) && x == xS && y == yS && z == zS){
                return sign;
            }
        }
        return null;
    }

    public void removeSign(int id){
        arenaSigns.removeIf(sign -> sign.getId() == id);
    }

    public void createSign(Player player, SignChangeEvent event){
        if(!PlayerUtils.isMineChessAdmin(player)){
            return;
        }

        String[] lines = event.getLines();
        if(!lines[0].equals("[minechess]")){
            return;
        }

        String arenaName = lines[1];
        Arena arena = plugin.getArenaManager().getArenaByName(arenaName);
        if(arena == null){
            return;
        }

        FileConfiguration messagesConfig = plugin.getMessagesConfig();
        List<String> signFormat = messagesConfig.getStringList("signFormat");
        String status = getSignStatus(arena,messagesConfig);
        int currentPlayers = arena.getGamePlayers().size();
        int maxPlayers = 2;

        boolean isMiniMessage = plugin.getConfigsManager().getMainConfigManager().isUseMiniMessage();
        for(int i=0;i<signFormat.size();i++){
            String line = signFormat.get(i).replace("%arena%",arenaName).replace("%status%",status)
                            .replace("%current_players%",currentPlayers+"")
                            .replace("%max_players%",maxPlayers+"");
            if(isMiniMessage){
                MiniMessageUtils.setSignEventLine(event,i,line);
            }else{
                event.setLine(i, MessagesManager.getLegacyColoredMessage(line));
            }
        }

        ArenaSign arenaSign = new ArenaSign(arenaSigns.size()+1, arenaName, event.getBlock().getLocation());
        arenaSigns.add(arenaSign);
        plugin.getConfigsManager().getSignsConfigManager().saveSign(arenaSign);
    }

    public void breakSign(Player player,BlockBreakEvent event){
        Block block = event.getBlock();
        Location location = block.getLocation();

        if(!block.getType().name().contains("SIGN")){
            return;
        }

        ArenaSign arenaSign = getSignByLocation(location);
        if(arenaSign != null){
            if(!PlayerUtils.isMineChessAdmin(player)){
                event.setCancelled(true);
                return;
            }

            removeSign(arenaSign.getId());
            plugin.getConfigsManager().getSignsConfigManager().deleteSign(arenaSign.getId());
        }
    }

    public void openSign(PlayerSignOpenEvent event){
        ArenaSign arenaSign = getSignByLocation(event.getSign().getLocation());
        if(arenaSign != null){
            event.setCancelled(true);
        }
    }

    public void start(){
        new BukkitRunnable(){
            @Override
            public void run() {
                if(stop){
                    this.cancel();
                }else{
                    execute();
                }
            }
        }.runTaskTimer(plugin,0L,30L);
    }

    public void stop(){
        this.stop = true;
    }

    public void execute(){
        ArenaManager arenaManager = plugin.getArenaManager();
        ArrayList<ArenaSign> arenaSignsClone = new ArrayList<>(arenaSigns);
        boolean isMiniMessage = plugin.getConfigsManager().getMainConfigManager().isUseMiniMessage();

        for(ArenaSign arenaSign : arenaSignsClone){
            Location l = arenaSign.getLocation();
            Arena arena = arenaManager.getArenaByName(arenaSign.getArenaName());
            if(arena == null){
                continue;
            }

            GameStatus currentStatus = arena.getStatus();
            int currentPlayers = arena.getGamePlayers().size();
            if(currentStatus.equals(arenaSign.getPreviousGameStatus()) && currentPlayers == arenaSign.getPreviousNumberOfPlayers()){
                continue;
            }

            if(OtherUtils.isChunkLoaded(l)){
                Block block = l.getWorld().getBlockAt(l);
                if(!block.getType().name().contains("SIGN")){
                    removeSign(arenaSign.getId());
                    plugin.getConfigsManager().getSignsConfigManager().deleteSign(arenaSign.getId());
                    continue;
                }

                Sign sign = (Sign) block.getState();

                //SignSide side = sign.getSide(arenaSign.getSide());
                String arenaName = arena.getName();
                FileConfiguration messagesConfig = plugin.getMessagesConfig();
                List<String> signFormat = messagesConfig.getStringList("signFormat");
                String status = getSignStatus(arena,messagesConfig);
                int maxPlayers = 2;
                for(int i=0;i<signFormat.size();i++){
                    String line = signFormat.get(i).replace("%arena%",arenaName).replace("%status%",status)
                            .replace("%current_players%",currentPlayers+"")
                            .replace("%max_players%",maxPlayers+"");
                    //side.setLine(i, MessagesManager.getColoredMessage(line));
                    if(isMiniMessage){
                        MiniMessageUtils.setSignLine(sign,i,line);
                    }else{
                        sign.setLine(i, MessagesManager.getLegacyColoredMessage(line));
                    }
                }
                sign.update();

                arenaSign.setPreviousGameStatus(arena.getStatus());
                arenaSign.setPreviousNumberOfPlayers(currentPlayers);
            }
        }
    }

    public static String getSignStatus(Arena arena,FileConfiguration messagesConfig){
        return switch (arena.getStatus()) {
            case WAITING -> messagesConfig.getString("signStatusWaiting");
            case STARTING -> messagesConfig.getString("signStatusStarting");
            case PLAYING -> messagesConfig.getString("signStatusIngame");
            case ENDING -> messagesConfig.getString("signStatusFinishing");
            case DISABLED -> messagesConfig.getString("signStatusDisabled");
        };
    }

    public void clickSign(Player player, PlayerInteractEvent event){
        if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            return;
        }
        Block block = event.getClickedBlock();
        if(block == null || !block.getType().name().contains("SIGN")){
            return;
        }

        ArenaSign arenaSign = getSignByLocation(block.getLocation());
        if(arenaSign != null){
            event.setCancelled(true);

            ArenaManager arenaManager = plugin.getArenaManager();
            Arena arena = arenaManager.getArenaByName(arenaSign.getArenaName());
            if(arena != null){
                plugin.getArenaManager().joinArena(player,arena);
            }
        }
    }
}
