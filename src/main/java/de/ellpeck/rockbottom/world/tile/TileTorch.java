package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.render.tile.TileTorchRenderer;

public class TileTorch extends TileBasic{

    public TileTorch(){
        super(RockBottomAPI.createInternalRes("torch"));
        this.addProps(StaticTileProps.TORCH_FACING);
    }

    @Override
    public int getLight(IWorld world, int x, int y, TileLayer layer){
        return 25;
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y){
        return null;
    }

    @Override
    public boolean canPlaceInLayer(TileLayer layer){
        return layer == TileLayer.MAIN;
    }

    @Override
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer){
        return this.getTorchState(world, x, y) != null;
    }

    @Override
    public TileState getPlacementState(IWorld world, int x, int y, TileLayer layer, ItemInstance instance, AbstractEntityPlayer placer){
        return this.getTorchState(world, x, y);
    }

    @Override
    public void onChangeAround(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer){
        if(!world.isClient()){
            TileState state = this.getTorchState(world, x, y);

            if(state == null){
                world.destroyTile(x, y, layer, null, this.forceDrop);
            }
            else if(state != world.getState(x, y)){
                world.setState(x, y, state);
            }
        }
    }

    @Override
    public boolean canStay(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer){
        return this.getTorchState(world, x, y) != null;
    }

    private TileState getTorchState(IWorld world, int x, int y){
        int meta;

        if(world.getState(x, y-1).getTile().isFullTile()){
            meta = 0;
        }
        else if(world.getState(x+1, y).getTile().isFullTile()){
            meta = 1;
        }
        else if(world.getState(x-1, y).getTile().isFullTile()){
            meta = 2;
        }
        else if(world.getState(TileLayer.BACKGROUND, x, y).getTile().isFullTile()){
            meta = 3;
        }
        else{
            return null;
        }

        return this.getDefState().prop(StaticTileProps.TORCH_FACING, meta);
    }

    @Override
    public boolean isFullTile(){
        return false;
    }

    @Override
    protected ITileRenderer createRenderer(IResourceName name){
        return new TileTorchRenderer(name);
    }
}
