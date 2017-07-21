package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.tile.state.BoolProp;
import de.ellpeck.rockbottom.api.tile.state.TileProp;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.init.AbstractGame;

public class TileLog extends TileBasic{

    public static final BoolProp PROP_NATURAL = new BoolProp("natural", true);

    public TileLog(){
        super(AbstractGame.internalRes("log"));
    }

    public static void scheduleDestroyAround(IWorld world, int x, int y){
        for(TileLayer layer : TileLayer.LAYERS){
            for(Direction direction : Direction.ADJACENT_INCLUDING_NONE){
                Tile tile = world.getState(layer, direction.x+x, direction.y+y).getTile();

                if(tile instanceof TileLog || tile instanceof TileLeaves){
                    world.scheduleUpdate(direction.x+x, direction.y+y, layer, 5);
                }
            }
        }
    }

    @Override
    public void onDestroyed(IWorld world, int x, int y, Entity destroyer, TileLayer layer, boolean forceDrop){
        super.onDestroyed(world, x, y, destroyer, layer, forceDrop);

        if(world.getState(layer, x, y).getProperty(PROP_NATURAL)){
            scheduleDestroyAround(world, x, y);
        }
    }

    @Override
    public void onScheduledUpdate(IWorld world, int x, int y, TileLayer layer){
        if(world.getState(layer, x, y).getProperty(PROP_NATURAL)){
            world.destroyTile(x, y, layer, null, true);
            scheduleDestroyAround(world, x, y);
        }
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y){
        return world.getState(x, y).getProperty(PROP_NATURAL) ? null : super.getBoundBox(world, x, y);
    }

    @Override
    public TileState getPlacementState(IWorld world, int x, int y, TileLayer layer, ItemInstance instance, AbstractEntityPlayer placer){
        return this.getDefState().withProperty(PROP_NATURAL, false);
    }

    @Override
    public float getHardness(IWorld world, int x, int y, TileLayer layer){
        float hard = super.getHardness(world, x, y, layer);
        return world.getState(layer, x, y).getProperty(PROP_NATURAL) ? hard*6F : hard;
    }

    @Override
    public TileProp[] getProperties(){
        return new TileProp[]{PROP_NATURAL};
    }
}
