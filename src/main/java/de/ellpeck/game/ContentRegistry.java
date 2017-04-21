package de.ellpeck.game;

import de.ellpeck.game.item.Item;
import de.ellpeck.game.item.ItemTool;
import de.ellpeck.game.item.ToolType;
import de.ellpeck.game.util.Registry;
import de.ellpeck.game.world.entity.Entity;
import de.ellpeck.game.world.entity.EntityItem;
import de.ellpeck.game.world.tile.*;
import org.newdawn.slick.util.Log;

public final class ContentRegistry{

    public static final Registry<Tile> TILE_REGISTRY = new Registry<>("tile_registry", Short.MAX_VALUE);
    public static final Registry<Item> ITEM_REGISTRY = new Registry<>("item_registry", Short.MAX_VALUE);
    public static final Registry<Class<? extends Entity>> ENTITY_REGISTRY = new Registry<>("entity_registry", Short.MAX_VALUE);

    public static final Tile TILE_AIR = new TileAir(0).register();
    public static final Tile TILE_DIRT = new TileDirt(1).addEffectiveTool(ToolType.SHOVEL, 1).setForceDrop().register();
    public static final Tile TILE_ROCK = new TileBasic(2, "rock").setHardness(5F).addEffectiveTool(ToolType.PICKAXE, 1).register();
    public static final Tile TILE_GRASS = new TileGrass(3).addEffectiveTool(ToolType.SHOVEL, 1).setForceDrop().register();
    public static final Tile TILE_SMELTER = new TileSmelter(4).register();
    public static final Tile TILE_TORCH = new TileTorch(5).setHardness(0F).setForceDrop().register();
    //public static final Tile TILE_WATER = new TileLiquid(6, "water", 3).register();
    public static final Tile TILE_CHEST = new TileChest(7).addEffectiveTool(ToolType.AXE, 1).setForceDrop().register();
    public static final Tile TILE_LOG = new TileLog(8).setHardness(2F).addEffectiveTool(ToolType.AXE, 1).setForceDrop().register();
    public static final Tile TILE_LEAVES = new TileLeaves(9).setHardness(0.25F).setForceDrop().register();
    public static final Tile TILE_SAPLING = new TileSapling(10).setHardness(0F).setForceDrop().register();
    public static final Tile TILE_COAL_ORE = new TileBasic(11, "coal_ore").setHardness(8F).addEffectiveTool(ToolType.PICKAXE, 2).register();

    public static final Item ITEM_SUPER_TOOL = new ItemTool(0, "super_tool").addToolType(ToolType.PICKAXE, 100).addToolType(ToolType.SHOVEL, 100).register();
    public static final Item ITEM_WOOD_PICK = new ItemTool(1, "pick_wood").addToolType(ToolType.PICKAXE, 1).register();
    public static final Item ITEM_ROCK_PICK = new ItemTool(2, "pick_rock").addToolType(ToolType.PICKAXE, 2).register();

    public static void init(){
        ENTITY_REGISTRY.register(0, EntityItem.class);

        Log.info("Registered "+TILE_REGISTRY.getSize()+" tiles!");
        Log.info("Registered "+ITEM_REGISTRY.getSize()+" items!");
        Log.info("Registered "+ENTITY_REGISTRY.getSize()+" entity types!");
    }
}
