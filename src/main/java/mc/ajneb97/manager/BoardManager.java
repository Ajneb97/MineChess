package mc.ajneb97.manager;

import mc.ajneb97.MineChess;
import mc.ajneb97.config.model.PieceColorStructure;
import mc.ajneb97.config.model.PieceHologramsValues;
import mc.ajneb97.config.model.PieceStructure;
import mc.ajneb97.config.model.PiecesHologramsConfig;
import mc.ajneb97.model.chess.Piece;
import mc.ajneb97.utils.BlockUtils;
import mc.ajneb97.utils.ServerVersion;
import mc.ajneb97.config.MainConfigManager;
import mc.ajneb97.model.Arena;
import mc.ajneb97.model.PlayerColor;
import mc.ajneb97.model.chess.Board;
import mc.ajneb97.model.internal.PieceToUpdate;
import mc.ajneb97.utils.GameUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class BoardManager {

    private MineChess plugin;
    public static final int CELL_SIZE = 3;

    public BoardManager(MineChess plugin){
        this.plugin = plugin;
    }

    public void buildBoardFloor(Arena arena, Location location){
        Material whiteCellMaterial = arena.getBoardWhiteCellBlock();
        Material blackCellMaterial = arena.getBoardBlackCellBlock();

        Location startLocation = new Location(location.getWorld(),location.getBlockX(),location.getBlockY(),location.getBlockZ()).add(0,-1,0);
        arena.configureLocations(startLocation,CELL_SIZE);

        Location currentLocation = startLocation.clone();
        boolean buildWhite = true;
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                if(buildWhite){
                    buildCell(currentLocation.clone().add(j*CELL_SIZE,0,0),whiteCellMaterial);
                }else{
                    buildCell(currentLocation.clone().add(j*CELL_SIZE,0,0),blackCellMaterial);
                }
                buildWhite = !buildWhite;
            }
            buildWhite = !buildWhite;
            currentLocation.add(0,0,CELL_SIZE);
        }

        plugin.getConfigsManager().getArenasConfigManager().saveArena(arena);
    }

    public void removeBoardFloor(Arena arena){
        if(arena.getBoardStartLocation() == null){
            return;
        }

        Location currentLocation = arena.getBoardStartLocation().clone();
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                removeCell(currentLocation.clone().add(j*CELL_SIZE,0,0));
            }
            currentLocation.add(0,0,CELL_SIZE);
        }
    }

    private void buildCell(Location currentLocation, Material material){
        World world = currentLocation.getWorld();
        for(int i=0;i<CELL_SIZE;i++){
            for(int j=0;j<CELL_SIZE;j++){
                world.getBlockAt(currentLocation.clone().add(j,0,0)).setType(material);
            }
            currentLocation.add(0,0,1);
        }
    }

    private void removeCell(Location currentLocation){
        World world = currentLocation.getWorld();
        for(int i=0;i<CELL_SIZE;i++){
            for(int j=0;j<CELL_SIZE;j++){
                world.getBlockAt(currentLocation.clone().add(j,0,0)).setType(Material.AIR);
            }
            currentLocation.add(0,0,1);
        }
    }

    public void buildBoardPieces(Arena arena){
        Board board = arena.getBoard();
        MainConfigManager mainConfigManager = plugin.getConfigsManager().getMainConfigManager();
        for(int x=0;x<8;x++) {
            for(int y=0;y<8;y++) {
                Piece piece = board.getPiece(x,y);
                if(piece != null){
                    buildPiece(piece,x,y,arena,mainConfigManager);
                }
            }
        }
    }

    public void removeBoardPieces(Arena arena){
        Board board = arena.getBoard();

        if(arena.getBoardStartLocation() == null){
            return;
        }

        for(int x=0;x<8;x++) {
            for(int y=0;y<8;y++) {
                Piece piece = board.getPiece(x,y);
                removePiece(piece,x,y,arena);
            }
        }
    }

    // Make sure arenas are clean after server start.
    public void cleanArenas(){
        for(Arena arena : plugin.getArenaManager().getArenas()){
            removeBoardPieces(arena);
        }
    }

    public void buildPiece(Piece piece,int x,int y,Arena arena,MainConfigManager mainConfigManager){
        Location startingLocation = arena.getBoardStartLocation();
        /*
            Size 3:
            (0,0) -> (3/2,3/2)
            (1,0) -> (3/2+3,3/2+3)
         */
        double locIncreaseX = (double) CELL_SIZE/2 + CELL_SIZE*x;
        double locIncreaseZ = (double) CELL_SIZE/2 + CELL_SIZE*y;
        PieceStructure pieceStructure = mainConfigManager.getPieceStructure(piece.getType());
        Location currentLocation = startingLocation.clone().add(locIncreaseX,0,locIncreaseZ);
        World world = currentLocation.getWorld();

        buildPieceBlock(piece,pieceStructure,currentLocation.clone(),world);
        buildPieceHologram(piece,pieceStructure,currentLocation.clone(),world,mainConfigManager);
    }

    private void buildPieceBlock(Piece piece,PieceStructure pieceStructure,Location currentLocation,World world){
        List<String> structure = piece.getColor().equals(PlayerColor.BLACK) ?
                pieceStructure.getBlackPiece().getBlocks() : pieceStructure.getWhitePiece().getBlocks();
        for(int i=structure.size()-1;i>=0;i--){
            String blockStructure = structure.get(i);
            currentLocation = currentLocation.add(0,1,0);

            Block block = world.getBlockAt(currentLocation);
            if(blockStructure.startsWith("PLAYER_HEAD") && blockStructure.contains(";")){
                block.setType(Material.PLAYER_HEAD);
                BlockUtils.setHeadTextureData(block,blockStructure.split(";")[1],null);

                Rotatable rotatable = (Rotatable)block.getBlockData();
                if(piece.getColor().equals(PlayerColor.BLACK)){
                    rotatable.setRotation(BlockFace.NORTH);
                }else{
                    rotatable.setRotation(BlockFace.SOUTH);
                }
                block.setBlockData(rotatable);
                block.getState().update(true);
            }else{
                block.setType(Material.valueOf(blockStructure));
                if(block.getBlockData() instanceof Directional directional){
                    if(piece.getColor().equals(PlayerColor.BLACK)){
                        directional.setFacing(BlockFace.SOUTH);
                    }else{
                        directional.setFacing(BlockFace.NORTH);
                    }
                    block.setBlockData(directional);
                    block.getState().update(true);
                }
            }
        }
    }

    private void buildPieceHologram(Piece piece,PieceStructure pieceStructure,Location currentLocation,World world,MainConfigManager mainConfigManager){
        PiecesHologramsConfig piecesHologramsConfig = mainConfigManager.getPiecesHologramsConfig();
        if(!piecesHologramsConfig.isEnabled()){
            return;
        }

        PieceHologramsValues pieceHologramsValues = piecesHologramsConfig.getValues();
        PieceColorStructure pieceColorStructure = piece.getColor().equals(PlayerColor.BLACK) ?
                pieceStructure.getBlackPiece() : pieceStructure.getWhitePiece();
        if(pieceColorStructure.getHologramsValues() != null){
            pieceHologramsValues = pieceColorStructure.getHologramsValues();
        }

        FileConfiguration messagesConfig = plugin.getMessagesConfig();
        String text = pieceHologramsValues.getText();
        String pieceName = GameUtils.getPieceNameFromConfig(piece.getType(),messagesConfig);
        String pieceColor = GameUtils.getColorFromConfig(piece.getColor(),messagesConfig);

        currentLocation = currentLocation.add(0,pieceHologramsValues.getOffsetY(),0);

        ServerVersion serverVersion = MineChess.serverVersion;

        ArmorStand a;
        if(serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_17_R1)){
            if(serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_20_R3)){
                a = world.spawn(currentLocation,ArmorStand.class, armorStand -> {
                    setHologramProperties(armorStand,text,pieceName,pieceColor);
                });
            }else{
                try {
                    a = (ArmorStand) world.getClass().getMethod("spawn",Location.class,Class.class,org.bukkit.util.Consumer.class)
                            .invoke(world, currentLocation, ArmorStand.class, (org.bukkit.util.Consumer<ArmorStand>) armorStand -> {
                                setHologramProperties(armorStand,text,pieceName,pieceColor);
                            });
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        }else{
            a = world.spawn(currentLocation, ArmorStand.class);
            setHologramProperties(a,text,pieceName,pieceColor);
        }

        if(piece.getHologram() != null){
            piece.getHologram().remove();
        }
        piece.setHologram(a);
    }

    private void setHologramProperties(ArmorStand armorStand,String text,String pieceName,String pieceColor){
        armorStand.setGravity(false);
        armorStand.setAI(false);
        armorStand.setInvulnerable(true);
        armorStand.setInvisible(true);
        armorStand.setMarker(true);
        armorStand.setPersistent(false);

        MainConfigManager mainConfigManager = plugin.getConfigsManager().getMainConfigManager();
        text = text.replace("%piece%",pieceName).replace("%color%",pieceColor);
        if(mainConfigManager.isUseMiniMessage()){
            armorStand.customName(MiniMessage.miniMessage().deserialize(text));
        }else{
            armorStand.setCustomName(MessagesManager.getLegacyColoredMessage(text));
        }
        armorStand.setCustomNameVisible(true);

        armorStand.getPersistentDataContainer().set(new NamespacedKey(plugin, "minechess"), PersistentDataType.STRING, "piece_hologram");
    }

    public void updateCell(PieceToUpdate pieceToUpdate, Arena arena){
        MainConfigManager mainConfigManager = plugin.getConfigsManager().getMainConfigManager();
        Piece piece = arena.getBoard().getPiece(pieceToUpdate.getX(),pieceToUpdate.getY());
        removePiece(pieceToUpdate.getPiece(),pieceToUpdate.getX(),pieceToUpdate.getY(),arena);
        if(piece != null){
            buildPiece(piece,pieceToUpdate.getX(),pieceToUpdate.getY(),arena,mainConfigManager);
        }
    }

    public void removePiece(Piece piece,int x,int y,Arena arena){
        int maxHeight = 5;
        Location startingLocation = arena.getBoardStartLocation();

        double locIncreaseX = (double) CELL_SIZE/2 + CELL_SIZE*x;
        double locIncreaseZ = (double) CELL_SIZE/2 + CELL_SIZE*y;

        Location currentLocation = startingLocation.clone().add(locIncreaseX,0,locIncreaseZ);
        World world = currentLocation.getWorld();
        for(int i=maxHeight-1;i>=0;i--){
            currentLocation = currentLocation.add(0,1,0);
            world.getBlockAt(currentLocation).setType(Material.AIR);
        }

        // Hologram
        PiecesHologramsConfig piecesHologramsConfig = plugin.getConfigsManager().getMainConfigManager().getPiecesHologramsConfig();
        if(piecesHologramsConfig.isEnabled() && piece != null && piece.getHologram() != null){
            piece.getHologram().remove();
        }
    }

    public int[] getPiecePositionFromLocation(Location l,Arena arena){
        int blockX = l.getBlockX();
        int blockZ = l.getBlockZ();
        int newPointX = (blockX - arena.getBoardStartLocation().getBlockX())/CELL_SIZE;
        int newPointZ = (blockZ - arena.getBoardStartLocation().getBlockZ())/CELL_SIZE;

        if(newPointX < 0 || newPointX > 7){
            return null;
        }
        if(newPointZ < 0 || newPointZ > 7){
            return null;
        }

        return new int[]{newPointX,newPointZ};
    }

    public Location getCellLocationFromPosition(int[] position,Arena arena){
        int boardStartX = arena.getBoardStartLocation().getBlockX();
        int boardStartZ = arena.getBoardStartLocation().getBlockZ();

        int x = boardStartX + position[0] * CELL_SIZE + CELL_SIZE / 2;
        int z = boardStartZ + position[1] * CELL_SIZE + CELL_SIZE / 2;

        return new Location(arena.getBoardStartLocation().getWorld(), x, arena.getBoardStartLocation().getY(), z);
    }

    public Location getCellLocationFromPositionCentered(int[] position,Arena arena){
        Location l = getCellLocationFromPosition(position,arena);

        l.add(-1+(double)CELL_SIZE/2,0, -1+(double)CELL_SIZE/2);
        return l;
    }

    public ArrayList<Location> getCellBlockLocationsFromPosition(int[] position, Arena arena){
        ArrayList<Location> locations = new ArrayList<>();

        Location startLocation = getCellLocationFromPosition(position,arena).add(-1,0,-1);
        locations.add(startLocation);
        for(int x=0;x<CELL_SIZE;x++){
            for(int z=0;z<CELL_SIZE;z++){
                if(x == 0 && z == 0){
                    continue;
                }
                locations.add(startLocation.clone().add(x,0,z));
            }
        }

        return locations;
    }

    public PlayerColor getCellColor(int[] position){
        return (position[0] + position[1]) % 2 == 0 ? PlayerColor.WHITE : PlayerColor.BLACK;
    }
}
