package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.render.tile.SmelterTileRenderer;
import de.ellpeck.rockbottom.util.BoundBox;
import de.ellpeck.rockbottom.world.TileLayer;
import de.ellpeck.rockbottom.world.IWorld;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.tile.entity.TileEntity;
import de.ellpeck.rockbottom.world.tile.entity.TileEntitySmelter;

public class TileSmelter extends TileBasic{

    public TileSmelter(int id){
        super(id, "smelter");
    }

    @Override
    protected ITileRenderer createRenderer(String name){
        return new SmelterTileRenderer();
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y){
        return null;
    }

    @Override
    public boolean providesTileEntity(){
        return true;
    }

    @Override
    public boolean canPlace(World world, int x, int y, TileLayer layer){
        return super.canPlace(world, x, y, layer) && world.getTile(x, y-1).isFullTile();
    }

    @Override
    public void onChangeAround(World world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer){
        if(layer == changedLayer){
            if(!world.getTile(layer, x, y-1).isFullTile()){
                world.destroyTile(x, y, layer, null, true);
            }
        }
    }

    @Override
    public TileEntity provideTileEntity(World world, int x, int y){
        return new TileEntitySmelter(world, x, y);
    }

    @Override
    public boolean isFullTile(){
        return false;
    }
}