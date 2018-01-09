package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.Collections;
import java.util.List;

public class TileLeaves extends TileBasic{

    public TileLeaves(){
        super(RockBottomAPI.createInternalRes("leaves"));
        this.addProps(StaticTileProps.NATURAL);
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y, TileLayer layer){
        return world.getState(layer, x, y).get(StaticTileProps.NATURAL) ? null : super.getBoundBox(world, x, y, layer);
    }

    @Override
    public TileState getPlacementState(IWorld world, int x, int y, TileLayer layer, ItemInstance instance, AbstractEntityPlayer placer){
        return super.getPlacementState(world, x, y, layer, instance, placer).prop(StaticTileProps.NATURAL, false);
    }

    @Override
    public List<ItemInstance> getDrops(IWorld world, int x, int y, TileLayer layer, Entity destroyer){
        return Util.RANDOM.nextDouble() >= 0.75 ? Collections.singletonList(new ItemInstance(GameContent.TILE_SAPLING)) : Collections.emptyList();
    }

    @Override
    public boolean isFullTile(){
        return false;
    }

    @Override
    public void onChangeAround(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer){
        if(!world.isClient()){
            if(world.getState(layer, x, y).get(StaticTileProps.NATURAL)){
                int rangeX = 2;
                int rangeY = 4;

                for(int addX = -rangeX; addX <= rangeX; addX++){
                    for(int addY = -rangeY; addY <= rangeY; addY++){
                        TileState state = world.getState(layer, x+addX, y+addY);
                        if(state.getTile().doesSustainLeaves(world, x+addX, y+addY, layer)){
                            return;
                        }
                    }
                }

                world.scheduleUpdate(x, y, layer, Util.RANDOM.nextInt(25)+5);
            }
        }
    }

    @Override
    public void onScheduledUpdate(IWorld world, int x, int y, TileLayer layer, int scheduledMeta){
        if(!world.isClient()){
            if(world.getState(layer, x, y).get(StaticTileProps.NATURAL)){
                world.destroyTile(x, y, layer, null, true);
            }
        }
    }
}
