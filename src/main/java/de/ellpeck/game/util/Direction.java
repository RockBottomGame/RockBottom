package de.ellpeck.game.util;

public enum Direction{
    NONE(0, 0),

    UP(0, 1),
    DOWN(0, -1),
    LEFT(-1, 0),
    RIGHT(1, 0),

    LEFT_UP(-1, 1),
    LEFT_DOWN(-1, -1),
    RIGHT_UP(1, 1),
    RIGHT_DOWN(1, -1);

    public final int x;
    public final int y;

    public static final Direction[] ADJACENT_DIRECTIONS = new Direction[]{UP, RIGHT, DOWN, LEFT};
    public static final Direction[] DIAGONAL_DIRECTIONS = new Direction[]{LEFT_UP, RIGHT_UP, RIGHT_DOWN, LEFT_DOWN};
    public static final Direction[] ALL_DIRECTIONS = new Direction[]{LEFT_UP, UP, RIGHT_UP, RIGHT, RIGHT_DOWN, DOWN, LEFT_DOWN, LEFT};

    Direction(int x, int y){
        this.x = x;
        this.y = y;
    }
}

