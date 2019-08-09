package de.ellpeck.rockbottom.construction;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.construction.compendium.construction.ConstructionRecipe;
import de.ellpeck.rockbottom.api.item.Item;

import java.util.ArrayList;
import java.util.List;

public final class ConstructionRegistry {

    public static final List<ConstructionRecipe> BRITTLE_TOOLS = new ArrayList<>();
    public static final List<ConstructionRecipe> STONE_TOOLS = new ArrayList<>();
    public static final List<ConstructionRecipe> COPPER_TOOLS = new ArrayList<>();
    public static ConstructionRecipe ladder;
    public static ConstructionRecipe chest;
    public static ConstructionRecipe grassTorch;
    public static ConstructionRecipe simpleFurnace;
    public static ConstructionRecipe torch;
    public static ConstructionRecipe mortar;
    public static ConstructionRecipe pestle;
    public static ConstructionRecipe simpleHoe;
    public static ConstructionRecipe constructionTable;

    public static void postInit() {
        BRITTLE_TOOLS.add(getRecipe(GameContent.ITEM_BRITTLE_PICKAXE));
        BRITTLE_TOOLS.add(getRecipe(GameContent.ITEM_BRITTLE_AXE));
        BRITTLE_TOOLS.add(getRecipe(GameContent.ITEM_BRITTLE_SHOVEL));
        BRITTLE_TOOLS.add(getRecipe(GameContent.ITEM_BRITTLE_SWORD));

        STONE_TOOLS.add(getRecipe(GameContent.ITEM_STONE_PICKAXE));
        STONE_TOOLS.add(getRecipe(GameContent.ITEM_STONE_AXE));
        STONE_TOOLS.add(getRecipe(GameContent.ITEM_STONE_SHOVEL));
        STONE_TOOLS.add(getRecipe(GameContent.ITEM_STONE_SWORD));
        STONE_TOOLS.add(getRecipe(GameContent.ITEM_WRENCH));
        STONE_TOOLS.add(getRecipe(GameContent.ITEM_SAW));
        STONE_TOOLS.add(getRecipe(GameContent.ITEM_HAMMER));
        STONE_TOOLS.add(getRecipe(GameContent.ITEM_MALLET));
        STONE_TOOLS.add(getRecipe(GameContent.ITEM_CHISEL));

        COPPER_TOOLS.add(getRecipe(GameContent.ITEM_COPPER_PICKAXE));
        COPPER_TOOLS.add(getRecipe(GameContent.ITEM_COPPER_AXE));
        COPPER_TOOLS.add(getRecipe(GameContent.ITEM_COPPER_SHOVEL));
        COPPER_TOOLS.add(getRecipe(GameContent.ITEM_COPPER_SWORD));


        ladder = getRecipe(GameContent.TILE_LADDER.getItem());
        chest = getRecipe(GameContent.TILE_CHEST.getItem());
        grassTorch = getRecipe(GameContent.TILE_GRASS_TORCH.getItem());
        simpleFurnace = getRecipe(GameContent.TILE_SIMPLE_FURNACE.getItem());
        torch = getRecipe(GameContent.TILE_TORCH.getItem());
        mortar = getRecipe(GameContent.TILE_MORTAR.getItem());
        pestle = getRecipe(GameContent.ITEM_PESTLE);
        simpleHoe = getRecipe(GameContent.ITEM_SIMPLE_HOE);
        constructionTable = getRecipe(GameContent.TILE_CONSTRUCTION_TABLE.getItem());
    }

    private static ConstructionRecipe getRecipe(Item item) {
        return ConstructionRecipe.forName(item.getName());
    }
}
