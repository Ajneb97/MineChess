package mc.ajneb97.config.model;

import java.util.List;

public class GameActionsGame {
    private List<String> selectPiece;
    private List<String> deselectPiece;
    private List<String> changeTurn;
    private List<String> movePiece;
    private List<String> capturePiece;
    private List<String> capturePieceEnPassant;
    private List<String> kingInCheck;
    private List<String> castling;
    private List<String> promotion;

    public GameActionsGame(List<String> selectPiece, List<String> deselectPiece, List<String> changeTurn, List<String> movePiece,
                           List<String> capturePiece, List<String> capturePieceEnPassant, List<String> kingInCheck,
                           List<String> castling, List<String> promotion) {
        this.selectPiece = selectPiece;
        this.deselectPiece = deselectPiece;
        this.changeTurn = changeTurn;
        this.movePiece = movePiece;
        this.capturePiece = capturePiece;
        this.capturePieceEnPassant = capturePieceEnPassant;
        this.kingInCheck = kingInCheck;
        this.castling = castling;
        this.promotion = promotion;
    }

    public List<String> getSelectPiece() {
        return selectPiece;
    }

    public List<String> getDeselectPiece() {
        return deselectPiece;
    }

    public List<String> getChangeTurn() {
        return changeTurn;
    }

    public List<String> getMovePiece() {
        return movePiece;
    }

    public List<String> getCapturePiece() {
        return capturePiece;
    }

    public List<String> getCapturePieceEnPassant() {
        return capturePieceEnPassant;
    }

    public List<String> getKingInCheck() {
        return kingInCheck;
    }

    public List<String> getCastling() {
        return castling;
    }

    public List<String> getPromotion() {
        return promotion;
    }
}
