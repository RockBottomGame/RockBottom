package de.ellpeck.game.world.tile;

import de.ellpeck.game.ContentRegistry;
import de.ellpeck.game.item.ItemInstance;
import de.ellpeck.game.world.World;
import de.ellpeck.game.world.entity.Entity;

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
    public void onChangeAround(World world, int x, int y, int changedX, int changedY){
        if(world.getTile(x, y+1).isFullTile()){
            world.setTile(x, y, ContentRegistry.TILE_DIRT);
        }
    }
}
