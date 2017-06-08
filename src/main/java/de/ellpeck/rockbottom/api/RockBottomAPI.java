package de.ellpeck.rockbottom.api;

import de.ellpeck.rockbottom.api.util.reg.NameRegistry;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.game.world.entity.Entity;
import de.ellpeck.rockbottom.api.tile.Tile;

public final class RockBottomAPI{

    public static final NameRegistry<Tile> TILE_REGISTRY = new NameRegistry<>("tile_registry");
    public static final NameRegistry<Item> ITEM_REGISTRY = new NameRegistry<>("item_registry");
    public static final NameRegistry<Class<? extends Entity>> ENTITY_REGISTRY = new NameRegistry<>("entity_registry");

}
