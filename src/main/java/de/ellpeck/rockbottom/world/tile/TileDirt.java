package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.ContentRegistry;
import de.ellpeck.rockbottom.world.World;

public class TileDirt extends TileBasic{

    public TileDirt(){
        super("dirt");
    }

    @Override
    public void updateRandomly(World world, int x, int y){
        if(world.isPosLoaded(x, y+1) && !world.getTile(x, y+1).isFullTile()){
            world.setTile(x, y, ContentRegistry.TILE_GRASS);
        }
    }
}
