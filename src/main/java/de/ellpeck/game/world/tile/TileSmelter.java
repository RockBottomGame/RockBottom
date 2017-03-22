package de.ellpeck.game.world.tile;

import de.ellpeck.game.render.tile.ITileRenderer;
import de.ellpeck.game.render.tile.SmelterTileRenderer;
import de.ellpeck.game.util.BoundBox;
import de.ellpeck.game.world.Chunk;
import de.ellpeck.game.world.Chunk.TileLayer;
import de.ellpeck.game.world.IWorld;
import de.ellpeck.game.world.World;
import de.ellpeck.game.world.tile.entity.TileEntity;
import de.ellpeck.game.world.tile.entity.TileEntitySmelter;

public class TileSmelter extends Tile{

    private final ITileRenderer renderer = new SmelterTileRenderer();

    public TileSmelter(int id){
        super(id);
    }

    @Override
    public ITileRenderer getRenderer(){
        return this.renderer;
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
    public void onChangeAround(World world, int x, int y, int changedX, int changedY){
        if(!world.getTile(x, y-1).isFullTile()){
            world.destroyTile(x, y, TileLayer.MAIN, null);
        }
    }

    @Override
    public TileEntity provideTileEntity(World world, int x, int y){
        return new TileEntitySmelter(world, x, y);
    }
}
