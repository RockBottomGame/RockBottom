package de.ellpeck.rockbottom.init;

import de.ellpeck.rockbottom.ContentRegistry;
import de.ellpeck.rockbottom.api.IApiHandler;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.event.IEventHandler;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.util.reg.NameToIndexInfo;
import de.ellpeck.rockbottom.api.world.WorldInfo;
import de.ellpeck.rockbottom.apiimpl.ApiHandler;
import de.ellpeck.rockbottom.apiimpl.EventHandler;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.construction.ConstructionRegistry;
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
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import de.ellpeck.rockbottom.world.entity.player.InteractionManager;
import joptsimple.internal.Strings;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class RockBottom extends AbstractGame{

    protected Settings settings;
    private EntityPlayer player;
    private PlayerDesign playerDesign;
    private GuiManager guiManager;
    private InteractionManager interactionManager;
    private AssetManager assetManager;
    private ParticleManager particleManager;
    private UUID uniqueId;
    private boolean isDebug;
    private boolean isLightDebug;
    private boolean isForegroundDebug;
    private boolean isBackgroundDebug;
    private boolean isItemInfoDebug;
    private WorldRenderer worldRenderer;
    private int lastWidth;
    private int lastHeight;

    public static void init(){
        doInit(new RockBottom());
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
    public void init(IGameInstance game, IApiHandler apiHandler, IEventHandler eventHandler){
        super.init(game, apiHandler, eventHandler);

        WorldRenderer.init();
    }

    @Override
    public void postInit(IGameInstance game, IApiHandler apiHandler, IEventHandler eventHandler){
        super.postInit(game, apiHandler, eventHandler);

        this.guiManager = new GuiManager();
        this.interactionManager = new InteractionManager();

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
    protected Container makeContainer() throws SlickException{
        return new Container(this);
    }

    @Override
    public int getAutosaveInterval(){
        return this.settings.autosaveIntervalSeconds;
    }

    @Override
    protected void doUpdate(){
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
    public void startWorld(File worldFile, WorldInfo info){
        super.startWorld(worldFile, info);

        this.player = this.world.createPlayer(this.uniqueId, this.playerDesign, null);
        this.world.addEntity(this.player);

        this.guiManager.reInitSelf(this);
        this.guiManager.closeGui();
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
    }

    @Override
    public void quitWorld(){
        super.quitWorld();

        if(this.player != null){
            if(RockBottomAPI.getNet().isClient()){
                Log.info("Sending disconnection packet");
                RockBottomAPI.getNet().sendToServer(new PacketDisconnect(this.player.getUniqueId()));
            }
        }

        this.player = null;

        this.guiManager.reInitSelf(this);
        this.guiManager.openGui(new GuiMainMenu());
    }

    @Override
    public PlayerDesign getPlayerDesign(){
        return this.playerDesign;
    }

    @Override
    public boolean isDedicatedServer(){
        return false;
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

        if(this.world != null){
            this.worldRenderer.render(this, this.assetManager, this.particleManager, g, this.world, this.player, this.interactionManager);

            if(this.isDebug){
                DebugRenderer.render(this, this.assetManager, this.world, this.player, container, g);
            }
        }

        g.setLineWidth(this.getGuiScale());
        this.guiManager.render(this, this.assetManager, g, this.player);
    }

    @Override
    public void openIngameMenu(){
        this.guiManager.openGui(new GuiMenu());

        if(!RockBottomAPI.getNet().isClient()){
            this.world.save();
        }
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
    public IAssetManager getAssetManager(){
        return this.assetManager;
    }

    @Override
    public ParticleManager getParticleManager(){
        return this.particleManager;
    }

    @Override
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

    @Override
    public Settings getSettings(){
        return this.settings;
    }
}
