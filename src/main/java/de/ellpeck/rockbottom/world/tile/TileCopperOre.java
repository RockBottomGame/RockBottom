package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.ContentRegistry;
import de.ellpeck.rockbottom.item.ItemInstance;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.entity.Entity;

import java.util.Collections;
import java.util.List;

public class TileCopperOre extends TileBasic{

    public TileCopperOre(){
        super("copper_ore");
    }

    @Override
    public List<ItemInstance> getDrops(World world, int x, int y, Entity destroyer){
        return Collections.singletonList(new ItemInstance(ContentRegistry.ITEM_COPPER_CLUSTER));
    }
}
