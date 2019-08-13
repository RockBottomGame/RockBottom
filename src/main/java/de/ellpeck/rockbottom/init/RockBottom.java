package de.ellpeck.rockbottom.init;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import de.ellpeck.rockbottom.Main;
import de.ellpeck.rockbottom.api.*;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.IShaderProgram;
import de.ellpeck.rockbottom.api.assets.texture.ITexture;
import de.ellpeck.rockbottom.api.data.IDataManager;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.event.IEventHandler;
import de.ellpeck.rockbottom.api.event.impl.LoadSettingsEvent;
import de.ellpeck.rockbottom.api.event.impl.MakeCameraCoordsEvent;
import de.ellpeck.rockbottom.api.event.impl.PlayerLeaveWorldEvent;
import de.ellpeck.rockbottom.api.event.impl.WorldLoadEvent;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentTranslation;
import de.ellpeck.rockbottom.api.render.IPlayerDesign;
import de.ellpeck.rockbottom.api.toast.IToaster;
import de.ellpeck.rockbottom.api.toast.ToastBasic;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.DynamicRegistryInfo;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.WorldInfo;
import de.ellpeck.rockbottom.apiimpl.InputHandler;
import de.ellpeck.rockbottom.apiimpl.Renderer;
import de.ellpeck.rockbottom.apiimpl.Toaster;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.assets.sound.SoundHandler;
import de.ellpeck.rockbottom.assets.tex.Texture;
import de.ellpeck.rockbottom.content.ContentManager;
import de.ellpeck.rockbottom.gui.DebugRenderer;
import de.ellpeck.rockbottom.gui.GuiInformation;
import de.ellpeck.rockbottom.gui.GuiLogo;
import de.ellpeck.rockbottom.gui.GuiManager;
import de.ellpeck.rockbottom.gui.menu.GuiMainMenu;
import de.ellpeck.rockbottom.gui.menu.GuiMenu;
import de.ellpeck.rockbottom.log.Logging;
import de.ellpeck.rockbottom.net.client.ClientWorld;
import de.ellpeck.rockbottom.net.packet.toserver.PacketDisconnect;
import de.ellpeck.rockbottom.net.server.ConnectedPlayer;
import de.ellpeck.rockbottom.particle.ParticleManager;
import de.ellpeck.rockbottom.render.WorldRenderer;
import de.ellpeck.rockbottom.render.design.PlayerDesign;
import de.ellpeck.rockbottom.util.ChangelogManager;
import de.ellpeck.rockbottom.util.CrashManager;
import de.ellpeck.rockbottom.world.AbstractWorld;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import de.ellpeck.rockbottom.world.entity.player.InteractionManager;
import joptsimple.internal.Strings;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class RockBottom extends AbstractGame {

    private static final UUID DEFAULT_UUID = new UUID(0, 0);
    private final GLFWErrorCallback errorCallback = new GLFWErrorCallback() {
        @Override
        public void invoke(int error, long description) {
            Logging.glfwLogger.log(Level.WARNING, GLFWErrorCallback.getDescription(description), new RuntimeException());
        }
    };
    public AssetManager assetManager;
    public UUID uniqueId;
    public Renderer renderer;
    protected Settings settings;
    private EntityPlayer player;
    private IPlayerDesign playerDesign;
    private GuiManager guiManager;
    private InteractionManager interactionManager;
    private ParticleManager particleManager;
    private Toaster toaster;
    private WorldRenderer worldRenderer;
    private int windowedWidth;
    private int windowedHeight;
    private InputHandler input;
    private int width;
    private int height;
    private long windowId;
    private boolean isFullscreen;

    public static void startGame() {
        doInit(new RockBottom());
    }

    public static void savePlayerDesign(IGameInstance game, IPlayerDesign design) {
        try {
            IDataManager data = game.getDataManager();
            FileWriter writer = new FileWriter(data.getPlayerDesignFile());
            Util.GSON.toJson(design, writer);
            writer.close();
        } catch (Exception e) {
            RockBottomAPI.logger().log(Level.WARNING, "Couldn't save player design to file", e);
        }
    }

    @Override
    public void init() {
        RockBottomAPI.logger().info("Initializing GLFW");

        GLFW.glfwSetErrorCallback(this.errorCallback);

        Preconditions.checkState(GLFW.glfwInit(), "Unable to inialize GLFW");

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);

        RockBottomAPI.logger().info("Initializing window");

        this.windowId = GLFW.glfwCreateWindow(Main.width, Main.height, AbstractGame.NAME + ' ' + AbstractGame.VERSION, MemoryUtil.NULL, MemoryUtil.NULL);
        if (this.windowId == MemoryUtil.NULL) {
            GLFW.glfwTerminate();
            throw new IllegalStateException("Unable to create window");
        }

        GLFWVidMode mode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        GLFW.glfwSetWindowPos(this.windowId, mode.width() / 2 - Main.width / 2, mode.height() / 2 - Main.height / 2);

        RockBottomAPI.logger().info("Initializing system");

        GLFW.glfwMakeContextCurrent(this.windowId);
        GL.createCapabilities();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        try {
            String[] icons = new String[]{"16x16.png", "32x32.png", "128x128.png"};
            GLFWImage.Buffer imageBuffer = GLFWImage.malloc(icons.length);

            for (int i = 0; i < icons.length; i++) {
                Texture texture = new Texture(ContentManager.getResourceAsStream("assets/rockbottom/tex/icon/" + icons[i]));
                imageBuffer.position(i).width(texture.getTextureWidth()).height(texture.getTextureHeight()).pixels(texture.getPixelData());
            }

            imageBuffer.position(0);
            GLFW.glfwSetWindowIcon(this.windowId, imageBuffer);
            imageBuffer.free();
        } catch (Exception e) {
            RockBottomAPI.logger().log(Level.WARNING, "Couldn't set game icon", e);
        }

        this.getWindowSize();
        this.renderer = new Renderer(this);

        this.renderer.begin();
        try {
            ITexture tex = new Texture(ContentManager.getResourceAsStream("assets/rockbottom/tex/intro/loading.png"));
            tex.draw(0, 0, this.width, this.height);

            List<String> lines = new ArrayList<>();

            BufferedReader reader = new BufferedReader(new InputStreamReader(ContentManager.getResourceAsStream("assets/rockbottom/text/loading.txt"), Charsets.UTF_8));
            while (true) {
                String line = reader.readLine();
                if (line != null) {
                    lines.add(line);
                } else {
                    break;
                }
            }
            reader.close();

            String line = lines.get((int) (Util.getTimeMillis() % lines.size())) + "...";

            float scale = (this.width / this.height) * 2F;
            List<String> list = this.renderer.simpleFont.splitTextToLength(this.width - 10, scale, true, line);
            for (int i = 0; i < list.size(); i++) {
                this.renderer.simpleFont.drawCenteredString(this.width / 2, 30 + (this.renderer.simpleFont.getHeight(scale) * i), list.get(i), scale, false);
            }
        } catch (Exception e) {
            RockBottomAPI.logger().log(Level.WARNING, "Couldn't render loading screen", e);
        }
        this.renderer.end();

        GLFW.glfwShowWindow(this.windowId);
        GLFW.glfwSwapBuffers(this.windowId);
        GLFW.glfwPollEvents();

        GLFW.glfwSetWindowSizeCallback(this.windowId, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                RockBottom.this.onResize();
            }
        });

        this.input = new InputHandler(this);
        SoundHandler.init();

        RockBottomAPI.logger().info("Finished initializing system");

        ChangelogManager.loadChangelog();
        super.init();

        //TODO Remove this once the auth system is actually there
        this.tempUUID();

        this.guiManager.updateDimensions();
        if (!Main.skipIntro) {
            this.guiManager.openGui(new GuiLogo("intro.ellpeck", new GuiLogo("intro.wiiv", new GuiMainMenu())));
        } else {
            this.guiManager.openGui(new GuiMainMenu());
        }
        this.guiManager.fadeIn(30, null);
    }

    private void tempUUID() {
        File dataFile = new File(this.dataManager.getGameDir(), "uuid.dat");
        if (dataFile.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile)));
                this.uniqueId = UUID.fromString(reader.readLine());
                RockBottomAPI.logger().info("Setting game uuid to " + this.uniqueId);
                reader.close();
                return;
            } catch (Exception e) {
                RockBottomAPI.logger().log(Level.WARNING, "Couldn't read game uuid", e);
            }
        }

        this.uniqueId = UUID.randomUUID();
        RockBottomAPI.logger().info("Creating new game uuid " + this.uniqueId);
        try {
            dataFile.getParentFile().mkdirs();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataFile)));
            writer.write(this.uniqueId.toString());
            writer.close();
        } catch (Exception e) {
            RockBottomAPI.logger().log(Level.WARNING, "Couldn't write game uuid", e);
        }
    }

    protected void getWindowSize() {
        MemoryStack stack = MemoryStack.stackPush();
        IntBuffer width = stack.mallocInt(1);
        IntBuffer height = stack.mallocInt(1);

        GLFW.glfwGetFramebufferSize(this.windowId, width, height);

        this.width = width.get();
        this.height = height.get();
        stack.pop();
    }

    protected void onResize() {
        this.getWindowSize();
        this.renderer.calcScales();

        GL11.glViewport(0, 0, this.width, this.height);

        if (this.guiManager != null) {
            this.guiManager.updateDimensions();
        }

        if (this.assetManager != null) {
            this.assetManager.onResize(this.width, this.height);
        }
    }

    @Override
    public void preInit(IGameInstance game, IApiHandler apiHandler, IEventHandler eventHandler) {
        super.preInit(game, apiHandler, eventHandler);

        this.settings = new Settings();
        RockBottomAPI.getEventHandler().fireEvent(new LoadSettingsEvent(this.settings));
        this.settings.load();

        this.setFullscreen(this.settings.fullscreen);

        this.assetManager = new AssetManager(this);
        this.assetManager.load();

        this.setPlayerDesign();
        this.renderer.calcScales();
    }

    private void setPlayerDesign() {
        try {
            FileReader reader = new FileReader(this.dataManager.getPlayerDesignFile());
            this.playerDesign = Util.GSON.fromJson(reader, PlayerDesign.class);
        } catch (Exception e) {
            this.playerDesign = new PlayerDesign();
        }

        if (Strings.isNullOrEmpty(this.playerDesign.getName())) {
            PlayerDesign.randomizeDesign(this.playerDesign);
            RockBottomAPI.logger().info("Randomizing player design");

            savePlayerDesign(this, this.playerDesign);
        }
    }

    @Override
    public void postInit(IGameInstance game, IApiHandler apiHandler, IEventHandler eventHandler) {
        super.postInit(game, apiHandler, eventHandler);

        this.guiManager = new GuiManager();
        this.interactionManager = new InteractionManager();

        this.worldRenderer = new WorldRenderer();
        this.particleManager = new ParticleManager();
        this.toaster = new Toaster();
    }

    @Override
    public void setFullscreen(boolean fullscreen) {
        if (this.isFullscreen != fullscreen) {
            try {
                long monitor = GLFW.glfwGetPrimaryMonitor();
                GLFWVidMode mode = GLFW.glfwGetVideoMode(monitor);

                if (fullscreen) {
                    this.windowedWidth = this.width;
                    this.windowedHeight = this.height;

                    GLFW.glfwSetWindowMonitor(this.windowId, monitor, 0, 0, mode.width(), mode.height(), mode.refreshRate());
                    this.isFullscreen = true;
                } else {
                    int x = mode.width() / 2 - this.windowedWidth / 2;
                    int y = mode.height() / 2 - this.windowedHeight / 2;
                    GLFW.glfwSetWindowMonitor(this.windowId, MemoryUtil.NULL, x, y, this.windowedWidth, this.windowedHeight, mode.refreshRate());
                    this.isFullscreen = false;
                }

                this.onResize();
            } catch (Exception e) {
                RockBottomAPI.logger().log(Level.WARNING, "Failed to set fullscreen", e);
            }
        }
    }

    @Override
    public int getAutosaveInterval() {
        return this.settings.autosaveIntervalSeconds;
    }

    @Override
    public int getPlayerCap() {
        return 10;
    }

    @Override
    protected void update() {
        this.input.update();

        if (this.world != null && this.player != null) {
            Gui gui = this.guiManager.getGui();
            if (gui == null || !gui.doesPauseGame() || RockBottomAPI.getNet().isActive()) {
                this.world.update(this);
                this.interactionManager.update(this);

                this.particleManager.update(this);
                this.worldRenderer.update(this.getPlayerWorld(), this.particleManager);
            }
        }

        if (RockBottomAPI.getNet().isClient()) {
            if (!RockBottomAPI.getNet().isConnectedToServer()) {
                this.quitWorld();
                this.guiManager.openGui(new GuiInformation(this.guiManager.getGui(), 0.5F, true, this.assetManager.localize(ResourceName.intern("info.reject.server_down"))));
            }
        }

        this.guiManager.update(this);
        this.toaster.update();
    }

    @Override
    public void startWorld(File worldFile, WorldInfo info, boolean isNewlyCreated) {
        super.startWorld(worldFile, info, isNewlyCreated);

        this.player = this.world.createPlayer(this.getUniqueId(), this.playerDesign, null, true);
        this.player.world.addEntity(this.player);
        this.player.world.addPlayer(this.player);

        this.guiManager.closeGui();
        this.guiManager.updateDimensions();
        this.toaster.cancelAllToasts();
    }

    @Override
    public void joinWorld(DataSet playerSet, WorldInfo info, ResourceName subName, DataSet worldData, DynamicRegistryInfo regInfo) {
        RockBottomAPI.logger().info("Joining world");

        this.world = new ClientWorld(info, regInfo);
        this.world.loadWorldData(worldData);
        this.world.setSubName(subName);

        RockBottomAPI.getEventHandler().fireEvent(new WorldLoadEvent(this.world, info, regInfo));

        this.player = this.world.createPlayer(this.getUniqueId(), this.playerDesign, null, false);
        this.player.load(playerSet, true);

        this.world.addEntity(this.player);
        this.world.addPlayer(this.player);
    }

    @Override
    public void changeWorld(ResourceName subName, DataSet worldData) {
        if (this.world != null) {
            if (this.world.isClient()) {
                this.world.unloadEverything();

                this.world.setSubName(subName);
                this.world.loadWorldData(worldData);
                this.world.addEntity(this.player);

                RockBottomAPI.getEventHandler().fireEvent(new WorldLoadEvent(this.world, this.world.getWorldInfo(), this.world.getRegInfo()));
            } else {
                throw new UnsupportedOperationException("Cannot change the world this way on a non-client instance");
            }
        }
    }

    @Override
    public void quitWorld() {
        super.quitWorld();

        if (this.player != null) {
            if (RockBottomAPI.getNet().isClient()) {
                RockBottomAPI.logger().info("Sending disconnection packet");
                RockBottomAPI.getNet().sendToServer(new PacketDisconnect(this.player.getUniqueId()));
            }

            RockBottomAPI.getEventHandler().fireEvent(new PlayerLeaveWorldEvent(this.player, this.player instanceof ConnectedPlayer));
            this.player = null;
        }

        if (this.guiManager != null) {
            this.guiManager.closeGui();
            this.guiManager.updateDimensions();
            this.guiManager.openGui(new GuiMainMenu());
        }

        if (this.toaster != null) {
            this.toaster.cancelAllToasts();
        }
    }

    @Override
    public IPlayerDesign getPlayerDesign() {
        return this.playerDesign;
    }

    @Override
    public void setPlayerDesign(String jsonString) {
        this.playerDesign = Util.GSON.fromJson(jsonString, PlayerDesign.class);
    }

    @Override
    public boolean isDedicatedServer() {
        return false;
    }

    @Override
    protected void shutdown() {
        super.shutdown();

        RockBottomAPI.logger().info("Disposing of resources");

        if (this.renderer != null) {
            this.renderer.dispose();
        }
        if (this.assetManager != null) {
            this.assetManager.dispose();
        }

        SoundHandler.dispose();

        if (this.windowId != MemoryUtil.NULL) {
            GLFW.glfwDestroyWindow(this.windowId);
            Callbacks.glfwFreeCallbacks(this.windowId);
        }

        GLFW.glfwTerminate();
        this.errorCallback.free();

        RockBottomAPI.logger().info("Successfully disposed of resources.");
    }

    @Override
    protected void onCrash() {
        super.onCrash();

        try {
            CrashManager.addInfo("--------- Debug Info ---------");
            for (String s : DebugRenderer.getInfo(this, this.getPlayerWorld(), this.player, this.renderer)) {
                CrashManager.addInfo(s);
            }
            CrashManager.addInfo("------------------------------");
        } catch (Exception e) {
            CrashManager.addInfo("Debug information couldn't be gathered");
        }
    }

    @Override
    protected void updateTickless() {
        if (GLFW.glfwWindowShouldClose(this.windowId)) {
            this.exit();
        } else {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            if (this.player != null) {
                MakeCameraCoordsEvent event = new MakeCameraCoordsEvent(this.player, this.player.getLerpedX(), this.player.getLerpedY() - 0.5D);
                RockBottomAPI.getEventHandler().fireEvent(event);
                this.renderer.cameraX = event.cameraX;
                this.renderer.cameraY = event.cameraY;
            } else {
                this.renderer.cameraX = 0D;
                this.renderer.cameraY = 0D;
            }
            SoundHandler.setListenerPos(this.renderer.cameraX, this.renderer.cameraY);

            this.render();

            Texture.binds = 0;

            GLFW.glfwSwapBuffers(this.windowId);
            GLFW.glfwPollEvents();
        }
    }

    protected void render() {
        this.renderer.setDefaultProgram(this.assetManager.getShaderProgram(IShaderProgram.WORLD_SHADER));
        this.worldRenderer.calcCameraValues(this.assetManager, this.renderer);
        this.renderer.begin();

        if (this.player != null) {
            this.worldRenderer.render(this, this.assetManager, this.particleManager, this.renderer, (AbstractWorld) this.getPlayerWorld(), this.player, this.interactionManager);
        }

        this.renderer.setDefaultProgram(this.assetManager.getShaderProgram(IShaderProgram.GUI_SHADER));

        float scale = this.renderer.getGuiScale();

        IShaderProgram guiShader = assetManager.getShaderProgram(IShaderProgram.GUI_SHADER);
        guiShader.bind();
        guiShader.setUniform("scale", scale, scale);

        this.renderer.setScale(1, 1);

        this.guiManager.render(this, this.assetManager, this.renderer, this.player);
        this.toaster.render(this, this.assetManager, this.renderer);

        this.renderer.setScale(1F, 1F);

        if (this.renderer.isDebug()) {
            DebugRenderer.render(this, this.assetManager, this.getPlayerWorld(), this.player, this.renderer);
        }

        this.renderer.end();
    }

    @Override
    public void openIngameMenu() {
        this.guiManager.openGui(new GuiMenu());

        if (!RockBottomAPI.getNet().isClient()) {
            this.world.save();
        }
    }

    @Override
    public EntityPlayer getPlayer() {
        return this.player;
    }

    @Override
    public GuiManager getGuiManager() {
        return this.guiManager;
    }

    @Override
    public InteractionManager getInteractionManager() {
        return this.interactionManager;
    }

    @Override
    public IWorld getPlayerWorld() {
        if (this.player == null) {
            return null;
        } else {
            return this.player.world;
        }
    }

    @Override
    public IAssetManager getAssetManager() {
        return this.assetManager;
    }

    @Override
    public IRenderer getRenderer() {
        return this.renderer;
    }

    @Override
    public ParticleManager getParticleManager() {
        return this.particleManager;
    }

    @Override
    public IInputHandler getInput() {
        return this.input;
    }

    @Override
    public IToaster getToaster() {
        return this.toaster;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public long getWindow() {
        return this.windowId;
    }

    @Override
    public UUID getUniqueId() {
        if (this.uniqueId != null) {
            return this.uniqueId;
        } else {
            return this.getDefaultUniqueId();
        }
    }

    @Override
    public UUID getDefaultUniqueId() {
        return DEFAULT_UUID;
    }

    public void takeScreenshot() {
        try {
            RockBottomAPI.logger().info("Taking screenshot");

            GL11.glReadBuffer(GL11.GL_FRONT);
            int colors = 4;

            ByteBuffer buf = BufferUtils.createByteBuffer(this.width * this.height * colors);
            GL11.glReadPixels(0, 0, this.width, this.height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);

            BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);

            for (int x = 0; x < this.width; x++) {
                for (int y = 0; y < this.height; y++) {
                    int i = (x + (y * this.width)) * colors;

                    int r = buf.get(i) & 0xFF;
                    int g = buf.get(i + 1) & 0xFF;
                    int b = buf.get(i + 2) & 0xFF;

                    image.setRGB(x, this.height - (y + 1), Colors.rgb(r, g, b));
                }
            }

            File dir = this.dataManager.getScreenshotDir();
            if (!dir.exists()) {
                dir.mkdirs();
                RockBottomAPI.logger().info("Creating screenshot folder at " + dir);
            }

            File file = new File(dir, new SimpleDateFormat("dd.MM.yy_HH.mm.ss").format(new Date()) + ".png");
            ImageIO.write(image, "png", file);

            RockBottomAPI.logger().info("Saved screenshot to " + file);
            this.toaster.displayToast(new ToastBasic(new ChatComponentTranslation(ResourceName.intern("info.screenshot.title")), new ChatComponentTranslation(ResourceName.intern("info.screenshot"), file.getName()), 350));
        } catch (Exception e) {
            RockBottomAPI.logger().log(Level.WARNING, "Couldn't take screenshot", e);
        }
    }

    @Override
    public Settings getSettings() {
        return this.settings;
    }
}
