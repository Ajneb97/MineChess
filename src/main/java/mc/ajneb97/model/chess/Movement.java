package mc.ajneb97.model.chess;

public class Movement {
    private int x;
    private int y;
    private MovementType type;
    private boolean putsInCheck;

    public Movement(int x, int y) {
        this.x = x;
        this.y = y;
        this.type = MovementType.NORMAL;
    }

    public Movement(int x, int y, MovementType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public MovementType getType() {
        return type;
    }

    public void setType(MovementType type) {
        this.type = type;
    }

    public boolean isPutsInCheck() {
        return putsInCheck;
    }

    public void setPutsInCheck(boolean putsInCheck) {
        this.putsInCheck = putsInCheck;
    }
}
