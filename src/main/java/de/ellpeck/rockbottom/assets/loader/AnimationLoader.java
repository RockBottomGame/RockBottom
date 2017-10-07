package de.ellpeck.rockbottom.assets.loader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetLoader;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.assets.Texture;
import de.ellpeck.rockbottom.assets.anim.Animation;

public class AnimationLoader implements IAssetLoader<Animation>{

    @Override
    public IResourceName getAssetIdentifier(){
        return RockBottomAPI.createInternalRes("anim");
    }

    @Override
    public Animation loadAsset(IAssetManager manager, IResourceName resourceName, String path, JsonElement element, String elementName, IMod loadingMod) throws Exception{
        JsonArray array = element.getAsJsonArray();
        String anim = array.get(0).getAsString();
        String texture = array.get(1).getAsString();

        Animation animation = Animation.fromStream(new Texture(AssetManager.getResource(path+texture), resourceName.toString(), false), AssetManager.getResource(path+anim));
        RockBottomAPI.logger().config("Loaded animation "+resourceName+" from "+path+anim+" and "+path+texture+" for mod "+loadingMod.getDisplayName());

        return animation;
    }
}
