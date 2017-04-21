package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.gui.menu.GuiSelectWorld;
import de.ellpeck.rockbottom.util.Util;
import de.ellpeck.rockbottom.world.World.WorldInfo;
import org.newdawn.slick.util.Log;

import java.io.File;

public class ComponentSelectWorldButton extends ComponentButton{

    private final GuiSelectWorld gui;
    private final File worldFile;
    private WorldInfo info;

    private boolean exists;

    public ComponentSelectWorldButton(GuiSelectWorld gui, int id, int x, int y, int sizeX, int sizeY){
        super(gui, id, x, y, sizeX, sizeY, null);
        this.gui = gui;

        this.worldFile = new File(RockBottom.get().dataManager.saveDirectory, "world"+(id+1));
        this.exists = this.worldFile.isDirectory();

        this.info = new WorldInfo(this.worldFile);
        if(this.exists){
            this.info.load();
        }
    }

    @Override
    protected String getText(){
        if(this.exists){
            AssetManager manager = RockBottom.get().assetManager;
            return manager.localize("info."+(this.gui.deleteMode ? "delete_world" : "world"), this.id+1);
        }
        else{
            return "-----";
        }
    }

    @Override
    protected String[] getHover(){
        if(this.exists){
            AssetManager manager = RockBottom.get().assetManager;

            String[] hover = new String[2];
            hover[0] = manager.localize("info.seed")+": "+this.info.seed;
            hover[1] = manager.localize("info.time")+": "+this.info.currentWorldTime;
            return hover;
        }
        return super.getHover();
    }

    @Override
    public boolean onPressed(RockBottom game){
        if(this.gui.deleteMode){
            if(this.exists){
                try{
                    Util.deleteFolder(this.worldFile);
                    this.exists = false;
                    this.info = new WorldInfo(this.worldFile);

                    Log.info("Successfully deleted world "+(this.id+1)+".");
                }
                catch(Exception e){
                    Log.error("Couldn't delete world "+(this.id+1)+"!", e);
                }
                return true;
            }
        }
        else{
            game.startWorld(this.worldFile, this.info);
            return true;
        }
        return false;
    }
}
