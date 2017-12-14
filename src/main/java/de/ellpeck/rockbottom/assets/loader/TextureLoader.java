package de.ellpeck.rockbottom.assets.loader;

import com.google.common.base.Charsets;
import com.google.gson.*;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetLoader;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.ITexture;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.assets.tex.RenderedTexture;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextureLoader implements IAssetLoader<ITexture>{

    private final Map<String, Map<String, JsonElement>> additionalDataCache = new HashMap<>();

    @Override
    public IResourceName getAssetIdentifier(){
        return RockBottomAPI.createInternalRes("tex");
    }

    @Override
    public ITexture loadAsset(IAssetManager manager, IResourceName resourceName, String path, JsonElement element, String elementName, IMod loadingMod) throws Exception{
        String resPath = null;
        Map<String, JsonElement> additionalData = null;
        List<ITexture> variations = null;
        int variationCount = 0;
        boolean shouldStitch = true;

        if(element.isJsonObject()){
            JsonObject object = element.getAsJsonObject();
            resPath = path+object.get("path").getAsString();

            if(object.has("should_stitch")){
                shouldStitch = object.get("should_stitch").getAsBoolean();
            }

            if(object.has("variations")){
                JsonArray varArray = object.get("variations").getAsJsonArray();
                for(JsonElement variation : varArray){
                    String dataPath = path+variation.getAsString();

                    if(variations == null){
                        variations = new ArrayList<>();
                    }

                    List<ITexture> finalVar = variations;
                    IStitchCallback callback = (stitchX, stitchY, stitchedTexture) -> finalVar.add(stitchedTexture);

                    if(shouldStitch){
                        manager.getTextureStitcher().loadTexture(resourceName.addSuffix("_variation_"+variationCount).toString(), AssetManager.getResource(dataPath), false, callback);
                    }
                    else{
                        callback.onStitched(0, 0, new RenderedTexture(AssetManager.getResource(dataPath), false));
                    }

                    variationCount++;
                }
            }

            if(object.has("data")){
                String dataPath = path+object.get("data").getAsString();
                if(additionalData == null){
                    additionalData = this.additionalDataCache.get(dataPath);
                    if(additionalData == null){
                        InputStreamReader reader = new InputStreamReader(AssetManager.getResource(dataPath), Charsets.UTF_8);
                        JsonObject main = new JsonParser().parse(reader).getAsJsonObject();
                        if(main != null){
                            additionalData = new HashMap<>();

                            for(Map.Entry<String, JsonElement> entry : main.entrySet()){
                                additionalData.put(entry.getKey(), entry.getValue());
                            }

                            this.additionalDataCache.put(dataPath, additionalData);
                        }
                    }
                }
            }
        }
        else{
            resPath = path+element.getAsString();
        }

        RenderedTexture texture = new RenderedTexture(AssetManager.getResource(resPath), false);

        if(additionalData != null){
            texture.setAdditionalData(additionalData);
        }

        if(variations != null){
            texture.setVariations(variations);
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
            RenderedTexture main = new RenderedTexture(AssetManager.getResource(path+file), false);

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
