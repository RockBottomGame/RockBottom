package de.ellpeck.rockbottom;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.resource.ResourceRegistry;
import de.ellpeck.rockbottom.api.entity.EntityItem;
import de.ellpeck.rockbottom.api.item.ItemBasic;
import de.ellpeck.rockbottom.api.item.ItemMeta;
import de.ellpeck.rockbottom.api.item.ToolType;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.world.gen.biome.BiomeBasic;
import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.item.ItemGlowCluster;
import de.ellpeck.rockbottom.item.ItemTool;
import de.ellpeck.rockbottom.world.gen.cave.WorldGenBasicCaves;
import de.ellpeck.rockbottom.world.gen.feature.WorldGenTrees;
import de.ellpeck.rockbottom.world.gen.landscape.WorldGenBasicUnderground;
import de.ellpeck.rockbottom.world.gen.landscape.WorldGenHills;
import de.ellpeck.rockbottom.world.gen.landscape.WorldGenPebbles;
import de.ellpeck.rockbottom.world.gen.ore.WorldGenCoal;
import de.ellpeck.rockbottom.world.gen.ore.WorldGenCopper;
import de.ellpeck.rockbottom.world.gen.ore.WorldGenGlow;
import de.ellpeck.rockbottom.world.tile.*;
import org.newdawn.slick.util.Log;

public final class ContentRegistry{

    public static void init(){
        new TileAir().register();
        new TileDirt().addEffectiveTool(ToolType.SHOVEL, 1).setForceDrop().register();
        new TileBasic(AbstractGame.internalRes("stone")).setHardness(5F).addEffectiveTool(ToolType.PICKAXE, 1).register().addResource(ResourceRegistry.RAW_STONE);
        new TileGrass().addEffectiveTool(ToolType.SHOVEL, 1).setForceDrop().register();
        new TileBasic(AbstractGame.internalRes("wood_boards")).setHardness(2F).addEffectiveTool(ToolType.AXE, 1).setForceDrop().register().addResource(ResourceRegistry.WOOD_BOARDS);
        new TileTorch(AbstractGame.internalRes("torch")).setHardness(0F).setForceDrop().register();
        new TileChest().addEffectiveTool(ToolType.AXE, 1).setForceDrop().register();
        new TileLog().setHardness(3F).addEffectiveTool(ToolType.AXE, 1).setForceDrop().register().addResource(ResourceRegistry.WOOD_LOG);
        new TileLeaves().setHardness(0.25F).setForceDrop().register().addResource(ResourceRegistry.LEAVES);
        new TileCoalOre().setHardness(12F).addEffectiveTool(ToolType.PICKAXE, 1).register();
        new TileCopperOre().setHardness(18F).addEffectiveTool(ToolType.PICKAXE, 2).register();
        new TileSmelter().setHardness(20F).addEffectiveTool(ToolType.PICKAXE, 2).register();
        new TileSeparator().setHardness(20F).addEffectiveTool(ToolType.PICKAXE, 2).register();
        new TileSapling().setHardness(0F).setForceDrop().register();
        new TileLadder().setHardness(2F).setForceDrop().addEffectiveTool(ToolType.AXE, 1).register();
        new TileGlowOre().setHardness(15F).addEffectiveTool(ToolType.PICKAXE, 2).register();
        new TileStamper().setHardness(18F).addEffectiveTool(ToolType.PICKAXE, 1).register();
        new TileBasic(AbstractGame.internalRes("hardened_stone")).setHardness(10F).addEffectiveTool(ToolType.PICKAXE, 2).register().addResource(ResourceRegistry.PROCESSED_STONE);
        new TileDoor().setHardness(2.5F).setForceDrop().addEffectiveTool(ToolType.AXE, 1).register();
        new TileConstructionTable().setHardness(5F).addEffectiveTool(ToolType.PICKAXE, 1).setForceDrop().register();
        new TileLamp(AbstractGame.internalRes("lamp")).setHardness(1.5F).setForceDrop().register();
        new TilePebbles().setHardness(0F).setForceDrop().register();
        new TileSign().setHardness(1F).addEffectiveTool(ToolType.AXE, 1).setForceDrop().register();

        new ItemTool(AbstractGame.internalRes("super_tool"), 50F).addToolType(ToolType.AXE, 100).addToolType(ToolType.PICKAXE, 100).addToolType(ToolType.SHOVEL, 100).register();
        new ItemTool(AbstractGame.internalRes("wood_pickaxe"), 2F).addToolType(ToolType.PICKAXE, 1).register();
        new ItemTool(AbstractGame.internalRes("stone_pickaxe"), 4F).addToolType(ToolType.PICKAXE, 2).register();
        new ItemTool(AbstractGame.internalRes("stone_axe"), 4F).addToolType(ToolType.AXE, 2).register();
        new ItemTool(AbstractGame.internalRes("stone_shovel"), 2F).addToolType(ToolType.SHOVEL, 2).register();
        new ItemMeta(AbstractGame.internalRes("coal")).addSubItem(AbstractGame.internalRes("charcoal")).register().addResource(ResourceRegistry.COAL);
        new ItemBasic(AbstractGame.internalRes("copper_cluster")).register().addResource(ResourceRegistry.RAW_COPPER);
        new ItemBasic(AbstractGame.internalRes("copper_grit")).register().addResource(ResourceRegistry.PARTLY_PROCESSED_COPPER);
        new ItemBasic(AbstractGame.internalRes("copper_ingot")).register().addResource(ResourceRegistry.PROCESSED_COPPER);
        new ItemTool(AbstractGame.internalRes("copper_pickaxe"), 8F).addToolType(ToolType.PICKAXE, 4).register();
        new ItemTool(AbstractGame.internalRes("copper_axe"), 8F).addToolType(ToolType.AXE, 4).register();
        new ItemTool(AbstractGame.internalRes("copper_shovel"), 4F).addToolType(ToolType.SHOVEL, 4).register();
        new ItemBasic(AbstractGame.internalRes("slag")).register().addResource(ResourceRegistry.SLAG);
        new ItemGlowCluster().register();

        new BiomeBasic(AbstractGame.internalRes("sky"), Integer.MAX_VALUE, 2, 1000).register();
        new BiomeBasic(AbstractGame.internalRes("grassland"), 1, -1, 1000).register();

        RockBottomAPI.ENTITY_REGISTRY.register(AbstractGame.internalRes("item"), EntityItem.class);

        RockBottomAPI.WORLD_GENERATORS.add(WorldGenHills.class);
        RockBottomAPI.WORLD_GENERATORS.add(WorldGenBasicUnderground.class);
        RockBottomAPI.WORLD_GENERATORS.add(WorldGenTrees.class);
        RockBottomAPI.WORLD_GENERATORS.add(WorldGenCoal.class);
        RockBottomAPI.WORLD_GENERATORS.add(WorldGenCopper.class);
        RockBottomAPI.WORLD_GENERATORS.add(WorldGenBasicCaves.class);
        RockBottomAPI.WORLD_GENERATORS.add(WorldGenGlow.class);
        RockBottomAPI.WORLD_GENERATORS.add(WorldGenPebbles.class);
    }
}
