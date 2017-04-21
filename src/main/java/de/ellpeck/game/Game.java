package de.ellpeck.game;

import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.data.DataManager;
import de.ellpeck.game.data.set.DataSet;
import de.ellpeck.game.gui.*;
import de.ellpeck.game.gui.menu.GuiMainMenu;
import de.ellpeck.game.gui.menu.GuiMenu;
import de.ellpeck.game.net.NetHandler;
import de.ellpeck.game.net.chat.ChatLog;
import de.ellpeck.game.net.client.ClientWorld;
import de.ellpeck.game.net.packet.toserver.PacketDisconnect;
import de.ellpeck.game.particle.ParticleManager;
import de.ellpeck.game.render.WorldRenderer;
import de.ellpeck.game.util.IAction;
import de.ellpeck.game.util.Util;
import de.ellpeck.game.world.World;
import de.ellpeck.game.world.World.WorldInfo;
import de.ellpeck.game.world.entity.player.EntityPlayer;
import de.ellpeck.game.world.entity.player.InteractionManager;
import org.newdawn.slick.*;
import org.newdawn.slick.util.Log;

import java.io.File;
import java.util.*;

public class Game extends BasicGame{

    public static final String VERSION = "0.0.2";
    private static Game instance;

    private final List<IAction> scheduledActions = new ArrayList<>();

    private Container container;
    public DataManager dataManager;
    public Settings settings;

    public EntityPlayer player;
    public GuiManager guiManager;
    public InteractionManager interactionManager;
    public ChatLog chatLog;

    public World world;

    public AssetManager assetManager;
    private WorldRenderer worldRenderer;
    public ParticleManager particleManager;

    private long lastPollTime;
    public int tpsAverage;
    private int tpsAccumulator;
    public int fpsAverage;
    private int fpsAccumulator;

    public UUID uniqueId;

    public boolean isDebug;
    public boolean isLightDebug;
    public boolean isForegroundDebug;
    public boolean isBackgroundDebug;

    public Game(){
        super("Game "+Game.VERSION);

        Log.info("Setting game instance to "+this);
        instance = this;
    }

    @Override
    public void init(GameContainer container) throws SlickException{
        Log.info("----- Initializing game -----");

        this.dataManager = new DataManager(this);
        this.settings = this.dataManager.loadSettings();

        this.container = (Container)container;
        this.container.setTargetFrameRate(this.settings.targetFps);

        this.assetManager = new AssetManager();
        this.assetManager.create(this);

        ContentRegistry.init();
        WorldRenderer.init();

        this.guiManager = new GuiManager();
        this.interactionManager = new InteractionManager();
        this.chatLog = new ChatLog();

        this.worldRenderer = new WorldRenderer();
        this.particleManager = new ParticleManager();

        Log.info("----- Done initializing game -----");
        this.quitWorld();
    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException{
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

        if(NetHandler.isClient()){
            if(!NetHandler.isConnectedToServer()){
                this.quitWorld();
                NetHandler.shutdown();
            }
        }

        if(this.world != null && this.player != null){
            Gui gui = this.guiManager.getGui();
            if(gui == null || !gui.doesPauseGame() || NetHandler.isActive()){
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
            else if(key == this.settings.keyInventory.key){
                this.player.openGuiContainer(new GuiInventory(this.player), this.player.inventoryContainer);
                return;
            }
            else if(key == this.settings.keyChat.key && NetHandler.isActive()){
                this.guiManager.openGui(new GuiChat());
                return;
            }
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

        this.guiManager.render(this, this.assetManager, g, this.player);
    }

    public boolean isInWorld(){
        return this.world != null;
    }

    public void startWorld(File worldFile, WorldInfo info){
        Log.info("Starting world with file "+worldFile);

        this.world = new World(info);
        this.world.initFiles(worldFile);

        if(this.world.info.seed == 0){
            this.world.info.seed = Util.RANDOM.nextLong();
        }

        this.player = this.world.createPlayer(this.uniqueId, null);
        this.world.addEntity(this.player);

        this.guiManager.reInitSelf(this);
        this.guiManager.closeGui();

        Log.info("Successfully started world with file "+worldFile);
    }

    public void joinWorld(DataSet playerSet, WorldInfo info){
        Log.info("Joining world");

        this.world = new ClientWorld(info);

        this.player = this.world.createPlayer(this.uniqueId, null);
        this.player.load(playerSet);
        this.world.addEntity(this.player);

        this.guiManager.reInitSelf(this);
        this.guiManager.closeGui();

        Log.info("Successfully joined world");
    }

    public void quitWorld(){
        Log.info("Quitting current world");

        if(NetHandler.isClient()){
            Log.info("Sending disconnection packet");
            NetHandler.sendToServer(new PacketDisconnect(this.player.getUniqueId()));
        }

        NetHandler.shutdown();

        this.world = null;
        this.player = null;

        this.guiManager.reInitSelf(this);
        this.guiManager.openGui(new GuiMainMenu());

        Log.info("Successfully quit current world");
    }

    public void openIngameMenu(){
        this.guiManager.openGui(new GuiMenu());

        if(!this.world.isClient()){
            this.world.save();
        }
    }

    public void scheduleAction(IAction action){
        synchronized(this.scheduledActions){
            this.scheduledActions.add(action);
        }
    }

    public Container getContainer(){
        return this.container;
    }

    public double getWidthInWorld(){
        return (double)this.container.getWidth()/(double)this.settings.renderScale;
    }

    public double getHeightInWorld(){
        return (double)this.container.getHeight()/(double)this.settings.renderScale;
    }

    public double getWidthInGui(){
        return (double)this.container.getWidth()/(double)this.settings.guiScale;
    }

    public double getHeightInGui(){
        return (double)this.container.getHeight()/(double)this.settings.guiScale;
    }

    public float getMouseInGuiX(){
        return (float)this.container.getInput().getMouseX()/(float)this.settings.guiScale;
    }

    public float getMouseInGuiY(){
        return (float)this.container.getInput().getMouseY()/(float)this.settings.guiScale;
    }

    public static Game get(){
        return instance;
    }
}
