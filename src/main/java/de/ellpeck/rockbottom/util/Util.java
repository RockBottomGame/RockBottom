package de.ellpeck.rockbottom.util;

import de.ellpeck.rockbottom.Constants;
import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.gui.Gui;
import de.ellpeck.rockbottom.item.Item;
import de.ellpeck.rockbottom.item.ItemInstance;
import de.ellpeck.rockbottom.render.item.IItemRenderer;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
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

    public static void setClipboard(String s){
        try{
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(s), null);
        }
        catch(Exception ignored){
        }
    }

    public static String getClipboard(){
        try{
            return (String)Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        }
        catch(Exception ignored){
            return "";
        }
    }

    public static void describeItem(RockBottom game, AssetManager manager, Graphics g, ItemInstance instance){
        boolean advanced = game.getContainer().getInput().isKeyDown(game.settings.keyAdvancedInfo.key);

        List<String> desc = new ArrayList<>();
        instance.getItem().describeItem(manager, instance, desc, advanced);

        Gui.drawHoverInfoAtMouse(game, manager, g, true, 0, desc);
    }

    public static void renderSlotInGui(RockBottom game, AssetManager manager, Graphics g, ItemInstance slot, float x, float y, float scale){
        Gui.drawScaledImage(g, manager.getImage("gui.slot"), x, y, scale, game.settings.guiColor);

        if(slot != null){
            renderItemInGui(game, manager, g, slot, x+3F*scale, y+3F*scale, scale, Color.white);
        }
    }

    public static void renderItemInGui(RockBottom game, AssetManager manager, Graphics g, ItemInstance slot, float x, float y, float scale, Color color){
        Item item = slot.getItem();
        IItemRenderer renderer = item.getRenderer();
        if(renderer != null){
            renderer.render(game, manager, g, item, slot, x, y, 12F*scale, color);
        }

        manager.getFont().drawStringFromRight(x+15F*scale, y+9F*scale, String.valueOf(slot.getAmount()), 0.25F*scale);
    }
}
