package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.MultiTile;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.gui.GuiSimpleFurnace;
import de.ellpeck.rockbottom.gui.container.ContainerSimpleFurnace;
import de.ellpeck.rockbottom.render.tile.TileSimpleFurnaceRenderer;
import de.ellpeck.rockbottom.world.tile.entity.TileEntitySimpleFurnace;

public class TileSimpleFurnace extends MultiTile{

    public TileSimpleFurnace(){
        super(RockBottomAPI.createInternalRes("simple_furnace"));
    }

    @Override
    public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractEntityPlayer player){
        Pos2 main = this.getMainPos(x, y, world.getState(layer, x, y));
        TileEntitySimpleFurnace tile = world.getTileEntity(layer, main.getX(), main.getY(), TileEntitySimpleFurnace.class);
        return tile != null && player.openGuiContainer(new GuiSimpleFurnace(player, tile), new ContainerSimpleFurnace(player, tile));
    }

    @Override
    public int getLight(IWorld world, int x, int y, TileLayer layer){
        Pos2 main = this.getMainPos(x, y, world.getState(layer, x, y));
        TileEntitySimpleFurnace tile = world.getTileEntity(layer, main.getX(), main.getY(), TileEntitySimpleFurnace.class);
        return tile != null && tile.isActive() ? 20 : 0;
    }

    @Override
    public boolean canProvideTileEntity(){
        return true;
    }

    @Override
    public TileEntity provideTileEntity(IWorld world, int x, int y, TileLayer layer){
        return this.isMainPos(x, y, world.getState(layer, x, y)) ? new TileEntitySimpleFurnace(world, x, y, layer) : null;
    }

    @Override
    public boolean canPlaceInLayer(TileLayer layer){
        return layer == TileLayer.MAIN;
    }

    @Override
    public boolean isFullTile(){
        return false;
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y, TileLayer layer){
        return null;
    }

    @Override
    protected ITileRenderer createRenderer(IResourceName name){
        return new TileSimpleFurnaceRenderer(name, this);
    }

    @Override
    protected boolean[][] makeStructure(){
        return new boolean[][]{
                {true},
                {true}
        };
    }

    @Override
    public int getWidth(){
        return 1;
    }

    @Override
    public int getHeight(){
        return 2;
    }

    @Override
    public int getMainX(){
        return 0;
    }

    @Override
    public int getMainY(){
        return 0;
    }
}
