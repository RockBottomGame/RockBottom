package de.ellpeck.game.world.tile;

import de.ellpeck.game.util.BoundBox;
import de.ellpeck.game.world.IWorld;
import de.ellpeck.game.world.TileLayer;
import de.ellpeck.game.world.World;

public class TileTorch extends TileBasic{

    public TileTorch(int id){
        super(id, "torch");
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y){
        return null;
    }

    @Override
    public boolean canPlace(World world, int x, int y, TileLayer layer){
        return super.canPlace(world, x, y, layer) && world.getTile(x, y-1).isFullTile();
    }

    @Override
    public boolean canPlaceInLayer(TileLayer layer){
        return layer == TileLayer.MAIN;
    }

    @Override
    public void onChangeAround(World world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer){
        if(layer == changedLayer){
            if(!world.getTile(layer, x, y-1).isFullTile()){
                world.destroyTile(x, y, layer, null);
            }
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
