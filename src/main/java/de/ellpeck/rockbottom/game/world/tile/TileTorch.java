package de.ellpeck.rockbottom.game.world.tile;

import de.ellpeck.rockbottom.game.render.tile.TorchTileRenderer;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.game.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.world.TileLayer;

public class TileTorch extends TileBasic{

    public TileTorch(){
        super("torch");
    }

    @Override
    protected ITileRenderer createRenderer(String name){
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
    public int getPlacementMeta(IWorld world, int x, int y, TileLayer layer, ItemInstance instance){
        return this.getPossibleTorchMeta(world, x, y);
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
        else if(world.getMeta(x, y) != meta){
            world.setMeta(x, y, meta);
        }
    }

    private int getPossibleTorchMeta(IWorld world, int x, int y){
        if(world.getTile(x, y-1).isFullTile()){
            return 0;
        }
        else if(world.getTile(TileLayer.BACKGROUND, x, y).isFullTile()){
            return 3;
        }
        else if(world.getTile(x-1, y).isFullTile()){
            return 1;
        }
        else if(world.getTile(x+1, y).isFullTile()){
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
