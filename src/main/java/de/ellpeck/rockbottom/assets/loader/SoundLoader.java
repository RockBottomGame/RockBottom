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

import java.util.HashSet;
import java.util.Set;

public class SoundLoader implements IAssetLoader<ISound>{

    private final Set<IResourceName> disabled = new HashSet<>();

    @Override
    public IResourceName getAssetIdentifier(){
        return ISound.ID;
    }

    @Override
    public void loadAsset(IAssetManager manager, IResourceName resourceName, String path, JsonElement element, String elementName, IMod loadingMod, ContentPack pack) throws Exception{
        if(!this.disabled.contains(resourceName)){
            String resPath = path+element.getAsString();

            SoundEffect sound = new SoundEffect(ContentManager.getResourceAsStream(resPath));
            if(manager.addAsset(this, resourceName, sound)){
                RockBottomAPI.logger().config("Loaded sound "+resourceName+" for mod "+loadingMod.getDisplayName());
            }
            else{
                RockBottomAPI.logger().info("Sound "+resourceName+" already exists, not adding sound for mod "+loadingMod.getDisplayName()+" with content pack "+pack.getName());
            }
        }
        else{
            RockBottomAPI.logger().info("Sound "+resourceName+" will not be loaded for mod "+loadingMod.getDisplayName()+" with content pack "+pack.getName()+" because it was disabled by another content pack!");
        }
    }

    @Override
    public void disableAsset(IAssetManager manager, IResourceName resourceName){
        this.disabled.add(resourceName);
    }
}
