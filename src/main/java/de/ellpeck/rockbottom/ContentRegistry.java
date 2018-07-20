package de.ellpeck.rockbottom;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.resource.ResUseInfo;
import de.ellpeck.rockbottom.api.construction.smelting.FuelInput;
import de.ellpeck.rockbottom.api.effect.BasicEffect;
import de.ellpeck.rockbottom.api.item.ItemBasic;
import de.ellpeck.rockbottom.api.item.ItemSword;
import de.ellpeck.rockbottom.api.item.ItemTool;
import de.ellpeck.rockbottom.api.item.ToolType;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.gen.biome.level.BiomeLevel;
import de.ellpeck.rockbottom.api.world.gen.biome.level.BiomeLevelBasic;
import de.ellpeck.rockbottom.item.*;
import de.ellpeck.rockbottom.world.entity.EntityFalling;
import de.ellpeck.rockbottom.world.entity.EntityFirework;
import de.ellpeck.rockbottom.world.entity.EntityItem;
import de.ellpeck.rockbottom.world.entity.EntitySlime;
import de.ellpeck.rockbottom.world.entity.player.knowledge.RecipeInformation;
import de.ellpeck.rockbottom.world.gen.WorldGenBiomes;
import de.ellpeck.rockbottom.world.gen.WorldGenHeights;
import de.ellpeck.rockbottom.world.gen.biome.*;
import de.ellpeck.rockbottom.world.gen.feature.*;
import de.ellpeck.rockbottom.world.gen.ore.WorldGenCoal;
import de.ellpeck.rockbottom.world.gen.ore.WorldGenCopper;
import de.ellpeck.rockbottom.world.tile.*;

public final class ContentRegistry {

    public static void init() {
        new TileAir().register();
        new TileSoil().register();
        new TileGrass().register();
        new TileStone().register();
        new TileGrassTuft().register();
        new TileLog().register();
        new TileLeaves().register();
        new TileFlower().register();
        new TilePebbles().register();
        new TileFalling(ResourceName.intern("sand")).register();
        new TileBasic(ResourceName.intern("sandstone")).register();
        new TileCoal().register();
        new TileTorch(ResourceName.intern("torch")).register();
        new TileSnow().register();
        new TileLadder().register();
        new TileChest().register();
        new TileSign().register();
        new TileSapling().register();
        new TileWater().register();
        new TileWoodBoards().register();
        new TileWoodDoor(ResourceName.intern("wood_door")).register();
        new TileWoodDoor(ResourceName.intern("wood_door_old")).register();
        new TileRemainsGoo().register();
        new TileGrassTorch().register();
        new TileCopper().register();
        new TileSimpleFurnace().register();
        new TileCaveMushroom().register();
        new TileStardrop().register();
        new TileLamp(ResourceName.intern("lamp_iron")).register();

        new ItemTool(ResourceName.intern("brittle_pickaxe"), 1.5F, 50, ToolType.PICKAXE, 1).register();
        new ItemTool(ResourceName.intern("brittle_axe"), 1.25F, 50, ToolType.AXE, 1).register();
        new ItemTool(ResourceName.intern("brittle_shovel"), 1.25F, 50, ToolType.SHOVEL, 1).register();
        new ItemSword(ResourceName.intern("brittle_sword"), 50, 4, 10, 1.5D, 0.25D).register();
        new ItemFirework().register();
        new ItemStartNote().register();
        new ItemBasic(ResourceName.intern("plant_fiber")).register();
        new ItemTwig().register();
        new ItemTool(ResourceName.intern("stone_pickaxe"), 2.5F, 120, ToolType.PICKAXE, 5).register();
        new ItemTool(ResourceName.intern("stone_axe"), 1.5F, 120, ToolType.AXE, 5).register();
        new ItemTool(ResourceName.intern("stone_shovel"), 1.5F, 120, ToolType.SHOVEL, 5).register();
        new ItemSword(ResourceName.intern("stone_sword"), 120, 8, 20, 2D, 0.5D).register();
        new ItemCopperCanister().register();
        new ItemTool(ResourceName.intern("super_pickaxe"), Float.MAX_VALUE, Short.MAX_VALUE, ToolType.PICKAXE, Integer.MAX_VALUE).addToolType(ToolType.AXE, Integer.MAX_VALUE).addToolType(ToolType.SHOVEL, Integer.MAX_VALUE).register();
        new ItemBasic(ResourceName.intern("copper_ingot")).register();
        new ItemTool(ResourceName.intern("copper_pickaxe"), 4F, 350, ToolType.PICKAXE, 10).register();
        new ItemTool(ResourceName.intern("copper_axe"), 2F, 350, ToolType.AXE, 10).register();
        new ItemTool(ResourceName.intern("copper_shovel"), 2F, 350, ToolType.SHOVEL, 10).register();
        new ItemSword(ResourceName.intern("copper_sword"), 350, 12, 30, 2D, 0.35D).register();
        new ItemRecipeNote().register();

        BiomeLevel sky = new BiomeLevelBasic(ResourceName.intern("sky"), 15, Integer.MAX_VALUE, false, 0).register();
        BiomeLevel surface = new BiomeLevelBasic(ResourceName.intern("surface"), -10, 15, true, 1000).register();
        BiomeLevel underground = new BiomeLevelBasic(ResourceName.intern("underground"), Integer.MIN_VALUE, -10, false, 500).register();

        new BiomeSky(ResourceName.intern("sky"), 0, sky).register();
        new BiomeGrassland(ResourceName.intern("grassland"), 1000, surface).register();
        new BiomeDesert(ResourceName.intern("desert"), 800, surface).register();
        new BiomeUnderground(ResourceName.intern("underground"), 1000, underground).register();
        new BiomeColdGrassland(ResourceName.intern("cold_grassland")).register();

        RockBottomAPI.ENTITY_REGISTRY.register(ResourceName.intern("item"), EntityItem.class);
        RockBottomAPI.ENTITY_REGISTRY.register(ResourceName.intern("falling"), EntityFalling.class);
        RockBottomAPI.ENTITY_REGISTRY.register(ResourceName.intern("firework"), EntityFirework.class);
        RockBottomAPI.ENTITY_REGISTRY.register(ResourceName.intern("slime"), EntitySlime.class);

        RockBottomAPI.WORLD_GENERATORS.register(WorldGenBiomes.ID, WorldGenBiomes.class);
        RockBottomAPI.WORLD_GENERATORS.register(WorldGenHeights.ID, WorldGenHeights.class);
        RockBottomAPI.WORLD_GENERATORS.register(ResourceName.intern("grass"), WorldGenGrass.class);
        RockBottomAPI.WORLD_GENERATORS.register(ResourceName.intern("trees"), WorldGenTrees.class);
        RockBottomAPI.WORLD_GENERATORS.register(ResourceName.intern("flowers"), WorldGenFlowers.class);
        RockBottomAPI.WORLD_GENERATORS.register(ResourceName.intern("pebbles"), WorldGenPebbles.class);
        RockBottomAPI.WORLD_GENERATORS.register(ResourceName.intern("coal"), WorldGenCoal.class);
        RockBottomAPI.WORLD_GENERATORS.register(ResourceName.intern("start_hut"), WorldGenStartHut.class);
        RockBottomAPI.WORLD_GENERATORS.register(ResourceName.intern("copper"), WorldGenCopper.class);
        RockBottomAPI.WORLD_GENERATORS.register(ResourceName.intern("caves"), WorldGenCaves.class);
        RockBottomAPI.WORLD_GENERATORS.register(ResourceName.intern("cave_mushrooms"), WorldGenCaveMushrooms.class);
        RockBottomAPI.WORLD_GENERATORS.register(ResourceName.intern("stardrops"), WorldGenStardrops.class);

        RockBottomAPI.INFORMATION_REGISTRY.register(RecipeInformation.REG_NAME, RecipeInformation.class);

        new BasicEffect(ResourceName.intern("speed"), false, false, 36000).register();
        new BasicEffect(ResourceName.intern("jump_height"), false, false, 36000).register();

        new FuelInput(new ResUseInfo(GameContent.RES_COAL), 1000).register();
        new FuelInput(new ResUseInfo(GameContent.RES_WOOD_RAW), 300).register();
        new FuelInput(new ResUseInfo(GameContent.RES_WOOD_PROCESSED), 100).register();
        new FuelInput(new ResUseInfo(GameContent.RES_PLANT_FIBER), 20).register();
        new FuelInput(new ResUseInfo(GameContent.RES_STICK), 20).register();

        EntitySlime.SPAWN_BEHAVIOR.register();
    }
}
