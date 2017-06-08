package de.ellpeck.rockbottom.game.world.tile;

import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.game.ContentRegistry;
import de.ellpeck.rockbottom.game.item.ItemInstance;
import de.ellpeck.rockbottom.game.world.World;
import de.ellpeck.rockbottom.game.world.entity.Entity;

import java.util.Collections;
import java.util.List;

public class TileCopperOre extends TileBasic{

    public TileCopperOre(){
        super("copper_ore");
    }

    @Override
    public List<ItemInstance> getDrops(IWorld world, int x, int y, Entity destroyer){
        return Collections.singletonList(new ItemInstance(ContentRegistry.ITEM_COPPER_CLUSTER));
    }
}
