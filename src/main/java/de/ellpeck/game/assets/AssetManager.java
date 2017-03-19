package de.ellpeck.game.assets;

import de.ellpeck.game.Constants;
import de.ellpeck.game.Game;
import de.ellpeck.game.Main;
import org.newdawn.slick.Image;
import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.Sound;
import org.newdawn.slick.util.Log;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AssetManager{

    private AssetSound missingSound;
    private AssetImage missingTexture;

    private final Map<String, IAsset> assets = new HashMap<>();

    public void create(Game game){
        try{
            Log.info("Loading resources...");
            this.loadAssets(Constants.ASSETS_FOLDER);

            ImageBuffer buffer = new ImageBuffer(2, 2);
            for(int x = 0; x < 2; x++){
                for(int y = 0; y < 2; y++){
                    boolean areEqual = x == y;
                    buffer.setRGBA(x, y, areEqual ? 255 : 0, 0, areEqual ? 0 : 255, 255);
                }
            }
            this.missingTexture = new AssetImage(buffer.getImage());

            this.missingSound = new AssetSound(null);
        }
        catch(Exception e){
            Main.doExceptionInfo(game, e);
        }
    }

    private void loadAssets(String path) throws Exception{
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
            else{
                Log.info("Found unknown resource definition "+key+" with path "+value+"!");
            }
        }
    }

    public <T> T getAssetWithFallback(String path, IAsset<T> fallback){
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
}
