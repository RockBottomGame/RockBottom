package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.init.AbstractGame;

public class TileLog extends TileBasic{

    public TileLog(){
        super(AbstractGame.internalRes("log"));
    }

    public static void scheduleDestroyAround(IWorld world, int x, int y){
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
    public void onDestroyed(IWorld world, int x, int y, Entity destroyer, TileLayer layer, boolean forceDrop){
        super.onDestroyed(world, x, y, destroyer, layer, forceDrop);

        if(world.getMeta(layer, x, y) == 0){
            scheduleDestroyAround(world, x, y);
        }
    }

    @Override
    public void onScheduledUpdate(IWorld world, int x, int y, TileLayer layer){
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
    public int getPlacementMeta(IWorld world, int x, int y, TileLayer layer, ItemInstance instance){
        return 1;
    }

    @Override
    public float getHardness(IWorld world, int x, int y, TileLayer layer){
        float hard = super.getHardness(world, x, y, layer);
        return world.getMeta(x, y) == 0 ? hard*6F : hard;
    }
}
