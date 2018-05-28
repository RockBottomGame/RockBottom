package de.ellpeck.rockbottom.init;

import co.pemery.auth.RockBottomAuthenticator;
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
import de.ellpeck.rockbottom.content.ContentManager;
import de.ellpeck.rockbottom.content.ContentPackLoader;
import de.ellpeck.rockbottom.data.DataManager;
import de.ellpeck.rockbottom.log.Logging;
import de.ellpeck.rockbottom.mod.ModLoader;
import de.ellpeck.rockbottom.net.NetHandler;
import de.ellpeck.rockbottom.net.PacketDecoder;
import de.ellpeck.rockbottom.net.PacketEncoder;
import de.ellpeck.rockbottom.net.chat.ChatLog;
import de.ellpeck.rockbottom.util.thread.ThreadHandler;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.entity.player.statistics.StatisticList;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.logging.Level;

public abstract class AbstractGame implements IGameInstance{

    public static final String VERSION = "0.2.3.3";
    public static final String NAME = "Rock Bottom";
    public static final String ID = "rockbottom";
    private static final int INTERVAL = 1000/Constants.TARGET_TPS;
    private final List<EnqueuedAction> enqueuedActions = new ArrayList<>();
    private boolean isRunning = true;
    protected DataManager dataManager;
    protected ChatLog chatLog;
    protected World world;
    private int tpsAverage;
    private int fpsAverage;
    private int totalTicks;
    private float tickDelta;

    public static void doInit(AbstractGame game){
        RockBottomAuthenticator.setMainServer("https://rockbottom.ellpeck.de/auth/");

        Internals internals = new Internals();
        RockBottomAPI.setInternals(internals);

        internals.setGame(game);
        internals.setMod(new ModLoader());
        internals.setContent(new ContentPackLoader());
        internals.setApi(new ApiHandler());
        internals.setHooks(new InternalHooks());
        internals.setEvent(new EventHandler());
        internals.setNet(new NetHandler());
        internals.setResource(new ResourceRegistry());

        try{
            game.init();

            int packetInfoTimer = 0;
            long lastPollTime = 0;
            int tpsAccumulator = 0;
            int fpsAccumulator = 0;

            long lastDeltaTime = Util.getTimeMillis();
            int deltaAccumulator = 0;

            while(game.isRunning){
                long time = Util.getTimeMillis();

                int delta = (int)(time-lastDeltaTime);
                game.tickDelta = delta/(float)INTERVAL;
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

                game.updateTickless();
                fpsAccumulator++;

                if(time-lastPollTime >= 1000){
                    game.tpsAverage = tpsAccumulator;
                    game.fpsAverage = fpsAccumulator;

                    tpsAccumulator = 0;
                    fpsAccumulator = 0;

                    lastPollTime = time;

                    packetInfoTimer++;
                    if(packetInfoTimer >= 30){
                        RockBottomAPI.logger().finer("Packets in the last 30 seconds: "+PacketDecoder.packetsReceived+" received, "+PacketEncoder.packetsSent+" sent");

                        PacketDecoder.packetsReceived = 0;
                        PacketEncoder.packetsSent = 0;
                        packetInfoTimer = 0;
                    }
                }

                Util.sleepSafe(1);
            }
        }
        catch(Exception e){
            game.onCrash();
            throw e;
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

    @Override
    public float getTickDelta(){
        return this.tickDelta;
    }

    public abstract int getAutosaveInterval();

    protected void shutdown(){
        this.quitWorld();
    }

    protected void onCrash(){

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
        this.dataManager = new DataManager();

        IModLoader modLoader = RockBottomAPI.getModLoader();
        modLoader.loadJarMods(this.dataManager.getModsDir());
        if(Main.unpackedModsDir != null){
            modLoader.loadUnpackedMods(Main.unpackedModsDir);
        }
        modLoader.sortMods();

        RockBottomAPI.getContentPackLoader().load(this.dataManager.getContentPacksDir());

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
    public void preInit(IGameInstance game, IApiHandler apiHandler, IEventHandler eventHandler){
        ThreadHandler.init(this);
    }

    @Override
    public void init(IGameInstance game, IApiHandler apiHandler, IEventHandler eventHandler){
        ContentRegistry.init();
    }

    @Override
    public void postInit(IGameInstance game, IApiHandler apiHandler, IEventHandler eventHandler){
        TileLayer.init();
        ContentManager.init(this);
        ConstructionRegistry.postInit();

        this.chatLog = new ChatLog();
    }

    @Override
    public void postPostInit(IGameInstance game, IApiHandler apiHandler, IEventHandler eventHandler){
        StatisticList.init();
        ChatLog.initCommands();
    }

    @Override
    public void startWorld(File worldFile, WorldInfo info, boolean isNewlyCreated){
        RockBottomAPI.logger().info("Starting world with file "+worldFile);

        NameToIndexInfo tileRegInfo = new NameToIndexInfo("tile_reg_world", new File(worldFile, "tile_reg_info.dat"), new File(worldFile, "tile_reg_info.json"), Integer.MAX_VALUE);
        this.populateIndexInfo(tileRegInfo, RockBottomAPI.TILE_STATE_REGISTRY);

        NameToIndexInfo biomeRegInfo = new NameToIndexInfo("biome_reg_world", new File(worldFile, "biome_reg_info.dat"), new File(worldFile, "biome_reg_info.json"), Short.MAX_VALUE);
        this.populateIndexInfo(biomeRegInfo, RockBottomAPI.BIOME_REGISTRY);

        DynamicRegistryInfo regInfo = new DynamicRegistryInfo(tileRegInfo, biomeRegInfo);

        this.world = new World(info, regInfo, worldFile);

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

    protected void updateTickless(){

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
    public String getContentLocation(){
        return "assets/rockbottom/content";
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

    @Override
    public void restart(){
        try{
            StringBuilder args = new StringBuilder();
            for(String arg : ManagementFactory.getRuntimeMXBean().getInputArguments()){
                if(!arg.toLowerCase(Locale.ROOT).contains("-agentlib")){
                    args.append(arg).append(' ');
                }
            }

            StringBuilder cmd = new StringBuilder('"'+System.getProperty("java.home")+"/bin/java"+"\" "+args);
            String[] mainCommand = System.getProperty("sun.java.command").split(" ");

            if(mainCommand[0].endsWith(".jar")){
                cmd.append("-jar ").append(new File(mainCommand[0]).getPath());
            }
            else{
                cmd.append("-cp \"").append(System.getProperty("java.class.path")).append("\" ").append(mainCommand[0]);
            }

            for(int i = 1; i < mainCommand.length; i++){
                cmd.append(' ').append(mainCommand[i]);
            }

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try{
                    Runtime.getRuntime().exec(cmd.toString());
                }
                catch(Exception e){
                    Logging.mainLogger.log(Level.WARNING, "There was an error while trying to restart the game", e);
                }
            }, ThreadHandler.SHUTDOWN_HOOK));

            this.exit();
        }
        catch(Exception e){
            RockBottomAPI.logger().log(Level.WARNING, "There was an error while trying to setup a game restart", e);
        }
    }

    @Override
    public boolean isRunning(){
        return this.isRunning;
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
