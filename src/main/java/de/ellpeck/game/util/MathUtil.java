package de.ellpeck.game.util;

import de.ellpeck.game.Constants;

public final class MathUtil{

    public static int floor(double value){
        int i = (int)value;
        return value < (double)i ? i-1 : i;
    }

    public static int ceil(double value){
        int i = (int)value;
        return value > (double)i ? i+1 : i;
    }

    public static int toGridPos(double worldPos){
        return floor(worldPos/(double)Constants.CHUNK_SIZE);
    }

    public static int toGridAlignedWorldPos(double worldPos){
        return toGridPos(worldPos)*Constants.CHUNK_SIZE;
    }

    public static int toWorldPos(int gridPos){
        return gridPos*Constants.CHUNK_SIZE;
    }
}
