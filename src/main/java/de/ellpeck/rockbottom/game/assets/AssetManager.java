package de.ellpeck.rockbottom.game.assets;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.assets.font.AssetFont;
import de.ellpeck.rockbottom.game.assets.font.Font;
import de.ellpeck.rockbottom.game.assets.local.AssetLocale;
import de.ellpeck.rockbottom.game.assets.local.Locale;
import org.newdawn.slick.*;
import org.newdawn.slick.util.Log;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AssetManager{

    private final Map<String, IAsset> assets = new HashMap<>();
    private AssetSound missingSound;
    private AssetImage missingTexture;
    private AssetLocale missingLocale;
    private AssetFont missingFont;
    private Locale currentLocale;
    private Font currentFont;

    public void create(RockBottom game) throws SlickException{
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
        this.missingFont = new AssetFont(new Font("fallback", this.missingTexture.get(), 1, 1, new HashMap<>(Collections.singletonMap('?', new Pos2(0, 0)))));

        Log.info("Loaded "+this.getAllOfType(AssetImage.class).size()+" image resources!");
        Log.info("Loaded "+this.getAllOfType(AssetSound.class).size()+" sound resources!");
        Log.info("Possible language settings: "+this.getAllOfType(AssetLocale.class).keySet());

        this.currentLocale = this.getLocale("us_english");
        this.currentFont = this.getFont("default");

        this.reloadCursor(game);
    }

    public void reloadCursor(IGameInstance game){
        try{
            GameContainer container = game.getContainer();

            if(!game.getSettings().hardwareCursor){
                container.setMouseCursor(this.getImage("gui.cursor").getScaledCopy(3F), 0, 0);
            }
            else{
                container.setDefaultMouseCursor();
            }
        }
        catch(SlickException e){
            Log.error("Could not set mouse cursor!", e);
        }
    }

    private void loadAssets() throws Exception{
        String path = "/assets";

        InputStream propStream = getResource(path+"/assets.info");
        Properties props = new Properties();
        props.load(propStream);

        for(String key : props.stringPropertyNames()){
            String value = props.getProperty(key);

            try{
                if(key.startsWith("font.")){
                    InputStream image = getResource(path+value+".png");
                    InputStream info = getResource(path+value+".info");

                    this.assets.put(key, new AssetFont(Font.fromStream(image, info, key)));
                    Log.info("Loaded font resource "+key+" with data "+value);
                }
                else if(key.startsWith("tex.")){
                    this.assets.put(key, new AssetImage(loadImage(key, path, value)));
                    Log.info("Loaded png resource "+key+" with data "+value);
                }
                else{
                    InputStream stream = getResource(path+value);

                    if(key.startsWith("sound.")){
                        this.assets.put(key, new AssetSound(new Sound(stream, key)));
                        Log.info("Loaded ogg resource "+key+" with data "+value);
                    }
                    else if(key.startsWith("loc.")){
                        this.assets.put(key, new AssetLocale(Locale.fromStream(stream, key)));
                        Log.info("Loaded localization resource "+key+" with data "+value);
                    }
                }
            }
            catch(Exception e){
                Log.error("Failed loading resource "+key+" with data "+value+"!", e);
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
        return this.getAssetWithFallback("tex."+path, this.missingTexture);
    }

    public Sound getSound(String path){
        return this.getAssetWithFallback("sound."+path, this.missingSound);
    }

    public Locale getLocale(String path){
        return this.getAssetWithFallback("loc."+path, this.missingLocale);
    }

    public Font getFont(String path){
        return this.getAssetWithFallback("font."+path, this.missingFont);
    }

    public String localize(String unloc, Object... format){
        return this.currentLocale.localize(unloc, format);
    }

    public Font getFont(){
        return this.currentFont;
    }

    public static InputStream getResource(String s){
        return AssetManager.class.getResourceAsStream(s);
    }

    private static Image loadImage(String key, String path, String value) throws Exception{
        if(value.startsWith("sub:")){
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
}
