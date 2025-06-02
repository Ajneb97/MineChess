package mc.ajneb97.config.model;

public class PieceInteractions {

    private PieceInteraction seeCell;
    private PieceInteraction selectedPiece;
    private PieceInteraction validMovements;
    private PieceInteraction seeValidMovementCell;

    public PieceInteractions(PieceInteraction seeCell, PieceInteraction selectedPiece, PieceInteraction validMovements,
                             PieceInteraction seeValidMovementCell) {
        this.seeCell = seeCell;
        this.selectedPiece = selectedPiece;
        this.validMovements = validMovements;
        this.seeValidMovementCell = seeValidMovementCell;
    }

    public PieceInteraction getSeeCell() {
        return seeCell;
    }

    public void setSeeCell(PieceInteraction seeCell) {
        this.seeCell = seeCell;
    }

    public PieceInteraction getSelectedPiece() {
        return selectedPiece;
    }

    public void setSelectedPiece(PieceInteraction selectedPiece) {
        this.selectedPiece = selectedPiece;
    }

    public PieceInteraction getValidMovements() {
        return validMovements;
    }

    public void setValidMovements(PieceInteraction validMovements) {
        this.validMovements = validMovements;
    }

    public PieceInteraction getSeeValidMovementCell() {
        return seeValidMovementCell;
    }

    public void setSeeValidMovementCell(PieceInteraction seeValidMovementCell) {
        this.seeValidMovementCell = seeValidMovementCell;
    }
}
