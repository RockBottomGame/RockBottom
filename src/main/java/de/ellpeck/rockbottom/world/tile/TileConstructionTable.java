package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.tile.MultiTile;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.gui.GuiConstructionTable;
import de.ellpeck.rockbottom.gui.container.ContainerConstructionTable;
import de.ellpeck.rockbottom.init.RockBottom;

public class TileConstructionTable extends MultiTile{

    public TileConstructionTable(){
        super(RockBottom.internalRes("construction_table"));
    }

    @Override
    protected boolean[][] makeStructure(){
        return new boolean[][]{
                {true, true}
        };
    }

    @Override
    public int getWidth(){
        return 2;
    }

    @Override
    public int getHeight(){
        return 1;
    }

    @Override
    public int getMainX(){
        return 0;
    }

    @Override
    public int getMainY(){
        return 0;
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y){
        return null;
    }

    @Override
    public boolean isFullTile(){
        return false;
    }

    @Override
    public boolean canPlaceInLayer(TileLayer layer){
        return layer == TileLayer.MAIN;
    }

    @Override
    public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractEntityPlayer player){
        player.openGuiContainer(new GuiConstructionTable(player), new ContainerConstructionTable(player));
        return true;
    }
}
