package de.ellpeck.rockbottom.construction;

import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.ContentRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ConstructionRegistry{

    public static final List<BasicRecipe> MANUAL_RECIPES = new ArrayList<>();
    public static final List<BasicRecipe> TABLE_RECIPES = new ArrayList<>();

    public static final Map<ItemInstance, Integer> FUEL_REGISTRY = new HashMap<>();

    public static final List<SmelterRecipe> SMELTER_RECIPES = new ArrayList<>();
    public static final List<SeparatorRecipe> SEPARATOR_RECIPES = new ArrayList<>();

    public static void init(){
        MANUAL_RECIPES.add(new BasicRecipe(new ItemInstance(ContentRegistry.TILE_TORCH, 8),
                new ItemInstance(ContentRegistry.TILE_WOOD_BOARDS),
                new ItemInstance(ContentRegistry.ITEM_COAL, 2)));
        MANUAL_RECIPES.add(new BasicRecipe(new ItemInstance(ContentRegistry.TILE_WOOD_BOARDS, 5),
                new ItemInstance(ContentRegistry.TILE_LOG)));
        MANUAL_RECIPES.add(new BasicRecipe(new ItemInstance(ContentRegistry.ITEM_WOOD_PICK),
                new ItemInstance(ContentRegistry.TILE_WOOD_BOARDS, 16)));
        MANUAL_RECIPES.add(new BasicRecipe(new ItemInstance(ContentRegistry.ITEM_ROCK_PICK),
                new ItemInstance(ContentRegistry.TILE_WOOD_BOARDS, 8),
                new ItemInstance(ContentRegistry.TILE_ROCK, 8)));
        MANUAL_RECIPES.add(new BasicRecipe(new ItemInstance(ContentRegistry.TILE_SMELTER),
                new ItemInstance(ContentRegistry.TILE_WOOD_BOARDS, 20),
                new ItemInstance(ContentRegistry.TILE_ROCK, 40),
                new ItemInstance(ContentRegistry.ITEM_COAL, 10)));
        MANUAL_RECIPES.add(new BasicRecipe(new ItemInstance(ContentRegistry.TILE_SEPARATOR),
                new ItemInstance(ContentRegistry.TILE_WOOD_BOARDS, 30),
                new ItemInstance(ContentRegistry.TILE_ROCK, 60),
                new ItemInstance(ContentRegistry.ITEM_COAL, 15),
                new ItemInstance(ContentRegistry.ITEM_COPPER_CLUSTER, 5)));
        MANUAL_RECIPES.add(new BasicRecipe(new ItemInstance(ContentRegistry.ITEM_SUPER_TOOL),
                new ItemInstance(ContentRegistry.ITEM_COPPER_INGOT, 30),
                new ItemInstance(ContentRegistry.ITEM_COPPER_GRIT, 20),
                new ItemInstance(ContentRegistry.ITEM_SLAG, 5)));

        FUEL_REGISTRY.put(new ItemInstance(ContentRegistry.TILE_LOG), 600);
        FUEL_REGISTRY.put(new ItemInstance(ContentRegistry.TILE_WOOD_BOARDS), 120);
        FUEL_REGISTRY.put(new ItemInstance(ContentRegistry.ITEM_COAL), 1200);
        FUEL_REGISTRY.put(new ItemInstance(ContentRegistry.ITEM_SLAG), 200);

        SMELTER_RECIPES.add(new SmelterRecipe(new ItemInstance(ContentRegistry.ITEM_COPPER_INGOT),
                new ItemInstance(ContentRegistry.ITEM_COPPER_GRIT),
                300));

        SEPARATOR_RECIPES.add(new SeparatorRecipe(new ItemInstance(ContentRegistry.ITEM_COPPER_GRIT, 2),
                new ItemInstance(ContentRegistry.ITEM_COPPER_CLUSTER),
                500,
                new ItemInstance(ContentRegistry.ITEM_SLAG),
                0.7F));
    }

    public static int getFuelValue(ItemInstance instance){
        for(Map.Entry<ItemInstance, Integer> entry : FUEL_REGISTRY.entrySet()){
            if(instance.isItemEqual(entry.getKey())){
                return entry.getValue();
            }
        }
        return 0;
    }

    public static SmelterRecipe getSmelterRecipe(ItemInstance input){
        for(SmelterRecipe recipe : SMELTER_RECIPES){
            if(input.isItemEqual(recipe.getInput())){
                return recipe;
            }
        }
        return null;
    }

    public static SeparatorRecipe getSeparatorRecipe(ItemInstance input){
        for(SeparatorRecipe recipe : SEPARATOR_RECIPES){
            if(input.isItemEqual(recipe.getInput())){
                return recipe;
            }
        }
        return null;
    }
}
