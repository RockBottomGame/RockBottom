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

public final class RockBottomAPI{

    public static final Random RANDOM = new Random();

    public static final NameRegistry<Tile> TILE_REGISTRY = new NameRegistry<>("tile_registry");
    public static final NameRegistry<Item> ITEM_REGISTRY = new NameRegistry<>("item_registry");
    public static final NameRegistry<Class<? extends Entity>> ENTITY_REGISTRY = new NameRegistry<>("entity_registry");
    public static final IndexRegistry<Class<? extends DataPart>> PART_REGISTRY = new IndexRegistry<>("part_registry", Byte.MAX_VALUE);
    public static final IndexRegistry<Class<? extends IPacket>> PACKET_REGISTRY = new IndexRegistry<>("packet_registry", Byte.MAX_VALUE);
    public static final Map<String, Command> COMMAND_REGISTRY = new HashMap<>();

    private static IApiHandler apiHandler;
    private static INetHandler netHandler;
    private static IEventHandler eventHandler;
    private static IGameInstance gameInstance;
    private static IModLoader modLoader;

    public static IApiHandler getApiHandler(){
        return apiHandler;
    }

    public static INetHandler getNet(){
        return netHandler;
    }

    public static IEventHandler getEventHandler(){
        return eventHandler;
    }

    public static IGameInstance getGame(){
        return gameInstance;
    }

    public static IModLoader getModLoader(){
        return modLoader;
    }

    public static IResourceName createInternalRes(String resource){
        return createRes(gameInstance, resource);
    }

    public static IResourceName createRes(IMod mod, String resource){
        return modLoader.createResourceName(mod, resource);
    }

    public static IResourceName createRes(String combined){
        return modLoader.createResourceName(combined);
    }

    public static void setApiHandler(IApiHandler api){
        if(apiHandler == null){
            apiHandler = api;
        }
    }

    public static void setNetHandler(INetHandler net){
        if(netHandler == null){
            netHandler = net;
        }
    }

    public static void setEventHandler(IEventHandler event){
        if(eventHandler == null){
            eventHandler = event;
        }
    }

    public static void setGameInstance(IGameInstance game){
        if(gameInstance == null){
            gameInstance = game;
        }
    }

    public static void setModLoader(IModLoader mod){
        if(modLoader == null){
            modLoader = mod;
        }
    }
}

