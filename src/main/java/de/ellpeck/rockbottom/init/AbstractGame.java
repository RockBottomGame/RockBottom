package de.ellpeck.rockbottom.init;

import de.ellpeck.rockbottom.ContentRegistry;
import de.ellpeck.rockbottom.Main;
import de.ellpeck.rockbottom.api.*;
import de.ellpeck.rockbottom.api.data.IDataManager;
import de.ellpeck.rockbottom.api.event.IEventHandler;
import de.ellpeck.rockbottom.api.event.impl.WorldLoadEvent;
import de.ellpeck.rockbottom.api.event.impl.WorldUnloadEvent;
import de.ellpeck.rockbottom.api.mod.IModLoader;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.util.reg.NameRegistry;
import de.ellpeck.rockbottom.api.util.reg.NameToIndexInfo;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.WorldInfo;
import de.ellpeck.rockbottom.apiimpl.ApiHandler;
import de.ellpeck.rockbottom.apiimpl.EventHandler;
import de.ellpeck.rockbottom.construction.ConstructionRegistry;
import de.ellpeck.rockbottom.data.DataManager;
import de.ellpeck.rockbottom.mod.ModLoader;
import de.ellpeck.rockbottom.net.NetHandler;
import de.ellpeck.rockbottom.net.chat.ChatLog;
import de.ellpeck.rockbottom.api.world.DynamicRegistryInfo;
import de.ellpeck.rockbottom.world.World;
import org.newdawn.slick.util.Log;

import java.io.File;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class AbstractGame implements IGameInstance{

    public static final String VERSION = "0.1.1";
    public static final String NAME = "Rock Bottom";
    public static final String ID = "rockbottom";
    private static final int INTERVAL = 1000/Constants.TARGET_TPS;
    private final List<Supplier<Boolean>> scheduledActions = new ArrayList<>();
    public boolean isRunning = true;
    protected DataManager dataManager;
    protected ChatLog chatLog;
    protected World world;
    private int tpsAverage;
    private int fpsAverage;
    private int totalTicks;

    public static void doInit(AbstractGame game){
        Internals internals = new Internals();
        RockBottomAPI.setInternals(internals);

        internals.setGame(game);
        internals.setMod(new ModLoader());
        internals.setApi(new ApiHandler());
        internals.setEvent(new EventHandler());
        internals.setNet(new NetHandler());

        try{
            game.init();

            long lastPollTime = 0;
            int tpsAccumulator = 0;
            int fpsAccumulator = 0;

            long lastDeltaTime = Util.getTimeMillis();
            int deltaAccumulator = 0;

            while(game.isRunning){
                long time = Util.getTimeMillis();

                int delta = (int)(time-lastDeltaTime);
                lastDeltaTime = time;

                deltaAccumulator += delta;
                if(deltaAccumulator >= INTERVAL){
                    long updates = deltaAccumulator/INTERVAL;
                    for(int i = 0; i < updates; i++){
                        game.updateTicked();
                        tpsAccumulator++;

                        deltaAccumulator -= INTERVAL;
                    }
                }

                game.updateTickless(delta);
                fpsAccumulator++;

                if(time-lastPollTime >= 1000){
                    game.tpsAverage = tpsAccumulator;
                    game.fpsAverage = fpsAccumulator;

                    tpsAccumulator = 0;
                    fpsAccumulator = 0;

                    lastPollTime = time;
                }

                try{
                    Thread.sleep(1);
                }
                catch(InterruptedException e){
                    RockBottomAPI.logger().fine("Failed to sleep in main game loop");
                }
            }
        }
        finally{
            game.shutdown();
            RockBottomAPI.logger().info("Game shutting down");
        }
    }

    public abstract int getAutosaveInterval();

    public void shutdown(){
        RockBottomAPI.getNet().shutdown();
    }

    @Override
    public int getTotalTicks(){
        return this.totalTicks;
    }

    private void updateTicked(){
        this.totalTicks++;

        synchronized(this.scheduledActions){
            for(int i = 0; i < this.scheduledActions.size(); i++){
                Supplier<Boolean> action = this.scheduledActions.get(i);

                if(action.get()){
                    this.scheduledActions.remove(i);
                    i--;
                }
            }
        }

        if(RockBottomAPI.getNet().isClient()){
            if(!RockBottomAPI.getNet().isConnectedToServer()){
                this.quitWorld();
            }
        }

        this.update();
    }

    public void init(){
        this.dataManager = new DataManager(this);

        IModLoader modLoader = RockBottomAPI.getModLoader();
        modLoader.loadJarMods(this.dataManager.getModsDir());
        if(Main.unpackedModsDir != null){
            modLoader.loadUnpackedMods(Main.unpackedModsDir);
        }
        modLoader.sortMods();

        modLoader.prePreInit();
        modLoader.preInit();
        modLoader.init();
        modLoader.postInit();
        modLoader.postPostInit();

        RockBottomAPI.logger().info("Registered "+RockBottomAPI.TILE_REGISTRY.getSize()+" tiles!");
        RockBottomAPI.logger().info("Registered "+RockBottomAPI.TILE_STATE_REGISTRY.getSize()+" tile states!");
        RockBottomAPI.logger().info("Registered "+RockBottomAPI.ITEM_REGISTRY.getSize()+" items!");
        RockBottomAPI.logger().info("Registered "+RockBottomAPI.ENTITY_REGISTRY.getSize()+" entities!");

        this.quitWorld();
    }

    protected void update(){
        if(this.world != null){
            this.world.update(this);
        }
    }

    @Override
    public void init(IGameInstance game, IApiHandler apiHandler, IEventHandler eventHandler){
        ContentRegistry.init();
        ConstructionRegistry.init();
    }

    @Override
    public void postInit(IGameInstance game, IApiHandler apiHandler, IEventHandler eventHandler){
        this.chatLog = new ChatLog();
    }

    @Override
    public void startWorld(File worldFile, WorldInfo info){
        RockBottomAPI.logger().info("Starting world with file "+worldFile);

        NameToIndexInfo tileRegInfo = new NameToIndexInfo("tile_reg_world", new File(worldFile, "tile_reg_info.dat"), Integer.MAX_VALUE);
        this.populateIndexInfo(tileRegInfo, RockBottomAPI.TILE_STATE_REGISTRY);

        NameToIndexInfo biomeRegInfo = new NameToIndexInfo("biome_reg_world", new File(worldFile, "biome_reg_info.dat"), Short.MAX_VALUE);
        this.populateIndexInfo(biomeRegInfo, RockBottomAPI.BIOME_REGISTRY);

        DynamicRegistryInfo regInfo = new DynamicRegistryInfo(tileRegInfo, biomeRegInfo);

        this.world = new World(info, regInfo);
        this.world.initFiles(worldFile);

        RockBottomAPI.getEventHandler().fireEvent(new WorldLoadEvent(this.world, info, regInfo));
    }

    private void populateIndexInfo(NameToIndexInfo info, NameRegistry reg){
        this.dataManager.loadPropSettings(info);
        info.populate(reg);

        if(info.needsSave()){
            this.dataManager.savePropSettings(info);
        }
    }

    @Override
    public void quitWorld(){
        RockBottomAPI.getNet().shutdown();

        if(this.world != null){
            RockBottomAPI.logger().info("Quitting current world");

            RockBottomAPI.getEventHandler().fireEvent(new WorldUnloadEvent(this.world));
            this.world = null;
        }

        if(this.chatLog != null){
            this.chatLog.clear();
        }
    }

    @Override
    public void scheduleAction(Supplier<Boolean> action){
        synchronized(this.scheduledActions){
            this.scheduledActions.add(action);
        }
    }

    protected void updateTickless(int delta){

    }

    @Override
    public IDataManager getDataManager(){
        return this.dataManager;
    }

    @Override
    public ChatLog getChatLog(){
        return this.chatLog;
    }

    @Override
    public IWorld getWorld(){
        return this.world;
    }

    @Override
    public int getTpsAverage(){
        return this.tpsAverage;
    }

    @Override
    public int getFpsAverage(){
        return this.fpsAverage;
    }

    @Override
    public URLClassLoader getClassLoader(){
        return Main.classLoader;
    }

    @Override
    public String getDisplayName(){
        return NAME;
    }

    @Override
    public String getId(){
        return ID;
    }

    @Override
    public String getVersion(){
        return VERSION;
    }

    @Override
    public String getResourceLocation(){
        return "/assets/rockbottom";
    }

    @Override
    public int getSortingPriority(){
        return Integer.MAX_VALUE;
    }

    @Override
    public String getDescription(){
        return "The base game and all its features";
    }

    @Override
    public boolean isDisableable(){
        return false;
    }

    @Override
    public boolean isRequiredOnClient(){
        return true;
    }

    @Override
    public boolean isRequiredOnServer(){
        return true;
    }

    @Override
    public void exit(){
        this.quitWorld();
        this.isRunning = false;
    }
}
