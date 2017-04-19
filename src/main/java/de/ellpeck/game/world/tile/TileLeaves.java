package de.ellpeck.game.world.tile;

import de.ellpeck.game.world.TileLayer;
import de.ellpeck.game.world.World;

public class TileLeaves extends TileBasic{

    public TileLeaves(int id){
        super(id, "leaves");
    }

    @Override
    public boolean isFullTile(){
        return false;
    }

    @Override
    public void onScheduledUpdate(World world, int x, int y, TileLayer layer){
        world.destroyTile(x, y, layer, null, true);
        TileLog.scheduleDestroyAround(world, x, y);
    }
}
