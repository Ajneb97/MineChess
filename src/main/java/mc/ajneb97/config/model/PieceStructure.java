package mc.ajneb97.config.model;

import mc.ajneb97.model.chess.PieceType;

public class PieceStructure {
    private PieceType pieceType;
    private int points;
    private PieceColorStructure blackPiece;
    private PieceColorStructure whitePiece;

    public PieceStructure(PieceType pieceType, int points, PieceColorStructure blackPiece, PieceColorStructure whitePiece) {
        this.pieceType = pieceType;
        this.points = points;
        this.blackPiece = blackPiece;
        this.whitePiece = whitePiece;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    public void setPieceType(PieceType pieceType) {
        this.pieceType = pieceType;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public PieceColorStructure getBlackPiece() {
        return blackPiece;
    }

    public void setBlackPiece(PieceColorStructure blackPiece) {
        this.blackPiece = blackPiece;
    }

    public PieceColorStructure getWhitePiece() {
        return whitePiece;
    }

    public void setWhitePiece(PieceColorStructure whitePiece) {
        this.whitePiece = whitePiece;
    }
}
