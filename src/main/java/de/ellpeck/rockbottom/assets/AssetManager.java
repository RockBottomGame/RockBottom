package de.ellpeck.rockbottom.assets;

import com.google.common.base.Charsets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.*;
import de.ellpeck.rockbottom.api.assets.Locale;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.gui.ISpecialCursor;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.assets.anim.Animation;
import de.ellpeck.rockbottom.assets.anim.AnimationRow;
import de.ellpeck.rockbottom.assets.loader.*;
import de.ellpeck.rockbottom.assets.sound.EmptySound;
import de.ellpeck.rockbottom.gui.cursor.CursorClosedHand;
import de.ellpeck.rockbottom.gui.cursor.CursorFinger;
import de.ellpeck.rockbottom.gui.cursor.CursorOpenHand;
import de.ellpeck.rockbottom.gui.cursor.CursorPointer;
import de.ellpeck.rockbottom.gui.menu.background.BlankTheme;
import de.ellpeck.rockbottom.init.RockBottom;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.ImageBuffer;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

public class AssetManager implements IAssetManager{

    static{
        new AnimationLoader().register();
        new FontLoader().register();
        new LocaleLoader().register();
        new SoundLoader().register();
        new TextureLoader().register();

        RockBottomAPI.SPECIAL_CURSORS.register(0, new CursorPointer());
        RockBottomAPI.SPECIAL_CURSORS.register(1, new CursorFinger());
        RockBottomAPI.SPECIAL_CURSORS.register(2, new CursorClosedHand());
        RockBottomAPI.SPECIAL_CURSORS.register(3, new CursorOpenHand());

        RockBottomAPI.MAIN_MENU_THEMES.register(0, new BlankTheme());
    }

    private final Map<IResourceName, IAsset> assets = new HashMap<>();
    private final List<ISpecialCursor> sortedCursors = new ArrayList<>();
    private final Map<ISpecialCursor, Cursor> cursors = new HashMap<>();
    private ISound missingSound;
    private ITexture missingTexture;
    private Locale missingLocale;
    private IFont missingFont;
    private IAnimation missingAnimation;
    private Locale currentLocale;
    private Locale defaultLocale;
    private IFont currentFont;

    public static InputStream getResource(String s){
        return AssetManager.class.getResourceAsStream(s);
    }

    public void load(RockBottom game){
        if(!this.assets.isEmpty()){
            this.assets.clear();
        }

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
        this.missingTexture = new Texture(buffer);
        this.missingSound = new EmptySound();
        this.missingLocale = new Locale("fallback");
        this.missingFont = new Font("fallback", this.missingTexture, 1, 1, new HashMap<>(Collections.singletonMap('?', new Pos2(0, 0))));
        this.missingAnimation = new Animation(this.missingTexture, 2, 2, new ArrayList<>(Collections.singletonList(new AnimationRow(new float[]{1F}))));

        RockBottomAPI.logger().info("Loaded "+this.getAllOfType(ITexture.class).size()+" texture resources!");
        RockBottomAPI.logger().info("Loaded "+this.getAllOfType(ISound.class).size()+" sound resources!");
        RockBottomAPI.logger().info("Loaded "+this.getAllOfType(IAnimation.class).size()+" animations!");
        RockBottomAPI.logger().info("Possible language settings: "+this.getAllOfType(Locale.class).keySet());

        this.defaultLocale = this.getLocale(RockBottomAPI.createInternalRes("us_english"));

        this.currentFont = this.getFont(RockBottomAPI.createInternalRes("default"));
        this.currentLocale = this.getAssetWithFallback(RockBottomAPI.createRes(game.getSettings().currentLocale), this.missingLocale);
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
                int width = texture.getWidth();
                int height = texture.getHeight();

                ByteBuffer buf = BufferUtils.createByteBuffer(width*height*4);

                for(int y = height-1; y >= 0; y--){
                    for(int x = 0; x < width; x++){
                        int color = texture.getTextureColor(x, y);

                        buf.put((byte)Colors.getBInt(color));
                        buf.put((byte)Colors.getGInt(color));
                        buf.put((byte)Colors.getRInt(color));
                        buf.put((byte)Colors.getAInt(color));
                    }
                }

                buf.flip();

                this.cursors.put(cursor, new Cursor(width, height, cursor.getHotspotX(), height-1-cursor.getHotspotY(), 1, buf.asIntBuffer(), null));
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
                Mouse.setNativeCursor(this.cursors.get(cursor));
            }
            else{
                Mouse.setNativeCursor(null);
            }

            RockBottomAPI.logger().config("Setting cursor to "+cursor);
        }
        catch(Exception e){
            RockBottomAPI.logger().log(Level.SEVERE, "Could not set mouse cursor!", e);
        }
    }

    @Override
    public <T extends IAsset> Map<IResourceName, T> getAllOfType(Class<T> type){
        Map<IResourceName, T> assets = new HashMap<>();

        for(Entry<IResourceName, IAsset> entry : this.assets.entrySet()){
            IAsset asset = entry.getValue();

            if(type.isAssignableFrom(asset.getClass())){
                assets.put(entry.getKey(), (T)asset);
            }
        }

        return assets;
    }

    private void loadAssets(){
        JsonParser parser = new JsonParser();

        for(IMod mod : RockBottomAPI.getModLoader().getActiveMods()){
            String path = mod.getResourceLocation();
            if(path != null && !path.isEmpty()){
                int prevAmount = this.assets.size();

                InputStream stream = getResource(path+"/assets.json");
                if(stream != null){
                    try{
                        InputStreamReader reader = new InputStreamReader(stream, Charsets.UTF_8);
                        JsonObject main = parser.parse(reader).getAsJsonObject();

                        for(Entry<String, JsonElement> resType : main.entrySet()){
                            String type = resType.getKey();
                            for(IAssetLoader loader : RockBottomAPI.ASSET_LOADER_REGISTRY.getUnmodifiable().values()){
                                IResourceName identifier = loader.getAssetIdentifier();
                                if(identifier.getResourceName().equals(type) || identifier.toString().equals(type)){
                                    String name = type.contains(Constants.RESOURCE_SEPARATOR) ? RockBottomAPI.createRes(type).getResourceName() : type;

                                    JsonObject resources = resType.getValue().getAsJsonObject();
                                    for(Entry<String, JsonElement> resource : resources.entrySet()){
                                        this.loadRes(mod, path, loader, name, resource.getValue(), resource.getKey());
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

                RockBottomAPI.logger().info("Loaded "+(this.assets.size()-prevAmount)+" assets from assets.json file for mod "+mod.getDisplayName()+" at path "+path);
            }
            else{
                RockBottomAPI.logger().info("Skipping mod "+mod.getDisplayName()+" that doesn't have a resource location");
            }
        }
    }

    private void loadRes(IMod mod, String path, IAssetLoader loader, String name, JsonElement element, String elementName){
        try{
            Map<IResourceName, IAsset> special = loader.dealWithSpecialCases(this, name, path, element, elementName, mod);
            if(special != null && !special.isEmpty()){
                this.assets.putAll(special);
            }
            else{
                if(!"*".equals(elementName)){
                    name += "."+elementName;
                }

                if(element.isJsonPrimitive() || element.isJsonArray()){
                    IResourceName resourceName = RockBottomAPI.createRes(mod, name);
                    IAsset asset = loader.loadAsset(this, resourceName, path, element, elementName, mod);
                    if(asset != null){
                        this.assets.put(resourceName, asset);
                    }
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
    public <T extends IAsset> T getAssetWithFallback(IResourceName path, T fallback){
        IAsset asset = this.assets.get(path);

        if(asset == null){
            this.assets.put(path, fallback);
            asset = fallback;

            RockBottomAPI.logger().warning("Resource with name "+path+" is missing!");
        }

        return (T)asset;
    }

    @Override
    public ITexture getTexture(IResourceName path){
        return this.getAssetWithFallback(path.addPrefix("tex."), this.missingTexture);
    }

    @Override
    public IAnimation getAnimation(IResourceName path){
        return this.getAssetWithFallback(path.addPrefix("anim."), this.missingAnimation);
    }

    @Override
    public ISound getSound(IResourceName path){
        return this.getAssetWithFallback(path.addPrefix("sound."), this.missingSound);
    }

    @Override
    public Locale getLocale(IResourceName path){
        return this.getAssetWithFallback(path.addPrefix("loc."), this.missingLocale);
    }

    @Override
    public IFont getFont(IResourceName path){
        return this.getAssetWithFallback(path.addPrefix("font."), this.missingFont);
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
            if(cursor.shouldUseCursor(game, game.getAssetManager(), game.getGraphics(), game.getGuiManager(), game.getInteractionManager())){
                return cursor;
            }
        }
        return null;
    }
}
