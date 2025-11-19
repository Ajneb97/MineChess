package mc.ajneb97.model.chess;

import mc.ajneb97.model.internal.MovePieceResult;
import mc.ajneb97.model.PlayerColor;
import mc.ajneb97.model.internal.CoordinateMovement;
import mc.ajneb97.model.internal.CoordinatePiece;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private Piece[][] board;

    public Board(){
        this.board = new Piece[8][8];
    }

    public Piece[][] getBoard() {
        return board;
    }

    public void setBoard(Piece[][] board) {
        this.board = board;
    }

    public void createInitialBoard(){
        this.board = new Piece[8][8];

        // Pawns
        for (int i = 0; i < 8; i++) {
            board[i][1] = new Piece(PieceType.PAWN, PlayerColor.BLACK);
            board[i][6] = new Piece(PieceType.PAWN, PlayerColor.WHITE);
        }

        // Blacks
        board[0][0] = new Piece(PieceType.ROOK, PlayerColor.BLACK);
        board[7][0] = new Piece(PieceType.ROOK, PlayerColor.BLACK);
        board[1][0] = new Piece(PieceType.KNIGHT, PlayerColor.BLACK);
        board[6][0] = new Piece(PieceType.KNIGHT, PlayerColor.BLACK);
        board[2][0] = new Piece(PieceType.BISHOP, PlayerColor.BLACK);
        board[5][0] = new Piece(PieceType.BISHOP, PlayerColor.BLACK);
        board[3][0] = new Piece(PieceType.QUEEN, PlayerColor.BLACK);
        board[4][0] = new Piece(PieceType.KING, PlayerColor.BLACK);

        // Whites
        board[0][7] = new Piece(PieceType.ROOK, PlayerColor.WHITE);
        board[7][7] = new Piece(PieceType.ROOK, PlayerColor.WHITE);
        board[1][7] = new Piece(PieceType.KNIGHT, PlayerColor.WHITE);
        board[6][7] = new Piece(PieceType.KNIGHT, PlayerColor.WHITE);
        board[2][7] = new Piece(PieceType.BISHOP, PlayerColor.WHITE);
        board[5][7] = new Piece(PieceType.BISHOP, PlayerColor.WHITE);
        board[3][7] = new Piece(PieceType.QUEEN, PlayerColor.WHITE);
        board[4][7] = new Piece(PieceType.KING, PlayerColor.WHITE);
    }

    public Piece getPiece(int x,int y){
        return board[x][y];
    }

    // Movement already validated
    public MovePieceResult move(int xStart, int yStart, Movement movement){
        int xEnd = movement.getX();
        int yEnd = movement.getY();

        ArrayList<CoordinatePiece> piecesToUpdate = new ArrayList<>();

        int[] capturedPiecePosition = null;
        PieceType capturedPieceType = null;
        Piece piece = board[xStart][yStart];
        piece.setHasMoved(true);

        // Update starting position
        piecesToUpdate.add(new CoordinatePiece(xStart,yStart,board[xStart][yStart]));
        board[xStart][yStart] = null;

        if(board[xEnd][yEnd] != null && !movement.getType().name().startsWith("CASTLING")){
            capturedPiecePosition = new int[]{xEnd,yEnd};
            capturedPieceType = board[xEnd][yEnd].getType();
        }

        if(!movement.getType().name().startsWith("CASTLING")){
            // Update end position
            piecesToUpdate.add(new CoordinatePiece(xEnd,yEnd,board[xEnd][yEnd]));
            board[xEnd][yEnd] = piece;
        }

        /* En Passant
            1. If movement is PAWN_TWO, check if there is an enemy pawn besides. If so,
               set the pawn status to en passant.
            2. If movement is EN_PASSANT, set null on the corresponding captured pawn.
         */
        if(movement.getType().equals(MovementType.PAWN_TWO)){
            if(isInsideBoard(xEnd+1,yEnd) && !isEmptyPosition(xEnd+1,yEnd) && !isSameColor(xEnd+1,yEnd,piece.getColor())
                    && board[xEnd+1][yEnd].getType().equals(PieceType.PAWN)){
                piece.setEnPassant(true);
            }
            if(isInsideBoard(xEnd-1,yEnd) && !isEmptyPosition(xEnd-1,yEnd) && !isSameColor(xEnd-1,yEnd,piece.getColor())
                    && board[xEnd-1][yEnd].getType().equals(PieceType.PAWN)){
                piece.setEnPassant(true);
            }
        }else if(movement.getType().equals(MovementType.EN_PASSANT)){
            int direction = piece.getColor() == PlayerColor.WHITE ? 1 : -1;
            capturedPiecePosition = new int[]{xEnd,yEnd+direction};
            capturedPieceType = board[xEnd][yEnd+direction].getType();

            // Update en passant end position
            piecesToUpdate.add(new CoordinatePiece(xEnd,yEnd+direction,board[xEnd][yEnd+direction]));
            board[xEnd][yEnd+direction] = null;
        }

        // Castling
        if(movement.getType().equals(MovementType.CASTLING_SHORT)){
            // Update positions
            piecesToUpdate.add(new CoordinatePiece(xEnd,yEnd,board[xEnd][yEnd]));
            piecesToUpdate.add(new CoordinatePiece(6,yStart,board[6][yStart]));
            piecesToUpdate.add(new CoordinatePiece(5,yStart,board[5][yStart]));

            board[6][yStart] = piece;
            board[5][yStart] = board[xEnd][yEnd];
            board[xEnd][yEnd] = null;
        }else if(movement.getType().equals(MovementType.CASTLING_LONG)){
            // Update positions
            piecesToUpdate.add(new CoordinatePiece(xEnd,yEnd,board[xEnd][yEnd]));
            piecesToUpdate.add(new CoordinatePiece(2,yStart,board[2][yStart]));
            piecesToUpdate.add(new CoordinatePiece(3,yStart,board[3][yStart]));

            board[2][yStart] = piece;
            board[3][yStart] = board[xEnd][yEnd];
            board[xEnd][yEnd] = null;
        }

        // Reset pawns on En Passant for opponent
        PlayerColor opponentColor = piece.getColor().equals(PlayerColor.WHITE) ? PlayerColor.BLACK : PlayerColor.WHITE;
        resetEnPassant(opponentColor);

        return new MovePieceResult(piecesToUpdate,capturedPieceType);
    }

    public CoordinatePiece promotePawn(int x, int y, PieceType pieceType, PlayerColor playerColor){
        Piece newPiece = new Piece(pieceType,playerColor);

        CoordinatePiece pieceToUpdate = new CoordinatePiece(x,y,board[x][y]);

        newPiece.setHasMoved(true);
        board[x][y] = newPiece;

        return pieceToUpdate;
    }

    public boolean verifyInCheck(PlayerColor playerColor){
        // Must verify the pieces of the opponent color and see if one of them can capture the playerColor King.
        int[] kingPosition = getKingPosition(playerColor);
        boolean kingInCheck = false;

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                Piece opponentPiece = board[i][j];
                if(opponentPiece == null || opponentPiece.getColor().equals(playerColor)){
                    continue;
                }

                // Check movement for opponent piece
                ArrayList<Movement> movementsOpponentPiece = getPossibleMovements(i,j,false);
                for(Movement m : movementsOpponentPiece){
                    if(m.getX() == kingPosition[0] && m.getY() == kingPosition[1]){
                        // Movement can capture King.
                        kingInCheck = true;
                        break;
                    }
                }

                if(kingInCheck) break;
            }

            if(kingInCheck) break;
        }

        return kingInCheck;
    }

    public ArrayList<Movement> getPossibleMovements(int x,int y,boolean checkKing){
        Piece piece = board[x][y];
        ArrayList<Movement> movements = null;
        switch(piece.getType()){
            case PAWN -> movements = getPossibleMovementsPawn(piece,x,y);
            case ROOK -> movements = getPossibleMovementsRook(piece,x,y);
            case KNIGHT -> movements = getPossibleMovementsKnight(piece,x,y);
            case BISHOP -> movements = getPossibleMovementsBishop(piece,x,y);
            case QUEEN -> movements = getPossibleMovementsQueen(piece,x,y);
            case KING -> movements = getPossibleMovementsKing(piece,x,y);
        }


        // 1. For each movement, simulate the board after that movement.
        // 2. For each opponent piece, get the movements.
        // 3. Check if the King can be captured after that movement.
        if(checkKing){
            Piece[][] boardBackup = deepCopyBoard(board);
            PlayerColor pieceColor = piece.getColor();

            for(Movement movement : movements){
                move(x,y,movement);

                boolean kingInCheck = verifyInCheck(pieceColor); // Verifies if pieceColor is in check.
                if (kingInCheck) {
                    movement.setPutsInCheck(true);
                }

                board = deepCopyBoard(boardBackup);
            }
        }

        return movements;
    }


    private ArrayList<Movement> getPossibleMovementsRook(Piece piece,int x,int y){
        /*
            0 0 x 0 0
            0 0 x 0 0
            x x Z x x
            0 0 x 0 0
            0 0 x 0 0
         */
        ArrayList<Movement> movements = new ArrayList<>();

        // Left
        for(int i=1;i<=8;i++){
            if(addMovementAndMustStop(movements,x-i,y,piece)) break;
        }

        // Right
        for(int i=1;i<=8;i++){
            if(addMovementAndMustStop(movements,x+i,y,piece)) break;
        }

        // Up
        for(int i=1;i<=8;i++){
            if(addMovementAndMustStop(movements,x,y-i,piece)) break;
        }

        // Down
        for(int i=1;i<=8;i++){
            if(addMovementAndMustStop(movements,x,y+i,piece)) break;
        }

        return movements;
    }

    private ArrayList<Movement> getPossibleMovementsBishop(Piece piece,int x,int y){
        /*
            x 0 0 0 x
            0 x 0 x 0
            0 0 Z 0 0
            0 x 0 x 0
            x 0 0 0 x
         */
        ArrayList<Movement> movements = new ArrayList<>();

        // Up Left
        for(int i=1;i<=8;i++){
            if(addMovementAndMustStop(movements,x-i,y-i,piece)) break;
        }

        // Up Right
        for(int i=1;i<=8;i++){
            if(addMovementAndMustStop(movements,x+i,y-i,piece)) break;
        }

        // Down Left
        for(int i=1;i<=8;i++){
            if(addMovementAndMustStop(movements,x-i,y+i,piece)) break;
        }

        // Down Right
        for(int i=1;i<=8;i++){
            if(addMovementAndMustStop(movements,x+i,y+i,piece)) break;
        }

        return movements;
    }

    private ArrayList<Movement> getPossibleMovementsKnight(Piece piece,int x,int y){
        /*
            0 x 0 x 0
            x 0 0 0 x
            0 0 Z 0 0
            x 0 0 0 x
            0 x 0 x 0
         */
        ArrayList<Movement> movements = new ArrayList<>();

        addMovementAndMustStop(movements,x-2,y-1,piece);
        addMovementAndMustStop(movements,x-1,y-2,piece);
        addMovementAndMustStop(movements,x+1,y-2,piece);
        addMovementAndMustStop(movements,x+2,y-1,piece);

        addMovementAndMustStop(movements,x+2,y+1,piece);
        addMovementAndMustStop(movements,x+1,y+2,piece);
        addMovementAndMustStop(movements,x-1,y+2,piece);
        addMovementAndMustStop(movements,x-2,y+1,piece);

        return movements;
    }

    private ArrayList<Movement> getPossibleMovementsKing(Piece piece,int x,int y){
        /*
            0 0 0 0 0
            0 x x x 0
            0 x Z x 0
            0 x x x 0
            0 0 0 0 0
         */
        ArrayList<Movement> movements = new ArrayList<>();

        // Normal
        addMovementAndMustStop(movements,x-1,y-1,piece);
        addMovementAndMustStop(movements,x,y-1,piece);
        addMovementAndMustStop(movements,x+1,y-1,piece);
        addMovementAndMustStop(movements,x+1,y,piece);
        addMovementAndMustStop(movements,x+1,y+1,piece);
        addMovementAndMustStop(movements,x,y+1,piece);
        addMovementAndMustStop(movements,x-1,y+1,piece);
        addMovementAndMustStop(movements,x-1,y,piece);

        // Castling
        if(!piece.isHasMoved()){
            // Short
            Piece rookShort = board[7][y];
            if(rookShort != null && !rookShort.isHasMoved()){
                boolean pieceBetween = false;
                for(int i=x+1;i<7;i++){
                    if(board[i][y] != null){
                        // Piece between
                        pieceBetween = true;
                        break;
                    }
                }
                if(!pieceBetween){
                    movements.add(new Movement(7,y,MovementType.CASTLING_SHORT));
                }
            }

            // Long
            Piece rookLong = board[0][y];
            if(rookLong != null && !rookLong.isHasMoved()){
                boolean pieceBetween = false;
                for(int i=x-1;i>0;i--){
                    if(board[i][y] != null){
                        // Piece between
                        pieceBetween = true;
                        break;
                    }
                }
                if(!pieceBetween){
                    movements.add(new Movement(0,y,MovementType.CASTLING_LONG));
                }
            }
        }

        return movements;
    }

    private ArrayList<Movement> getPossibleMovementsQueen(Piece piece,int x,int y){
        /*
            x 0 x 0 x
            0 x x x 0
            x x Z x x
            0 x x x 0
            x 0 x 0 x
         */
        ArrayList<Movement> movements = new ArrayList<>();

        movements.addAll(getPossibleMovementsBishop(piece,x,y));
        movements.addAll(getPossibleMovementsRook(piece,x,y));

        return movements;
    }

    private ArrayList<Movement> getPossibleMovementsPawn(Piece piece,int x,int y){
        /*
            Blacks       Whites
            0 0 Z 0 0    0 0 0 0 0
            0 0 x 0 0    0 0 x 0 0
            0 0 x 0 0    0 0 x 0 0
            0 0 0 0 0    0 0 Z 0 0
         */
        ArrayList<Movement> movements = new ArrayList<>();
        int direction = piece.getColor() == PlayerColor.WHITE ? -1 : 1;

        // Normal
        int newY = y+direction;
        if(isInsideBoard(x,newY) && isEmptyPosition(x,newY)){
            if((piece.getColor().equals(PlayerColor.WHITE) && newY == 0) || (piece.getColor().equals(PlayerColor.BLACK) && newY == 7)){
                // Promotion
                movements.add(new Movement(x,newY,MovementType.PROMOTION_NORMAL));
            }else{
                movements.add(new Movement(x,newY));
            }

            if(!piece.isHasMoved() && isInsideBoard(x,newY+direction) && isEmptyPosition(x,newY+direction)){
                movements.add(new Movement(x,newY+direction,MovementType.PAWN_TWO));
            }
        }

        // Capture Left
        int newX = x-1;
        newY = y+direction;
        if(isInsideBoard(newX,newY) && !isEmptyPosition(newX,newY) && !isSameColor(newX,newY,piece.getColor())){
            if((piece.getColor().equals(PlayerColor.WHITE) && newY == 0) || (piece.getColor().equals(PlayerColor.BLACK) && newY == 7)){
                movements.add(new Movement(newX,newY,MovementType.PROMOTION_CAPTURE));
            }else{
                movements.add(new Movement(newX,newY,MovementType.CAPTURE));
            }

        }

        // Capture Right
        newX = x+1;
        newY = y+direction;
        if(isInsideBoard(newX,newY) && !isEmptyPosition(newX,newY) && !isSameColor(newX,newY,piece.getColor())){
            if((piece.getColor().equals(PlayerColor.WHITE) && newY == 0) || (piece.getColor().equals(PlayerColor.BLACK) && newY == 7)){
                movements.add(new Movement(newX,newY,MovementType.PROMOTION_CAPTURE));
            }else{
                movements.add(new Movement(newX,newY,MovementType.CAPTURE));
            }
        }

        // En Passant
        // Check if there is an enemy pawn besides with the en passant status.
        // If so, add the corresponding cell movement.
        if(isInsideBoard(x+1,y) && !isEmptyPosition(x+1,y) && !isSameColor(x+1,y,piece.getColor()) && board[x+1][y].isEnPassant()){
            movements.add(new Movement(x+1,y+direction,MovementType.EN_PASSANT));
        }
        if(isInsideBoard(x-1,y) && !isEmptyPosition(x-1,y) && !isSameColor(x-1,y,piece.getColor()) && board[x-1][y].isEnPassant()){
            movements.add(new Movement(x-1,y+direction,MovementType.EN_PASSANT));
        }

        return movements;
    }

    private boolean isInsideBoard(int x,int y){
        return x >= 0 && y >= 0 && x < board.length && y < board.length;
    }

    private boolean isEmptyPosition(int x,int y){
        return board[x][y] == null;
    }

    private boolean isSameColor(int x,int y,PlayerColor color){
        Piece piece = board[x][y];
        if(piece != null){
            return piece.getColor().equals(color);
        }
        return false;
    }

    private boolean addMovementAndMustStop(ArrayList<Movement> movements,int newX,int newY,Piece piece){
        if(isInsideBoard(newX,newY)){
            if(isEmptyPosition(newX,newY)){
                movements.add(new Movement(newX,newY));
                return false;
            }else if(isSameColor(newX,newY,piece.getColor())){
                return true;
            }else{
                movements.add(new Movement(newX,newY,MovementType.CAPTURE));
                return true;
            }
        }
        return true;
    }

    private void resetEnPassant(PlayerColor color) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                Piece piece = board[i][j];
                if (piece != null && piece.getType().equals(PieceType.PAWN) && piece.getColor().equals(color)) {
                    piece.setEnPassant(false);
                }
            }
        }
    }

    private int[] getKingPosition(PlayerColor playerColor){
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                Piece piece = board[i][j];
                if(piece != null && piece.getType().equals(PieceType.KING) && piece.getColor().equals(playerColor)){
                    return new int[]{i,j};
                }
            }
        }
        return null;
    }

    public Piece[][] deepCopyBoard(Piece[][] originalBoard) {
        Piece[][] copy = new Piece[originalBoard.length][originalBoard[0].length];
        for (int i = 0; i < originalBoard.length; i++) {
            for (int j = 0; j < originalBoard[i].length; j++) {
                if(originalBoard[i][j] == null){
                    copy[i][j] = null;
                }else{
                    copy[i][j] = originalBoard[i][j].clone();
                }
            }
        }
        return copy;
    }

    public ArrayList<CoordinateMovement> getAllPossibleMovements(PlayerColor playerColor){
        ArrayList<CoordinateMovement> movements = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                Piece piece = board[i][j];
                if (piece == null || !piece.getColor().equals(playerColor)) {
                    continue;
                }

                ArrayList<Movement> movementsPiece = getPossibleMovements(i, j, true);
                for(Movement m : movementsPiece){
                    if(m.isPutsInCheck()){
                        continue;
                    }
                    movements.add(new CoordinateMovement(i,j,m));
                }
            }
        }
        return movements;
    }

    public List<CoordinatePiece> getPieces(PlayerColor playerColor){
        List<CoordinatePiece> pieces = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                Piece piece = board[i][j];
                if(piece != null && piece.getColor().equals(playerColor)){
                    pieces.add(new CoordinatePiece(i,j,piece));
                }
            }
        }
        return pieces;
    }

    public boolean isInsufficientMaterial(){
        List<CoordinatePiece> piecesWhite = getPieces(PlayerColor.WHITE);
        List<CoordinatePiece> piecesBlack = getPieces(PlayerColor.BLACK);

        int total = piecesWhite.size() + piecesBlack.size();

        if (total > 4) return false;

        // King against king
        if (piecesWhite.size() == 1 && piecesBlack.size() == 1) {
            return true;
        }

        // King against king+bishop | King against king+knight
        if (isKingAndMinorVsKing(piecesWhite, piecesBlack)) return true;
        if (isKingAndMinorVsKing(piecesBlack, piecesWhite)) return true;

        // King+bishop against king+bishop (bishop same color)
        return isKingBishopVsKingBishopSameColor(piecesWhite, piecesBlack);
    }

    private boolean isKingAndMinorVsKing(List<CoordinatePiece> a, List<CoordinatePiece> b) {
        if (a.size() == 2 && b.size() == 1) {
            for (CoordinatePiece p : a) {
                if (p.getPiece().getType() == PieceType.BISHOP || p.getPiece().getType() == PieceType.KNIGHT) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isKingBishopVsKingBishopSameColor(List<CoordinatePiece> a, List<CoordinatePiece> b) {
        if (a.size() == 2 && b.size() == 2) {
            CoordinatePiece aBishop = null;
            CoordinatePiece bBishop = null;

            for (CoordinatePiece p : a) {
                if (p.getPiece().getType() == PieceType.BISHOP) aBishop = p;
            }
            for (CoordinatePiece p : b) {
                if (p.getPiece().getType() == PieceType.BISHOP) bBishop = p;
            }

            if (aBishop != null && bBishop != null) {
                return isOnLightSquare(aBishop.getX(),aBishop.getY()) == isOnLightSquare(bBishop.getX(), bBishop.getY());
            }
        }
        return false;
    }

    public boolean isOnLightSquare(int x, int y) {
        return (x + y) % 2 == 0;
    }

    public void showBoardStatus(CommandSender sender){
        sender.sendMessage("Board Status:");
        for (int i = 0; i < board.length; i++) {
            StringBuilder row = new StringBuilder();
            for (int j = 0; j < board[i].length; j++) {
                Piece piece = board[i][j];
                if (piece == null) {
                    row.append("X ");
                } else {
                    row.append(piece.getType().toString().charAt(0)).append(" ");
                }
            }
            sender.sendMessage(row.toString().trim());
        }
    }
}
