package de.ellpeck.rockbottom.construction;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.KnowledgeBasedRecipe;
import de.ellpeck.rockbottom.api.construction.resource.ItemUseInfo;
import de.ellpeck.rockbottom.api.item.ItemInstance;

public final class ConstructionRegistry{

    public static void init(){
        new KnowledgeBasedRecipe(RockBottomAPI.createInternalRes("test"), new ItemInstance(GameContent.TILE_STONE, 10), new ItemUseInfo(GameContent.TILE_LOG, 20), new ItemUseInfo(GameContent.TILE_LEAVES, 40)).register(RockBottomAPI.MANUAL_CONSTRUCTION_RECIPES);
    }

}
