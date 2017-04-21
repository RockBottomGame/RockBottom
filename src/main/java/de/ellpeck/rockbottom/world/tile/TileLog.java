package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.item.ItemInstance;
import de.ellpeck.rockbottom.util.BoundBox;
import de.ellpeck.rockbottom.util.Direction;
import de.ellpeck.rockbottom.world.IWorld;
import de.ellpeck.rockbottom.world.TileLayer;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.entity.Entity;

public class TileLog extends TileBasic{

    public TileLog(int id){
        super(id, "log");
    }

    @Override
    public void onDestroyed(World world, int x, int y, Entity destroyer, TileLayer layer, boolean forceDrop){
        super.onDestroyed(world, x, y, destroyer, layer, forceDrop);

        if(world.getMeta(layer, x, y) == 0){
            scheduleDestroyAround(world, x, y);
        }
    }

    @Override
    public void onScheduledUpdate(World world, int x, int y, TileLayer layer){
        if(world.getMeta(layer, x, y) == 0){
            world.destroyTile(x, y, layer, null, true);
            scheduleDestroyAround(world, x, y);
        }
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y){
        return world.getMeta(x, y) == 0 ? null : super.getBoundBox(world, x, y);
    }

    @Override
    public int getPlacementMeta(World world, int x, int y, TileLayer layer, ItemInstance instance){
        return 1;
    }

    public static void scheduleDestroyAround(World world, int x, int y){
        for(TileLayer layer : TileLayer.LAYERS){
            for(Direction direction : Direction.ADJACENT_INCLUDING_NONE){
                Tile tile = world.getTile(layer, direction.x+x, direction.y+y);

                if(tile instanceof TileLog || tile instanceof TileLeaves){
                    world.scheduleUpdate(direction.x+x, direction.y+y, layer, 5);
                }
            }
        }
    }

    @Override
    public float getHardness(World world, int x, int y, TileLayer layer){
        float hard = super.getHardness(world, x, y, layer);
        return world.getMeta(x, y) == 0 ? hard*8F : hard;
    }
}
