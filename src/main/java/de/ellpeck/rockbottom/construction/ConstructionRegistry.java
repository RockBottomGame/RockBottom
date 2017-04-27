package de.ellpeck.rockbottom.construction;

import de.ellpeck.rockbottom.ContentRegistry;
import de.ellpeck.rockbottom.item.ItemInstance;

public final class ConstructionRegistry{

    public static final ConstructionList<BasicRecipe> MANUAL_RECIPES = new ConstructionList<>();
    public static final ConstructionList<BasicRecipe> TABLE_RECIPES = new ConstructionList<>();

    public static void init(){
        MANUAL_RECIPES.add(new BasicRecipe(new ItemInstance(ContentRegistry.TILE_TORCH, 3),
                new ItemInstance(ContentRegistry.TILE_LOG),
                new ItemInstance(ContentRegistry.TILE_COAL_ORE)));
    }
}
