package de.ellpeck.rockbottom;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.EntityItem;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.world.entity.player.knowledge.RecipeInformation;
import de.ellpeck.rockbottom.world.gen.WorldGenBiomes;
import de.ellpeck.rockbottom.world.gen.biome.BiomeGrassland;
import de.ellpeck.rockbottom.world.gen.biome.BiomeSky;
import de.ellpeck.rockbottom.world.gen.biome.BiomeUnderground;
import de.ellpeck.rockbottom.world.gen.feature.WorldGenFlowers;
import de.ellpeck.rockbottom.world.gen.feature.WorldGenGrass;
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

        new BiomeSky(RockBottomAPI.createInternalRes("sky"), Integer.MAX_VALUE, 1, 1000).register();
        new BiomeGrassland(RockBottomAPI.createInternalRes("grassland"), 0, -1, 1000).register();
        new BiomeUnderground(RockBottomAPI.createInternalRes("underground"), -2, Integer.MIN_VALUE, 1000).register();

        RockBottomAPI.ENTITY_REGISTRY.register(RockBottomAPI.createInternalRes("item"), EntityItem.class);

        RockBottomAPI.WORLD_GENERATORS.register(RockBottomAPI.createInternalRes("biomes"), WorldGenBiomes.class);
        RockBottomAPI.WORLD_GENERATORS.register(RockBottomAPI.createInternalRes("grass"), WorldGenGrass.class);
        RockBottomAPI.WORLD_GENERATORS.register(RockBottomAPI.createInternalRes("trees"), WorldGenTrees.class);
        RockBottomAPI.WORLD_GENERATORS.register(RockBottomAPI.createInternalRes("flowers"), WorldGenFlowers.class);

        RockBottomAPI.INFORMATION_REGISTRY.register(RockBottomAPI.createInternalRes("recipe"), RecipeInformation.class);
    }
}
