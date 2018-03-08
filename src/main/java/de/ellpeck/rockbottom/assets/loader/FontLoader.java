package de.ellpeck.rockbottom.assets.loader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetLoader;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.assets.Font;
import de.ellpeck.rockbottom.content.ContentManager;

public class FontLoader implements IAssetLoader<IFont>{

    @Override
    public IResourceName getAssetIdentifier(){
        return IFont.ID;
    }

    @Override
    public void loadAsset(IAssetManager manager, IResourceName resourceName, String path, JsonElement element, String elementName, IMod loadingMod) throws Exception{
        JsonArray array = element.getAsJsonArray();
        String info = array.get(0).getAsString();
        String texture = array.get(1).getAsString();

        manager.getTextureStitcher().loadTexture(resourceName.toString(), ContentManager.getResourceAsStream(path+texture), (stitchX, stitchY, stitchedTexture) -> {
            Font font = Font.fromStream(stitchedTexture, ContentManager.getResourceAsStream(path+info), resourceName.toString());
            RockBottomAPI.logger().config("Loaded font "+resourceName+" for mod "+loadingMod.getDisplayName());
            manager.addAsset(this, resourceName, font);
        });
    }
}
