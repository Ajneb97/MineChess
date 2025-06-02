package mc.ajneb97.manager.inventory;

import mc.ajneb97.model.items.CommonItem;

public class CustomItemsInventory {

    private CommonItem pieceRookBlack;
    private CommonItem pieceRookWhite;
    private CommonItem pieceBishopBlack;
    private CommonItem pieceBishopWhite;
    private CommonItem pieceKnightBlack;
    private CommonItem pieceKnightWhite;
    private CommonItem pieceQueenBlack;
    private CommonItem pieceQueenWhite;

    public CustomItemsInventory(CommonItem pieceRookBlack, CommonItem pieceRookWhite, CommonItem pieceBishopBlack,
                                CommonItem pieceBishopWhite, CommonItem pieceKnightBlack, CommonItem pieceKnightWhite,
                                CommonItem pieceQueenBlack, CommonItem pieceQueenWhite) {
        this.pieceRookBlack = pieceRookBlack;
        this.pieceRookWhite = pieceRookWhite;
        this.pieceBishopBlack = pieceBishopBlack;
        this.pieceBishopWhite = pieceBishopWhite;
        this.pieceKnightBlack = pieceKnightBlack;
        this.pieceKnightWhite = pieceKnightWhite;
        this.pieceQueenBlack = pieceQueenBlack;
        this.pieceQueenWhite = pieceQueenWhite;
    }


    public CommonItem getPieceRookBlack() {
        return pieceRookBlack;
    }

    public void setPieceRookBlack(CommonItem pieceRookBlack) {
        this.pieceRookBlack = pieceRookBlack;
    }

    public CommonItem getPieceRookWhite() {
        return pieceRookWhite;
    }

    public void setPieceRookWhite(CommonItem pieceRookWhite) {
        this.pieceRookWhite = pieceRookWhite;
    }

    public CommonItem getPieceBishopBlack() {
        return pieceBishopBlack;
    }

    public void setPieceBishopBlack(CommonItem pieceBishopBlack) {
        this.pieceBishopBlack = pieceBishopBlack;
    }

    public CommonItem getPieceBishopWhite() {
        return pieceBishopWhite;
    }

    public void setPieceBishopWhite(CommonItem pieceBishopWhite) {
        this.pieceBishopWhite = pieceBishopWhite;
    }

    public CommonItem getPieceKnightBlack() {
        return pieceKnightBlack;
    }

    public void setPieceKnightBlack(CommonItem pieceKnightBlack) {
        this.pieceKnightBlack = pieceKnightBlack;
    }

    public CommonItem getPieceKnightWhite() {
        return pieceKnightWhite;
    }

    public void setPieceKnightWhite(CommonItem pieceKnightWhite) {
        this.pieceKnightWhite = pieceKnightWhite;
    }

    public CommonItem getPieceQueenBlack() {
        return pieceQueenBlack;
    }

    public void setPieceQueenBlack(CommonItem pieceQueenBlack) {
        this.pieceQueenBlack = pieceQueenBlack;
    }

    public CommonItem getPieceQueenWhite() {
        return pieceQueenWhite;
    }

    public void setPieceQueenWhite(CommonItem pieceQueenWhite) {
        this.pieceQueenWhite = pieceQueenWhite;
    }
}
