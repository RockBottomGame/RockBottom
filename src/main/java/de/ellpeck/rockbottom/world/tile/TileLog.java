package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.construction.ConstructionRegistry;

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
    public void onDestroyed(IWorld world, int x, int y, Entity destroyer, TileLayer layer, boolean shouldDrop){
        super.onDestroyed(world, x, y, destroyer, layer, shouldDrop);

        if(destroyer != null && !world.isClient()){
            if(world.getState(layer, x, y).get(StaticTileProps.NATURAL)){
                int yOffset = 0;
                boolean foundOne;

                do{
                    foundOne = false;

                    for(Direction dir : new Direction[]{Direction.NONE, Direction.LEFT, Direction.RIGHT}){
                        TileState up = world.getState(layer, x+dir.x, y+yOffset);
                        if(up.getTile() == this && up.get(StaticTileProps.NATURAL)){
                            world.scheduleUpdate(x+dir.x, y+yOffset, layer, yOffset*3);
                            foundOne = true;
                        }
                    }

                    yOffset++;
                }
                while(foundOne);
            }

            if(destroyer instanceof AbstractEntityPlayer){
                ((AbstractEntityPlayer)destroyer).getKnowledge().teachRecipe(ConstructionRegistry.torch, true);
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

    @Override
    public boolean doesSustainLeaves(IWorld world, int x, int y, TileLayer layer){
        return world.getState(layer, x, y).get(StaticTileProps.NATURAL);
    }
}
