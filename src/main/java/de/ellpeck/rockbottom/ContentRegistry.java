package de.ellpeck.rockbottom;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.EntityItem;
import de.ellpeck.rockbottom.api.item.ItemBasic;
import de.ellpeck.rockbottom.api.item.ItemMeta;
import de.ellpeck.rockbottom.api.item.ToolType;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.world.gen.biome.BiomeBasic;
import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.item.ItemTool;
import de.ellpeck.rockbottom.world.gen.cave.WorldGenBasicCaves;
import de.ellpeck.rockbottom.world.gen.feature.WorldGenTrees;
import de.ellpeck.rockbottom.world.gen.landscape.WorldGenBasicUnderground;
import de.ellpeck.rockbottom.world.gen.landscape.WorldGenHills;
import de.ellpeck.rockbottom.world.gen.ore.WorldGenCoal;
import de.ellpeck.rockbottom.world.gen.ore.WorldGenCopper;
import de.ellpeck.rockbottom.world.tile.*;
import org.newdawn.slick.util.Log;

public final class ContentRegistry{

    public static void init(){
        new TileAir().register();
        new TileDirt().addEffectiveTool(ToolType.SHOVEL, 1).setForceDrop().register();
        new TileBasic(AbstractGame.internalRes("rock")).setHardness(5F).addEffectiveTool(ToolType.PICKAXE, 1).register();
        new TileGrass().addEffectiveTool(ToolType.SHOVEL, 1).setForceDrop().register();
        new TileBasic(AbstractGame.internalRes("wood_boards")).setHardness(2F).addEffectiveTool(ToolType.AXE, 1).setForceDrop().register();
        new TileTorch().setHardness(0F).setForceDrop().register();
        new TileChest().addEffectiveTool(ToolType.AXE, 1).setForceDrop().register();
        new TileLog().setHardness(3F).addEffectiveTool(ToolType.AXE, 1).setForceDrop().register();
        new TileLeaves().setHardness(0.25F).setForceDrop().register();
        new TileCoalOre().setHardness(12F).addEffectiveTool(ToolType.PICKAXE, 1).register();
        new TileCopperOre().setHardness(18F).addEffectiveTool(ToolType.PICKAXE, 2).register();
        new TileSmelter().setHardness(20F).addEffectiveTool(ToolType.PICKAXE, 2).register();
        new TileSeparator().setHardness(20F).addEffectiveTool(ToolType.PICKAXE, 2).register();
        new TileSapling().setHardness(0F).setForceDrop().register();
        new TileLadder().setHardness(2F).setForceDrop().addEffectiveTool(ToolType.AXE, 1).register();

        new ItemTool(AbstractGame.internalRes("super_tool"), 50F).addToolType(ToolType.AXE, 100).addToolType(ToolType.PICKAXE, 100).addToolType(ToolType.SHOVEL, 100).register();
        new ItemTool(AbstractGame.internalRes("pick_wood"), 2F).addToolType(ToolType.PICKAXE, 1).register();
        new ItemTool(AbstractGame.internalRes("pick_rock"), 4F).addToolType(ToolType.PICKAXE, 2).register();
        new ItemMeta(AbstractGame.internalRes("coal")).addSubItem(AbstractGame.internalRes("charcoal")).register();
        new ItemBasic(AbstractGame.internalRes("copper_cluster")).register();
        new ItemBasic(AbstractGame.internalRes("copper_grit")).register();
        new ItemBasic(AbstractGame.internalRes("copper_ingot")).register();
        new ItemBasic(AbstractGame.internalRes("slag")).register();

        new BiomeBasic(AbstractGame.internalRes("sky"), Integer.MAX_VALUE, 2, 1000).register();
        new BiomeBasic(AbstractGame.internalRes("grassland"), 1, -1, 1000).register();

        RockBottomAPI.ENTITY_REGISTRY.register(AbstractGame.internalRes("item"), EntityItem.class);

        RockBottomAPI.WORLD_GENERATORS.add(WorldGenHills.class);
        RockBottomAPI.WORLD_GENERATORS.add(WorldGenBasicUnderground.class);
        RockBottomAPI.WORLD_GENERATORS.add(WorldGenTrees.class);
        RockBottomAPI.WORLD_GENERATORS.add(WorldGenCoal.class);
        RockBottomAPI.WORLD_GENERATORS.add(WorldGenCopper.class);
        RockBottomAPI.WORLD_GENERATORS.add(WorldGenBasicCaves.class);

        Log.info("Registered "+RockBottomAPI.TILE_REGISTRY.getSize()+" tiles!");
        Log.info("Registered "+RockBottomAPI.ITEM_REGISTRY.getSize()+" items!");
        Log.info("Registered "+RockBottomAPI.ENTITY_REGISTRY.getSize()+" entity types!");
    }
}
