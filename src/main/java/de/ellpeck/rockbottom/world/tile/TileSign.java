package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.gui.GuiSign;
import de.ellpeck.rockbottom.render.tile.SignTileRenderer;
import de.ellpeck.rockbottom.world.tile.entity.TileEntitySign;

public class TileSign extends TileBasic{

    public TileSign(){
        super(RockBottomAPI.createInternalRes("sign"));
    }

    @Override
    protected ITileRenderer createRenderer(IResourceName name){
        return new SignTileRenderer(name);
    }

    @Override
    public boolean canPlaceInLayer(TileLayer layer){
        return layer == TileLayer.MAIN;
    }

    @Override
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer){
        return super.canPlace(world, x, y, layer) && world.getState(TileLayer.BACKGROUND, x, y).getTile().isFullTile();
    }

    @Override
    public boolean canProvideTileEntity(){
        return true;
    }

    @Override
    public TileEntity provideTileEntity(IWorld world, int x, int y){
        return new TileEntitySign(world, x, y);
    }

    @Override
    public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractEntityPlayer player){
        TileEntitySign tile = world.getTileEntity(x, y, TileEntitySign.class);
        return tile != null && player.openGui(new GuiSign(tile));
    }

    @Override
    public boolean isFullTile(){
        return false;
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y){
        return null;
    }
}