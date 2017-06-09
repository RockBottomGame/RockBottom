package de.ellpeck.rockbottom.game.assets;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.AssetImage;
import de.ellpeck.rockbottom.api.assets.AssetSound;
import de.ellpeck.rockbottom.api.assets.IAsset;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.AssetFont;
import de.ellpeck.rockbottom.api.assets.font.Font;
import de.ellpeck.rockbottom.api.assets.local.AssetLocale;
import de.ellpeck.rockbottom.api.assets.local.Locale;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.game.RockBottom;
import org.newdawn.slick.*;
import org.newdawn.slick.util.Log;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AssetManager implements IAssetManager{

    private final Map<IMod, String> assetProps = new HashMap<>();
    private final Map<IResourceName, IAsset> assets = new HashMap<>();
    private AssetSound missingSound;
    private AssetImage missingTexture;
    private AssetLocale missingLocale;
    private AssetFont missingFont;
    private Locale currentLocale;
    private Font currentFont;

    public void create(RockBottom game){
        try{
            Log.info("Loading resources...");

            RockBottomAPI.getModLoader().makeAssets();
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
        this.missingTexture = new AssetImage(buffer.getImage());
        this.missingSound = new AssetSound(null);
        this.missingLocale = new AssetLocale(new Locale("fallback"));
        this.missingFont = new AssetFont(new Font("fallback", this.missingTexture.get(), 1, 1, new HashMap<>(Collections.singletonMap('?', new Pos2(0, 0)))));

        Log.info("Loaded "+this.getAllOfType(AssetImage.class).size()+" image resources!");
        Log.info("Loaded "+this.getAllOfType(AssetSound.class).size()+" sound resources!");
        Log.info("Possible language settings: "+this.getAllOfType(AssetLocale.class).keySet());

        this.currentLocale = this.getLocale(RockBottom.internalRes("us_english"));
        this.currentFont = this.getFont(RockBottom.internalRes("default"));

        this.reloadCursor(game);
    }

    @Override
    public void reloadCursor(IGameInstance game){
        try{
            GameContainer container = game.getContainer();

            if(!game.getSettings().hardwareCursor){
                container.setMouseCursor(this.getImage(RockBottom.internalRes("gui.cursor")).getScaledCopy(3F), 0, 0);
            }
            else{
                container.setDefaultMouseCursor();
            }
        }
        catch(SlickException e){
            Log.error("Could not set mouse cursor!", e);
        }
    }

    @Override
    public void addAssetProp(IMod mod, String path){
        this.assetProps.put(mod, path);
    }

    private void loadAssets() throws Exception{
        for(Map.Entry<IMod, String> prop : this.assetProps.entrySet()){
            int loadAmount = 0;

            IMod mod = prop.getKey();
            String path = prop.getValue();

            InputStream propStream = getResource(path+"/assets.info");
            if(propStream != null){
                Properties props = new Properties();
                props.load(propStream);

                for(String key : props.stringPropertyNames()){
                    String value = props.getProperty(key);
                    IResourceName name = RockBottomAPI.createRes(mod, key);

                    boolean didLoad = true;
                    try{
                        if(key.startsWith("font.")){
                            InputStream image = getResource(path+value+".png");
                            InputStream info = getResource(path+value+".info");

                            this.assets.put(name, new AssetFont(Font.fromStream(image, info, key)));
                            Log.info("Loaded font resource "+name+" with data "+value);
                        }
                        else if(key.startsWith("tex.")){
                            this.assets.put(name, new AssetImage(loadImage(key, path, value)));
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
                                for(IAsset asset : this.getAllOfType(AssetLocale.class).values()){
                                    if(asset instanceof AssetLocale){
                                        AssetLocale locale = (AssetLocale)asset;
                                        if(locale.get().merge(loaded)){
                                            merged = true;
                                            break;
                                        }
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
    }

    @Override
    public Map<IResourceName, IAsset> getAllOfType(Class<? extends IAsset> type){
        Map<IResourceName, IAsset> assets = new HashMap<>();

        for(Map.Entry<IResourceName, IAsset> entry : this.assets.entrySet()){
            IAsset asset = entry.getValue();

            if(type.isAssignableFrom(asset.getClass())){
                assets.put(entry.getKey(), asset);
            }
        }

        return assets;
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
    public Image getImage(IResourceName path){
        return this.getAssetWithFallback(path.addPrefix("tex."), this.missingTexture);
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

    private static Image loadImage(String key, String path, String value) throws Exception{
        if(value.startsWith("sub.")){
            String[] parts = value.substring(4).split(",");

            Image main = new Image(getResource(path+parts[0]), key, false);

            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            int width = Integer.parseInt(parts[3]);
            int height = Integer.parseInt(parts[4]);

            return main.getSubImage(x, y, width, height);
        }
        else{
            return new Image(getResource(path+value), key, false);
        }
    }

    @Override
    public InputStream getResourceStream(String s){
        return getResource(s);
    }

    public static InputStream getResource(String s){
        return AssetManager.class.getResourceAsStream(s);
    }
}
