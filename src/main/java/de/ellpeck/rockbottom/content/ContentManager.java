package de.ellpeck.rockbottom.content;

import com.google.common.base.Charsets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.Main;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.content.IContentLoader;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;

public final class ContentManager{

    public static void init(IGameInstance game){
        new RecipeLoader().register();

        List<LoaderCallback> callbacks = new ArrayList<>();
        for(IContentLoader loader : RockBottomAPI.CONTENT_LOADER_REGISTRY.getUnmodifiable().values()){
            callbacks.add(new ContentCallback(loader, game));
        }
        loadContent(IMod:: getContentLocation, "content.json", callbacks);
    }

    public static void loadContent(Function<IMod, String> pathGetter, String file, List<LoaderCallback> callbacks){
        for(IMod mod : RockBottomAPI.getModLoader().getActiveMods()){
            String path = pathGetter.apply(mod);
            if(path != null && !path.isEmpty()){
                InputStream stream = getResourceAsStream(path+"/"+file);
                if(stream != null){
                    try{
                        InputStreamReader reader = new InputStreamReader(stream, Charsets.UTF_8);
                        JsonObject main = Util.JSON_PARSER.parse(reader).getAsJsonObject();
                        reader.close();

                        for(Map.Entry<String, JsonElement> resType : main.entrySet()){
                            String type = resType.getKey();
                            for(LoaderCallback callback : callbacks){
                                IResourceName identifier = callback.getIdentifier().addSuffix(".");
                                if(identifier.getResourceName().equals(type) || identifier.toString().equals(type)){
                                    JsonObject resources = resType.getValue().getAsJsonObject();
                                    for(Map.Entry<String, JsonElement> resource : resources.entrySet()){
                                        loadRes(mod, path, callback, "", resource.getValue(), resource.getKey());
                                    }

                                    break;
                                }
                            }
                        }
                    }
                    catch(Exception e){
                        RockBottomAPI.logger().log(Level.SEVERE, "Couldn't read "+file+" from mod "+mod.getDisplayName(), e);
                        continue;
                    }
                }
                else{
                    RockBottomAPI.logger().severe("Mod "+mod.getDisplayName()+" is missing "+file+" file at path "+path);
                    continue;
                }

                RockBottomAPI.logger().info("Loaded everything in "+file+" file for mod "+mod.getDisplayName()+" at path "+path);
            }
            else{
                RockBottomAPI.logger().info("Skipping mod "+mod.getDisplayName()+" that doesn't have a folder set for "+file);
            }
        }
    }

    private static void loadRes(IMod mod, String path, LoaderCallback loader, String name, JsonElement element, String elementName){
        try{
            if(!loader.dealWithSpecialCases(name, path, element, elementName, mod)){
                if("*".equals(elementName)){
                    name = name.substring(0, name.length()-1);
                }
                else if(!"*.".equals(elementName)){
                    name += elementName;
                }

                if(!elementName.endsWith(".")){
                    IResourceName resourceName = RockBottomAPI.createRes(mod, name);
                    loader.load(resourceName, path, element, elementName, mod);
                }
                else if(element.isJsonObject()){
                    JsonObject object = element.getAsJsonObject();
                    for(Map.Entry<String, JsonElement> entry : object.entrySet()){
                        loadRes(mod, path, loader, name, entry.getValue(), entry.getKey());
                    }
                }
            }
        }
        catch(Exception e){
            RockBottomAPI.logger().log(Level.SEVERE, "Couldn't load resource "+name+" for mod "+mod.getDisplayName(), e);
        }
    }

    public static InputStream getResourceAsStream(String s){
        return Main.classLoader.getResourceAsStream(s);
    }

    public static URL getResource(String s){
        return Main.classLoader.getResource(s);
    }

    public interface LoaderCallback{

        IResourceName getIdentifier();

        void load(IResourceName resourceName, String path, JsonElement element, String elementName, IMod loadingMod) throws Exception;

        boolean dealWithSpecialCases(String resourceName, String path, JsonElement element, String elementName, IMod loadingMod) throws Exception;
    }

    private static class ContentCallback implements LoaderCallback{

        private final IContentLoader loader;
        private final IGameInstance game;

        public ContentCallback(IContentLoader loader, IGameInstance game){
            this.loader = loader;
            this.game = game;
        }

        @Override
        public IResourceName getIdentifier(){
            return this.loader.getContentIdentifier();
        }

        @Override
        public void load(IResourceName resourceName, String path, JsonElement element, String elementName, IMod loadingMod) throws Exception{
            this.loader.loadContent(this.game, resourceName, path, element, elementName, loadingMod);
        }

        @Override
        public boolean dealWithSpecialCases(String resourceName, String path, JsonElement element, String elementName, IMod loadingMod) throws Exception{
            return this.loader.dealWithSpecialCases(this.game, resourceName, path, element, elementName, loadingMod);
        }
    }
}
