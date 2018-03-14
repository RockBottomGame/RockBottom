package de.ellpeck.rockbottom.assets;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.JsonElement;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.*;
import de.ellpeck.rockbottom.api.assets.Locale;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.assets.texture.ITexture;
import de.ellpeck.rockbottom.api.assets.texture.ImageBuffer;
import de.ellpeck.rockbottom.api.content.pack.ContentPack;
import de.ellpeck.rockbottom.api.event.impl.LoadAssetsEvent;
import de.ellpeck.rockbottom.api.gui.ISpecialCursor;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.render.engine.IDisposable;
import de.ellpeck.rockbottom.api.render.engine.VertexProcessor;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.assets.anim.Animation;
import de.ellpeck.rockbottom.assets.anim.AnimationRow;
import de.ellpeck.rockbottom.assets.loader.*;
import de.ellpeck.rockbottom.assets.stub.EmptySound;
import de.ellpeck.rockbottom.assets.tex.Texture;
import de.ellpeck.rockbottom.assets.tex.TextureStitcher;
import de.ellpeck.rockbottom.content.ContentManager;
import de.ellpeck.rockbottom.content.ContentManager.LoaderCallback;
import de.ellpeck.rockbottom.gui.cursor.CursorClosedHand;
import de.ellpeck.rockbottom.gui.cursor.CursorFinger;
import de.ellpeck.rockbottom.gui.cursor.CursorOpenHand;
import de.ellpeck.rockbottom.gui.cursor.CursorPointer;
import de.ellpeck.rockbottom.gui.menu.background.NatureTheme;
import de.ellpeck.rockbottom.gui.menu.background.StoneTheme;
import de.ellpeck.rockbottom.init.RockBottom;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryUtil;

import java.io.InputStream;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

public class AssetManager implements IAssetManager, IDisposable{

    static{
        new AnimationLoader().register();
        new FontLoader().register();
        new LocaleLoader().register();
        new SoundLoader().register();
        new TextureLoader().register();
        new ShaderLoader().register();

        RockBottomAPI.SPECIAL_CURSORS.register(0, new CursorPointer());
        RockBottomAPI.SPECIAL_CURSORS.register(1, new CursorFinger());
        RockBottomAPI.SPECIAL_CURSORS.register(2, new CursorClosedHand());
        RockBottomAPI.SPECIAL_CURSORS.register(3, new CursorOpenHand());

        RockBottomAPI.MAIN_MENU_THEMES.register(0, new StoneTheme());
        RockBottomAPI.MAIN_MENU_THEMES.register(1, new NatureTheme());
    }

    private final TextureStitcher stitcher = new TextureStitcher();
    private final Table<IResourceName, IResourceName, IAsset> assets = HashBasedTable.create();
    private final List<ISpecialCursor> sortedCursors = new ArrayList<>();
    private final Map<ISpecialCursor, Long> cursors = new HashMap<>();
    private ISound missingSound;
    private ITexture missingTexture;
    private Locale missingLocale;
    private IAnimation missingAnimation;
    private Locale currentLocale;
    private Locale defaultLocale;
    private IFont currentFont;
    private boolean isLocked = true;
    private final RockBottom game;

    public AssetManager(RockBottom game){
        this.game = game;
    }

    public void load(){
        this.dispose();
        if(!this.assets.isEmpty()){
            this.assets.clear();
        }

        this.isLocked = false;
        RockBottomAPI.getModLoader().preInitAssets();

        try{
            RockBottomAPI.logger().info("Loading resources...");

            List<ContentPack> packs = RockBottomAPI.getContentPackLoader().getActivePacks();
            Set<IAssetLoader> loaders = RockBottomAPI.ASSET_LOADER_REGISTRY.getUnmodifiable().values();

            List<LoaderCallback> callbacks = new ArrayList<>();
            for(IAssetLoader loader : loaders){
                callbacks.add(new AssetCallback(loader));
            }
            for(IMod mod : RockBottomAPI.getModLoader().getActiveMods()){
                ContentManager.loadContent(mod, mod.getResourceLocation(), "assets.json", callbacks, packs);
            }

            for(IAssetLoader loader : loaders){
                loader.finalize(this);
            }
        }
        catch(Exception e){
            RockBottomAPI.logger().log(Level.SEVERE, "Exception loading resources! ", e);
        }

        ImageBuffer buffer = new ImageBuffer(2, 2);
        for(int x = 0; x < 2; x++){
            for(int y = 0; y < 2; y++){
                buffer.setRGBA(x, y, 0, x == y ? 255 : 0, 0, 255);
            }
        }
        this.stitcher.loadTexture("missing", buffer, (stitchX, stitchY, stitchedTexture) -> this.missingTexture = stitchedTexture);

        try{
            this.stitcher.doStitch();
            this.stitcher.reset();
        }
        catch(Exception e){
            RockBottomAPI.logger().log(Level.SEVERE, "Exception stitching textures", e);
        }

        this.loadCursors();
        RockBottomAPI.getModLoader().initAssets();

        this.missingSound = new EmptySound();
        this.missingLocale = new Locale("fallback", new HashMap<>());
        this.missingAnimation = new Animation(this.missingTexture, 2, 2, new ArrayList<>(Collections.singletonList(new AnimationRow(new float[]{1F}))));

        RockBottomAPI.logger().info("Loaded "+this.getAllOfType(ITexture.ID).size()+" texture resources!");
        RockBottomAPI.logger().info("Loaded "+this.getAllOfType(ISound.ID).size()+" sound resources!");
        RockBottomAPI.logger().info("Loaded "+this.getAllOfType(IAnimation.ID).size()+" animations!");
        RockBottomAPI.logger().info("Possible language settings: "+this.getAllOfType(Locale.ID).keySet());

        this.defaultLocale = this.getLocale(RockBottomAPI.createInternalRes("us_english"));

        this.currentFont = this.getFont(RockBottomAPI.createInternalRes("default"));
        this.currentLocale = this.getLocale(RockBottomAPI.createRes(this.game.getSettings().currentLocale));

        RockBottomAPI.getEventHandler().fireEvent(new LoadAssetsEvent(this.game, this, this.game.getRenderer()));
        this.initInternalShaders(this.game.getWidth(), this.game.getHeight());

        RockBottomAPI.getModLoader().postInitAssets();
        this.isLocked = true;
    }

    private void initInternalShaders(int width, int height){
        IShaderProgram guiShader = this.getShaderProgram(IShaderProgram.GUI_SHADER);
        guiShader.setDefaultValues(width, height);

        IShaderProgram worldShader = this.getShaderProgram(IShaderProgram.WORLD_SHADER);
        worldShader.setDefaultValues(width, height);

        IShaderProgram breakShader = this.getShaderProgram(IShaderProgram.BREAK_SHADER);
        breakShader.setVertexProcessing(10, new VertexProcessor(){
            private int vertexCounter;

            @Override
            public void addVertex(IRenderer renderer, float x, float y, int color, float u, float v){
                super.addVertex(renderer, x, y, color, u, v);

                ITexture tex = AssetManager.this.getTexture(RockBottomAPI.createInternalRes("break."+Util.ceil(RockBottomAPI.getGame().getInteractionManager().getBreakProgress()*8F)));

                float breakU;
                float breakV;

                switch(this.vertexCounter){
                    case 0:
                    case 3:
                        breakU = 0F;
                        breakV = 0F;
                        break;
                    case 1:
                        breakU = 0F;
                        breakV = 1F;
                        break;
                    case 2:
                    case 4:
                        breakU = 1F;
                        breakV = 1F;
                        break;
                    default:
                        breakU = 1F;
                        breakV = 0F;
                        break;
                }

                renderer.put((breakU*tex.getRenderWidth()+tex.getRenderOffsetX())/tex.getTextureWidth())
                        .put((breakV*tex.getRenderHeight()+tex.getRenderOffsetY())/tex.getTextureHeight());

                this.vertexCounter++;
                if(this.vertexCounter >= 6){
                    this.vertexCounter = 0;
                }
            }

            @Override
            public void onFlush(IRenderer renderer){
                this.vertexCounter = 0;
            }
        });
        breakShader.setDefaultValues(width, height);
        breakShader.pointVertexAttribute("breakTexCoord", 2);
        breakShader.setUniform("breakImage", 1);
    }

    private void loadCursors(){
        if(!this.cursors.isEmpty()){
            this.cursors.clear();
        }
        if(!this.sortedCursors.isEmpty()){
            this.sortedCursors.clear();
        }

        this.sortedCursors.addAll(RockBottomAPI.SPECIAL_CURSORS.getUnmodifiable().values());
        this.sortedCursors.sort(Comparator.comparingInt(ISpecialCursor:: getPriority).reversed());

        for(ISpecialCursor cursor : this.sortedCursors){
            try{
                ITexture texture = this.getTexture(cursor.getTexture());
                int width = texture.getRenderWidth();
                int height = texture.getRenderHeight();

                GLFWImage image = GLFWImage.malloc();
                ByteBuffer buf = BufferUtils.createByteBuffer(width*height*4);

                for(int y = 0; y < height; y++){
                    for(int x = 0; x < width; x++){
                        int color = texture.getTextureColor(x, y);

                        buf.put((byte)Colors.getBInt(color));
                        buf.put((byte)Colors.getGInt(color));
                        buf.put((byte)Colors.getRInt(color));
                        buf.put((byte)Colors.getAInt(color));
                    }
                }

                ((Buffer)buf).flip();
                image.set(width, height, buf);

                this.cursors.put(cursor, GLFW.glfwCreateCursor(image, cursor.getHotspotX(), cursor.getHotspotY()));

                image.free();
            }
            catch(Exception e){
                RockBottomAPI.logger().log(Level.WARNING, "Could not load mouse cursor "+cursor, e);
            }
        }
    }

    @Override
    public void setCursor(ISpecialCursor cursor){
        try{
            if(!this.game.getSettings().hardwareCursor){
                GLFW.glfwSetCursor(this.game.getWindow(), this.cursors.get(cursor));
            }
            else{
                GLFW.glfwSetCursor(this.game.getWindow(), MemoryUtil.NULL);
            }

            RockBottomAPI.logger().config("Setting cursor to "+cursor);
        }
        catch(Exception e){
            RockBottomAPI.logger().log(Level.SEVERE, "Could not set mouse cursor!", e);
        }
    }

    @Override
    public <T extends IAsset> Map<IResourceName, T> getAllOfType(IResourceName identifier){
        return (Map<IResourceName, T>)this.assets.row(identifier);
    }

    @Override
    public <T extends IAsset> T getAssetWithFallback(IResourceName identifier, IResourceName path, T fallback){
        IAsset asset = this.assets.get(identifier, path);

        if(asset == null){
            this.assets.put(identifier, path, fallback);
            asset = fallback;

            RockBottomAPI.logger().warning("Resource with name "+path+" is missing!");
        }

        return (T)asset;
    }

    @Override
    public boolean hasAsset(IResourceName identifier, IResourceName path){
        return this.assets.contains(identifier, path);
    }

    @Override
    public ITexture getTexture(IResourceName path){
        return this.getAssetWithFallback(ITexture.ID, path, this.missingTexture);
    }

    @Override
    public IAnimation getAnimation(IResourceName path){
        return this.getAssetWithFallback(IAnimation.ID, path, this.missingAnimation);
    }

    @Override
    public ISound getSound(IResourceName path){
        return this.getAssetWithFallback(ISound.ID, path, this.missingSound);
    }

    @Override
    public IShaderProgram getShaderProgram(IResourceName path){
        return this.getAssetWithFallback(IShaderProgram.ID, path, this.game.renderer.simpleProgram);
    }

    @Override
    public Locale getLocale(IResourceName path){
        return this.getAssetWithFallback(Locale.ID, path, this.missingLocale);
    }

    @Override
    public IFont getFont(IResourceName path){
        return this.getAssetWithFallback(IFont.ID, path, this.game.renderer.simpleFont);
    }

    @Override
    public String localize(IResourceName unloc, Object... format){
        return this.currentLocale.localize(this.defaultLocale, unloc, format);
    }

    @Override
    public IFont getFont(){
        return this.currentFont;
    }

    @Override
    public void setFont(IFont font){
        this.currentFont = font;
    }

    @Override
    public Locale getLocale(){
        return this.currentLocale;
    }

    @Override
    public void setLocale(Locale locale){
        this.currentLocale = locale;
    }

    @Override
    public InputStream getResourceStream(String s){
        return ContentManager.getResourceAsStream(s);
    }

    @Override
    public URL getResourceURL(String s){
        return ContentManager.getResource(s);
    }

    @Override
    public ITexture getMissingTexture(){
        return this.missingTexture;
    }

    @Override
    public SimpleDateFormat getLocalizedDateFormat(){
        try{
            return new SimpleDateFormat(this.localize(RockBottomAPI.createInternalRes("date_format")));
        }
        catch(IllegalArgumentException e){
            return new SimpleDateFormat();
        }
    }

    @Override
    public ISpecialCursor pickCurrentCursor(){
        for(ISpecialCursor cursor : this.sortedCursors){
            if(cursor.shouldUseCursor(this.game, this.game.getAssetManager(), this.game.getRenderer(), this.game.getGuiManager(), this.game.getInteractionManager())){
                return cursor;
            }
        }
        return null;
    }

    @Override
    public void dispose(){
        Texture.unbindAllBanks();

        if(!this.assets.isEmpty()){
            for(IAsset asset : this.assets.values()){
                asset.dispose();
            }
        }

        this.disposeOptionalResource(this.missingAnimation);
        this.disposeOptionalResource(this.missingLocale);
        this.disposeOptionalResource(this.missingSound);
        this.disposeOptionalResource(this.missingTexture);
    }

    private void disposeOptionalResource(IAsset asset){
        if(asset != null){
            asset.dispose();
        }
    }

    public void onResize(int width, int height){
        for(IShaderProgram program : this.<IShaderProgram>getAllOfType(IShaderProgram.ID).values()){
            program.updateProjection(width, height);
        }
        this.game.renderer.simpleProgram.updateProjection(width, height);
    }

    @Override
    public TextureStitcher getTextureStitcher(){
        return this.stitcher;
    }

    @Override
    public boolean addAsset(IAssetLoader loader, IResourceName name, IAsset
            asset){
        if(!this.isLocked){
            IResourceName id = loader.getAssetIdentifier();
            if(!this.assets.contains(id, name)){
                this.assets.put(loader.getAssetIdentifier(), name, asset);
                return true;
            }
            else{
                return false;
            }
        }
        else{
            throw new UnsupportedOperationException("Cannot add assets to the asset manager while it's locked! Add assets during loading!");
        }
    }

    private class AssetCallback implements LoaderCallback{

        private final IAssetLoader loader;

        public AssetCallback(IAssetLoader loader){
            this.loader = loader;
        }

        @Override
        public IResourceName getIdentifier(){
            return this.loader.getAssetIdentifier();
        }

        @Override
        public void load(IResourceName resourceName, String path, JsonElement element, String elementName, IMod loadingMod, ContentPack pack) throws Exception{
            this.loader.loadAsset(AssetManager.this, resourceName, path, element, elementName, loadingMod, pack);
        }

        @Override
        public boolean dealWithSpecialCases(String resourceName, String path, JsonElement element, String elementName, IMod loadingMod, ContentPack pack) throws Exception{
            return this.loader.dealWithSpecialCases(AssetManager.this, resourceName, path, element, elementName, loadingMod, pack);
        }

        @Override
        public void disable(IResourceName resourceName){
            this.loader.disableAsset(AssetManager.this, resourceName);
        }
    }
}
