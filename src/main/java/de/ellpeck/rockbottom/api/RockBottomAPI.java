package de.ellpeck.rockbottom.api;

import de.ellpeck.rockbottom.api.data.set.part.DataPart;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.util.reg.IndexRegistry;
import de.ellpeck.rockbottom.api.util.reg.NameRegistry;
import de.ellpeck.rockbottom.game.world.entity.Entity;

import java.util.Random;

public final class RockBottomAPI{

    public static final Random RANDOM = new Random();

    public static final NameRegistry<Tile> TILE_REGISTRY = new NameRegistry<>("tile_registry");
    public static final NameRegistry<Item> ITEM_REGISTRY = new NameRegistry<>("item_registry");
    public static final NameRegistry<Class<? extends Entity>> ENTITY_REGISTRY = new NameRegistry<>("entity_registry");
    public static final IndexRegistry<Class<? extends DataPart>> PART_REGISTRY = new IndexRegistry<>("part_registry", Byte.MAX_VALUE);

    private static boolean init;
    private static IApiHandler apiHandler;
    private static IGameInstance gameInstance;

    public static IApiHandler getApiHandler(){
        return apiHandler;
    }

    public static IGameInstance getGame(){
        return gameInstance;
    }

    public static void set(IApiHandler handler, IGameInstance instance){
        if(!init){
            apiHandler = handler;
            gameInstance = instance;

            init = true;
        }
        else{
            throw new UnsupportedOperationException("Cannot set API Handler or Game Instance when it has already been set!");
        }
    }
}

