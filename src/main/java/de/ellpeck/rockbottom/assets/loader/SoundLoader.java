package de.ellpeck.rockbottom.assets.loader;

import com.google.gson.JsonElement;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetLoader;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.ISound;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.assets.sound.SoundEffect;

public class SoundLoader implements IAssetLoader<ISound>{

    @Override
    public IResourceName getAssetIdentifier(){
        return RockBottomAPI.createInternalRes("sound");
    }

    @Override
    public void loadAsset(IAssetManager manager, IResourceName resourceName, String path, JsonElement element, String elementName, IMod loadingMod) throws Exception{
        String resPath = path+element.getAsString();

        SoundEffect sound = new SoundEffect(AssetManager.getResourceAsStream(resPath));
        RockBottomAPI.logger().config("Loaded sound "+resourceName+" for mod "+loadingMod.getDisplayName());
        manager.addAsset(resourceName, sound);
    }
}
