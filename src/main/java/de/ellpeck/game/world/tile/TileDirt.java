package de.ellpeck.game.world.tile;

import de.ellpeck.game.ContentRegistry;
import de.ellpeck.game.world.World;

public class TileDirt extends TileBasic{

    public TileDirt(int id){
        super(id, "dirt");
    }

    @Override
    public boolean doesRandomUpdates(){
        return true;
    }

    @Override
    public void updateRandomly(World world, int x, int y){
        if(!world.getTile(x, y+1).isFullTile()){
            world.setTile(x, y, ContentRegistry.TILE_GRASS);
        }
    }
}
