package de.ellpeck.rockbottom.assets.loader;

import com.google.gson.JsonElement;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetLoader;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.ISound;
import de.ellpeck.rockbottom.api.content.pack.ContentPack;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.assets.sound.SoundEffect;
import de.ellpeck.rockbottom.content.ContentManager;

public class SoundLoader implements IAssetLoader<ISound>{

    @Override
    public IResourceName getAssetIdentifier(){
        return ISound.ID;
    }

    @Override
    public void loadAsset(IAssetManager manager, IResourceName resourceName, String path, JsonElement element, String elementName, IMod loadingMod, ContentPack pack) throws Exception{
        String resPath = path+element.getAsString();

        SoundEffect sound = new SoundEffect(ContentManager.getResourceAsStream(resPath));
        if(manager.addAsset(this, resourceName, sound)){
            RockBottomAPI.logger().config("Loaded sound "+resourceName+" for mod "+loadingMod.getDisplayName());
        }
        else{
            RockBottomAPI.logger().info("Sound "+resourceName+" already exists, not adding sound for mod "+loadingMod.getDisplayName()+" with content pack "+pack.getName());
        }
    }
}
