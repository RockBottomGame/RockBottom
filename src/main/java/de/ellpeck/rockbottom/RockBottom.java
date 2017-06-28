package de.ellpeck.rockbottom;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.IApiHandler;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.data.IDataManager;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.event.IEventHandler;
import de.ellpeck.rockbottom.api.gui.Gui;
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
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.construction.ConstructionRegistry;
import de.ellpeck.rockbottom.data.DataManager;
import de.ellpeck.rockbottom.gui.DebugRenderer;
import de.ellpeck.rockbottom.gui.GuiChat;
import de.ellpeck.rockbottom.gui.GuiInventory;
import de.ellpeck.rockbottom.gui.GuiManager;
import de.ellpeck.rockbottom.gui.menu.GuiMainMenu;
import de.ellpeck.rockbottom.gui.menu.GuiMenu;
import de.ellpeck.rockbottom.mod.ModLoader;
import de.ellpeck.rockbottom.net.NetHandler;
import de.ellpeck.rockbottom.net.chat.ChatLog;
import de.ellpeck.rockbottom.net.client.ClientWorld;
import de.ellpeck.rockbottom.net.packet.toserver.PacketDisconnect;
import de.ellpeck.rockbottom.particle.ParticleManager;
import de.ellpeck.rockbottom.render.PlayerDesign;
import de.ellpeck.rockbottom.render.WorldRenderer;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import de.ellpeck.rockbottom.world.entity.player.InteractionManager;
import joptsimple.internal.Strings;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.*;
import org.newdawn.slick.util.Log;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class RockBottom extends BasicGame implements IGameInstance{

    public static final String VERSION = "0.0.7";
    public static final String NAME = "Rock Bottom";
    public static final String ID = "rockbottom";

    private final List<IAction> scheduledActions = new ArrayList<>();
    private DataManager dataManager;

    private Settings settings;
    private EntityPlayer player;
    private PlayerDesign playerDesign;
    private GuiManager guiManager;
    private InteractionManager interactionManager;
    private ChatLog chatLog;
    private World world;
    private AssetManager assetManager;
    private ParticleManager particleManager;
    private int tpsAverage;
    private int fpsAverage;
    private UUID uniqueId;
    private boolean isDebug;
    private boolean isLightDebug;
    private boolean isForegroundDebug;
    private boolean isBackgroundDebug;
    private boolean isItemInfoDebug;
    private Container container;
    private WorldRenderer worldRenderer;
    private long lastPollTime;
    private int tpsAccumulator;
    private int fpsAccumulator;
    private int lastWidth;
    private int lastHeight;
    private int totalTicks;

    public RockBottom(){
        super(NAME+" "+VERSION);
    }

    public static IGameInstance get(){
        return RockBottomAPI.getGame();
    }

    public static IResourceName internalRes(String resource){
        return RockBottomAPI.createRes(get(), resource);
    }

    public static void init(){
        RockBottom game = new RockBottom();

        RockBottomAPI.setGameInstance(game);
        RockBottomAPI.setModLoader(new ModLoader());
        RockBottomAPI.setApiHandler(new ApiHandler());
        RockBottomAPI.setEventHandler(new EventHandler());
        RockBottomAPI.setNetHandler(new NetHandler());

        try{
            Container container = new Container(game);
            container.setForceExit(false);
            container.setUpdateOnlyWhenVisible(false);
            container.setAlwaysRender(true);
            container.setShowFPS(false);

            int interval = 1000/Constants.TARGET_TPS;
            container.setMinimumLogicUpdateInterval(interval);
            container.setMaximumLogicUpdateInterval(interval);

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

    @Override
    public void preInit(IGameInstance game, IApiHandler apiHandler, IEventHandler eventHandler){
        this.settings = new Settings();
        this.dataManager.loadPropSettings(this.settings);

        this.container.setTargetFrameRate(this.settings.targetFps);
        this.setFullscreen(this.settings.fullscreen);
        this.container.setVSync(this.settings.vsync);

        this.assetManager = new AssetManager();
        this.assetManager.create(this);

        this.setPlayerDesign();
    }

    private void setPlayerDesign(){
        this.playerDesign = new PlayerDesign();
        this.playerDesign.loadFromFile();

        if(Strings.isNullOrEmpty(this.playerDesign.getName())){
            PlayerDesign.randomizeDesign(this.playerDesign);
            Log.info("Randomizing player design");

            this.playerDesign.saveToFile();
        }
    }

    @Override
    public void init(IGameInstance game, IAssetManager assetManager, IApiHandler apiHandler, IEventHandler eventHandler){
        ContentRegistry.init();
        ConstructionRegistry.init();
        WorldRenderer.init();
    }

    @Override
    public void postInit(IGameInstance game, IAssetManager assetManager, IApiHandler apiHandler, IEventHandler eventHandler){
        this.guiManager = new GuiManager();
        this.interactionManager = new InteractionManager();
        this.chatLog = new ChatLog();

        this.worldRenderer = new WorldRenderer();
        this.particleManager = new ParticleManager();
    }

    @Override
    public void setFullscreen(boolean fullscreen){
        try{
            if(this.container.isFullscreen() != fullscreen){
                if(fullscreen){
                    this.lastWidth = this.container.getWidth();
                    this.lastHeight = this.container.getHeight();

                    this.container.setDisplayMode(this.container.getScreenWidth(), this.container.getScreenHeight(), true);
                }
                else{
                    this.container.setDisplayMode(this.lastWidth, this.lastHeight, false);
                    Display.setResizable(false); //Workaround for stupid LWJGL bug
                    Display.setResizable(true);
                }

                if(this.guiManager != null){
                    this.guiManager.setReInit();
                }
            }
        }
        catch(Exception e){
            Log.error("Failed to set fullscreen", e);
        }
    }

    @Override
    public int getTotalTicks(){
        return this.totalTicks;
    }

    @Override
    public PlayerDesign getPlayerDesign(){
        return this.playerDesign;
    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException{
        this.totalTicks++;
        this.tpsAccumulator++;

        long time = container.getTime();
        if(time-this.lastPollTime >= 1000){
            this.tpsAverage = this.tpsAccumulator;
            this.fpsAverage = this.fpsAccumulator;

            this.tpsAccumulator = 0;
            this.fpsAccumulator = 0;

            this.lastPollTime = time;
        }

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

        if(this.world != null && this.player != null){
            Gui gui = this.guiManager.getGui();
            if(gui == null || !gui.doesPauseGame() || RockBottomAPI.getNet().isActive()){
                this.world.update(this);
                this.interactionManager.update(this);

                this.particleManager.update(this);
            }
        }

        this.guiManager.update(this);
    }

    @Override
    public void mousePressed(int button, int x, int y){
        this.interactionManager.onMouseAction(this, button);
    }

    @Override
    public void keyPressed(int key, char c){
        if(this.guiManager.getGui() == null){
            if(key == this.settings.keyMenu.key){
                this.openIngameMenu();
                return;
            }
            else if(key == Input.KEY_F1){
                this.isDebug = !this.isDebug;
                return;
            }
            else if(key == Input.KEY_F2){
                this.isLightDebug = !this.isLightDebug;
                return;
            }
            else if(key == Input.KEY_F3){
                this.isForegroundDebug = !this.isForegroundDebug;
                return;
            }
            else if(key == Input.KEY_F4){
                this.isBackgroundDebug = !this.isBackgroundDebug;
                return;
            }
            else if(key == Input.KEY_F5){
                this.isItemInfoDebug = !this.isItemInfoDebug;
                return;
            }
            else if(key == this.settings.keyInventory.key){
                this.player.openGuiContainer(new GuiInventory(this.player), this.player.getInvContainer());
                return;
            }
            else if(key == this.settings.keyChat.key && RockBottomAPI.getNet().isActive()){
                this.guiManager.openGui(new GuiChat());
                return;
            }
        }

        if(key == this.settings.keyScreenshot.key){
            this.takeScreenshot();
            return;
        }

        this.interactionManager.onKeyboardAction(this, key, c);
    }

    @Override
    public void render(GameContainer container, Graphics g) throws SlickException{
        this.fpsAccumulator++;

        if(this.isInWorld()){
            this.worldRenderer.render(this, this.assetManager, this.particleManager, g, this.world, this.player, this.interactionManager);

            if(this.isDebug){
                DebugRenderer.render(this, this.assetManager, this.world, this.player, container, g);
            }
        }

        g.setLineWidth(this.getGuiScale());
        this.guiManager.render(this, this.assetManager, g, this.player);
    }

    @Override
    public boolean isInWorld(){
        return this.world != null;
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

        this.player = this.world.createPlayer(this.uniqueId, this.playerDesign, null);
        this.world.addEntity(this.player);

        this.guiManager.reInitSelf(this);
        this.guiManager.closeGui();

        Log.info("Successfully started world with file "+worldFile);
    }

    private void populateIndexInfo(NameToIndexInfo info, NameRegistry reg){
        this.dataManager.loadPropSettings(info);
        info.populate(reg);

        if(info.needsSave()){
            this.dataManager.savePropSettings(info);
        }
    }

    @Override
    public void joinWorld(DataSet playerSet, WorldInfo info, NameToIndexInfo tileRegInfo, NameToIndexInfo biomeRegInfo){
        Log.info("Joining world");

        this.world = new ClientWorld(info, tileRegInfo, biomeRegInfo);

        this.player = this.world.createPlayer(this.uniqueId, this.playerDesign, null);
        this.player.load(playerSet);
        this.world.addEntity(this.player);

        this.guiManager.reInitSelf(this);
        this.guiManager.closeGui();

        Log.info("Successfully joined world");
    }

    @Override
    public void quitWorld(){
        Log.info("Quitting current world");

        if(this.player != null){
            if(RockBottomAPI.getNet().isClient()){
                Log.info("Sending disconnection packet");
                RockBottomAPI.getNet().sendToServer(new PacketDisconnect(this.player.getUniqueId()));
            }
        }

        RockBottomAPI.getNet().shutdown();

        this.world = null;
        this.player = null;

        this.guiManager.reInitSelf(this);
        this.guiManager.openGui(new GuiMainMenu());

        Log.info("Successfully quit current world");
    }

    @Override
    public void openIngameMenu(){
        this.guiManager.openGui(new GuiMenu());

        if(!RockBottomAPI.getNet().isClient()){
            this.world.save();
        }
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
    public int getGuiScale(){
        return this.settings.guiScale;
    }

    @Override
    public int getWorldScale(){
        return this.settings.renderScale;
    }

    @Override
    public double getWidthInWorld(){
        int width = this.container.getWidth();
        int scale = this.getWorldScale();

        if((scale%2 == 0) != (width%2 == 0)){
            width++;
        }

        return (double)width/(double)scale;
    }

    @Override
    public double getHeightInWorld(){
        int height = this.container.getHeight();
        int scale = this.getWorldScale();

        if((scale%2 == 0) != (height%2 == 0)){
            height++;
        }

        return (double)height/(double)scale;
    }

    @Override
    public double getWidthInGui(){
        return (double)this.container.getWidth()/(double)this.getGuiScale();
    }

    @Override
    public double getHeightInGui(){
        return (double)this.container.getHeight()/(double)this.getGuiScale();
    }

    @Override
    public float getMouseInGuiX(){
        return (float)this.container.getInput().getMouseX()/(float)this.getGuiScale();
    }

    @Override
    public float getMouseInGuiY(){
        return (float)this.container.getInput().getMouseY()/(float)this.getGuiScale();
    }

    @Override
    public IDataManager getDataManager(){
        return this.dataManager;
    }

    @Override
    public Settings getSettings(){
        return this.settings;
    }

    @Override
    public EntityPlayer getPlayer(){
        return this.player;
    }

    @Override
    public GuiManager getGuiManager(){
        return this.guiManager;
    }

    @Override
    public InteractionManager getInteractionManager(){
        return this.interactionManager;
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
    public IAssetManager getAssetManager(){
        return this.assetManager;
    }

    @Override
    public ParticleManager getParticleManager(){
        return this.particleManager;
    }

    public void setUniqueId(UUID uniqueId){
        this.uniqueId = uniqueId;
    }

    @Override
    public UUID getUniqueId(){
        return this.uniqueId;
    }

    @Override
    public boolean isDebug(){
        return this.isDebug;
    }

    @Override
    public boolean isLightDebug(){
        return this.isLightDebug;
    }

    @Override
    public boolean isForegroundDebug(){
        return this.isForegroundDebug;
    }

    @Override
    public boolean isBackgroundDebug(){
        return this.isBackgroundDebug;
    }

    @Override
    public boolean isItemInfoDebug(){
        return this.isItemInfoDebug;
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

    private void takeScreenshot(){
        try{
            Log.info("Taking screenshot");

            GL11.glReadBuffer(GL11.GL_FRONT);
            int width = this.container.getWidth();
            int height = this.container.getHeight();
            int colors = 4;

            ByteBuffer buf = BufferUtils.createByteBuffer(width*height*colors);
            GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for(int x = 0; x < width; x++){
                for(int y = 0; y < height; y++){
                    int i = (x+(y*width))*colors;

                    int r = buf.get(i) & 0xFF;
                    int g = buf.get(i+1) & 0xFF;
                    int b = buf.get(i+2) & 0xFF;

                    image.setRGB(x, height-(y+1), (0xFF << 24) | (r << 16) | (g << 8) | b);
                }
            }

            File dir = this.dataManager.getScreenshotDir();
            if(!dir.exists()){
                dir.mkdirs();
                Log.info("Creating screenshot folder at "+dir);
            }

            File file = new File(dir, new SimpleDateFormat("dd.MM.yy_HH.mm.ss").format(new Date())+".png");
            ImageIO.write(image, "png", file);

            Log.info("Saved screenshot to "+file);
        }
        catch(Exception e){
            Log.error("Couldn't take screenshot", e);
        }
    }
}
