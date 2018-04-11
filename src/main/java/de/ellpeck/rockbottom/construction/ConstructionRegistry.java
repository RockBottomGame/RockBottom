package de.ellpeck.rockbottom.construction;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.construction.IRecipe;
import de.ellpeck.rockbottom.api.item.Item;

import java.util.ArrayList;
import java.util.List;

public final class ConstructionRegistry{

    public static final List<IRecipe> BRITTLE_TOOLS = new ArrayList<>();
    public static final List<IRecipe> STONE_TOOLS = new ArrayList<>();
    public static final List<IRecipe> COPPER_TOOLS = new ArrayList<>();
    public static IRecipe ladder;
    public static IRecipe chest;
    public static IRecipe grassTorch;
    public static IRecipe simpleFurnace;

    public static void postInit(){
        BRITTLE_TOOLS.add(getRecipe(GameContent.ITEM_BRITTLE_PICKAXE));
        BRITTLE_TOOLS.add(getRecipe(GameContent.ITEM_BRITTLE_AXE));
        BRITTLE_TOOLS.add(getRecipe(GameContent.ITEM_BRITTLE_SHOVEL));

        STONE_TOOLS.add(getRecipe(GameContent.ITEM_STONE_PICKAXE));
        STONE_TOOLS.add(getRecipe(GameContent.ITEM_STONE_AXE));
        STONE_TOOLS.add(getRecipe(GameContent.ITEM_STONE_SHOVEL));

        COPPER_TOOLS.add(getRecipe(GameContent.ITEM_COPPER_PICKAXE));
        COPPER_TOOLS.add(getRecipe(GameContent.ITEM_COPPER_AXE));
        COPPER_TOOLS.add(getRecipe(GameContent.ITEM_COPPER_SHOVEL));

        ladder = getRecipe(GameContent.TILE_LADDER.getItem());
        chest = getRecipe(GameContent.TILE_CHEST.getItem());
        grassTorch = getRecipe(GameContent.TILE_GRASS_TORCH.getItem());
        simpleFurnace = getRecipe(GameContent.TILE_SIMPLE_FURNACE.getItem());
    }

    private static IRecipe getRecipe(Item item){
        return IRecipe.forName(item.getName());
    }
}
