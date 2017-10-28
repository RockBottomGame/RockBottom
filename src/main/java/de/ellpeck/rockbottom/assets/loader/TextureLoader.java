package de.ellpeck.rockbottom.assets.loader;

import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetLoader;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.ITexture;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.assets.Texture;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class TextureLoader implements IAssetLoader<ITexture>{

    private final Map<String, Map<String, JsonElement>> additionalDataCache = new HashMap<>();

    @Override
    public IResourceName getAssetIdentifier(){
        return RockBottomAPI.createInternalRes("tex");
    }

    @Override
    public ITexture loadAsset(IAssetManager manager, IResourceName resourceName, String path, JsonElement element, String elementName, IMod loadingMod) throws Exception{
        String resPath;
        Map<String, JsonElement> additionalData;

        if(element instanceof JsonArray){
            JsonArray array = element.getAsJsonArray();
            resPath = path+array.get(0).getAsString();
            String dataPath = path+array.get(1).getAsString();

            additionalData = this.additionalDataCache.get(dataPath);
            if(additionalData == null){
                additionalData = new HashMap<>();

                InputStreamReader reader = new InputStreamReader(AssetManager.getResource(dataPath), Charsets.UTF_8);
                JsonObject main = new JsonParser().parse(reader).getAsJsonObject();
                for(Map.Entry<String, JsonElement> entry : main.entrySet()){
                    additionalData.put(entry.getKey(), entry.getValue());
                }

                this.additionalDataCache.put(dataPath, additionalData);
            }
        }
        else{
            resPath = path+element.getAsString();
            additionalData = null;
        }

        Texture texture = new Texture(AssetManager.getResource(resPath), resourceName.toString(), false);

        if(additionalData != null){
            texture.setAdditionalData(additionalData);
        }

        RockBottomAPI.logger().config("Loaded texture "+resourceName+" from "+resPath+" for mod "+loadingMod.getDisplayName());
        return texture;
    }

    @Override
    public Map<IResourceName, ITexture> dealWithSpecialCases(IAssetManager manager, String resourceName, String path, JsonElement element, String elementName, IMod loadingMod) throws Exception{
        if("subtexture".equals(elementName)){
            Map<IResourceName, ITexture> subTextures = new HashMap<>();

            JsonObject object = element.getAsJsonObject();

            String file = object.getAsJsonPrimitive("file").getAsString();
            Texture main = new Texture(AssetManager.getResource(path+file), loadingMod.getId()+"/"+resourceName, false);

            for(Map.Entry<String, JsonElement> entry : object.entrySet()){
                String key = entry.getKey();
                if(!"file".equals(key)){
                    JsonArray array = entry.getValue().getAsJsonArray();
                    IResourceName res = RockBottomAPI.createRes(loadingMod, "*".equals(key) ? resourceName : resourceName+"."+key);

                    ITexture texture = main.getSubTexture(array.get(0).getAsInt(), array.get(1).getAsInt(), array.get(2).getAsInt(), array.get(3).getAsInt());
                    subTextures.put(res, texture);

                    RockBottomAPI.logger().config("Loaded subtexture "+res+" from texture "+path+file+" for mod "+loadingMod.getDisplayName());
                }
            }

            return subTextures;
        }
        else{
            return null;
        }
    }

    @Override
    public void finalize(IAssetManager manager){
        this.additionalDataCache.clear();
    }
}
