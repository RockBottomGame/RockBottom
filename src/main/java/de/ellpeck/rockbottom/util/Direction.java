package de.ellpeck.rockbottom.util;

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

    public static final Direction[] ADJACENT = new Direction[]{UP, RIGHT, DOWN, LEFT};
    public static final Direction[] ADJACENT_INCLUDING_NONE = new Direction[]{NONE, UP, RIGHT, DOWN, LEFT};
    public static final Direction[] SURROUNDING = new Direction[]{LEFT_UP, UP, RIGHT_UP, RIGHT, RIGHT_DOWN, DOWN, LEFT_DOWN, LEFT};
    public static final Direction[] SURROUNDING_INCLUDING_NONE = new Direction[]{NONE, LEFT_UP, UP, RIGHT_UP, RIGHT, RIGHT_DOWN, DOWN, LEFT_DOWN, LEFT};
    public final int x;
    public final int y;

    Direction(int x, int y){
        this.x = x;
        this.y = y;
    }
}

