package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.WorldInfo;
import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.gui.menu.GuiSelectWorld;
import de.ellpeck.rockbottom.api.util.Util;
import org.newdawn.slick.util.Log;

import java.io.File;

public class ComponentSelectWorldButton extends ComponentButton{

    private static final IResourceName LOC_DELETE = RockBottom.internalRes("info.delete_world");
    private static final IResourceName LOC_WORLD = RockBottom.internalRes("info.world");
    private static final IResourceName LOC_SEED = RockBottom.internalRes("info.seed");
    private static final IResourceName LOC_TIME = RockBottom.internalRes("info.time");

    private final GuiSelectWorld gui;
    private final File worldFile;
    private WorldInfo info;

    private boolean exists;

    public ComponentSelectWorldButton(GuiSelectWorld gui, int id, int x, int y, int sizeX, int sizeY){
        super(gui, id, x, y, sizeX, sizeY, null);
        this.gui = gui;

        this.worldFile = new File(RockBottom.get().getDataManager().getWorldsDir(), "world"+(id+1));
        this.exists = this.worldFile.isDirectory();

        this.info = new WorldInfo(this.worldFile);
        if(this.exists){
            this.info.load();
        }
    }

    @Override
    protected String getText(){
        if(this.exists){
            IAssetManager manager = RockBottom.get().getAssetManager();
            return manager.localize(this.gui.deleteMode ? LOC_DELETE : LOC_WORLD, this.id+1);
        }
        else{
            return "-----";
        }
    }

    @Override
    protected String[] getHover(){
        if(this.exists){
            IAssetManager manager = RockBottom.get().getAssetManager();

            String[] hover = new String[2];
            hover[0] = manager.localize(LOC_SEED)+": "+this.info.seed;
            hover[1] = manager.localize(LOC_TIME)+": "+this.info.currentWorldTime;
            return hover;
        }
        return super.getHover();
    }

    @Override
    public boolean onPressed(IGameInstance game){
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
