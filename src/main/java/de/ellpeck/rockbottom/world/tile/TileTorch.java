package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.item.ItemInstance;
import de.ellpeck.rockbottom.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.render.tile.TorchTileRenderer;
import de.ellpeck.rockbottom.util.BoundBox;
import de.ellpeck.rockbottom.world.IWorld;
import de.ellpeck.rockbottom.world.TileLayer;
import de.ellpeck.rockbottom.world.World;

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
    public boolean canPlace(World world, int x, int y, TileLayer layer){
        return super.canPlace(world, x, y, layer) && this.getPossibleTorchMeta(world, x, y) >= 0;
    }

    @Override
    public int getPlacementMeta(World world, int x, int y, TileLayer layer, ItemInstance instance){
        return this.getPossibleTorchMeta(world, x, y);
    }

    @Override
    public boolean canPlaceInLayer(TileLayer layer){
        return layer == TileLayer.MAIN;
    }

    @Override
    public void onChangeAround(World world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer){
        int meta = this.getPossibleTorchMeta(world, x, y);
        if(meta < 0){
            world.destroyTile(x, y, layer, null, true);
        }
        else if(world.getMeta(x, y) != meta){
            world.setMeta(x, y, meta);
        }
    }

    private int getPossibleTorchMeta(World world, int x, int y){
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
    public int getLight(World world, int x, int y, TileLayer layer){
        return 25;
    }

    @Override
    public boolean isFullTile(){
        return false;
    }
}
