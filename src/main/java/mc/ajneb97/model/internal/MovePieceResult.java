package mc.ajneb97.model.internal;

import mc.ajneb97.model.chess.PieceType;

import java.util.ArrayList;

public class MovePieceResult {
    private ArrayList<PieceToUpdate> piecesToUpdate;
    private PieceType capturedPieceType;

    public MovePieceResult(ArrayList<PieceToUpdate> piecesToUpdate, PieceType capturedPieceType) {
        this.piecesToUpdate = piecesToUpdate;
        this.capturedPieceType = capturedPieceType;
    }

    public PieceType getCapturedPieceType() {
        return capturedPieceType;
    }

    public ArrayList<PieceToUpdate> getPiecesToUpdate() {
        return piecesToUpdate;
    }

}
