package de.ellpeck.rockbottom;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.EntityItem;
import de.ellpeck.rockbottom.api.item.ItemTool;
import de.ellpeck.rockbottom.api.item.ToolType;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.world.gen.biome.BiomeDesert;
import de.ellpeck.rockbottom.world.tile.TilePebbles;
import de.ellpeck.rockbottom.world.entity.player.knowledge.RecipeInformation;
import de.ellpeck.rockbottom.world.gen.WorldGenBiomes;
import de.ellpeck.rockbottom.world.gen.biome.BiomeGrassland;
import de.ellpeck.rockbottom.world.gen.biome.BiomeSky;
import de.ellpeck.rockbottom.world.gen.biome.BiomeUnderground;
import de.ellpeck.rockbottom.world.gen.feature.WorldGenFlowers;
import de.ellpeck.rockbottom.world.gen.feature.WorldGenGrass;
import de.ellpeck.rockbottom.world.gen.feature.WorldGenPebbles;
import de.ellpeck.rockbottom.world.gen.feature.WorldGenTrees;
import de.ellpeck.rockbottom.world.tile.*;

public final class ContentRegistry{

    public static void init(){
        new TileAir().register();
        new TileSoil().register();
        new TileGrass().register();
        new TileBasic(RockBottomAPI.createInternalRes("stone")).register();
        new TileGrassTuft().register();
        new TileLog().register();
        new TileLeaves().register();
        new TileFlower().register();
        new TilePebbles().register();
        new TileSand().register();
        new TileBasic(RockBottomAPI.createInternalRes("sandstone")).register();

        new ItemTool(RockBottomAPI.createInternalRes("brittle_pickaxe"), 2F, ToolType.PICKAXE, 1).register();
        new ItemTool(RockBottomAPI.createInternalRes("brittle_axe"), 2F, ToolType.AXE, 1).register();
        new ItemTool(RockBottomAPI.createInternalRes("brittle_shovel"), 2F, ToolType.SHOVEL, 1).register();

        new BiomeSky(RockBottomAPI.createInternalRes("sky"), Integer.MAX_VALUE, 40, 100).register();
        new BiomeGrassland(RockBottomAPI.createInternalRes("grassland"), 60, -5, 1000).register();
        new BiomeDesert(RockBottomAPI.createInternalRes("desert"), 60, -5, 300).register();
        new BiomeUnderground(RockBottomAPI.createInternalRes("underground"), -5, Integer.MIN_VALUE, 1000).register();

        RockBottomAPI.ENTITY_REGISTRY.register(RockBottomAPI.createInternalRes("item"), EntityItem.class);

        RockBottomAPI.WORLD_GENERATORS.register(RockBottomAPI.createInternalRes("biomes"), WorldGenBiomes.class);
        RockBottomAPI.WORLD_GENERATORS.register(RockBottomAPI.createInternalRes("grass"), WorldGenGrass.class);
        RockBottomAPI.WORLD_GENERATORS.register(RockBottomAPI.createInternalRes("trees"), WorldGenTrees.class);
        RockBottomAPI.WORLD_GENERATORS.register(RockBottomAPI.createInternalRes("flowers"), WorldGenFlowers.class);
        RockBottomAPI.WORLD_GENERATORS.register(RockBottomAPI.createInternalRes("pebbles"), WorldGenPebbles.class);

        RockBottomAPI.INFORMATION_REGISTRY.register(RockBottomAPI.createInternalRes("recipe"), RecipeInformation.class);
    }
}
