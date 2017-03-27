package de.ellpeck.game.world.tile;

import de.ellpeck.game.item.ItemInstance;
import de.ellpeck.game.util.Direction;
import de.ellpeck.game.world.TileLayer;
import de.ellpeck.game.world.World;

public class TileLiquid extends TileBasic{

    private static final Direction[] FLOW_DIRECTIONS = new Direction[]{Direction.DOWN, Direction.LEFT, Direction.RIGHT};

    private final int speed;

    public TileLiquid(int id, String name, int speed){
        super(id, name);
        this.speed = speed;
    }

    @Override
    public boolean canPlace(World world, int x, int y, TileLayer layer){
        return layer == TileLayer.MAIN && super.canPlace(world, x, y, layer);
    }

    @Override
    public byte getPlacementMeta(World world, int x, int y, TileLayer layer, ItemInstance instance){
        return 8;
    }

    @Override
    public void onAdded(World world, int x, int y){
        world.scheduleUpdate(x, y, TileLayer.MAIN, this.speed);
    }

    @Override
    public void onChangeAround(World world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer){
        if(changedLayer == layer){
            world.scheduleUpdate(x, y, TileLayer.MAIN, this.speed);
        }
    }

    @Override
    public void onScheduledUpdate(World world, int x, int y, TileLayer layer){
        int meta = world.getMeta(x, y);
        if(meta >= 2){
            for(Direction facing : FLOW_DIRECTIONS){
                int xOff = x+facing.x;
                int yOff = y+facing.y;

                Tile tile = world.getTile(xOff, yOff);
                if(tile.canReplace(world, xOff, yOff, TileLayer.MAIN)){
                    int half = meta/2;

                    world.setTile(xOff, yOff, this);
                    world.setMeta(xOff, yOff, (byte)half);

                    world.setMeta(x, y, (byte)(meta-half));

                    break;
                }
            }
        }
    }
}
