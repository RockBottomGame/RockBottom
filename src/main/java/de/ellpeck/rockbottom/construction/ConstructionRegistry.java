package de.ellpeck.rockbottom.construction;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.BasicRecipe;
import de.ellpeck.rockbottom.api.construction.resource.ItemUseInfo;
import de.ellpeck.rockbottom.api.item.ItemInstance;

public final class ConstructionRegistry{

    public static void init(){
        new BasicRecipe(RockBottomAPI.createInternalRes("test_recipe"), new ItemInstance(GameContent.TILE_SOIL, 10), new ItemUseInfo(GameContent.TILE_STONE), new ItemUseInfo(GameContent.TILE_LOG, 20)).register(RockBottomAPI.MANUAL_CONSTRUCTION_RECIPES);
    }

}
