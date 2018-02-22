package de.ellpeck.rockbottom.init;

import de.ellpeck.rockbottom.ContentRegistry;
import de.ellpeck.rockbottom.Main;
import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.IApiHandler;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.resource.IResourceRegistry;
import de.ellpeck.rockbottom.api.data.IDataManager;
import de.ellpeck.rockbottom.api.event.IEventHandler;
import de.ellpeck.rockbottom.api.event.impl.WorldCreationEvent;
import de.ellpeck.rockbottom.api.event.impl.WorldLoadEvent;
import de.ellpeck.rockbottom.api.event.impl.WorldUnloadEvent;
import de.ellpeck.rockbottom.api.internal.Internals;
import de.ellpeck.rockbottom.api.mod.IModLoader;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IRegistry;
import de.ellpeck.rockbottom.api.util.reg.NameRegistry;
import de.ellpeck.rockbottom.api.util.reg.NameToIndexInfo;
import de.ellpeck.rockbottom.api.world.DynamicRegistryInfo;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.WorldInfo;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.apiimpl.ApiHandler;
import de.ellpeck.rockbottom.apiimpl.EventHandler;
import de.ellpeck.rockbottom.apiimpl.InternalHooks;
import de.ellpeck.rockbottom.apiimpl.ResourceRegistry;
import de.ellpeck.rockbottom.construction.ConstructionRegistry;
import de.ellpeck.rockbottom.data.DataManager;
import de.ellpeck.rockbottom.mod.ModLoader;
import de.ellpeck.rockbottom.net.NetHandler;
import de.ellpeck.rockbottom.net.chat.ChatLog;
import de.ellpeck.rockbottom.world.World;

import java.io.File;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.logging.Level;

public abstract class AbstractGame implements IGameInstance{

    public static final String VERSION = "0.2.3";
    public static final String NAME = "Rock Bottom";
    public static final String ID = "rockbottom";
    private static final int INTERVAL = 1000/Constants.TARGET_TPS;
    private final List<EnqueuedAction> enqueuedActions = new ArrayList<>();
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
        internals.setHooks(new InternalHooks());
        internals.setEvent(new EventHandler());
        internals.setNet(new NetHandler());
        internals.setResource(new ResourceRegistry());

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
            try{
                RockBottomAPI.logger().info("Game shutting down");
                game.shutdown();
            }
            catch(Exception e){
                RockBottomAPI.logger().log(Level.SEVERE, "There was an error while shutting down the game and disposing of resources", e);
            }
        }
    }

    public abstract int getAutosaveInterval();

    public void shutdown(){
        this.quitWorld();
    }

    @Override
    public int getTotalTicks(){
        return this.totalTicks;
    }

    private void updateTicked(){
        this.totalTicks++;

        synchronized(this.enqueuedActions){
            for(int i = 0; i < this.enqueuedActions.size(); i++){
                EnqueuedAction action = this.enqueuedActions.get(i);

                if(action.condition == null || action.condition.test(this)){
                    action.action.accept(this, action.object);

                    this.enqueuedActions.remove(i);
                    i--;
                }
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

        RockBottomAPI.logger().info("--------- Registry Info ---------");

        for(IRegistry registry : RockBottomAPI.getAllRegistries()){
            RockBottomAPI.logger().info(registry+": "+registry.getSize()+" entries");
        }

        IResourceRegistry res = RockBottomAPI.getResourceRegistry();
        RockBottomAPI.logger().info("resource_registry: "+res.getAllResourceNames().size()+" names, "+res.getAllResources().size()+" resources");

        RockBottomAPI.logger().info("---------------------------------");
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
        TileLayer.init();
    }

    @Override
    public void startWorld(File worldFile, WorldInfo info, boolean isNewlyCreated){
        RockBottomAPI.logger().info("Starting world with file "+worldFile);

        NameToIndexInfo tileRegInfo = new NameToIndexInfo("tile_reg_world", new File(worldFile, "tile_reg_info.dat"), new File(worldFile, "tile_reg_info.json"), Integer.MAX_VALUE);
        this.populateIndexInfo(tileRegInfo, RockBottomAPI.TILE_STATE_REGISTRY);

        NameToIndexInfo biomeRegInfo = new NameToIndexInfo("biome_reg_world", new File(worldFile, "biome_reg_info.dat"), new File(worldFile, "biome_reg_info.json"), Short.MAX_VALUE);
        this.populateIndexInfo(biomeRegInfo, RockBottomAPI.BIOME_REGISTRY);

        DynamicRegistryInfo regInfo = new DynamicRegistryInfo(tileRegInfo, biomeRegInfo);

        this.world = new World(info, regInfo);
        this.world.initFiles(worldFile);

        if(isNewlyCreated){
            RockBottomAPI.getEventHandler().fireEvent(new WorldCreationEvent(worldFile, this.world, info, regInfo));
        }

        RockBottomAPI.getEventHandler().fireEvent(new WorldLoadEvent(this.world, info, regInfo));
    }

    private void populateIndexInfo(NameToIndexInfo info, NameRegistry reg){
        info.load();
        info.populate(reg);

        if(info.needsSave()){
            info.save();
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
        return "assets/rockbottom";
    }

    @Override
    public int getSortingPriority(){
        return Integer.MAX_VALUE;
    }

    @Override
    public String getDescription(){
        return "Rock Bottom is a 2-dimensional sidescrolling game in which you collect resources and build different tools and machines in order to try to figure out why you're on this planet and what the people that were here before you did!";
    }

    @Override
    public String[] getAuthors(){
        return new String[]{"Ellpeck", "wiiv"};
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
        this.isRunning = false;
    }

    @Override
    public <T> void enqueueAction(BiConsumer<IGameInstance, T> action, T object){
        this.enqueueAction(action, object, null);
    }

    @Override
    public <T> void enqueueAction(BiConsumer<IGameInstance, T> action, T object, Predicate<IGameInstance> condition){
        synchronized(this.enqueuedActions){
            this.enqueuedActions.add(new EnqueuedAction(action, object, condition));
        }
    }

    private static class EnqueuedAction<T>{

        public final BiConsumer<IGameInstance, T> action;
        public final T object;
        public final Predicate<IGameInstance> condition;

        public EnqueuedAction(BiConsumer<IGameInstance, T> action, T object, Predicate<IGameInstance> condition){
            this.action = action;
            this.object = object;
            this.condition = condition;
        }
    }
}
