package de.ellpeck.rockbottom.construction;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.BasicRecipe;
import de.ellpeck.rockbottom.api.construction.SeparatorRecipe;
import de.ellpeck.rockbottom.api.construction.SmelterRecipe;
import de.ellpeck.rockbottom.api.item.ItemInstance;

public final class ConstructionRegistry{

    public static void init(){
        RockBottomAPI.MANUAL_CONSTRUCTION_RECIPES.add(new BasicRecipe(new ItemInstance(GameContent.TILE_TORCH, 8),
                new ItemInstance(GameContent.TILE_WOOD_BOARDS),
                new ItemInstance(GameContent.ITEM_COAL, 2, Constants.META_WILDCARD)));
        RockBottomAPI.MANUAL_CONSTRUCTION_RECIPES.add(new BasicRecipe(new ItemInstance(GameContent.TILE_WOOD_BOARDS, 5),
                new ItemInstance(GameContent.TILE_LOG)));
        RockBottomAPI.MANUAL_CONSTRUCTION_RECIPES.add(new BasicRecipe(new ItemInstance(GameContent.ITEM_WOOD_PICK),
                new ItemInstance(GameContent.TILE_WOOD_BOARDS, 16)));
        RockBottomAPI.MANUAL_CONSTRUCTION_RECIPES.add(new BasicRecipe(new ItemInstance(GameContent.ITEM_ROCK_PICK),
                new ItemInstance(GameContent.TILE_WOOD_BOARDS, 8),
                new ItemInstance(GameContent.TILE_ROCK, 8)));
        RockBottomAPI.MANUAL_CONSTRUCTION_RECIPES.add(new BasicRecipe(new ItemInstance(GameContent.TILE_SMELTER),
                new ItemInstance(GameContent.TILE_WOOD_BOARDS, 20),
                new ItemInstance(GameContent.TILE_ROCK, 40),
                new ItemInstance(GameContent.ITEM_COAL, 10, Constants.META_WILDCARD)));
        RockBottomAPI.MANUAL_CONSTRUCTION_RECIPES.add(new BasicRecipe(new ItemInstance(GameContent.TILE_SEPARATOR),
                new ItemInstance(GameContent.TILE_WOOD_BOARDS, 30),
                new ItemInstance(GameContent.TILE_ROCK, 60),
                new ItemInstance(GameContent.ITEM_COAL, 15, Constants.META_WILDCARD),
                new ItemInstance(GameContent.ITEM_COPPER_CLUSTER, 5)));
        RockBottomAPI.MANUAL_CONSTRUCTION_RECIPES.add(new BasicRecipe(new ItemInstance(GameContent.ITEM_SUPER_TOOL),
                new ItemInstance(GameContent.ITEM_COPPER_INGOT, 30),
                new ItemInstance(GameContent.ITEM_COPPER_GRIT, 20),
                new ItemInstance(GameContent.ITEM_SLAG, 5)));

        RockBottomAPI.FUEL_REGISTRY.put(new ItemInstance(GameContent.TILE_LOG), 600);
        RockBottomAPI.FUEL_REGISTRY.put(new ItemInstance(GameContent.TILE_WOOD_BOARDS), 120);
        RockBottomAPI.FUEL_REGISTRY.put(new ItemInstance(GameContent.ITEM_COAL), 1800);
        RockBottomAPI.FUEL_REGISTRY.put(new ItemInstance(GameContent.ITEM_COAL, 1, 1), 1600);
        RockBottomAPI.FUEL_REGISTRY.put(new ItemInstance(GameContent.ITEM_SLAG), 200);

        RockBottomAPI.SMELTER_RECIPES.add(new SmelterRecipe(new ItemInstance(GameContent.ITEM_COPPER_INGOT),
                new ItemInstance(GameContent.ITEM_COPPER_GRIT),
                300));
        RockBottomAPI.SMELTER_RECIPES.add(new SmelterRecipe(new ItemInstance(GameContent.ITEM_COAL, 1, 1),
                new ItemInstance(GameContent.TILE_LOG),
                800));

        RockBottomAPI.SEPARATOR_RECIPES.add(new SeparatorRecipe(new ItemInstance(GameContent.ITEM_COPPER_GRIT, 2),
                new ItemInstance(GameContent.ITEM_COPPER_CLUSTER),
                500,
                new ItemInstance(GameContent.ITEM_SLAG),
                0.7F));
    }

}
