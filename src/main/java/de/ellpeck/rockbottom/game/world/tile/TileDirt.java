package de.ellpeck.rockbottom.game.world.tile;

import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.game.ContentRegistry;

public class TileDirt extends TileBasic{

    public TileDirt(){
        super("dirt");
    }

    @Override
    public void updateRandomly(IWorld world, int x, int y){
        if(world.isPosLoaded(x, y+1) && !world.getTile(x, y+1).isFullTile()){
            world.setTile(x, y, ContentRegistry.TILE_GRASS);
        }
    }
}
