package de.ellpeck.game;

import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.data.DataManager;
import de.ellpeck.game.gui.DebugRenderer;
import de.ellpeck.game.gui.Gui;
import de.ellpeck.game.gui.GuiInventory;
import de.ellpeck.game.particle.ParticleManager;
import de.ellpeck.game.render.WorldRenderer;
import de.ellpeck.game.world.World;
import de.ellpeck.game.world.entity.player.EntityPlayer;
import de.ellpeck.game.world.entity.player.InteractionManager;
import org.newdawn.slick.*;

import java.io.File;
import java.util.UUID;

public class Game extends BasicGame{

    private static Game instance;

    private GameContainer container;
    public DataManager dataManager;
    public Settings settings;

    private EntityPlayer player;
    public InteractionManager interactionManager;

    private World world;

    private AssetManager assetManager;
    private WorldRenderer worldRenderer;
    public ParticleManager particleManager;

    private long lastPollTime;
    public int tpsAverage;
    private int tpsAccumulator;
    public int fpsAverage;
    private int fpsAccumulator;

    public UUID uniqueId;
    public boolean isDebug;

    public Game(){
        super("Game");
        instance = this;
    }

    @Override
    public void init(GameContainer container) throws SlickException{
        this.dataManager = new DataManager(this);
        this.settings = this.dataManager.loadSettings();

        this.container = container;

        this.assetManager = new AssetManager();
        this.assetManager.create(this);

        ContentRegistry.init();

        this.world = new World(new File(this.dataManager.saveDirectory, "world"));
        if(this.world.getSeed() == 0){
            this.world.setSeed(this.world.rand.nextLong());
        }

        this.player = this.world.addPlayer(this.uniqueId);
        this.interactionManager = new InteractionManager(this.player);

        this.worldRenderer = new WorldRenderer();
        this.particleManager = new ParticleManager();
    }

    @Override
    public boolean closeRequested(){
        this.world.save();
        return true;
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

        Gui gui = this.player.guiManager.getGui();
        if(gui == null || !gui.doesPauseGame()){
            this.world.update(this);
            this.interactionManager.update(this);

            this.particleManager.update(this);
        }
    }

    @Override
    public void mousePressed(int button, int x, int y){
        this.interactionManager.onMouseAction(this, button);
    }

    @Override
    public void keyPressed(int key, char c){
        if(this.player.guiManager.getGui() == null){
            if(key == Input.KEY_F1){
                this.isDebug = !this.isDebug;
                return;
            }
            else if(key == Input.KEY_ESCAPE || key == Input.KEY_E){
                this.player.guiManager.openGui(new GuiInventory(this.player));
                return;
            }
        }

        this.interactionManager.onKeyboardAction(this, key);
    }

    @Override
    public void render(GameContainer container, Graphics g) throws SlickException{
        this.fpsAccumulator++;

        this.worldRenderer.render(this, this.assetManager, this.particleManager, g, this.world, this.player, this.interactionManager);

        if(this.isDebug){
            DebugRenderer.render(this, this.world, this.player, container, g);
        }

        this.player.guiManager.render(this, this.assetManager, g, this.player);
    }

    public GameContainer getContainer(){
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

    public static Game get(){
        return instance;
    }
}
