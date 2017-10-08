package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

public class TileLog extends TileBasic{

    public TileLog(){
        super(RockBottomAPI.createInternalRes("log"));
        this.addProps(StaticTileProps.NATURAL);
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y){
        return world.getState(x, y).get(StaticTileProps.NATURAL) ? null : super.getBoundBox(world, x, y);
    }

    @Override
    public float getHardness(IWorld world, int x, int y, TileLayer layer){
        float hardness = super.getHardness(world, x, y, layer);
        return world.getState(layer, x, y).get(StaticTileProps.NATURAL) ? 3F*hardness : hardness;
    }

    @Override
    public TileState getPlacementState(IWorld world, int x, int y, TileLayer layer, ItemInstance instance, AbstractEntityPlayer placer){
        return super.getPlacementState(world, x, y, layer, instance, placer).prop(StaticTileProps.NATURAL, false);
    }

    @Override
    public void onRemoved(IWorld world, int x, int y, TileLayer layer){
        if(!world.isClient()){
            if(world.getState(layer, x, y).get(StaticTileProps.NATURAL)){
                for(Direction dir : new Direction[]{Direction.LEFT, Direction.RIGHT, Direction.UP}){
                    TileState state = world.getState(layer, x+dir.x, y+dir.y);
                    if(state.getTile() == this){
                        world.scheduleUpdate(x+dir.x, y+dir.y, layer, 3);
                    }
                }
            }
        }
    }

    @Override
    public void onScheduledUpdate(IWorld world, int x, int y, TileLayer layer){
        if(!world.isClient()){
            if(world.getState(layer, x, y).get(StaticTileProps.NATURAL)){
                world.destroyTile(x, y, layer, null, true);
            }
        }
    }

    @Override
    public boolean doesSustainLeaves(IWorld world, int x, int y, TileLayer layer){
        return world.getState(layer, x, y).get(StaticTileProps.NATURAL);
    }
}
