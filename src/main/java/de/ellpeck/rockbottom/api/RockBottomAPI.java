package de.ellpeck.rockbottom.api;

import de.ellpeck.rockbottom.api.data.set.part.DataPart;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.net.INetHandler;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.util.reg.IndexRegistry;
import de.ellpeck.rockbottom.api.util.reg.NameRegistry;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.net.chat.Command;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public final class RockBottomAPI{

    public static final Random RANDOM = new Random();

    public static final NameRegistry<Tile> TILE_REGISTRY = new NameRegistry<>("tile_registry");
    public static final NameRegistry<Item> ITEM_REGISTRY = new NameRegistry<>("item_registry");
    public static final NameRegistry<Class<? extends Entity>> ENTITY_REGISTRY = new NameRegistry<>("entity_registry");
    public static final IndexRegistry<Class<? extends DataPart>> PART_REGISTRY = new IndexRegistry<>("part_registry", Byte.MAX_VALUE);
    public static final IndexRegistry<Class<? extends IPacket>> PACKET_REGISTRY = new IndexRegistry<>("packet_registry", Byte.MAX_VALUE);
    public static final Map<String, Command> COMMAND_REGISTRY = new HashMap<>();

    private static boolean init;
    private static IApiHandler apiHandler;
    private static INetHandler netHandler;
    private static IGameInstance gameInstance;

    public static IApiHandler getApiHandler(){
        return apiHandler;
    }

    public static IGameInstance getGame(){
        return gameInstance;
    }

    public static INetHandler getNet(){
        return netHandler;
    }

    public static void set(IApiHandler api, INetHandler net, IGameInstance instance){
        if(!init){
            apiHandler = api;
            netHandler = net;
            gameInstance = instance;

            init = true;
        }
        else{
            throw new UnsupportedOperationException("Cannot set API values after they have been set!");
        }
    }
}

