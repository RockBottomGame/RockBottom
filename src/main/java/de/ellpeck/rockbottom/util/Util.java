package de.ellpeck.rockbottom.util;

import de.ellpeck.rockbottom.Constants;
import org.newdawn.slick.Color;

import java.io.File;
import java.util.Random;

public final class Util{

    public static final Random RANDOM = new Random();

    public static double distance(double x1, double y1, double x2, double y2){
        return Math.sqrt(distanceSq(x1, y1, x2, y2));
    }

    public static double distanceSq(double x1, double y1, double x2, double y2){
        double dx = x2-x1;
        double dy = y2-y1;
        return (dx*dx)+(dy*dy);
    }

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

    public static void deleteFolder(File file) throws Exception{
        if(file.isDirectory()){
            for(File child : file.listFiles()){
                deleteFolder(child);
            }
        }
        file.delete();
    }

    public static Color randomColor(Random rand){
        return new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
    }

    public static String trimString(String s, int length){
        if(s.length() <= length){
            return s;
        }
        else{
            return s.substring(0, length);
        }
    }
}
