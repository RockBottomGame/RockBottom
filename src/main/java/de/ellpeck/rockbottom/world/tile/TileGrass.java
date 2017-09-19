package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.Collections;
import java.util.List;

public class TileGrass extends TileBasic{

    public TileGrass(){
        super(RockBottomAPI.createInternalRes("grass"));
    }

    @Override
    public List<ItemInstance> getDrops(IWorld world, int x, int y, TileLayer layer, Entity destroyer){
        return Collections.singletonList(new ItemInstance(GameContent.TILE_SOIL));
    }

    @Override
    public void onChangeAround(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer){
        super.onChangeAround(world, x, y, layer, changedX, changedY, changedLayer);

        if(world.getState(layer, x, y+1).getTile().isFullTile()){
            world.setState(layer, x, y, GameContent.TILE_SOIL.getDefState());
        }
    }

    @Override
    public void updateRandomly(IWorld world, int x, int y, TileLayer layer){
        for(Direction dir : Direction.SURROUNDING){
            if(world.isPosLoaded(x+dir.x, y+dir.y)){
                TileState state = world.getState(layer, x+dir.x, y+dir.y);

                if(state.getTile().canGrassSpreadTo(world, x+dir.x, y+dir.y, layer)){
                    world.setState(layer, x+dir.x, y+dir.y, this.getDefState());
                }
            }
        }
    }

    @Override
    public TileState getPlacementState(IWorld world, int x, int y, TileLayer layer, ItemInstance instance, AbstractEntityPlayer placer){
        return world.getState(layer, x, y+1).getTile().isFullTile() ? GameContent.TILE_SOIL.getDefState() : this.getDefState();
    }
}
