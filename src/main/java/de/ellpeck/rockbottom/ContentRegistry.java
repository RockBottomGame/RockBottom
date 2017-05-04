package de.ellpeck.rockbottom;

import de.ellpeck.rockbottom.item.Item;
import de.ellpeck.rockbottom.item.ItemBasic;
import de.ellpeck.rockbottom.item.ItemTool;
import de.ellpeck.rockbottom.item.ToolType;
import de.ellpeck.rockbottom.util.Registry;
import de.ellpeck.rockbottom.world.entity.Entity;
import de.ellpeck.rockbottom.world.entity.EntityItem;
import de.ellpeck.rockbottom.world.tile.*;
import org.newdawn.slick.util.Log;

public final class ContentRegistry{

    public static final Registry<Tile> TILE_REGISTRY = new Registry<>("tile_registry", Short.MAX_VALUE);
    public static final Registry<Item> ITEM_REGISTRY = new Registry<>("item_registry", Short.MAX_VALUE);
    public static final Registry<Class<? extends Entity>> ENTITY_REGISTRY = new Registry<>("entity_registry", Short.MAX_VALUE);

    public static final Tile TILE_AIR = new TileAir(0).register();
    public static final Tile TILE_DIRT = new TileDirt(1).addEffectiveTool(ToolType.SHOVEL, 1).setForceDrop().register();
    public static final Tile TILE_ROCK = new TileBasic(2, "rock").setHardness(5F).addEffectiveTool(ToolType.PICKAXE, 1).register();
    public static final Tile TILE_GRASS = new TileGrass(3).addEffectiveTool(ToolType.SHOVEL, 1).setForceDrop().register();
    public static final Tile TILE_TORCH = new TileTorch(5).setHardness(0F).setForceDrop().register();
    public static final Tile TILE_CHEST = new TileChest(7).addEffectiveTool(ToolType.AXE, 1).setForceDrop().register();
    public static final Tile TILE_LOG = new TileLog(8).setHardness(2F).addEffectiveTool(ToolType.AXE, 1).setForceDrop().register();
    public static final Tile TILE_LEAVES = new TileLeaves(9).setHardness(0.25F).setForceDrop().register();
    public static final Tile TILE_SAPLING = new TileSapling(10).setHardness(0F).setForceDrop().register();
    public static final Tile TILE_COAL_ORE = new TileCoalOre(11).setHardness(8F).addEffectiveTool(ToolType.PICKAXE, 1).register();
    public static final Tile TILE_COPPER_ORE = new TileCopperOre(12).setHardness(16F).addEffectiveTool(ToolType.PICKAXE, 2).register();
    public static final Tile TILE_SMELTER = new TileSmelter(13).setHardness(20F).addEffectiveTool(ToolType.PICKAXE, 2).register();

    public static final Item ITEM_SUPER_TOOL = new ItemTool(8192, "super_tool").addToolType(ToolType.PICKAXE, 100).addToolType(ToolType.SHOVEL, 100).register();
    public static final Item ITEM_WOOD_PICK = new ItemTool(8193, "pick_wood").addToolType(ToolType.PICKAXE, 1).register();
    public static final Item ITEM_ROCK_PICK = new ItemTool(8194, "pick_rock").addToolType(ToolType.PICKAXE, 2).register();
    public static final Item ITEM_COAL = new ItemBasic(8195, "coal").register();
    public static final Item ITEM_COPPER_CLUSTER = new ItemBasic(8196, "copper_cluster").register();
    public static final Item ITEM_COPPER_GRIT = new ItemBasic(8197, "copper_grit").register();
    public static final Item ITEM_COPPER_INGOT = new ItemBasic(8198, "copper_ingot").register();
    public static final Item ITEM_SLAG = new ItemBasic(8199, "slag").register();

    public static void init(){
        ENTITY_REGISTRY.register(0, EntityItem.class);

        Log.info("Registered "+TILE_REGISTRY.getSize()+" tiles!");
        Log.info("Registered "+ITEM_REGISTRY.getSize()+" items!");
        Log.info("Registered "+ENTITY_REGISTRY.getSize()+" entity types!");
    }
}
