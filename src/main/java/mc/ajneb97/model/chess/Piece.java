package mc.ajneb97.model.chess;

import mc.ajneb97.model.PlayerColor;
import org.bukkit.entity.ArmorStand;

public class Piece {
    private PieceType type;
    private PlayerColor color;
    private boolean hasMoved;
    private boolean isEnPassant;

    // Hologram
    private ArmorStand hologram;

    public Piece(PieceType type, PlayerColor color) {
        this.type = type;
        this.color = color;
        this.hasMoved = false;
        this.isEnPassant = false;
    }

    public PieceType getType() {
        return type;
    }

    public void setType(PieceType type) {
        this.type = type;
    }

    public PlayerColor getColor() {
        return color;
    }

    public void setColor(PlayerColor color) {
        this.color = color;
    }

    public boolean isHasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public boolean isEnPassant() {
        return isEnPassant;
    }

    public void setEnPassant(boolean enPassant) {
        isEnPassant = enPassant;
    }

    public ArmorStand getHologram() {
        return hologram;
    }

    public void setHologram(ArmorStand hologram) {
        this.hologram = hologram;
    }

    public Piece clone(){
        Piece piece = new Piece(type,color);
        piece.setHasMoved(hasMoved);piece.setEnPassant(isEnPassant);
        piece.setHologram(hologram);
        return piece;
    }
}
