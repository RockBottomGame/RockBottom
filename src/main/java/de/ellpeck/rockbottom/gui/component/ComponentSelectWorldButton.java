package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.WorldInfo;
import de.ellpeck.rockbottom.gui.menu.GuiSelectWorld;
import de.ellpeck.rockbottom.init.AbstractGame;

import java.io.File;

public class ComponentSelectWorldButton extends ComponentButton{

    private static final IResourceName LOC_SEED = AbstractGame.internalRes("info.seed");
    private static final IResourceName LOC_TIME = AbstractGame.internalRes("info.time");

    public File worldFile;
    private WorldInfo info;

    public ComponentSelectWorldButton(Gui gui, int id, int x, int y, int sizeX, int sizeY){
        super(gui, id, x, y, sizeX, sizeY, null);
    }

    public void setWorld(File file){
        this.worldFile = file;

        this.info = new WorldInfo(this.worldFile);
        this.info.load();
    }

    @Override
    protected String getText(){
        return this.worldFile.getName();
    }

    @Override
    protected String[] getHover(){
        IAssetManager manager = AbstractGame.get().getAssetManager();

        String[] hover = new String[2];
        hover[0] = manager.localize(LOC_SEED)+": "+this.info.seed;
        hover[1] = manager.localize(LOC_TIME)+": "+this.info.currentWorldTime;
        return hover;
    }

    @Override
    public boolean onPressed(IGameInstance game){
        game.startWorld(this.worldFile, this.info);
        return true;
    }
}
