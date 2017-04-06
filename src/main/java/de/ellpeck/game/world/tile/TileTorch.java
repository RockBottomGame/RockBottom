package de.ellpeck.game.world.tile;

import de.ellpeck.game.item.ItemInstance;
import de.ellpeck.game.render.tile.ITileRenderer;
import de.ellpeck.game.render.tile.TorchTileRenderer;
import de.ellpeck.game.util.BoundBox;
import de.ellpeck.game.world.IWorld;
import de.ellpeck.game.world.TileLayer;
import de.ellpeck.game.world.World;

public class TileTorch extends TileBasic{

    public TileTorch(int id){
        super(id, "torch");
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
        if(layer == changedLayer){
            if(this.getPossibleTorchMeta(world, x, y) < 0){
                world.destroyTile(x, y, layer, null);
            }
        }
    }

    private int getPossibleTorchMeta(World world, int x, int y){
        if(world.getTile(x, y-1).isFullTile()){
            return 0;
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
    public byte getLight(World world, int x, int y, TileLayer layer){
        return 25;
    }

    @Override
    public boolean isFullTile(){
        return false;
    }
}
