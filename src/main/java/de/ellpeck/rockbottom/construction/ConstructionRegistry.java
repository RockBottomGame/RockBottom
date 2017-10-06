package de.ellpeck.rockbottom.construction;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.construction.IRecipe;
import de.ellpeck.rockbottom.api.construction.KnowledgeBasedRecipe;
import de.ellpeck.rockbottom.api.construction.resource.ItemUseInfo;
import de.ellpeck.rockbottom.api.item.ItemInstance;

import java.util.ArrayList;
import java.util.List;

public final class ConstructionRegistry{

    public static final List<IRecipe> BRITTLE_TOOLS = new ArrayList<>();

    public static void init(){
        BRITTLE_TOOLS.add(new KnowledgeBasedRecipe(new ItemInstance(GameContent.ITEM_BRITTLE_PICKAXE), new ItemUseInfo(GameContent.TILE_PEBBLES, 12), new ItemUseInfo(GameContent.TILE_LOG, 2)).registerManual());
        BRITTLE_TOOLS.add(new KnowledgeBasedRecipe(new ItemInstance(GameContent.ITEM_BRITTLE_AXE), new ItemUseInfo(GameContent.TILE_PEBBLES, 10), new ItemUseInfo(GameContent.TILE_LOG, 2)).registerManual());
        BRITTLE_TOOLS.add(new KnowledgeBasedRecipe(new ItemInstance(GameContent.ITEM_BRITTLE_SHOVEL), new ItemUseInfo(GameContent.TILE_PEBBLES, 8), new ItemUseInfo(GameContent.TILE_LOG, 2)).registerManual());
    }

}
