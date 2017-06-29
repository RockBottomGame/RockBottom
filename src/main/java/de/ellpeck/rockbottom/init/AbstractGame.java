package de.ellpeck.rockbottom.init;

import de.ellpeck.rockbottom.ContentRegistry;
import de.ellpeck.rockbottom.Main;
import de.ellpeck.rockbottom.api.IApiHandler;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.IDataManager;
import de.ellpeck.rockbottom.api.event.IEventHandler;
import de.ellpeck.rockbottom.api.mod.IModLoader;
import de.ellpeck.rockbottom.api.util.IAction;
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
import de.ellpeck.rockbottom.world.World;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;

import java.io.File;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGame extends BasicGame implements IGameInstance{

    public static final String VERSION = "0.0.7";
    public static final String NAME = "Rock Bottom";
    public static final String ID = "rockbottom";
    private final List<IAction> scheduledActions = new ArrayList<>();
    public int tpsAverage;
    public int fpsAverage;
    public int tpsAccumulator;
    public int fpsAccumulator;
    protected DataManager dataManager;
    protected ChatLog chatLog;
    protected World world;
    protected Container container;
    private int totalTicks;

    public AbstractGame(){
        super(NAME+" "+VERSION);
    }

    public static void doInit(AbstractGame game){
        RockBottomAPI.setGameInstance(game);
        RockBottomAPI.setModLoader(new ModLoader());
        RockBottomAPI.setApiHandler(new ApiHandler());
        RockBottomAPI.setEventHandler(new EventHandler());
        RockBottomAPI.setNetHandler(new NetHandler());

        try{
            Container container = game.makeContainer();
            container.setForceExit(false);
            container.start();
        }
        catch(SlickException e){
            Log.error("Exception initializing game", e);
        }
        finally{
            RockBottomAPI.getNet().shutdown();
        }

        Log.info("Game shutting down");
    }

    protected abstract Container makeContainer() throws SlickException;

    public abstract int getAutosaveInterval();

    public static IGameInstance get(){
        return RockBottomAPI.getGame();
    }

    public static IResourceName internalRes(String resource){
        return RockBottomAPI.createRes(get(), resource);
    }

    @Override
    public int getTotalTicks(){
        return this.totalTicks;
    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException{
        this.totalTicks++;
        this.tpsAccumulator++;

        synchronized(this.scheduledActions){
            for(int i = 0; i < this.scheduledActions.size(); i++){
                IAction action = this.scheduledActions.get(i);

                if(action.run()){
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

        this.doUpdate();
    }

    @Override
    public void init(GameContainer container) throws SlickException{
        this.container = (Container)container;
        this.dataManager = new DataManager(this);

        IModLoader modLoader = RockBottomAPI.getModLoader();
        modLoader.loadJarMods(this.dataManager.getModsDir());
        if(Main.unpackedModsDir != null){
            modLoader.loadUnpackedMods(Main.unpackedModsDir);
        }
        modLoader.sortMods();

        modLoader.preInit();
        modLoader.init();
        modLoader.postInit();

        this.quitWorld();
    }

    protected void doUpdate(){
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
        Log.info("Starting world with file "+worldFile);

        NameToIndexInfo tileRegInfo = new NameToIndexInfo("tile_reg_world", new File(worldFile, "tile_reg_info.dat"), Short.MAX_VALUE);
        this.populateIndexInfo(tileRegInfo, RockBottomAPI.TILE_REGISTRY);

        NameToIndexInfo biomeRegInfo = new NameToIndexInfo("biome_reg_world", new File(worldFile, "biome_reg_info.dat"), Short.MAX_VALUE);
        this.populateIndexInfo(biomeRegInfo, RockBottomAPI.BIOME_REGISTRY);

        if(info.seed == 0){
            info.seed = Util.RANDOM.nextLong();
        }

        this.world = new World(info, tileRegInfo, biomeRegInfo);
        this.world.initFiles(worldFile);
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
        Log.info("Quitting current world");

        RockBottomAPI.getNet().shutdown();
        this.world = null;
    }

    @Override
    public void scheduleAction(IAction action){
        synchronized(this.scheduledActions){
            this.scheduledActions.add(action);
        }
    }

    @Override
    public GameContainer getContainer(){
        return this.container;
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
}
