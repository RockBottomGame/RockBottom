package de.ellpeck.rockbottom.init;

import de.ellpeck.rockbottom.ContentRegistry;
import de.ellpeck.rockbottom.Main;
import de.ellpeck.rockbottom.api.*;
import de.ellpeck.rockbottom.api.data.IDataManager;
import de.ellpeck.rockbottom.api.event.IEventHandler;
import de.ellpeck.rockbottom.api.event.impl.WorldCreationEvent;
import de.ellpeck.rockbottom.api.event.impl.WorldLoadEvent;
import de.ellpeck.rockbottom.api.internal.Internals;
import de.ellpeck.rockbottom.api.mod.IModLoader;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IRegistry;
import de.ellpeck.rockbottom.api.util.reg.NameRegistry;
import de.ellpeck.rockbottom.api.util.reg.NameToIndexInfo;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.DynamicRegistryInfo;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.WorldInfo;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.apiimpl.ApiHandler;
import de.ellpeck.rockbottom.apiimpl.EventHandler;
import de.ellpeck.rockbottom.apiimpl.InternalHooks;
import de.ellpeck.rockbottom.apiimpl.ResourceRegistry;
import de.ellpeck.rockbottom.construction.RecipeCache;
import de.ellpeck.rockbottom.content.ContentManager;
import de.ellpeck.rockbottom.content.ContentPackLoader;
import de.ellpeck.rockbottom.data.DataManager;
import de.ellpeck.rockbottom.log.Logging;
import de.ellpeck.rockbottom.mod.ModLoader;
import de.ellpeck.rockbottom.net.NetHandler;
import de.ellpeck.rockbottom.net.chat.ChatLog;
import de.ellpeck.rockbottom.util.CrashManager;
import de.ellpeck.rockbottom.util.thread.ThreadHandler;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.entity.player.statistics.StatisticList;

import java.io.File;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;

public abstract class AbstractGame implements IGameInstance {

    public static final String VERSION = "0.3.7";
    public static final String NAME = "Rock Bottom";
    public static final String ID = "rockbottom";
    private static final int INTERVAL = 1000 / Constants.TARGET_TPS;
    private final Deque<EnqueuedAction> enqueuedActions = new ArrayDeque<>();
    protected DataManager dataManager;
    protected ChatLog chatLog;
    protected World world;
    private boolean isRunning = true;
    private int tpsAverage;
    private int fpsAverage;
    private int totalTicks;
    private float tickDelta;

    public static void doInit(AbstractGame game) {
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

        try {
            game.init();

            long lastPollTime = 0;
            int tpsAccumulator = 0;
            int fpsAccumulator = 0;

            long lastDeltaTime = Util.getTimeMillis();
            long lastTickTime = lastDeltaTime;
            int deltaAccumulator = 0;

            while (game.isRunning) {
                long time = Util.getTimeMillis();

                deltaAccumulator += (time - lastDeltaTime);
                lastDeltaTime = time;

                if (deltaAccumulator >= INTERVAL) {
                    long updates = deltaAccumulator / INTERVAL;
                    for (int i = 0; i < updates; i++) {
                        game.updateTicked();
                        tpsAccumulator++;

                        deltaAccumulator -= INTERVAL;
                        lastTickTime = time;
                    }
                }

                game.tickDelta = (time - lastTickTime) / (float) INTERVAL;

                game.updateTickless();
                fpsAccumulator++;

                if (time - lastPollTime >= 1000) {
                    game.tpsAverage = tpsAccumulator;
                    game.fpsAverage = fpsAccumulator;

                    tpsAccumulator = 0;
                    fpsAccumulator = 0;

                    lastPollTime = time;
                }

                Util.sleepSafe(1);
            }
        } catch (Exception e) {
            game.onCrash();
            throw e;
        } finally {
            try {
                RockBottomAPI.logger().info("Game shutting down");
                game.shutdown();
            } catch (Exception e) {
                RockBottomAPI.logger().log(Level.SEVERE, "There was an error while shutting down the game and disposing of resources", e);
            }
        }
    }

    @Override
    public float getTickDelta() {
        return this.tickDelta;
    }

    public abstract int getAutosaveInterval();

    protected void shutdown() {
        this.quitWorld();
    }

    protected void onCrash() {
        try {
            this.printRegistryInfo(CrashManager::addInfo);
        } catch (Exception e) {
            CrashManager.addInfo("Registry information couldn't be gathered");
        }
    }

    @Override
    public int getTotalTicks() {
        return this.totalTicks;
    }

    private void updateTicked() {
        this.totalTicks++;

        this.update();

        while (!this.enqueuedActions.isEmpty()) {
            EnqueuedAction action;
            synchronized (this.enqueuedActions) {
                action = this.enqueuedActions.removeFirst();
            }
            action.action.accept(this, action.object);
        }
    }

    public void init() {
        this.dataManager = new DataManager();

        IModLoader modLoader = RockBottomAPI.getModLoader();
        modLoader.loadJarMods(this.dataManager.getModsDir());
        if (Main.unpackedModsDir != null) {
            modLoader.loadUnpackedMods(Main.unpackedModsDir);
        }
        modLoader.sortMods();

        RockBottomAPI.getContentPackLoader().load(this.dataManager.getContentPacksDir());

        modLoader.prePreInit();
        modLoader.preInit();
        modLoader.init();
        modLoader.postInit();
        modLoader.postPostInit();

        this.printRegistryInfo(s -> RockBottomAPI.logger().info(s));
    }

    protected void printRegistryInfo(Consumer<String> writer) {
        writer.accept("--------- Registry Info ---------");
        writer.accept("There are " + Registries.REGISTRIES.getSize() + " registered registries.");
        for (Map.Entry<ResourceName, IRegistry> entry : Registries.REGISTRIES.entrySet()) {
            writer.accept(entry.getKey() + ": " + entry.getValue().getSize() + " entries");
        }
        writer.accept("---------------------------------");
    }

    protected void update() {
        if (this.world != null) {
            this.world.update(this);
        }
    }

    @Override
    public void preInit(IGameInstance game, IApiHandler apiHandler, IEventHandler eventHandler) {
        ThreadHandler.init(this);
    }

    @Override
    public void init(IGameInstance game, IApiHandler apiHandler, IEventHandler eventHandler) {
        ContentRegistry.init();
    }

    @Override
    public void postInit(IGameInstance game, IApiHandler apiHandler, IEventHandler eventHandler) {
        TileLayer.init();
        ContentManager.init(this);
		RecipeCache.postInit();

        this.chatLog = new ChatLog();
    }

    @Override
    public void postPostInit(IGameInstance game, IApiHandler apiHandler, IEventHandler eventHandler) {
        StatisticList.init();
        ChatLog.initCommands();
    }

    @Override
    public void startWorld(File worldFile, WorldInfo info, boolean isNewlyCreated) {
        RockBottomAPI.logger().info("Starting world with file " + worldFile);

        NameToIndexInfo tileRegInfo = new NameToIndexInfo(ResourceName.intern("tile_reg_world"), new File(worldFile, "tile_reg_info.json"), Integer.MAX_VALUE);
        this.populateIndexInfo(tileRegInfo, Registries.TILE_STATE_REGISTRY);

        NameToIndexInfo biomeRegInfo = new NameToIndexInfo(ResourceName.intern("biome_reg_world"), new File(worldFile, "biome_reg_info.json"), Short.MAX_VALUE);
        this.populateIndexInfo(biomeRegInfo, Registries.BIOME_REGISTRY);

        DynamicRegistryInfo regInfo = new DynamicRegistryInfo(tileRegInfo, biomeRegInfo);

        this.world = new World(info, regInfo, worldFile, false);

        if (isNewlyCreated) {
            RockBottomAPI.getEventHandler().fireEvent(new WorldCreationEvent(worldFile, this.world, info, regInfo));
        }

        RockBottomAPI.getEventHandler().fireEvent(new WorldLoadEvent(this.world, info, regInfo));
    }

    private void populateIndexInfo(NameToIndexInfo info, NameRegistry reg) {
        info.load();
        info.populate(reg);

        if (info.needsSave()) {
            info.save();
        }
    }

    @Override
    public void quitWorld() {
        RockBottomAPI.getNet().shutdown();

        if (this.world != null) {
            RockBottomAPI.logger().info("Quitting current world");

            this.world.onUnloaded();
            this.world = null;
        }

        if (this.chatLog != null) {
            this.chatLog.clear();
        }
    }

    protected void updateTickless() {

    }

    @Override
    public IDataManager getDataManager() {
        return this.dataManager;
    }

    @Override
    public ChatLog getChatLog() {
        return this.chatLog;
    }

    @Override
    public IWorld getWorld() {
        return this.world;
    }

    @Override
    public int getTpsAverage() {
        return this.tpsAverage;
    }

    @Override
    public int getFpsAverage() {
        return this.fpsAverage;
    }

    @Override
    public URLClassLoader getClassLoader() {
        return Main.classLoader;
    }

    @Override
    public String getDisplayName() {
        return NAME;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public String getResourceLocation() {
        return "assets/rockbottom";
    }

    @Override
    public String getContentLocation() {
        return "assets/rockbottom/content";
    }

    @Override
    public int getSortingPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String getDescription() {
        return "Rock Bottom is a 2-dimensional sidescrolling game in which you collect resources and build different tools and machines in order to try to figure out why you're on this planet and what the people that were here before you did!";
    }

    @Override
    public String[] getAuthors() {
        return new String[]{"Ellpeck", "wiiv", "raphydaphy", "Quarris", "canitzp"};
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

    @Override
    public boolean isRequiredOnClient() {
        return true;
    }

    @Override
    public boolean isRequiredOnServer() {
        return true;
    }

    @Override
    public void exit() {
        this.isRunning = false;
    }

    @Override
    public <T> void enqueueAction(BiConsumer<IGameInstance, T> action, T object) {
        synchronized (this.enqueuedActions) {
            this.enqueuedActions.add(new EnqueuedAction(action, object));
        }
    }

    @Override
    public void restart() {
        try {
            StringBuilder args = new StringBuilder();
            for (String arg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
                if (!arg.toLowerCase(Locale.ROOT).contains("-agentlib")) {
                    args.append(arg).append(' ');
                }
            }

            StringBuilder cmd = new StringBuilder('"' + System.getProperty("java.home") + "/bin/java" + "\" " + args);
            String[] mainCommand = System.getProperty("sun.java.command").split(" ");

            if (mainCommand[0].endsWith(".jar")) {
                cmd.append("-jar ").append(new File(mainCommand[0]).getPath());
            } else {
                cmd.append("-cp \"").append(System.getProperty("java.class.path")).append("\" ").append(mainCommand[0]);
            }

            for (int i = 1; i < mainCommand.length; i++) {
                cmd.append(' ').append(mainCommand[i]);
            }

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    Runtime.getRuntime().exec(cmd.toString());
                } catch (Exception e) {
                    Logging.mainLogger.log(Level.WARNING, "There was an error while trying to restart the game", e);
                }
            }, ThreadHandler.SHUTDOWN_HOOK));

            this.exit();
        } catch (Exception e) {
            RockBottomAPI.logger().log(Level.WARNING, "There was an error while trying to setup a game restart", e);
        }
    }

    @Override
    public boolean isRunning() {
        return this.isRunning;
    }

    @Override
    public InputStream getResourceStream(String s) {
        return ContentManager.getResourceAsStream(s);
    }

    @Override
    public URL getResourceURL(String s) {
        return ContentManager.getResource(s);
    }

    private static class EnqueuedAction<T> {

        public final BiConsumer<IGameInstance, T> action;
        public final T object;

        public EnqueuedAction(BiConsumer<IGameInstance, T> action, T object) {
            this.action = action;
            this.object = object;
        }
    }
}
