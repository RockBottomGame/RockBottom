package de.ellpeck.game.assets;

import de.ellpeck.game.Game;
import de.ellpeck.game.assets.local.AssetLocale;
import de.ellpeck.game.assets.local.Locale;
import org.newdawn.slick.Image;
import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.util.Log;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AssetManager{

    private AssetSound missingSound;
    private AssetImage missingTexture;
    private AssetLocale missingLocale;

    private final Map<String, IAsset> assets = new HashMap<>();

    private Locale currentLocale;

    public void create(Game game) throws SlickException{
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
        this.missingTexture = new AssetImage(buffer.getImage());
        this.missingSound = new AssetSound(null);
        this.missingLocale = new AssetLocale(new Locale("fallback"));

        Log.info("Loaded "+this.getAllOfType(AssetImage.class).size()+" image resources!");
        Log.info("Loaded "+this.getAllOfType(AssetSound.class).size()+" sound resources!");

        Log.info("Possible language settings: "+this.getAllOfType(AssetLocale.class).keySet());
        this.currentLocale = this.getLocale("us_english");

        if(!game.settings.hardwareCursor){
            game.getContainer().setMouseCursor(this.getImage("gui.cursor").getScaledCopy(game.settings.cursorScale), 0, 0);
        }
    }

    private void loadAssets() throws Exception{
        String path = "/assets";

        InputStream propStream = AssetManager.class.getResourceAsStream(path+"/assets.properties");
        Properties props = new Properties();
        props.load(propStream);

        for(String key : props.stringPropertyNames()){
            String value = props.getProperty(key);
            InputStream stream = AssetManager.class.getResourceAsStream(path+value);

            if(value.endsWith(".png")){
                this.assets.put(key, new AssetImage(new Image(stream, key, false)));
                Log.info("Loaded png resource "+key+" with path "+value);
            }
            else if(value.endsWith(".ogg")){
                this.assets.put(key, new AssetSound(new Sound(stream, key)));
                Log.info("Loaded ogg resource "+key+" with path "+value);
            }
            else if(value.endsWith(".loc")){
                this.assets.put(key, new AssetLocale(Locale.fromStream(stream, key)));
                Log.info("Loaded localization resource "+key+" with path "+value);
            }
            else{
                Log.warn("Found unknown resource definition "+key+" with path "+value+"!");
            }
        }
    }

    public Map<String, IAsset> getAllOfType(Class<? extends IAsset> type){
        Map<String, IAsset> assets = new HashMap<>();

        for(Map.Entry<String, IAsset> entry : this.assets.entrySet()){
            IAsset asset = entry.getValue();

            if(type.isAssignableFrom(asset.getClass())){
                assets.put(entry.getKey(), asset);
            }
        }

        return assets;
    }

    private <T> T getAssetWithFallback(String path, IAsset<T> fallback){
        IAsset asset = this.assets.get(path);

        if(asset == null){
            this.assets.put(path, fallback);
            asset = fallback;

            Log.warn("Resource with name "+path+" is missing!");
        }

        return (T)asset.get();
    }

    public Image getImage(String path){
        return this.getAssetWithFallback(path, this.missingTexture);
    }

    public Sound getSound(String path){
        return this.getAssetWithFallback(path, this.missingSound);
    }

    public Locale getLocale(String path){
        return this.getAssetWithFallback(path, this.missingLocale);
    }

    public String localize(String unloc, Object... format){
        return this.currentLocale.localize(unloc, format);
    }
}
