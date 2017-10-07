package de.ellpeck.rockbottom.assets.loader;

import com.google.gson.JsonElement;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetLoader;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.Locale;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.assets.AssetManager;

public class LocaleLoader implements IAssetLoader<Locale>{

    @Override
    public IResourceName getAssetIdentifier(){
        return RockBottomAPI.createInternalRes("loc");
    }

    @Override
    public Locale loadAsset(IAssetManager manager, IResourceName resourceName, String path, JsonElement element, String elementName, IMod loadingMod) throws Exception{
        String resPath = path+element.getAsString();
        boolean merged = false;

        Locale locale = Locale.fromStream(AssetManager.getResource(resPath), elementName);
        for(Locale asset : manager.getAllOfType(Locale.class).values()){
            if(asset.merge(locale)){
                merged = true;
                break;
            }
        }

        if(!merged){
            RockBottomAPI.logger().config("Loaded locale "+resourceName+" from "+resPath+" for mod "+loadingMod.getDisplayName());
            return locale;
        }
        else{
            return null;
        }
    }
}
