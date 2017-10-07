package de.ellpeck.rockbottom.assets.loader;

import com.google.gson.JsonElement;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetLoader;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.ITexture;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.assets.Texture;

public class TextureLoader implements IAssetLoader<ITexture>{

    @Override
    public IResourceName getAssetIdentifier(){
        return RockBottomAPI.createInternalRes("tex");
    }

    @Override
    public ITexture loadAsset(IAssetManager manager, IResourceName resourceName, String path, JsonElement element, String elementName, IMod loadingMod) throws Exception{
        String resPath = path+element.getAsString();

        ITexture texture = new Texture(AssetManager.getResource(resPath), resourceName.toString(), false);
        RockBottomAPI.logger().config("Loaded texture "+resourceName+" from "+resPath+" for mod "+loadingMod.getDisplayName());

        return texture;
    }
}
