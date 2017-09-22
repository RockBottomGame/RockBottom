package de.ellpeck.rockbottom.assets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.AssetSound;
import de.ellpeck.rockbottom.api.assets.IAsset;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.anim.Animation;
import de.ellpeck.rockbottom.api.assets.anim.AnimationRow;
import de.ellpeck.rockbottom.api.assets.anim.AssetAnimation;
import de.ellpeck.rockbottom.api.assets.font.AssetFont;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.assets.local.AssetLocale;
import de.ellpeck.rockbottom.api.assets.local.Locale;
import de.ellpeck.rockbottom.api.assets.tex.AssetTexture;
import de.ellpeck.rockbottom.api.assets.tex.ITexture;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.apiimpl.Texture;
import de.ellpeck.rockbottom.init.RockBottom;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.Sound;
import org.newdawn.slick.opengl.CursorLoader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

public class AssetManager implements IAssetManager{

    private final Map<IResourceName, IAsset> assets = new HashMap<>();
    private AssetSound missingSound;
    private AssetTexture missingTexture;
    private AssetLocale missingLocale;
    private AssetFont missingFont;
    private AssetAnimation missingAnimation;
    private Locale currentLocale;
    private Locale defaultLocale;
    private IFont currentFont;

    public static InputStream getResource(String s){
        return AssetManager.class.getResourceAsStream(s);
    }

    public void create(RockBottom game){
        try{
            RockBottomAPI.logger().info("Loading resources...");
            this.loadAssets();
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
        this.missingTexture = new AssetTexture(new Texture(buffer));
        this.missingSound = new AssetSound(null);
        this.missingLocale = new AssetLocale(new Locale("fallback"));
        this.missingFont = new AssetFont(new Font("fallback", this.missingTexture.get(), 1, 1, new HashMap<>(Collections.singletonMap('?', new Pos2(0, 0)))));
        this.missingAnimation = new AssetAnimation(new Animation(this.missingTexture.get(), 2, 2, new ArrayList<>(Collections.singletonList(new AnimationRow(new float[]{1F})))));

        RockBottomAPI.logger().info("Loaded "+this.getAllOfType(AssetTexture.class).size()+" texture resources!");
        RockBottomAPI.logger().info("Loaded "+this.getAllOfType(AssetSound.class).size()+" sound resources!");
        RockBottomAPI.logger().info("Loaded "+this.getAllOfType(AssetAnimation.class).size()+" animations!");
        RockBottomAPI.logger().info("Possible language settings: "+this.getAllOfType(AssetLocale.class).keySet());

        this.defaultLocale = this.getLocale(RockBottomAPI.createInternalRes("us_english"));

        this.currentFont = this.getFont(RockBottomAPI.createInternalRes("default"));
        this.currentLocale = this.getAssetWithFallback(RockBottomAPI.createRes(game.getSettings().currentLocale), this.missingLocale);

        this.reloadCursor(game);
    }

    @Override
    public void reloadCursor(IGameInstance game){
        try{
            if(!game.getSettings().hardwareCursor){
                Texture texture = (Texture)this.getTexture(RockBottomAPI.createInternalRes("gui.cursor"));
                Texture temp = new Texture(texture.getWidth(), texture.getHeight());

                Graphics g = temp.getGraphics();
                g.drawImage(texture.getFlippedCopy(false, true), 0, 0);

                ByteBuffer buffer = BufferUtils.createByteBuffer(temp.getWidth()*temp.getHeight()*4);
                g.getArea(0, 0, temp.getWidth(), temp.getHeight(), buffer);

                Cursor cursor = CursorLoader.get().getCursor(buffer, 0, 0, temp.getWidth(), temp.getHeight());
                Mouse.setNativeCursor(cursor);

                g.flush();
            }
            else{
                Mouse.setNativeCursor(null);
            }
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

    private void loadAssets() throws Exception{
        JsonParser parser = new JsonParser();

        for(IMod mod : RockBottomAPI.getModLoader().getActiveMods()){
            String path = mod.getResourceLocation();
            if(path != null && !path.isEmpty()){
                int prevAmount = this.assets.size();

                InputStream stream = getResource(path+"/assets.json");
                if(stream != null){
                    try{
                        InputStreamReader reader = new InputStreamReader(stream);
                        JsonObject main = parser.parse(reader).getAsJsonObject();

                        for(Entry<String, JsonElement> resType : main.entrySet()){
                            String type = resType.getKey();
                            JsonObject resources = resType.getValue().getAsJsonObject();

                            for(Entry<String, JsonElement> resource : resources.entrySet()){
                                this.loadRes(mod, path, type, type, resource.getValue(), resource.getKey());
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

    private void loadRes(IMod mod, String path, String type, String name, JsonElement element, String elementName){
        try{
            if("subtexture".equals(elementName)){
                JsonObject object = element.getAsJsonObject();

                String file = object.getAsJsonPrimitive("file").getAsString();
                Texture main = new Texture(getResource(path+file), mod.getId()+"/"+name, false);

                for(Entry<String, JsonElement> entry : object.entrySet()){
                    String key = entry.getKey();
                    if(!"file".equals(key)){
                        JsonArray array = entry.getValue().getAsJsonArray();
                        IResourceName res = RockBottomAPI.createRes(mod, "*".equals(key) ? name : name+"."+key);

                        ITexture texture = main.getSubTexture(array.get(0).getAsInt(), array.get(1).getAsInt(), array.get(2).getAsInt(), array.get(3).getAsInt());
                        this.assets.put(res, new AssetTexture(texture));

                        RockBottomAPI.logger().config("Loaded subtexture "+res+" from texture "+path+file+" for mod "+mod.getDisplayName());
                    }
                }
            }
            else{
                if(!"*".equals(elementName)){
                    name += "."+elementName;
                }

                if(element.isJsonPrimitive() || element.isJsonArray()){
                    IResourceName res = RockBottomAPI.createRes(mod, name);

                    if("tex".equals(type)){
                        String resPath = path+element.getAsString();

                        AssetTexture texture = new AssetTexture(new Texture(getResource(resPath), res.toString(), false));
                        this.assets.put(res, texture);

                        RockBottomAPI.logger().config("Loaded texture "+res+" from "+resPath+" for mod "+mod.getDisplayName());
                    }
                    else if("loc".equals(type)){
                        String resPath = path+element.getAsString();
                        boolean merged = false;

                        Locale locale = Locale.fromStream(getResource(resPath), elementName);
                        for(AssetLocale asset : this.getAllOfType(AssetLocale.class).values()){
                            if(asset.get().merge(locale)){
                                merged = true;
                                break;
                            }
                        }

                        if(!merged){
                            this.assets.put(res, new AssetLocale(locale));
                            RockBottomAPI.logger().config("Loaded locale "+res+" from "+resPath+" for mod "+mod.getDisplayName());
                        }
                    }
                    else if("font".equals(type)){
                        JsonArray array = element.getAsJsonArray();
                        String info = array.get(0).getAsString();
                        String texture = array.get(1).getAsString();

                        AssetFont font = new AssetFont(Font.fromStream(new Texture(getResource(path+texture), res.toString(), false), getResource(path+info), res.toString()));
                        this.assets.put(res, font);

                        RockBottomAPI.logger().config("Loaded font "+res+" from "+path+info+" and "+path+texture+" for mod "+mod.getDisplayName());
                    }
                    else if("anim".equals(type)){
                        JsonArray array = element.getAsJsonArray();
                        String anim = array.get(0).getAsString();
                        String texture = array.get(1).getAsString();

                        AssetAnimation animation = new AssetAnimation(Animation.fromStream(new Texture(getResource(path+texture), res.toString(), false), getResource(path+anim)));
                        this.assets.put(res, animation);

                        RockBottomAPI.logger().config("Loaded animation "+res+" from "+path+anim+" and "+path+texture+" for mod "+mod.getDisplayName());
                    }
                    else{
                        RockBottomAPI.logger().warning("Found unknown resource type "+type+" from mod "+mod.getDisplayName());
                    }
                }
                else if(element.isJsonObject()){
                    JsonObject object = element.getAsJsonObject();
                    for(Entry<String, JsonElement> entry : object.entrySet()){
                        this.loadRes(mod, path, type, name, entry.getValue(), entry.getKey());
                    }
                }
            }
        }
        catch(Exception e){
            RockBottomAPI.logger().log(Level.SEVERE, "Couldn't load resource "+name+" for mod "+mod.getDisplayName(), e);
        }
    }

    @Override
    public <T> T getAssetWithFallback(IResourceName path, IAsset<T> fallback){
        IAsset asset = this.assets.get(path);

        if(asset == null){
            this.assets.put(path, fallback);
            asset = fallback;

            RockBottomAPI.logger().warning("Resource with name "+path+" is missing!");
        }

        return (T)asset.get();
    }

    @Override
    public ITexture getTexture(IResourceName path){
        return this.getAssetWithFallback(path.addPrefix("tex."), this.missingTexture);
    }

    @Override
    public Animation getAnimation(IResourceName path){
        return this.getAssetWithFallback(path.addPrefix("anim."), this.missingAnimation);
    }

    @Override
    public Sound getSound(IResourceName path){
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
        return this.missingTexture.get();
    }

    @Override
    public SimpleDateFormat getLocalizedDateFormat(){
        return new SimpleDateFormat(this.localize(RockBottomAPI.createInternalRes("date_format")));
    }
}
