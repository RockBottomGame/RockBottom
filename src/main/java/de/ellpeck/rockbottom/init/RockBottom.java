package de.ellpeck.rockbottom.init;

import de.ellpeck.rockbottom.Main;
import de.ellpeck.rockbottom.api.IApiHandler;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IGraphics;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.tex.ITexture;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.event.IEventHandler;
import de.ellpeck.rockbottom.api.event.impl.LoadSettingsEvent;
import de.ellpeck.rockbottom.api.event.impl.PlayerLeaveWorldEvent;
import de.ellpeck.rockbottom.api.event.impl.WorldLoadEvent;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.toast.IToaster;
import de.ellpeck.rockbottom.api.world.DynamicRegistryInfo;
import de.ellpeck.rockbottom.api.world.WorldInfo;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.apiimpl.Graphics;
import de.ellpeck.rockbottom.apiimpl.Toaster;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.assets.Texture;
import de.ellpeck.rockbottom.gui.*;
import de.ellpeck.rockbottom.gui.menu.GuiMainMenu;
import de.ellpeck.rockbottom.gui.menu.GuiMenu;
import de.ellpeck.rockbottom.net.client.ClientWorld;
import de.ellpeck.rockbottom.net.packet.toserver.PacketDisconnect;
import de.ellpeck.rockbottom.net.server.ConnectedPlayer;
import de.ellpeck.rockbottom.particle.ParticleManager;
import de.ellpeck.rockbottom.render.PlayerDesign;
import de.ellpeck.rockbottom.render.WorldRenderer;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import de.ellpeck.rockbottom.world.entity.player.InteractionManager;
import joptsimple.internal.Strings;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.newdawn.slick.Input;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.Music;
import org.newdawn.slick.opengl.ImageIOImageData;
import org.newdawn.slick.opengl.LoadableImageData;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;

public class RockBottom extends AbstractGame implements InputListener{

    protected Settings settings;
    private EntityPlayer player;
    private PlayerDesign playerDesign;
    private GuiManager guiManager;
    private InteractionManager interactionManager;
    private AssetManager assetManager;
    private ParticleManager particleManager;
    private Toaster toaster;
    private UUID uniqueId;
    private boolean isDebug;
    private boolean isLightDebug;
    private boolean isItemInfoDebug;
    private boolean isChunkBorderDebug;
    private WorldRenderer worldRenderer;
    private int windowedWidth;
    private int windowedHeight;
    private IGraphics graphics;
    private Input input;
    private int lastWidth;
    private int lastHeight;

    private float displayRatio;
    private float guiScale;
    private float worldScale;
    private float guiWidth;
    private float guiHeight;
    private float worldWidth;
    private float worldHeight;

    public static void startGame(){
        doInit(new RockBottom());
    }

    @Override
    public void init(){
        try{
            Display.setDisplayMode(new DisplayMode(Main.width, Main.height));
            Display.setFullscreen(Main.fullscreen);
        }
        catch(LWJGLException e){
            RockBottomAPI.logger().log(Level.SEVERE, "Couldn't set initial display mode", e);
        }

        Display.setTitle(AbstractGame.NAME+" "+AbstractGame.VERSION);
        Display.setResizable(true);

        try{
            String[] icons = new String[]{"16x16.png", "32x32.png", "128x128.png"};
            ByteBuffer[] bufs = new ByteBuffer[icons.length];

            LoadableImageData data = new ImageIOImageData();
            for(int i = 0; i < icons.length; i++){
                bufs[i] = data.loadImage(AssetManager.getResource("/assets/rockbottom/tex/icon/"+icons[i]), false, null);
            }

            Display.setIcon(bufs);
        }
        catch(Exception e){
            RockBottomAPI.logger().log(Level.WARNING, "Couldn't set game icon", e);
        }

        AccessController.doPrivileged((PrivilegedAction)() -> {
            try{
                PixelFormat format = new PixelFormat(8, 8, 0);
                Display.create(format);
            }
            catch(LWJGLException e){
                RockBottomAPI.logger().log(Level.WARNING, "Couldn't create pixel format", e);

                try{
                    Display.create();
                }
                catch(LWJGLException e2){
                    throw new RuntimeException("Failed to initialize LWJGL display", e2);
                }
            }
            return null;
        });

        RockBottomAPI.logger().info("Initializing system");

        this.initGraphics();

        try{
            ITexture tex = new Texture(AssetManager.getResource("/assets/rockbottom/tex/intro/loading.png"), "loading", false);
            tex.draw(0, 0, Display.getWidth(), Display.getHeight());
            Display.update();
        }
        catch(Exception e){
            RockBottomAPI.logger().log(Level.WARNING, "Couldn't render loading screen image", e);
        }

        RockBottomAPI.logger().info("Finished initializing system");

        super.init();

        this.guiManager.updateDimensions();
        this.guiManager.fadeIn(30, null);

        if(!Main.skipIntro){
            this.guiManager.openGui(new GuiLogo("intro.ellpeck", new GuiLogo("intro.wiiv", new GuiMainMenu())));
        }
        else{
            this.guiManager.openGui(new GuiMainMenu());
        }
    }

    protected void initGraphics(){
        int width = Display.getWidth();
        int height = Display.getHeight();

        this.lastWidth = width;
        this.lastHeight = height;

        this.graphics = new Graphics();
        Renderer.get().initDisplay(width, height);
        Renderer.get().enterOrtho(width, height);

        this.input = new Input(height);
        this.input.addListener(this);
    }

    @Override
    public void preInit(IGameInstance game, IApiHandler apiHandler, IEventHandler eventHandler){
        this.settings = new Settings();
        RockBottomAPI.getEventHandler().fireEvent(new LoadSettingsEvent(this.settings));
        this.dataManager.loadPropSettings(this.settings);

        this.setFullscreen(this.settings.fullscreen);
        Display.setVSyncEnabled(this.settings.vsync);

        this.assetManager = new AssetManager();
        this.assetManager.create(this);

        RockBottomAPI.getModLoader().initAssets();

        this.setPlayerDesign();
        this.calcScales();
    }

    private void setPlayerDesign(){
        this.playerDesign = new PlayerDesign();
        this.playerDesign.loadFromFile();

        if(Strings.isNullOrEmpty(this.playerDesign.getName())){
            PlayerDesign.randomizeDesign(this.playerDesign);
            RockBottomAPI.logger().info("Randomizing player design");

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
        this.toaster = new Toaster();

        TileLayer.initLayerList();
    }

    @Override
    public void setFullscreen(boolean fullscreen){
        try{
            if(Display.isFullscreen() != fullscreen){
                if(fullscreen){
                    this.windowedWidth = Display.getWidth();
                    this.windowedHeight = Display.getHeight();

                    Display.setDisplayMode(Display.getDesktopDisplayMode());
                    Display.setFullscreen(true);
                }
                else{
                    Display.setDisplayMode(new DisplayMode(this.windowedWidth, this.windowedHeight));
                    Display.setFullscreen(false);

                    Display.setResizable(false); //Workaround for stupid LWJGL bug
                    Display.setResizable(true);
                }

                this.initGraphics();
                this.calcScales();

                if(this.guiManager != null){
                    this.guiManager.updateDimensions();
                }
            }
        }
        catch(Exception e){
            RockBottomAPI.logger().log(Level.WARNING, "Failed to set fullscreen", e);
        }
    }

    @Override
    public int getAutosaveInterval(){
        return this.settings.autosaveIntervalSeconds;
    }

    @Override
    protected void update(){
        if(this.world != null && this.player != null){
            Gui gui = this.guiManager.getGui();
            if(gui == null || !gui.doesPauseGame() || RockBottomAPI.getNet().isActive()){
                this.world.update(this);
                this.interactionManager.update(this);

                this.particleManager.update(this);
            }
        }

        this.guiManager.update(this);
        this.toaster.update();
    }

    @Override
    public void startWorld(File worldFile, WorldInfo info){
        super.startWorld(worldFile, info);

        this.player = this.world.createPlayer(this.uniqueId, this.playerDesign, null);
        this.world.addEntity(this.player);

        this.guiManager.updateDimensions();
        this.guiManager.closeGui();
        this.toaster.cancelAllToasts();
    }

    @Override
    public void joinWorld(DataSet playerSet, WorldInfo info, DynamicRegistryInfo regInfo){
        RockBottomAPI.logger().info("Joining world");

        this.world = new ClientWorld(info, regInfo);
        RockBottomAPI.getEventHandler().fireEvent(new WorldLoadEvent(this.world, info, regInfo));

        this.player = this.world.createPlayer(this.uniqueId, this.playerDesign, null);
        this.player.load(playerSet);
        this.world.addEntity(this.player);

        this.guiManager.updateDimensions();
        this.guiManager.closeGui();
        this.toaster.cancelAllToasts();
    }

    @Override
    public void quitWorld(){
        super.quitWorld();

        if(this.player != null){
            if(RockBottomAPI.getNet().isClient()){
                RockBottomAPI.logger().info("Sending disconnection packet");
                RockBottomAPI.getNet().sendToServer(new PacketDisconnect(this.player.getUniqueId()));
            }
        }

        if(this.player != null){
            RockBottomAPI.getEventHandler().fireEvent(new PlayerLeaveWorldEvent(this.player, this.player instanceof ConnectedPlayer));
            this.player = null;
        }

        this.guiManager.updateDimensions();
        this.guiManager.openGui(new GuiMainMenu());
        this.toaster.cancelAllToasts();
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
    public void shutdown(){
        super.shutdown();

        Display.destroy();
        AL.destroy();
    }

    @Override
    public void mouseWheelMoved(int change){

    }

    @Override
    public void mouseClicked(int button, int x, int y, int clickCount){

    }

    @Override
    public void mousePressed(int button, int x, int y){
        this.interactionManager.onMouseAction(this, button);
    }

    @Override
    public void mouseReleased(int button, int x, int y){

    }

    @Override
    public void mouseMoved(int oldx, int oldy, int newx, int newy){

    }

    @Override
    public void mouseDragged(int oldx, int oldy, int newx, int newy){

    }

    @Override
    public void keyPressed(int key, char c){
        if(this.guiManager.getGui() == null){
            if(Settings.KEY_MENU.isKey(key)){
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
            else if(key == Input.KEY_F5){
                this.isItemInfoDebug = !this.isItemInfoDebug;
                return;
            }
            else if(key == Input.KEY_F6){
                this.isChunkBorderDebug = !this.isChunkBorderDebug;
                return;
            }
            else if(Settings.KEY_INVENTORY.isKey(key)){
                this.player.openGuiContainer(new GuiInventory(this.player), this.player.getInvContainer());
                return;
            }
            else if(Settings.KEY_CHAT.isKey(key) && RockBottomAPI.getNet().isActive()){
                this.guiManager.openGui(new GuiChat());
                return;
            }
        }

        if(Settings.KEY_SCREENSHOT.isKey(key)){
            this.takeScreenshot();
            return;
        }

        this.interactionManager.onKeyboardAction(this, key, c);
    }

    @Override
    public void keyReleased(int key, char c){

    }

    @Override
    protected void updateTickless(int delta){
        if(Display.isCloseRequested()){
            this.exit();
        }
        else{
            this.input.poll(Display.getWidth(), Display.getHeight());

            Music.poll(delta);

            Renderer.get().glClear(SGL.GL_COLOR_BUFFER_BIT | SGL.GL_DEPTH_BUFFER_BIT);
            Renderer.get().glLoadIdentity();
            Renderer.get().glDisable(SGL.GL_POLYGON_SMOOTH);

            this.render();

            Renderer.get().flush();

            if(this.settings.targetFps != -1){
                Display.sync(this.settings.targetFps);
            }

            Display.update();

            if(!Display.isFullscreen() && Display.wasResized()){
                if(this.lastWidth != Display.getWidth() || this.lastHeight != Display.getHeight()){
                    this.initGraphics();
                    this.calcScales();

                    this.guiManager.updateDimensions();
                }
            }
        }
    }

    protected void render(){
        if(this.world != null){
            this.worldRenderer.render(this, this.assetManager, this.particleManager, this.graphics, this.world, this.player, this.interactionManager);

            if(this.isDebug){
                DebugRenderer.render(this, this.assetManager, this.world, this.player, this.graphics);
            }
        }

        this.graphics.pushMatrix();
        this.graphics.scale(this.guiScale, this.guiScale);

        this.guiManager.render(this, this.assetManager, this.graphics, this.player);
        this.toaster.render(this, this.assetManager, this.graphics);

        this.graphics.popMatrix();
    }

    @Override
    public void openIngameMenu(){
        this.guiManager.openGui(new GuiMenu());

        if(!RockBottomAPI.getNet().isClient()){
            this.world.save();
        }
    }

    @Override
    public void calcScales(){
        RockBottomAPI.logger().config("Calculating render scales");

        float width = Display.getWidth();
        float height = Display.getHeight();

        this.displayRatio = Math.min(width/16F, height/9F);

        this.guiScale = (this.getDisplayRatio()/20F)*this.settings.guiScale;
        this.guiWidth = width/this.guiScale;
        this.guiHeight = height/this.guiScale;

        this.worldScale = this.getDisplayRatio()*this.settings.renderScale;
        this.worldWidth = width/this.worldScale;
        this.worldHeight = height/this.worldScale;

        RockBottomAPI.logger().config("Successfully calculated render scales");
    }

    @Override
    public float getDisplayRatio(){
        return this.displayRatio;
    }

    @Override
    public float getGuiScale(){
        return this.guiScale;
    }

    @Override
    public float getWorldScale(){
        return this.worldScale;
    }

    @Override
    public float getWidthInWorld(){
        return this.worldWidth;
    }

    @Override
    public float getHeightInWorld(){
        return this.worldHeight;
    }

    @Override
    public float getWidthInGui(){
        return this.guiWidth;
    }

    @Override
    public float getHeightInGui(){
        return this.guiHeight;
    }

    @Override
    public float getMouseInGuiX(){
        return (float)this.input.getMouseX()/this.getGuiScale();
    }

    @Override
    public float getMouseInGuiY(){
        return (float)this.input.getMouseY()/this.getGuiScale();
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
    public IGraphics getGraphics(){
        return this.graphics;
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
    public Input getInput(){
        return this.input;
    }

    @Override
    public IToaster getToaster(){
        return this.toaster;
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
    public boolean isItemInfoDebug(){
        return this.isItemInfoDebug;
    }

    @Override
    public boolean isChunkBorderDebug(){
        return this.isChunkBorderDebug;
    }

    private void takeScreenshot(){
        try{
            RockBottomAPI.logger().info("Taking screenshot");

            GL11.glReadBuffer(GL11.GL_FRONT);
            int width = Display.getWidth();
            int height = Display.getHeight();
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
                RockBottomAPI.logger().info("Creating screenshot folder at "+dir);
            }

            File file = new File(dir, new SimpleDateFormat("dd.MM.yy_HH.mm.ss").format(new Date())+".png");
            ImageIO.write(image, "png", file);

            RockBottomAPI.logger().info("Saved screenshot to "+file);
        }
        catch(Exception e){
            RockBottomAPI.logger().log(Level.WARNING, "Couldn't take screenshot", e);
        }
    }

    @Override
    public Settings getSettings(){
        return this.settings;
    }

    @Override
    public void controllerLeftPressed(int controller){

    }

    @Override
    public void controllerLeftReleased(int controller){

    }

    @Override
    public void controllerRightPressed(int controller){

    }

    @Override
    public void controllerRightReleased(int controller){

    }

    @Override
    public void controllerUpPressed(int controller){

    }

    @Override
    public void controllerUpReleased(int controller){

    }

    @Override
    public void controllerDownPressed(int controller){

    }

    @Override
    public void controllerDownReleased(int controller){

    }

    @Override
    public void controllerButtonPressed(int controller, int button){

    }

    @Override
    public void controllerButtonReleased(int controller, int button){

    }

    @Override
    public void setInput(Input input){

    }

    @Override
    public boolean isAcceptingInput(){
        return true;
    }

    @Override
    public void inputEnded(){

    }

    @Override
    public void inputStarted(){

    }
}
