package de.ellpeck.rockbottom.assets;

import com.google.common.base.Charsets;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.*;
import de.ellpeck.rockbottom.api.assets.Locale;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.assets.texture.ITexture;
import de.ellpeck.rockbottom.api.assets.texture.ImageBuffer;
import de.ellpeck.rockbottom.api.event.impl.LoadAssetsEvent;
import de.ellpeck.rockbottom.api.gui.ISpecialCursor;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.render.engine.IDisposable;
import de.ellpeck.rockbottom.api.render.engine.VertexProcessor;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.assets.anim.Animation;
import de.ellpeck.rockbottom.assets.anim.AnimationRow;
import de.ellpeck.rockbottom.assets.loader.*;
import de.ellpeck.rockbottom.assets.shader.ShaderProgram;
import de.ellpeck.rockbottom.assets.stub.EmptyShaderProgram;
import de.ellpeck.rockbottom.assets.stub.EmptySound;
import de.ellpeck.rockbottom.assets.tex.Texture;
import de.ellpeck.rockbottom.assets.tex.TextureStitcher;
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
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
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
    private IFont missingFont;
    private IAnimation missingAnimation;
    private Locale currentLocale;
    private Locale defaultLocale;
    private IFont currentFont;
    private IShaderProgram missingShader;
    private boolean isLocked = true;

    public static InputStream getResourceAsStream(String s){
        return AssetManager.class.getResourceAsStream(s);
    }

    public static URL getResource(String s){
        return AssetManager.class.getResource(s);
    }

    public void load(RockBottom game){
        this.dispose();

        if(!this.assets.isEmpty()){
            this.assets.clear();
        }

        this.isLocked = false;

        try{
            RockBottomAPI.logger().info("Loading resources...");

            this.loadAssets();

            for(IAssetLoader loader : RockBottomAPI.ASSET_LOADER_REGISTRY.getUnmodifiable().values()){
                loader.finalize(this);
            }
        }
        catch(Exception e){
            RockBottomAPI.logger().log(Level.SEVERE, "Exception loading resources! ", e);
        }

        ImageBuffer buffer = new ImageBuffer(2, 2);
        for(int x = 0; x < 2; x++){
            for(int y = 0; y < 2; y++){
                boolean areEqual = x == y;
                buffer.setRGBA(x, y, areEqual ? 255 : 0, 0, areEqual ? 0 : 255, 255);
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

        this.missingSound = new EmptySound();
        this.missingLocale = new Locale("fallback", new HashMap<>());
        this.missingFont = new Font("fallback", this.missingTexture, 1, 1, new HashMap<>(Collections.singletonMap('?', new Pos2(0, 0))));
        this.missingAnimation = new Animation(this.missingTexture, 2, 2, new ArrayList<>(Collections.singletonList(new AnimationRow(new float[]{1F}))));
        this.missingShader = new EmptyShaderProgram();

        RockBottomAPI.logger().info("Loaded "+this.getAllOfType(ITexture.ID).size()+" texture resources!");
        RockBottomAPI.logger().info("Loaded "+this.getAllOfType(ISound.ID).size()+" sound resources!");
        RockBottomAPI.logger().info("Loaded "+this.getAllOfType(IAnimation.ID).size()+" animations!");
        RockBottomAPI.logger().info("Possible language settings: "+this.getAllOfType(Locale.ID).keySet());

        this.defaultLocale = this.getLocale(RockBottomAPI.createInternalRes("us_english"));

        this.currentFont = this.getFont(RockBottomAPI.createInternalRes("default"));
        this.currentLocale = this.getLocale(RockBottomAPI.createRes(game.getSettings().currentLocale));

        RockBottomAPI.getEventHandler().fireEvent(new LoadAssetsEvent(game, this, game.getRenderer()));
        this.initInternalShaders(game.getWidth(), game.getHeight());

        this.isLocked = true;
    }

    private void initInternalShaders(int width, int height){
        IShaderProgram guiShader = this.getShaderProgram(ShaderProgram.GUI_SHADER);
        guiShader.setDefaultValues(width, height);

        IShaderProgram worldShader = this.getShaderProgram(ShaderProgram.WORLD_SHADER);
        worldShader.setDefaultValues(width, height);

        IShaderProgram breakShader = this.getShaderProgram(ShaderProgram.BREAK_SHADER);
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

    public void loadCursors(){
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

                buf.flip();
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
    public void setCursor(IGameInstance game, ISpecialCursor cursor){
        try{
            if(!game.getSettings().hardwareCursor){
                GLFW.glfwSetCursor(game.getWindow(), this.cursors.get(cursor));
            }
            else{
                GLFW.glfwSetCursor(game.getWindow(), MemoryUtil.NULL);
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

    private void loadAssets(){
        for(IMod mod : RockBottomAPI.getModLoader().getActiveMods()){
            String path = mod.getResourceLocation();
            if(path != null && !path.isEmpty()){
                InputStream stream = getResourceAsStream(path+"/assets.json");
                if(stream != null){
                    try{
                        InputStreamReader reader = new InputStreamReader(stream, Charsets.UTF_8);
                        JsonObject main = Util.JSON_PARSER.parse(reader).getAsJsonObject();

                        for(Entry<String, JsonElement> resType : main.entrySet()){
                            String type = resType.getKey();
                            for(IAssetLoader loader : RockBottomAPI.ASSET_LOADER_REGISTRY.getUnmodifiable().values()){
                                IResourceName identifier = loader.getAssetIdentifier().addSuffix(".");
                                if(identifier.getResourceName().equals(type) || identifier.toString().equals(type)){
                                    JsonObject resources = resType.getValue().getAsJsonObject();
                                    for(Entry<String, JsonElement> resource : resources.entrySet()){
                                        this.loadRes(mod, path, loader, "", resource.getValue(), resource.getKey());
                                    }

                                    break;
                                }
                            }
                        }
                    }
                    catch(Exception e){
                        RockBottomAPI.logger().log(Level.SEVERE, "Couldn't read assets.json from mod "+mod.getDisplayName(), e);
                    }
                }
                else{
                    RockBottomAPI.logger().severe("Mod "+mod.getDisplayName()+" is missing assets.json file at path "+path);
                }

                RockBottomAPI.logger().info("Loaded assets from assets.json file for mod "+mod.getDisplayName()+" at path "+path);
            }
            else{
                RockBottomAPI.logger().info("Skipping mod "+mod.getDisplayName()+" that doesn't have a resource location");
            }
        }
    }

    private void loadRes(IMod mod, String path, IAssetLoader loader, String name, JsonElement element, String elementName){
        try{
            if(!loader.dealWithSpecialCases(this, name, path, element, elementName, mod)){
                if("*".equals(elementName)){
                    name = name.substring(0, name.length()-1);
                }
                else if(!"*.".equals(elementName)){
                    name += elementName;
                }

                if(!elementName.endsWith(".")){
                    IResourceName resourceName = RockBottomAPI.createRes(mod, name);
                    loader.loadAsset(this, resourceName, path, element, elementName, mod);
                }
                else if(element.isJsonObject()){
                    JsonObject object = element.getAsJsonObject();
                    for(Entry<String, JsonElement> entry : object.entrySet()){
                        this.loadRes(mod, path, loader, name, entry.getValue(), entry.getKey());
                    }
                }
            }
        }
        catch(Exception e){
            RockBottomAPI.logger().log(Level.SEVERE, "Couldn't load resource "+name+" for mod "+mod.getDisplayName(), e);
        }
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
        return this.getAssetWithFallback(IShaderProgram.ID, path, this.missingShader);
    }

    @Override
    public Locale getLocale(IResourceName path){
        return this.getAssetWithFallback(Locale.ID, path, this.missingLocale);
    }

    @Override
    public IFont getFont(IResourceName path){
        return this.getAssetWithFallback(IFont.ID, path, this.missingFont);
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
        return getResourceAsStream(s);
    }

    @Override
    public URL getResourceURL(String s){
        return getResource(s);
    }

    @Override
    public ITexture getMissingTexture(){
        return this.missingTexture;
    }

    @Override
    public SimpleDateFormat getLocalizedDateFormat(){
        return new SimpleDateFormat(this.localize(RockBottomAPI.createInternalRes("date_format")));
    }

    @Override
    public ISpecialCursor pickCurrentCursor(IGameInstance game){
        for(ISpecialCursor cursor : this.sortedCursors){
            if(cursor.shouldUseCursor(game, game.getAssetManager(), game.getRenderer(), game.getGuiManager(), game.getInteractionManager())){
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

        if(this.missingTexture != null){
            this.missingTexture.dispose();
        }
    }

    public void onResize(int width, int height){
        for(IShaderProgram program : this.<IShaderProgram>getAllOfType(IShaderProgram.ID).values()){
            program.updateProjection(width, height);
        }
    }

    @Override
    public TextureStitcher getTextureStitcher(){
        return this.stitcher;
    }

    @Override
    public void addAsset(IAssetLoader loader, IResourceName name, IAsset asset){
        if(!this.isLocked){
            this.assets.put(loader.getAssetIdentifier(), name, asset);
        }
        else{
            throw new UnsupportedOperationException("Cannot add assets to the asset manager while it's locked! Add assets during loading!");
        }
    }
}
