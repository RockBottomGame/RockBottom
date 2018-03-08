package de.ellpeck.rockbottom.assets.loader;

import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetLoader;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.texture.ITexture;
import de.ellpeck.rockbottom.api.assets.texture.stitcher.IStitchCallback;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.assets.tex.Texture;
import de.ellpeck.rockbottom.content.ContentManager;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextureLoader implements IAssetLoader<ITexture>{

    ;
    private final Map<String, Map<String, JsonElement>> additionalDataCache = new HashMap<>();

    @Override
    public IResourceName getAssetIdentifier(){
        return ITexture.ID;
    }

    @Override
    public void loadAsset(IAssetManager manager, IResourceName resourceName, String path, JsonElement element, String elementName, IMod loadingMod) throws Exception{
        this.makeTexture(manager, resourceName.toString(), element, path, (stitchX, stitchY, stitchedTexture) -> {
            RockBottomAPI.logger().config("Loaded texture "+resourceName+" for mod "+loadingMod.getDisplayName());
            manager.addAsset(this, resourceName, stitchedTexture);
        });
    }

    private void makeTexture(IAssetManager manager, String refName, JsonElement element, String path, IStitchCallback callback) throws Exception{
        String resPath;
        Map<String, JsonElement> additionalData = null;
        List<ITexture> variations = null;
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

                    List<ITexture> finalVars = variations;
                    IStitchCallback call = (stitchX, stitchY, stitchedTexture) -> finalVars.add(stitchedTexture);

                    if(shouldStitch){
                        manager.getTextureStitcher().loadTexture(refName+"_variation_"+variations.size(), ContentManager.getResourceAsStream(dataPath), call);
                    }
                    else{
                        call.onStitched(0, 0, new Texture(ContentManager.getResourceAsStream(dataPath)));
                    }
                }
            }

            if(object.has("data")){
                String dataPath = path+object.get("data").getAsString();
                if(additionalData == null){
                    additionalData = this.additionalDataCache.get(dataPath);
                    if(additionalData == null){
                        InputStreamReader reader = new InputStreamReader(ContentManager.getResourceAsStream(dataPath), Charsets.UTF_8);
                        JsonObject main = Util.JSON_PARSER.parse(reader).getAsJsonObject();
                        reader.close();

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

        Map<String, JsonElement> finalAdditionalData = additionalData;
        List<ITexture> finalVariations = variations;
        IStitchCallback back = (stitchX, stitchY, stitchedTexture) -> {
            if(finalAdditionalData != null){
                stitchedTexture.setAdditionalData(finalAdditionalData);
            }

            if(finalVariations != null){
                stitchedTexture.setVariations(finalVariations);
            }

            callback.onStitched(stitchX, stitchY, stitchedTexture);
        };

        if(shouldStitch){
            manager.getTextureStitcher().loadTexture(refName, ContentManager.getResourceAsStream(resPath), back);
        }
        else{
            back.onStitched(0, 0, new Texture(ContentManager.getResourceAsStream(resPath)));
        }
    }

    @Override
    public boolean dealWithSpecialCases(IAssetManager manager, String resourceName, String path, JsonElement element, String elementName, IMod loadingMod) throws Exception{
        if("subtexture".equals(elementName)){
            JsonObject object = element.getAsJsonObject();

            this.makeTexture(manager, "subtexture_"+resourceName, object.get("file"), path, (stitchX, stitchY, stitchedTexture) -> {
                for(Map.Entry<String, JsonElement> entry : object.entrySet()){
                    String key = entry.getKey();
                    if(!"file".equals(key)){
                        JsonArray array = entry.getValue().getAsJsonArray();

                        String resName;
                        if("*".equals(key)){
                            resName = resourceName.substring(0, resourceName.length()-1);
                        }
                        else{
                            resName = resourceName+key;
                        }
                        IResourceName res = RockBottomAPI.createRes(loadingMod, resName);

                        ITexture texture = stitchedTexture.getSubTexture(array.get(0).getAsInt(), array.get(1).getAsInt(), array.get(2).getAsInt(), array.get(3).getAsInt());
                        RockBottomAPI.logger().config("Loaded subtexture "+res+" for mod "+loadingMod.getDisplayName());
                        manager.addAsset(this, res, texture);
                    }
                }
            });

            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void finalize(IAssetManager manager){
        this.additionalDataCache.clear();
    }
}
