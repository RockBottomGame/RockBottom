package de.ellpeck.game.world.tile;

import de.ellpeck.game.item.ItemInstance;
import de.ellpeck.game.util.BoundBox;
import de.ellpeck.game.world.IWorld;
import de.ellpeck.game.world.TileLayer;
import de.ellpeck.game.world.World;
import de.ellpeck.game.world.entity.Entity;

import java.util.Collections;
import java.util.List;

public class TileLeaves extends TileBasic{

    public TileLeaves(int id){
        super(id, "leaves");
    }

    @Override
    public boolean isFullTile(){
        return false;
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y){
        return world.getMeta(x, y) == 0 ? null : super.getBoundBox(world, x, y);
    }

    @Override
    public int getPlacementMeta(World world, int x, int y, TileLayer layer, ItemInstance instance){
        return 1;
    }

    @Override
    public List<ItemInstance> getDrops(World world, int x, int y, Entity destroyer){
        return Collections.emptyList();
    }

    @Override
    public void onScheduledUpdate(World world, int x, int y, TileLayer layer){
        if(world.getMeta(x, y) == 0){
            world.destroyTile(x, y, layer, null, true);
            TileLog.scheduleDestroyAround(world, x, y);
        }
    }
}
