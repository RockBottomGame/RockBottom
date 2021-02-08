package de.ellpeck.rockbottom;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.construction.resource.ResUseInfo;
import de.ellpeck.rockbottom.api.construction.smelting.FuelInput;
import de.ellpeck.rockbottom.api.effect.BasicEffect;
import de.ellpeck.rockbottom.api.item.*;
import de.ellpeck.rockbottom.api.tile.BasicTile;
import de.ellpeck.rockbottom.api.tile.PlatformTile;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.gen.biome.level.BiomeLevel;
import de.ellpeck.rockbottom.api.world.gen.biome.level.BasicBiomeLevel;
import de.ellpeck.rockbottom.construction.category.*;
import de.ellpeck.rockbottom.construction.criteria.BreakTileCriterion;
import de.ellpeck.rockbottom.construction.criteria.PickupItemCriterion;
import de.ellpeck.rockbottom.construction.criteria.ReachDepthCriterion;
import de.ellpeck.rockbottom.item.*;
import de.ellpeck.rockbottom.world.entity.*;
import de.ellpeck.rockbottom.world.entity.player.knowledge.RecipeInformation;
import de.ellpeck.rockbottom.world.gen.BiomeWorldGen;
import de.ellpeck.rockbottom.world.gen.HeightWorldGen;
import de.ellpeck.rockbottom.world.gen.biome.*;
import de.ellpeck.rockbottom.world.gen.feature.*;
import de.ellpeck.rockbottom.world.gen.ore.CoalWorldGen;
import de.ellpeck.rockbottom.world.gen.ore.CopperWorldGen;
import de.ellpeck.rockbottom.world.gen.ore.TinWorldGen;
import de.ellpeck.rockbottom.world.tile.*;

public final class ContentRegistry {

    public static void init() {
        new AirTile().register();
        new SoilTile().register();
        new GrassTile().register();
        new BasicTile(ResourceName.intern("stone")).setChiselable().register();
        new GrassTuftTile().register();
        new LogTile().register();
        new LeavesTile().register();
        new FlowerTile().register();
        new PebblesTile().register();
        new FallingTile(ResourceName.intern("sand")).register();
        new BasicTile(ResourceName.intern("sandstone")).setChiselable().register();
        new OreMaterialTile(ResourceName.intern("coal")).register();
        new TorchTile(ResourceName.intern("torch")).register();
        new SnowTile().register();
        new LadderTile().register();
        new ChestTile().register();
        new SignTile().register();
        new SaplingTile().register();
        new WaterTile().register();
        new WoodBoardsTile().setChiselable().register();
        new WoodDoorTile(ResourceName.intern("wood_door")).register();
        new WoodDoorTile(ResourceName.intern("old_wood_door")).register();
        new GooRemainsTile().register();
        new GrassTorchTile().register();
        new CopperTile().register();
        new OreMaterialTile(ResourceName.intern("tin")).register();
        new SpinningWheelTile().register();
        new SimpleFurnaceTile().register();
        new CombinerTile().register();
        new ConstructionTableTile().register();
        new SmithingTableTile().register();
        new CaveMushroomTile().register();
        new StardropTile().register();
        new LampTile(ResourceName.intern("iron_lamp")).register();
        new MortarTile().register();
        new TilledSoilTile().register();
        new CornTile().register();
        new CottonTile().register();
        new GlassTile().register();
        new PlatformTile().register();
        new RopeTile(ResourceName.intern("plant_rope")).register();

        new ToolItem(ResourceName.intern("brittle_pickaxe"), 1.5F, 50, ToolProperty.PICKAXE, 1).register();
        new ToolItem(ResourceName.intern("brittle_axe"), 1.25F, 50, ToolProperty.AXE, 1).register();
        new ToolItem(ResourceName.intern("brittle_shovel"), 1.25F, 50, ToolProperty.SHOVEL, 1).register();
        new SwordItem(ResourceName.intern("brittle_sword"), 50, 4, 10, 1.5D, 0.25D).register();
        new ToolItem(ResourceName.intern("wrench"), 1, 100, ToolProperty.WRENCH, 1).register();
        new ToolItem(ResourceName.intern("saw"), 1, 100, ToolProperty.SAW, 1).register();
        new ToolItem(ResourceName.intern("hammer"), 1, 100, ToolProperty.HAMMER, 1).register();
        new ToolItem(ResourceName.intern("mallet"), 1, 100, ToolProperty.MALLET, 1).register();
        new ChiselItem(ResourceName.intern("chisel"), 1, 100, ToolProperty.CHISEL, 1).register();
        new FireworkItem().register();
        new StartNoteItem().register();
        new BasicItem(ResourceName.intern("plant_fiber")).register();
        new BasicItem(ResourceName.intern("yarn")).register();
        new TwigItem().register();
        new BasicItem(ResourceName.intern("stick")).register();
        new ToolItem(ResourceName.intern("stone_pickaxe"), 2.5F, 120, ToolProperty.PICKAXE, 5).register();
        new ToolItem(ResourceName.intern("stone_axe"), 1.5F, 120, ToolProperty.AXE, 5).register();
        new ToolItem(ResourceName.intern("stone_shovel"), 1.5F, 120, ToolProperty.SHOVEL, 5).register();
        new SwordItem(ResourceName.intern("stone_sword"), 120, 8, 20, 2D, 0.5D).register();
        new CopperCanisterItem().register();
        new ToolItem(ResourceName.intern("super_pickaxe"), Float.MAX_VALUE, Short.MAX_VALUE, ToolProperty.PICKAXE, Integer.MAX_VALUE).addToolProperty(ToolProperty.AXE, Integer.MAX_VALUE).addToolProperty(ToolProperty.SHOVEL, Integer.MAX_VALUE).register();
        new BasicItem(ResourceName.intern("copper_ingot")).register();
        new ToolItem(ResourceName.intern("copper_pickaxe"), 4F, 350, ToolProperty.PICKAXE, 10).register();
        new ToolItem(ResourceName.intern("copper_axe"), 2F, 350, ToolProperty.AXE, 10).register();
        new ToolItem(ResourceName.intern("copper_shovel"), 2F, 350, ToolProperty.SHOVEL, 10).register();
        new SwordItem(ResourceName.intern("copper_sword"), 350, 12, 30, 2D, 0.35D).register();
        new BasicItem(ResourceName.intern("tin_ingot")).register();
        new BasicItem(ResourceName.intern("bronze_ingot")).register();
        new ToolItem(ResourceName.intern("bronze_pickaxe"), 6F, 650, ToolProperty.PICKAXE, 20).register();
        new ToolItem(ResourceName.intern("bronze_axe"), 4F, 650, ToolProperty.AXE, 20).register();
        new ToolItem(ResourceName.intern("bronze_shovel"), 4F, 650, ToolProperty.SHOVEL, 20).register();
        new SwordItem(ResourceName.intern("bronze_sword"), 650, 15, 30, 2D, 0.4D).register();
        new StorageContainerItem(ResourceName.intern("bronze_canister"), 4).register();
        new RecipeNoteItem().register();
        new BowlItem().register();
        new ToolItem(ResourceName.intern("pestle"), 1F, 64, ToolProperty.PESTLE, 1).register();
        new MushItem().register();
        new BoomerangItem(ResourceName.intern("wood_boomerang"), 50, 4, 0.25, 8).register();
        new ToolItem(ResourceName.intern("simple_hoe"), 1F, 50, ToolProperty.HOE, 1).register();

        BiomeLevel sky = new BasicBiomeLevel(ResourceName.intern("sky"), 15, Integer.MAX_VALUE, false, 0).register();
        BiomeLevel surface = new BasicBiomeLevel(ResourceName.intern("surface"), -10, 15, true, 1000).register();
        BiomeLevel underground = new BasicBiomeLevel(ResourceName.intern("underground"), Integer.MIN_VALUE, -10, false, 500).register();

        new SkyBiome(ResourceName.intern("sky"), 0, sky).register();
        new GrasslandBiome(ResourceName.intern("grassland"), 1000, surface).register();
        new DesertBiome(ResourceName.intern("desert"), 800, surface).register();
        new UndergroundBiome(ResourceName.intern("underground"), 1000, underground).register();
        new ColdGrasslandBiome(ResourceName.intern("cold_grassland")).register();

        Registries.ENTITY_REGISTRY.register(ItemEntity.ID, ItemEntity::new);
        Registries.ENTITY_REGISTRY.register(FallingEntity.ID, FallingEntity::new);
        Registries.ENTITY_REGISTRY.register(FireworkEntity.ID, FireworkEntity::new);
        Registries.ENTITY_REGISTRY.register(SlimeEntity.ID, SlimeEntity::new);
        Registries.ENTITY_REGISTRY.register(BoomerangEntity.ID, BoomerangEntity::new);
        Registries.ENTITY_REGISTRY.register(FireEntity.ID, FireEntity::new);

        Registries.WORLD_GENERATORS.register(BiomeWorldGen.ID, BiomeWorldGen::new);
        Registries.WORLD_GENERATORS.register(HeightWorldGen.ID, HeightWorldGen::new);
        Registries.WORLD_GENERATORS.register(ResourceName.intern("grass"), GrassWorldGen::new);
        Registries.WORLD_GENERATORS.register(ResourceName.intern("trees"), TreesWorldGen::new);
        Registries.WORLD_GENERATORS.register(ResourceName.intern("flowers"), FlowersWorldGen::new);
        Registries.WORLD_GENERATORS.register(ResourceName.intern("pebbles"), PebblesWorldGen::new);
        Registries.WORLD_GENERATORS.register(ResourceName.intern("coal"), CoalWorldGen::new);
        Registries.WORLD_GENERATORS.register(ResourceName.intern("start_hut"), StartHutWorldGen::new);
        Registries.WORLD_GENERATORS.register(ResourceName.intern("copper"), CopperWorldGen::new);
        Registries.WORLD_GENERATORS.register(ResourceName.intern("tin"), TinWorldGen::new);
        Registries.WORLD_GENERATORS.register(ResourceName.intern("caves"), CavesWorldGen::new);
        Registries.WORLD_GENERATORS.register(ResourceName.intern("cave_mushrooms"), CaveMushroomsWorldGen::new);
        Registries.WORLD_GENERATORS.register(ResourceName.intern("stardrops"), StardropsWorldGen::new);
        Registries.WORLD_GENERATORS.register(ResourceName.intern("lakes"), LakesWorldGen::new);
        Registries.WORLD_GENERATORS.register(ResourceName.intern("corn"), CornWorldGen::new);
        Registries.WORLD_GENERATORS.register(ResourceName.intern("cotton"), CottonWorldGen::new);

        Registries.INFORMATION_REGISTRY.register(RecipeInformation.REG_NAME, RecipeInformation::new);

        new BreakTileCriterion().register();
        new PickupItemCriterion().register();
        new ReachDepthCriterion().register();

        new BasicEffect(ResourceName.intern("speed"), false, false, 36000, 10).register();
        new BasicEffect(ResourceName.intern("jump_height"), false, false, 36000, 20).register();
        new BasicEffect(ResourceName.intern("range"), false, false, 36000, 10).register();
        new BasicEffect(ResourceName.intern("pickup_range"), false, false, 36000, 10).register();

        new FuelInput(new ResUseInfo(GameContent.Resources.COAL), 1000).register();
        new FuelInput(new ResUseInfo(GameContent.Resources.WOOD_RAW), 300).register();
        new FuelInput(new ResUseInfo(GameContent.Resources.WOOD_PROCESSED), 100).register();
        new FuelInput(new ResUseInfo(GameContent.Resources.PLANT_FIBER), 20).register();
        new FuelInput(new ResUseInfo(GameContent.Resources.STICK), 20).register();

        SlimeEntity.SPAWN_BEHAVIOR.register();

        ManualConstructionCategory.INSTANCE.register();
        new ConstructionTableCategory().register();
        new SmithingTableCategory().register();
        new MortarCategory().register();
        new SmeltingCategory().register();
    }
}
