package de.ellpeck.rockbottom.construction;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.construction.BasicRecipe;
import de.ellpeck.rockbottom.api.construction.IRecipe;
import de.ellpeck.rockbottom.api.construction.KnowledgeBasedRecipe;
import de.ellpeck.rockbottom.api.construction.resource.ItemUseInfo;
import de.ellpeck.rockbottom.api.item.ItemInstance;

import java.util.ArrayList;
import java.util.List;

public final class ConstructionRegistry{

    public static final List<IRecipe> BRITTLE_TOOLS = new ArrayList<>();
    public static IRecipe torch;
    public static IRecipe ladder;
    public static IRecipe chest;

    public static void init(){
        BRITTLE_TOOLS.add(new KnowledgeBasedRecipe(new ItemInstance(GameContent.ITEM_BRITTLE_PICKAXE), new ItemUseInfo(GameContent.TILE_PEBBLES, 12), new ItemUseInfo(GameContent.WOOD_BOARDS, 4)).registerManual());
        BRITTLE_TOOLS.add(new KnowledgeBasedRecipe(new ItemInstance(GameContent.ITEM_BRITTLE_AXE), new ItemUseInfo(GameContent.TILE_PEBBLES, 10), new ItemUseInfo(GameContent.WOOD_BOARDS, 4)).registerManual());
        BRITTLE_TOOLS.add(new KnowledgeBasedRecipe(new ItemInstance(GameContent.ITEM_BRITTLE_SHOVEL), new ItemUseInfo(GameContent.TILE_PEBBLES, 8), new ItemUseInfo(GameContent.WOOD_BOARDS, 4)).registerManual());

        torch = new KnowledgeBasedRecipe(new ItemInstance(GameContent.TILE_TORCH, 3), new ItemUseInfo(GameContent.TILE_COAL, 1), new ItemUseInfo(GameContent.WOOD_BOARDS, 1)).registerManual();

        ladder = new KnowledgeBasedRecipe(new ItemInstance(GameContent.TILE_LADDER, 5), new ItemUseInfo(GameContent.WOOD_BOARDS, 8)).registerManual();

        chest = new KnowledgeBasedRecipe(new ItemInstance(GameContent.TILE_CHEST), new ItemUseInfo(GameContent.WOOD_BOARDS, 20), new ItemUseInfo(GameContent.TILE_LOG, 4)).registerManual();

        new BasicRecipe(new ItemInstance(GameContent.TILE_SIGN), new ItemUseInfo(GameContent.WOOD_BOARDS, 8)).registerManual();
        new BasicRecipe(new ItemInstance(GameContent.WOOD_BOARDS, 3), new ItemUseInfo(GameContent.TILE_LOG)).registerManual();
        new BasicRecipe(new ItemInstance(GameContent.TILE_WOOD_DOOR), new ItemUseInfo(GameContent.WOOD_BOARDS, 16)).registerManual();
    }

}
