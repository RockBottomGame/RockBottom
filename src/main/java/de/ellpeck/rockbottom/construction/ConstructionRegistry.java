package de.ellpeck.rockbottom.construction;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.BasicRecipe;
import de.ellpeck.rockbottom.api.construction.SeparatorRecipe;
import de.ellpeck.rockbottom.api.construction.SmelterRecipe;
import de.ellpeck.rockbottom.api.construction.StamperRecipe;
import de.ellpeck.rockbottom.api.construction.resource.ItemUseInfo;
import de.ellpeck.rockbottom.api.construction.resource.ResUseInfo;
import de.ellpeck.rockbottom.api.construction.resource.ResourceRegistry;
import de.ellpeck.rockbottom.api.item.ItemInstance;

public final class ConstructionRegistry{

    public static void init(){
        RockBottomAPI.MANUAL_CONSTRUCTION_RECIPES.add(new BasicRecipe(new ItemInstance(GameContent.TILE_TORCH, 8),
                new ResUseInfo(ResourceRegistry.WOOD_BOARDS),
                new ResUseInfo(ResourceRegistry.COAL, 2)));
        RockBottomAPI.MANUAL_CONSTRUCTION_RECIPES.add(new BasicRecipe(new ItemInstance(GameContent.TILE_WOOD_BOARDS, 5),
                new ResUseInfo(ResourceRegistry.WOOD_LOG)));
        RockBottomAPI.MANUAL_CONSTRUCTION_RECIPES.add(new BasicRecipe(new ItemInstance(GameContent.TILE_LADDER, 5),
                new ResUseInfo(ResourceRegistry.WOOD_BOARDS, 20)));
        RockBottomAPI.MANUAL_CONSTRUCTION_RECIPES.add(new BasicRecipe(new ItemInstance(GameContent.TILE_CONSTRUCTION_TABLE),
                new ResUseInfo(ResourceRegistry.WOOD_LOG, 4),
                new ResUseInfo(ResourceRegistry.WOOD_BOARDS, 20),
                new ResUseInfo(ResourceRegistry.RAW_STONE, 20)));
        RockBottomAPI.MANUAL_CONSTRUCTION_RECIPES.add(new BasicRecipe(new ItemInstance(GameContent.ITEM_WOOD_PICK),
                new ResUseInfo(ResourceRegistry.WOOD_BOARDS, 16)));
        RockBottomAPI.CONSTRUCTION_TABLE_RECIPES.add(new BasicRecipe(new ItemInstance(GameContent.ITEM_ROCK_PICK),
                new ResUseInfo(ResourceRegistry.WOOD_BOARDS, 8),
                new ResUseInfo(ResourceRegistry.RAW_STONE, 8)));
        RockBottomAPI.CONSTRUCTION_TABLE_RECIPES.add(new BasicRecipe(new ItemInstance(GameContent.TILE_STAMPER),
                new ResUseInfo(ResourceRegistry.RAW_STONE, 10),
                new ResUseInfo(ResourceRegistry.WOOD_BOARDS, 4)));
        RockBottomAPI.CONSTRUCTION_TABLE_RECIPES.add(new BasicRecipe(new ItemInstance(GameContent.TILE_SMELTER),
                new ResUseInfo(ResourceRegistry.WOOD_BOARDS, 20),
                new ResUseInfo(ResourceRegistry.PROCESSED_STONE, 40),
                new ResUseInfo(ResourceRegistry.COAL, 10)));
        RockBottomAPI.CONSTRUCTION_TABLE_RECIPES.add(new BasicRecipe(new ItemInstance(GameContent.TILE_SEPARATOR),
                new ResUseInfo(ResourceRegistry.WOOD_BOARDS, 30),
                new ResUseInfo(ResourceRegistry.PROCESSED_STONE, 60),
                new ResUseInfo(ResourceRegistry.COAL, 15),
                new ResUseInfo(ResourceRegistry.RAW_COPPER, 5)));
        RockBottomAPI.CONSTRUCTION_TABLE_RECIPES.add(new BasicRecipe(new ItemInstance(GameContent.TILE_CHEST),
                new ResUseInfo(ResourceRegistry.WOOD_LOG, 5),
                new ResUseInfo(ResourceRegistry.WOOD_BOARDS, 30),
                new ResUseInfo(ResourceRegistry.PROCESSED_COPPER)));
        RockBottomAPI.CONSTRUCTION_TABLE_RECIPES.add(new BasicRecipe(new ItemInstance(GameContent.ITEM_SUPER_TOOL),
                new ResUseInfo(ResourceRegistry.PROCESSED_COPPER, 30),
                new ResUseInfo(ResourceRegistry.PARTLY_PROCESSED_COPPER, 20),
                new ResUseInfo(ResourceRegistry.SLAG, 5)));
        RockBottomAPI.CONSTRUCTION_TABLE_RECIPES.add(new BasicRecipe(new ItemInstance(GameContent.TILE_LAMP),
                new ItemUseInfo(new ItemInstance(GameContent.ITEM_GLOW_CLUSTER)),
                new ResUseInfo(ResourceRegistry.PROCESSED_STONE, 2)));

        RockBottomAPI.STAMPER_RECIPES.add(new StamperRecipe(new ResUseInfo(ResourceRegistry.RAW_STONE, 2),
                new ItemInstance(GameContent.TILE_HARDENED_STONE)));

        RockBottomAPI.FUEL_REGISTRY.put(new ResUseInfo(ResourceRegistry.WOOD_LOG), 600);
        RockBottomAPI.FUEL_REGISTRY.put(new ResUseInfo(ResourceRegistry.WOOD_BOARDS), 120);
        RockBottomAPI.FUEL_REGISTRY.put(new ResUseInfo(ResourceRegistry.COAL), 1800);
        RockBottomAPI.FUEL_REGISTRY.put(new ResUseInfo(ResourceRegistry.SLAG), 200);

        RockBottomAPI.SMELTER_RECIPES.add(new SmelterRecipe(new ItemInstance(GameContent.ITEM_COPPER_INGOT),
                new ResUseInfo(ResourceRegistry.PARTLY_PROCESSED_COPPER),
                300));
        RockBottomAPI.SMELTER_RECIPES.add(new SmelterRecipe(new ItemInstance(GameContent.ITEM_COAL, 1, 1),
                new ResUseInfo(ResourceRegistry.WOOD_LOG),
                800));

        RockBottomAPI.SEPARATOR_RECIPES.add(new SeparatorRecipe(new ItemInstance(GameContent.ITEM_COPPER_GRIT, 2),
                new ResUseInfo(ResourceRegistry.RAW_COPPER),
                500,
                new ItemInstance(GameContent.ITEM_SLAG),
                0.7F));
    }

}
