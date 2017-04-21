package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.item.ItemInstance;
import de.ellpeck.rockbottom.util.BoundBox;
import de.ellpeck.rockbottom.world.IWorld;
import de.ellpeck.rockbottom.world.TileLayer;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.entity.Entity;

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
        if(world.getMeta(layer, x, y) == 0){
            world.destroyTile(x, y, layer, null, true);
            TileLog.scheduleDestroyAround(world, x, y);
        }
    }
}
