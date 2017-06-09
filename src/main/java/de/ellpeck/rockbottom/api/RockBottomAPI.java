package de.ellpeck.rockbottom.api;

import de.ellpeck.rockbottom.api.data.set.part.DataPart;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.event.IEventHandler;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.mod.IModLoader;
import de.ellpeck.rockbottom.api.net.INetHandler;
import de.ellpeck.rockbottom.api.net.chat.Command;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.util.reg.IndexRegistry;
import de.ellpeck.rockbottom.api.util.reg.NameRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * The main API class
 * <br> Use this to access important game values
 */
public final class RockBottomAPI{

    /**
     * An unseeded {@link Random} for general use
     */
    public static final Random RANDOM = new Random();

    /**
     * The registry for {@link Tile}
     * <br> Use this to register custom tiles
     */
    public static final NameRegistry<Tile> TILE_REGISTRY = new NameRegistry<>("tile_registry");
    /**
     * The registry for {@link Item}
     * <br> Use this to register custom items
     */
    public static final NameRegistry<Item> ITEM_REGISTRY = new NameRegistry<>("item_registry");
    /**
     * The registry for {@link Entity}
     * <br> Use this to register custom entity types
     */
    public static final NameRegistry<Class<? extends Entity>> ENTITY_REGISTRY = new NameRegistry<>("entity_registry");
    /**
     * The registry for {@link de.ellpeck.rockbottom.api.data.set.DataSet} parts
     * <br> Use {@link IndexRegistry#getNextFreeId} to register a custom part
     */
    public static final IndexRegistry<Class<? extends DataPart>> PART_REGISTRY = new IndexRegistry<>("part_registry", Byte.MAX_VALUE);
    /**
     * The registry for {@link IPacket}
     * <br> Use {@link IndexRegistry#getNextFreeId()} too register a custom packet
     */
    public static final IndexRegistry<Class<? extends IPacket>> PACKET_REGISTRY = new IndexRegistry<>("packet_registry", Byte.MAX_VALUE);
    /**
     * The registry for {@link Command}
     * <br> Use this to register custom commands
     */
    public static final Map<String, Command> COMMAND_REGISTRY = new HashMap<>();

    private static IApiHandler apiHandler;
    private static INetHandler netHandler;
    private static IEventHandler eventHandler;
    private static IGameInstance gameInstance;
    private static IModLoader modLoader;

    /**
     * See {@link IApiHandler} for more information
     *
     * @return The {@link IApiHandler}
     */
    public static IApiHandler getApiHandler(){
        return apiHandler;
    }

    /**
     * See {@link INetHandler} for more information
     *
     * @return The {@link INetHandler}
     */
    public static INetHandler getNet(){
        return netHandler;
    }

    /**
     * See {@link IEventHandler} for more information
     *
     * @return The {@link IEventHandler}
     */
    public static IEventHandler getEventHandler(){
        return eventHandler;
    }

    /**
     * See {@link IGameInstance} for more information
     *
     * @return The {@link IGameInstance}
     */
    public static IGameInstance getGame(){
        return gameInstance;
    }

    /**
     * See {@link IModLoader} for more information
     *
     * @return The {@link IModLoader}
     */
    public static IModLoader getModLoader(){
        return modLoader;
    }

    /**
     * Creates an internal {@link IResourceName} to be used for vanilla game related resources
     *
     * @param resource The name of the resource to create
     * @return The {@link IResourceName}
     */
    public static IResourceName createInternalRes(String resource){
        return createRes(gameInstance, resource);
    }

    /**
     * Creates an {@link IResourceName} to be used by the specified {@link IMod}
     *
     * @param mod      The mod to use this resource name
     * @param resource The name of the resource to create
     * @return The {@link IResourceName}
     */
    public static IResourceName createRes(IMod mod, String resource){
        return modLoader.createResourceName(mod, resource);
    }

    /**
     * Creates an {@link IResourceName} out of a combined string of the mod id
     * and the resource itself
     *
     * @throws IllegalArgumentException if the specified string cannot be parsed as an {@link IResourceName}
     * @param combined The combined name
     * @return The parsed {@link IResourceName}
     */
    public static IResourceName createRes(String combined){
        return modLoader.createResourceName(combined);
    }

    /**
     * For game internal use only
     */
    public static void setApiHandler(IApiHandler api){
        if(apiHandler == null){
            apiHandler = api;
        }
    }

    /**
     * For game internal use only
     */
    public static void setNetHandler(INetHandler net){
        if(netHandler == null){
            netHandler = net;
        }
    }

    /**
     * For game internal use only
     */
    public static void setEventHandler(IEventHandler event){
        if(eventHandler == null){
            eventHandler = event;
        }
    }

    /**
     * For game internal use only
     */
    public static void setGameInstance(IGameInstance game){
        if(gameInstance == null){
            gameInstance = game;
        }
    }

    /**
     * For game internal use only
     */
    public static void setModLoader(IModLoader mod){
        if(modLoader == null){
            modLoader = mod;
        }
    }
}

