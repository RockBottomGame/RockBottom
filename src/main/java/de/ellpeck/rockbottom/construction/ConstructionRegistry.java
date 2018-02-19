package de.ellpeck.rockbottom.construction;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.construction.BasicRecipe;
import de.ellpeck.rockbottom.api.construction.IRecipe;
import de.ellpeck.rockbottom.api.construction.KnowledgeBasedRecipe;
import de.ellpeck.rockbottom.api.construction.resource.ResUseInfo;
import de.ellpeck.rockbottom.api.item.ItemInstance;

import java.util.ArrayList;
import java.util.List;

public final class ConstructionRegistry{

    public static final List<IRecipe> BRITTLE_TOOLS = new ArrayList<>();
    public static IRecipe ladder;
    public static IRecipe chest;
    public static IRecipe grassTorch;

    public static void init(){
        BRITTLE_TOOLS.add(new KnowledgeBasedRecipe(new ItemInstance(GameContent.ITEM_BRITTLE_PICKAXE), new ResUseInfo(GameContent.RES_PEBBLES, 12), new ResUseInfo(GameContent.RES_STICK, 4)).registerManual());
        BRITTLE_TOOLS.add(new KnowledgeBasedRecipe(new ItemInstance(GameContent.ITEM_BRITTLE_AXE), new ResUseInfo(GameContent.RES_PEBBLES, 10), new ResUseInfo(GameContent.RES_STICK, 4)).registerManual());
        BRITTLE_TOOLS.add(new KnowledgeBasedRecipe(new ItemInstance(GameContent.ITEM_BRITTLE_SHOVEL), new ResUseInfo(GameContent.RES_PEBBLES, 8), new ResUseInfo(GameContent.RES_STICK, 4)).registerManual());

        ladder = new KnowledgeBasedRecipe(new ItemInstance(GameContent.TILE_LADDER, 5), new ResUseInfo(GameContent.RES_WOOD_PROCESSED, 8)).registerManual();
        chest = new KnowledgeBasedRecipe(new ItemInstance(GameContent.TILE_CHEST), new ResUseInfo(GameContent.RES_WOOD_PROCESSED, 20), new ResUseInfo(GameContent.RES_WOOD_RAW, 4)).registerManual();
        grassTorch = new KnowledgeBasedRecipe(new ItemInstance(GameContent.TILE_GRASS_TORCH, 2), new ResUseInfo(GameContent.RES_PLANT_FIBER, 4), new ResUseInfo(GameContent.RES_STICK, 3)).registerManual();

        new BasicRecipe(new ItemInstance(GameContent.TILE_SIGN), new ResUseInfo(GameContent.RES_WOOD_PROCESSED, 8)).registerManual();
        new BasicRecipe(new ItemInstance(GameContent.WOOD_BOARDS, 3), new ResUseInfo(GameContent.RES_WOOD_RAW)).registerManual();
        new BasicRecipe(new ItemInstance(GameContent.TILE_WOOD_DOOR), new ResUseInfo(GameContent.RES_WOOD_PROCESSED, 16)).registerManual();
    }

}
