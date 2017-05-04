package de.ellpeck.rockbottom.construction;

import de.ellpeck.rockbottom.ContentRegistry;
import de.ellpeck.rockbottom.item.ItemInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ConstructionRegistry{

    public static final List<BasicRecipe> MANUAL_RECIPES = new ArrayList<>();
    public static final List<BasicRecipe> TABLE_RECIPES = new ArrayList<>();

    public static final List<SmelterRecipe> SMELTER_RECIPES = new ArrayList<>();
    public static final Map<ItemInstance, Integer> FUEL_REGISTRY = new HashMap<>();

    public static void init(){
        MANUAL_RECIPES.add(new BasicRecipe(new ItemInstance(ContentRegistry.TILE_TORCH, 3),
                new ItemInstance(ContentRegistry.TILE_LOG),
                new ItemInstance(ContentRegistry.TILE_COAL_ORE)));

        FUEL_REGISTRY.put(new ItemInstance(ContentRegistry.ITEM_COAL), 800);
        FUEL_REGISTRY.put(new ItemInstance(ContentRegistry.ITEM_SLAG), 200);

        SMELTER_RECIPES.add(new SmelterRecipe(new ItemInstance(ContentRegistry.ITEM_COPPER_INGOT),
                new ItemInstance(ContentRegistry.ITEM_COPPER_GRIT),
                300));
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
}
