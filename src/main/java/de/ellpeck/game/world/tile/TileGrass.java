package de.ellpeck.game.world.tile;

import de.ellpeck.game.ContentRegistry;
import de.ellpeck.game.item.ItemInstance;
import de.ellpeck.game.world.Chunk.TileLayer;
import de.ellpeck.game.world.World;
import de.ellpeck.game.world.entity.Entity;
import de.ellpeck.game.world.entity.player.EntityPlayer;

import java.util.Collections;
import java.util.List;

public class TileGrass extends TileBasic{

    public TileGrass(int id){
        super(id, "grass");
    }

    @Override
    public List<ItemInstance> getDrops(World world, int x, int y, Entity destroyer){
        return Collections.singletonList(new ItemInstance(ContentRegistry.TILE_DIRT));
    }

    @Override
    public void onChangeAround(World world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer){
        if(layer == changedLayer){
            if(world.getTile(layer, x, y+1).isFullTile()){
                world.setTile(layer, x, y, ContentRegistry.TILE_DIRT);
            }
        }
    }

    @Override
    public void doPlace(World world, int x, int y, TileLayer layer, ItemInstance instance, EntityPlayer placer){
        super.doPlace(world, x, y, layer, instance, placer);
        this.onChangeAround(world, x, y, layer, x, y, layer);
    }
}
