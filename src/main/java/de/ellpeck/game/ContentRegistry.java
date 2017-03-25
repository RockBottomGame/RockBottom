package de.ellpeck.game;

import de.ellpeck.game.item.Item;
import de.ellpeck.game.util.Registry;
import de.ellpeck.game.world.entity.Entity;
import de.ellpeck.game.world.entity.EntityItem;
import de.ellpeck.game.world.entity.player.EntityPlayer;
import de.ellpeck.game.world.tile.*;
import org.newdawn.slick.util.Log;

public final class ContentRegistry{

    public static final Registry<Tile> TILE_REGISTRY = new Registry<>();
    public static final Registry<Item> ITEM_REGISTRY = new Registry<>();
    public static final Registry<Class<? extends Entity>> ENTITY_REGISTRY = new Registry<>();

    public static final Tile TILE_AIR = new TileAir(0).register();
    public static final Tile TILE_DIRT = new TileDirt(1).register();
    public static final Tile TILE_ROCK = new TileBasic(2, "rock").setHardness(5F).register();
    public static final Tile TILE_GRASS = new TileGrass(3).register();
    public static final Tile TILE_SMELTER = new TileSmelter(4).register();

    public static void init(){
        ENTITY_REGISTRY.register(0, EntityPlayer.class);
        ENTITY_REGISTRY.register(1, EntityItem.class);

        Log.info("Registered "+TILE_REGISTRY.getSize()+" tiles!");
        Log.info("Registered "+ITEM_REGISTRY.getSize()+" items!");
        Log.info("Registered "+ENTITY_REGISTRY.getSize()+" entity types!");
    }
}
