package mc.ajneb97.model.internal;

import mc.ajneb97.model.chess.Movement;

public class CoordinateMovement {
    private int fromX;
    private int fromY;
    private Movement movement;

    public CoordinateMovement(int fromX, int fromY, Movement movement) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.movement = movement;
    }

    public int getFromX() {
        return fromX;
    }

    public void setFromX(int fromX) {
        this.fromX = fromX;
    }

    public int getFromY() {
        return fromY;
    }

    public void setFromY(int fromY) {
        this.fromY = fromY;
    }

    public Movement getMovement() {
        return movement;
    }

    public void setMovement(Movement movement) {
        this.movement = movement;
    }
}
