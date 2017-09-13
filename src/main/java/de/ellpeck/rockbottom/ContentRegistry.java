package de.ellpeck.rockbottom;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.EntityItem;
import de.ellpeck.rockbottom.api.world.gen.biome.BiomeBasic;
import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.world.tile.TileAir;

public final class ContentRegistry{

    public static void init(){
        new TileAir().register();

        new BiomeBasic(AbstractGame.internalRes("sky"), Integer.MAX_VALUE, 2, 1000).register();

        RockBottomAPI.ENTITY_REGISTRY.register(AbstractGame.internalRes("item"), EntityItem.class);
    }
}
