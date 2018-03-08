package de.ellpeck.rockbottom.construction;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.IRecipe;
import de.ellpeck.rockbottom.api.item.Item;

import java.util.ArrayList;
import java.util.List;

public final class ConstructionRegistry{

    public static final List<IRecipe> BRITTLE_TOOLS = new ArrayList<>();
    public static IRecipe ladder;
    public static IRecipe chest;
    public static IRecipe grassTorch;

    public static void init(){

    }

    public static void postInit(){
        BRITTLE_TOOLS.add(getRecipe(GameContent.ITEM_BRITTLE_PICKAXE));
        BRITTLE_TOOLS.add(getRecipe(GameContent.ITEM_BRITTLE_AXE));
        BRITTLE_TOOLS.add(getRecipe(GameContent.ITEM_BRITTLE_SHOVEL));

        ladder = getRecipe(GameContent.TILE_LADDER.getItem());
        chest = getRecipe(GameContent.TILE_CHEST.getItem());
        grassTorch = getRecipe(GameContent.TILE_GRASS_TORCH.getItem());
    }

    private static IRecipe getRecipe(Item item){
        return RockBottomAPI.ALL_CONSTRUCTION_RECIPES.get(item.getName());
    }
}
