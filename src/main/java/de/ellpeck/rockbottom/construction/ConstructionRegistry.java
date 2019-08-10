package de.ellpeck.rockbottom.construction;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.construction.compendium.construction.ConstructionRecipe;
import de.ellpeck.rockbottom.api.item.Item;

import java.util.ArrayList;
import java.util.List;

public final class ConstructionRegistry {
    public static ConstructionRecipe chest;
    public static ConstructionRecipe simpleFurnace;
    public static ConstructionRecipe mortar;
    public static ConstructionRecipe constructionTable;

    public static void postInit() {
        chest = getRecipe(GameContent.TILE_CHEST.getItem());
        simpleFurnace = getRecipe(GameContent.TILE_SIMPLE_FURNACE.getItem());
        mortar = getRecipe(GameContent.TILE_MORTAR.getItem());
        constructionTable = getRecipe(GameContent.TILE_CONSTRUCTION_TABLE.getItem());
    }

    private static ConstructionRecipe getRecipe(Item item) {
        return ConstructionRecipe.forName(item.getName());
    }
}
