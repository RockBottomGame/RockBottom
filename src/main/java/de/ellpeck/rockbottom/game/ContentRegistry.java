package de.ellpeck.rockbottom.game;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.game.world.tile.*;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.item.ItemBasic;
import de.ellpeck.rockbottom.game.item.ItemTool;
import de.ellpeck.rockbottom.api.item.ToolType;
import de.ellpeck.rockbottom.api.entity.EntityItem;
import org.newdawn.slick.util.Log;

public final class ContentRegistry{

    public static final Tile TILE_AIR = new TileAir().register();
    public static final Tile TILE_DIRT = new TileDirt().addEffectiveTool(ToolType.SHOVEL, 1).setForceDrop().register();
    public static final Tile TILE_ROCK = new TileBasic("rock").setHardness(5F).addEffectiveTool(ToolType.PICKAXE, 1).register();
    public static final Tile TILE_GRASS = new TileGrass().addEffectiveTool(ToolType.SHOVEL, 1).setForceDrop().register();
    public static final Tile TILE_WOOD_BOARDS = new TileBasic("wood_boards").setHardness(2F).addEffectiveTool(ToolType.AXE, 1).setForceDrop().register();
    public static final Tile TILE_TORCH = new TileTorch().setHardness(0F).setForceDrop().register();
    public static final Tile TILE_CHEST = new TileChest().addEffectiveTool(ToolType.AXE, 1).setForceDrop().register();
    public static final Tile TILE_LOG = new TileLog().setHardness(3F).addEffectiveTool(ToolType.AXE, 1).setForceDrop().register();
    public static final Tile TILE_LEAVES = new TileLeaves().setHardness(0.25F).setForceDrop().register();
    public static final Tile TILE_SAPLING = new TileSapling().setHardness(0F).setForceDrop().register();
    public static final Tile TILE_COAL_ORE = new TileCoalOre().setHardness(12F).addEffectiveTool(ToolType.PICKAXE, 1).register();
    public static final Tile TILE_COPPER_ORE = new TileCopperOre().setHardness(18F).addEffectiveTool(ToolType.PICKAXE, 2).register();
    public static final Tile TILE_SMELTER = new TileSmelter().setHardness(20F).addEffectiveTool(ToolType.PICKAXE, 2).register();
    public static final Tile TILE_SEPARATOR = new TileSeparator().setHardness(20F).addEffectiveTool(ToolType.PICKAXE, 2).register();

    public static final Item ITEM_SUPER_TOOL = new ItemTool("super_tool", 50F).addToolType(ToolType.AXE, 100).addToolType(ToolType.PICKAXE, 100).addToolType(ToolType.SHOVEL, 100).register();
    public static final Item ITEM_WOOD_PICK = new ItemTool("pick_wood", 2F).addToolType(ToolType.PICKAXE, 1).register();
    public static final Item ITEM_ROCK_PICK = new ItemTool("pick_rock", 4F).addToolType(ToolType.PICKAXE, 2).register();
    public static final Item ITEM_COAL = new ItemBasic("coal").register();
    public static final Item ITEM_COPPER_CLUSTER = new ItemBasic("copper_cluster").register();
    public static final Item ITEM_COPPER_GRIT = new ItemBasic("copper_grit").register();
    public static final Item ITEM_COPPER_INGOT = new ItemBasic("copper_ingot").register();
    public static final Item ITEM_SLAG = new ItemBasic("slag").register();

    public static void init(){
        RockBottomAPI.ENTITY_REGISTRY.register("item", EntityItem.class);

        Log.info("Registered "+RockBottomAPI.TILE_REGISTRY.getSize()+" tiles!");
        Log.info("Registered "+RockBottomAPI.ITEM_REGISTRY.getSize()+" items!");
        Log.info("Registered "+RockBottomAPI.ENTITY_REGISTRY.getSize()+" entity types!");
    }
}
