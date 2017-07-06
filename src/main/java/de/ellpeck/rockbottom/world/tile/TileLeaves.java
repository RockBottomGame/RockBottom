package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.init.AbstractGame;

import java.util.Collections;
import java.util.List;

public class TileLeaves extends TileBasic{

    public TileLeaves(){
        super(AbstractGame.internalRes("leaves"));
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
    public int getPlacementMeta(IWorld world, int x, int y, TileLayer layer, ItemInstance instance){
        return 1;
    }

    @Override
    public List<ItemInstance> getDrops(IWorld world, int x, int y, TileLayer layer, Entity destroyer){
        if(Util.RANDOM.nextFloat() >= 0.95F){
            return Collections.singletonList(new ItemInstance(GameContent.TILE_SAPLING));
        }
        else{
            return Collections.emptyList();
        }
    }

    @Override
    public void onScheduledUpdate(IWorld world, int x, int y, TileLayer layer){
        if(world.getMeta(layer, x, y) == 0){
            world.destroyTile(x, y, layer, null, true);
            TileLog.scheduleDestroyAround(world, x, y);
        }
    }
}
