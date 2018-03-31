package de.ellpeck.rockbottom.assets.loader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetLoader;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.content.pack.ContentPack;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.assets.font.Font;
import de.ellpeck.rockbottom.content.ContentManager;

import java.util.HashSet;
import java.util.Set;

public class FontLoader implements IAssetLoader<IFont>{

    private final Set<IResourceName> disabled = new HashSet<>();

    @Override
    public IResourceName getAssetIdentifier(){
        return IFont.ID;
    }

    @Override
    public void loadAsset(IAssetManager manager, IResourceName resourceName, String path, JsonElement element, String elementName, IMod loadingMod, ContentPack pack){
        if(!this.disabled.contains(resourceName)){
            JsonArray array = element.getAsJsonArray();
            String info = array.get(0).getAsString();
            String texture = array.get(1).getAsString();

            manager.getTextureStitcher().loadTexture(resourceName.toString(), ContentManager.getResourceAsStream(path+texture), (stitchX, stitchY, stitchedTexture) -> {
                Font font = Font.fromStream(stitchedTexture, ContentManager.getResourceAsStream(path+info), resourceName.toString());
                if(manager.addAsset(this, resourceName, font)){
                    RockBottomAPI.logger().config("Loaded font "+resourceName+" for mod "+loadingMod.getDisplayName());
                }
                else{
                    RockBottomAPI.logger().info("Font "+resourceName+" already exists, not adding font for mod "+loadingMod.getDisplayName()+" with content pack "+pack.getName());
                }
            });
        }
        else{
            RockBottomAPI.logger().info("Font "+resourceName+" will not be loaded for mod "+loadingMod.getDisplayName()+" with content pack "+pack.getName()+" because it was disabled by another content pack!");
        }
    }

    @Override
    public void disableAsset(IAssetManager manager, IResourceName resourceName){
        this.disabled.add(resourceName);
    }
}
