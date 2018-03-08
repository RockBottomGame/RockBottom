package de.ellpeck.rockbottom.assets.loader;

import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAnimation;
import de.ellpeck.rockbottom.api.assets.IAssetLoader;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.texture.stitcher.IStitchCallback;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.assets.anim.Animation;
import de.ellpeck.rockbottom.assets.anim.AnimationRow;
import de.ellpeck.rockbottom.assets.tex.Texture;
import de.ellpeck.rockbottom.content.ContentManager;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimationLoader implements IAssetLoader<Animation>{

    private final Map<String, CachedAnimInfo> rowCache = new HashMap<>();

    @Override
    public IResourceName getAssetIdentifier(){
        return IAnimation.ID;
    }

    @Override
    public void loadAsset(IAssetManager manager, IResourceName resourceName, String path, JsonElement element, String elementName, IMod loadingMod) throws Exception{
        boolean shouldStitch = true;
        String anim;
        String texture;

        if(element.isJsonObject()){
            JsonObject object = element.getAsJsonObject();
            anim = object.get("data").getAsString();
            texture = object.get("path").getAsString();

            if(object.has("should_stitch")){
                shouldStitch = object.get("should_stitch").getAsBoolean();
            }
        }
        else{
            JsonArray array = element.getAsJsonArray();
            anim = array.get(0).getAsString();
            texture = array.get(1).getAsString();
        }

        int frameWidth = 0;
        int frameHeight = 0;

        CachedAnimInfo cachedInfo = this.rowCache.get(path+anim);
        if(cachedInfo == null){
            List<AnimationRow> rows = new ArrayList<>();

            InputStream infoStream = ContentManager.getResourceAsStream(path+anim);
            JsonObject main = Util.JSON_PARSER.parse(new InputStreamReader(infoStream, Charsets.UTF_8)).getAsJsonObject();
            infoStream.close();

            for(Map.Entry<String, JsonElement> entry : main.getAsJsonObject().entrySet()){
                String key = entry.getKey();
                JsonArray data = entry.getValue().getAsJsonArray();

                if("size".equals(key)){
                    frameWidth = data.get(0).getAsInt();
                    frameHeight = data.get(1).getAsInt();
                }
                else if("data".equals(key)){
                    for(JsonElement e : data){
                        JsonArray a = e.getAsJsonArray();
                        float[] times = new float[a.size()];

                        for(int i = 0; i < times.length; i++){
                            times[i] = a.get(i).getAsFloat();
                        }

                        rows.add(new AnimationRow(times));
                    }
                }
                else{
                    for(int i = 0; i < data.size(); i++){
                        AnimationRow row = rows.get(i);
                        JsonElement[] additionalData = new JsonElement[row.getFrameAmount()];

                        JsonArray a = data.get(i).getAsJsonArray();
                        for(int j = 0; j < additionalData.length; j++){
                            additionalData[j] = a.get(j);
                        }

                        row.addAdditionalFrameData(key, additionalData);
                    }
                }
            }

            cachedInfo = new CachedAnimInfo(rows, frameWidth, frameHeight);
            this.rowCache.put(path+anim, cachedInfo);
        }

        CachedAnimInfo finalCachedInfo = cachedInfo;
        IStitchCallback callback = (stitchX, stitchY, stitchedTexture) -> {
            Animation animation = new Animation(stitchedTexture, finalCachedInfo.width, finalCachedInfo.height, finalCachedInfo.rows);
            RockBottomAPI.logger().config("Loaded animation "+resourceName+" for mod "+loadingMod.getDisplayName());
            manager.addAsset(this, resourceName, animation);
        };

        if(shouldStitch){
            manager.getTextureStitcher().loadTexture(resourceName.toString(), ContentManager.getResourceAsStream(path+texture), callback);
        }
        else{
            callback.onStitched(0, 0, new Texture(ContentManager.getResourceAsStream(path+texture)));
        }
    }

    @Override
    public void finalize(IAssetManager manager){
        this.rowCache.clear();
    }

    private static class CachedAnimInfo{

        public final List<AnimationRow> rows;
        public final int width;
        public final int height;

        public CachedAnimInfo(List<AnimationRow> rows, int width, int height){
            this.rows = rows;
            this.width = width;
            this.height = height;
        }
    }
}
