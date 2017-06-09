package de.ellpeck.rockbottom.game.world.tile;

import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.game.ContentRegistry;
import de.ellpeck.rockbottom.game.RockBottom;

import java.util.Collections;
import java.util.List;

public class TileCoalOre extends TileBasic{

    public TileCoalOre(){
        super(RockBottom.internalRes("coal_ore"));
    }

    @Override
    public List<ItemInstance> getDrops(IWorld world, int x, int y, Entity destroyer){
        return Collections.singletonList(new ItemInstance(ContentRegistry.ITEM_COAL));
    }
}
