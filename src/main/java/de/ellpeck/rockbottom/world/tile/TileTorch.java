package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.tile.state.IntProp;
import de.ellpeck.rockbottom.api.tile.state.TileProp;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.render.tile.TorchTileRenderer;

public class TileTorch extends TileBasic{

    public static final IntProp PROP_FACING = new IntProp("facing", 0, 4);

    public TileTorch(){
        super(AbstractGame.internalRes("torch"));
    }

    @Override
    public TileProp[] getProperties(){
        return new TileProp[]{PROP_FACING};
    }

    @Override
    protected ITileRenderer createRenderer(IResourceName name){
        return new TorchTileRenderer();
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y){
        return null;
    }

    @Override
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer){
        return super.canPlace(world, x, y, layer) && this.getPossibleTorchMeta(world, x, y) >= 0;
    }

    @Override
    public TileState getPlacementState(IWorld world, int x, int y, TileLayer layer, ItemInstance instance, AbstractEntityPlayer placer){
        return this.getDefState().withProperty(PROP_FACING, this.getPossibleTorchMeta(world, x, y));
    }

    @Override
    public boolean canPlaceInLayer(TileLayer layer){
        return layer == TileLayer.MAIN;
    }

    @Override
    public void onChangeAround(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer){
        int meta = this.getPossibleTorchMeta(world, x, y);
        if(meta < 0){
            world.destroyTile(x, y, layer, null, true);
        }
        else if(world.getState(x, y).getProperty(PROP_FACING) != meta){
            world.setState(x, y, this.getDefState().withProperty(PROP_FACING, meta));
        }
    }

    private int getPossibleTorchMeta(IWorld world, int x, int y){
        if(world.getState(x, y-1).getTile().isFullTile()){
            return 0;
        }
        else if(world.getState(TileLayer.BACKGROUND, x, y).getTile().isFullTile()){
            return 3;
        }
        else if(world.getState(x-1, y).getTile().isFullTile()){
            return 1;
        }
        else if(world.getState(x+1, y).getTile().isFullTile()){
            return 2;
        }
        else{
            return -1;
        }
    }

    @Override
    public int getLight(IWorld world, int x, int y, TileLayer layer){
        return 25;
    }

    @Override
    public boolean isFullTile(){
        return false;
    }
}
