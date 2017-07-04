package de.ellpeck.rockbottom.assets;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.AssetSound;
import de.ellpeck.rockbottom.api.assets.IAsset;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.anim.Animation;
import de.ellpeck.rockbottom.api.assets.anim.AnimationRow;
import de.ellpeck.rockbottom.api.assets.anim.AssetAnimation;
import de.ellpeck.rockbottom.api.assets.font.AssetFont;
import de.ellpeck.rockbottom.api.assets.font.Font;
import de.ellpeck.rockbottom.api.assets.local.AssetLocale;
import de.ellpeck.rockbottom.api.assets.local.Locale;
import de.ellpeck.rockbottom.api.assets.tex.AssetTexture;
import de.ellpeck.rockbottom.api.assets.tex.Texture;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.init.RockBottom;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.Sound;
import org.newdawn.slick.opengl.CursorLoader;
import org.newdawn.slick.util.Log;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.*;

public class AssetManager implements IAssetManager{

    private final Map<IResourceName, IAsset> assets = new HashMap<>();
    private AssetSound missingSound;
    private AssetTexture missingTexture;
    private AssetLocale missingLocale;
    private AssetFont missingFont;
    private AssetAnimation missingAnimation;
    private Locale currentLocale;
    private Font currentFont;

    private static Texture loadTexture(String key, String path, String value) throws Exception{
        if(value.startsWith("sub.")){
            String[] parts = value.substring(4).split(",");

            Texture main = new Texture(getResource(path+parts[0]), key, false);

            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            int width = Integer.parseInt(parts[3]);
            int height = Integer.parseInt(parts[4]);

            return main.getSubTexture(x, y, width, height);
        }
        else{
            return new Texture(getResource(path+value), key, false);
        }
    }

    public static InputStream getResource(String s){
        return AssetManager.class.getResourceAsStream(s);
    }

    public void create(RockBottom game){
        try{
            Log.info("Loading resources...");
            this.loadAssets();
        }
        catch(Exception e){
            Log.error("Exception loading resources! ", e);
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
        this.missingAnimation = new AssetAnimation(new Animation(this.missingTexture.get(), 2, 2, new ArrayList<>(Collections.singletonList(new AnimationRow(1, 1F)))));

        Log.info("Loaded "+this.getAllOfType(AssetTexture.class).size()+" texture resources!");
        Log.info("Loaded "+this.getAllOfType(AssetSound.class).size()+" sound resources!");
        Log.info("Loaded "+this.getAllOfType(AssetAnimation.class).size()+" animations!");
        Log.info("Possible language settings: "+this.getAllOfType(AssetLocale.class).keySet());

        this.currentLocale = this.getLocale(AbstractGame.internalRes("us_english"));
        this.currentFont = this.getFont(AbstractGame.internalRes("default"));

        this.reloadCursor(game);
    }

    @Override
    public void reloadCursor(IGameInstance game){
        try{
            if(!game.getSettings().hardwareCursor){
                Texture texture = this.getTexture(AbstractGame.internalRes("gui.cursor"));
                Texture temp = new Texture(texture.getWidth(), texture.getHeight());

                Graphics g = temp.getGraphics();
                g.drawImage(texture.getFlippedCopy(false, true), 0, 0);
                g.flush();

                ByteBuffer buffer = BufferUtils.createByteBuffer(temp.getWidth()*temp.getHeight()*4);
                g.getArea(0, 0, temp.getWidth(), temp.getHeight(), buffer);

                Cursor cursor = CursorLoader.get().getCursor(buffer, 0, 0, temp.getWidth(), temp.getHeight());
                Mouse.setNativeCursor(cursor);
            }
            else{
                Mouse.setNativeCursor(null);
            }
        }
        catch(Exception e){
            Log.error("Could not set mouse cursor!", e);
        }
    }

    @Override
    public <T extends IAsset> Map<IResourceName, T> getAllOfType(Class<T> type){
        Map<IResourceName, T> assets = new HashMap<>();

        for(Map.Entry<IResourceName, IAsset> entry : this.assets.entrySet()){
            IAsset asset = entry.getValue();

            if(type.isAssignableFrom(asset.getClass())){
                assets.put(entry.getKey(), (T)asset);
            }
        }

        return assets;
    }

    private void loadAssets() throws Exception{
        for(IMod mod : RockBottomAPI.getModLoader().getActiveMods()){
            String path = mod.getResourceLocation();
            if(path != null && !path.isEmpty()){
                int loadAmount = 0;

                InputStream propStream = getResource(path+"/assets.info");
                if(propStream != null){
                    Properties props = new Properties();
                    props.load(propStream);

                    for(String key : props.stringPropertyNames()){
                        String value = props.getProperty(key);
                        IResourceName name = RockBottomAPI.createRes(mod, key);

                        boolean didLoad = true;
                        try{
                            if(key.startsWith("anim.")){
                                String[] split = value.split(",");
                                InputStream texture = getResource(path+split[0]);
                                InputStream info = getResource(path+split[1]);

                                this.assets.put(name, new AssetAnimation(Animation.fromStream(texture, info, key)));
                                Log.info("Loaded animation resource "+name+" with data "+value);
                            }
                            else if(key.startsWith("font.")){
                                String[] split = value.split(",");
                                InputStream texture = getResource(path+split[0]);
                                InputStream info = getResource(path+split[1]);

                                this.assets.put(name, new AssetFont(Font.fromStream(texture, info, key)));
                                Log.info("Loaded font resource "+name+" with data "+value);
                            }
                            else if(key.startsWith("tex.")){
                                this.assets.put(name, new AssetTexture(loadTexture(key, path, value)));
                                Log.info("Loaded png resource "+name+" with data "+value);
                            }
                            else{
                                InputStream stream = getResource(path+value);

                                if(key.startsWith("sound.")){
                                    this.assets.put(name, new AssetSound(new Sound(stream, key)));
                                    Log.info("Loaded ogg resource "+name+" with data "+value);
                                }
                                else if(key.startsWith("loc.")){
                                    boolean merged = false;

                                    Locale loaded = Locale.fromStream(stream, key);
                                    for(AssetLocale asset : this.getAllOfType(AssetLocale.class).values()){
                                        if(asset.get().merge(loaded)){
                                            merged = true;
                                            break;
                                        }
                                    }

                                    if(!merged){
                                        this.assets.put(name, new AssetLocale(loaded));
                                        Log.info("Loaded localization resource "+name+" with data "+value);
                                    }
                                }
                                else{
                                    Log.warn("Couldn't load resource with key "+key+" and value "+value+" from assets.info for mod "+mod.getDisplayName()+" at path "+path+"!");
                                    didLoad = false;
                                }
                            }

                            if(didLoad){
                                loadAmount++;
                            }
                        }
                        catch(Exception e){
                            Log.error("Failed loading resource "+name+" with data "+value+"!", e);
                        }
                    }
                }
                else{
                    Log.error("Mod "+mod.getDisplayName()+" is missing assets.info file at path "+path);
                }

                Log.info("Loaded "+loadAmount+" assets from assets.info file for mod "+mod.getDisplayName()+" at path "+path);
            }
            else{
                Log.info("Skipping mod "+mod.getDisplayName()+" that doesn't have a resource location");
            }
        }
    }

    @Override
    public <T> T getAssetWithFallback(IResourceName path, IAsset<T> fallback){
        IAsset asset = this.assets.get(path);

        if(asset == null){
            this.assets.put(path, fallback);
            asset = fallback;

            Log.warn("Resource with name "+path+" is missing!");
        }

        return (T)asset.get();
    }

    @Override
    public Texture getTexture(IResourceName path){
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
    public Font getFont(IResourceName path){
        return this.getAssetWithFallback(path.addPrefix("font."), this.missingFont);
    }

    @Override
    public String localize(IResourceName unloc, Object... format){
        return this.currentLocale.localize(unloc, format);
    }

    @Override
    public Font getFont(){
        return this.currentFont;
    }

    @Override
    public InputStream getResourceStream(String s){
        return getResource(s);
    }

    @Override
    public Texture getMissingTexture(){
        return this.missingTexture.get();
    }
}
