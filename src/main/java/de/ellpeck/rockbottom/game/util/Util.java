package de.ellpeck.rockbottom.game.util;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.item.IItemRenderer;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.gui.Gui;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.util.Log;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Util{

    public static final Random RANDOM = RockBottomAPI.RANDOM;

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

    public static Entity createEntity(String name, IWorld world){
        Class<? extends Entity> entityClass = RockBottomAPI.ENTITY_REGISTRY.get(name);

        try{
            return entityClass.getConstructor(IWorld.class).newInstance(world);
        }
        catch(Exception e){
            Log.error("Couldn't initialize entity with name "+name, e);
            return null;
        }
    }
}
