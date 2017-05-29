package de.ellpeck.rockbottom;

import de.ellpeck.rockbottom.item.Item;
import de.ellpeck.rockbottom.item.ItemBasic;
import de.ellpeck.rockbottom.item.ItemTool;
import de.ellpeck.rockbottom.item.ToolType;
import de.ellpeck.rockbottom.util.reg.NameRegistry;
import de.ellpeck.rockbottom.world.entity.Entity;
import de.ellpeck.rockbottom.world.entity.EntityItem;
import de.ellpeck.rockbottom.world.tile.*;
import org.newdawn.slick.util.Log;

public final class ContentRegistry{

    public static final NameRegistry<Tile> TILE_REGISTRY = new NameRegistry<>("tile_registry");
    public static final NameRegistry<Item> ITEM_REGISTRY = new NameRegistry<>("item_registry");
    public static final NameRegistry<Class<? extends Entity>> ENTITY_REGISTRY = new NameRegistry<>("entity_registry");

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
    public static final Tile TILE_COAL_ORE = new TileCoalOre().setHardness(8F).addEffectiveTool(ToolType.PICKAXE, 1).register();
    public static final Tile TILE_COPPER_ORE = new TileCopperOre().setHardness(16F).addEffectiveTool(ToolType.PICKAXE, 2).register();
    public static final Tile TILE_SMELTER = new TileSmelter().setHardness(20F).addEffectiveTool(ToolType.PICKAXE, 2).register();
    public static final Tile TILE_SEPARATOR = new TileSeparator().setHardness(20F).addEffectiveTool(ToolType.PICKAXE, 2).register();

    public static final Item ITEM_SUPER_TOOL = new ItemTool("super_tool").addToolType(ToolType.AXE, 100).addToolType(ToolType.PICKAXE, 100).addToolType(ToolType.SHOVEL, 100).register();
    public static final Item ITEM_WOOD_PICK = new ItemTool("pick_wood").addToolType(ToolType.PICKAXE, 1).register();
    public static final Item ITEM_ROCK_PICK = new ItemTool("pick_rock").addToolType(ToolType.PICKAXE, 2).register();
    public static final Item ITEM_COAL = new ItemBasic("coal").register();
    public static final Item ITEM_COPPER_CLUSTER = new ItemBasic("copper_cluster").register();
    public static final Item ITEM_COPPER_GRIT = new ItemBasic("copper_grit").register();
    public static final Item ITEM_COPPER_INGOT = new ItemBasic("copper_ingot").register();
    public static final Item ITEM_SLAG = new ItemBasic("slag").register();

    public static void init(){
        ENTITY_REGISTRY.register("item", EntityItem.class);

        Log.info("Registered "+TILE_REGISTRY.getSize()+" tiles!");
        Log.info("Registered "+ITEM_REGISTRY.getSize()+" items!");
        Log.info("Registered "+ENTITY_REGISTRY.getSize()+" entity types!");
    }
}
