package de.ellpeck.game.util;

public enum Direction{
    UP(0, 1),
    DOWN(0, -1),
    LEFT(-1, 0),
    RIGHT(1, 0),
    NONE(0, 0);

    public final int offsetX;
    public final int offsetY;

    public static final Direction[] DIRECTIONS = new Direction[]{UP, DOWN, LEFT, RIGHT};

    Direction(int offsetX, int offsetY){
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
}

