package de.ellpeck.rockbottom.game.world.tile;

import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.game.ContentRegistry;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.game.RockBottom;

import java.util.Collections;
import java.util.List;

public class TileGrass extends TileBasic{

    public TileGrass(){
        super(RockBottom.internalRes("grass"));
    }

    @Override
    public List<ItemInstance> getDrops(IWorld world, int x, int y, Entity destroyer){
        return Collections.singletonList(new ItemInstance(ContentRegistry.TILE_DIRT));
    }

    @Override
    public void onChangeAround(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer){
        if(layer == changedLayer){
            if(world.getTile(layer, x, y+1).isFullTile()){
                world.setTile(layer, x, y, ContentRegistry.TILE_DIRT);
            }
        }
    }

    @Override
    public void doPlace(IWorld world, int x, int y, TileLayer layer, ItemInstance instance, AbstractEntityPlayer placer){
        super.doPlace(world, x, y, layer, instance, placer);
        this.onChangeAround(world, x, y, layer, x, y, layer);
    }
}
