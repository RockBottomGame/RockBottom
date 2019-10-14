package de.ellpeck.rockbottom.content;

import com.google.common.base.Charsets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.Main;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.content.IContentLoader;
import de.ellpeck.rockbottom.api.content.pack.ContentPack;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.content.recipes.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public final class ContentManager {

    public static void init(IGameInstance game) {
        new ConstructionRecipeLoader().register();
        new SmeltingRecipeLoader().register();
        new CombinerRecipeLoader().register();
        new SmithingRecipeLoader().register();
        new MortarRecipeLoader().register();
        new StructureLoader().register();

        List<ContentPack> packs = RockBottomAPI.getContentPackLoader().getActivePacks();
        Set<IContentLoader> loaders = Registries.CONTENT_LOADER_REGISTRY.values();

        List<LoaderCallback> callbacks = new ArrayList<>();
        for (IContentLoader loader : loaders) {
            callbacks.add(new ContentCallback(loader, game));
        }
        for (IMod mod : RockBottomAPI.getModLoader().getActiveMods()) {
            loadContent(mod, mod.getContentLocation(), "content.json", callbacks, packs);
        }

        for (IContentLoader loader : loaders) {
            loader.finalize(game);
        }
    }

    public static void loadContent(IMod mod, String path, String file, List<LoaderCallback> callbacks, List<ContentPack> contentPacks) {
        if (path != null && !path.isEmpty()) {
            for (ContentPack pack : contentPacks) {
                String pathPrefix = pack.isDefault() ? "" : pack.getId() + '/';
                InputStream stream = getResourceAsStream(pathPrefix + path + '/' + file);

                if (stream != null) {
                    RockBottomAPI.logger().info("Loading " + file + " file for mod " + mod.getDisplayName() + " at path " + pathPrefix + path + " in content pack " + pack.getName());

                    try {
                        InputStreamReader reader = new InputStreamReader(stream, Charsets.UTF_8);
                        JsonObject main = Util.JSON_PARSER.parse(reader).getAsJsonObject();
                        reader.close();

                        for (Map.Entry<String, JsonElement> resType : main.entrySet()) {
                            String type = resType.getKey();
                            for (LoaderCallback callback : callbacks) {
                                ResourceName identifier = callback.getIdentifier().addSuffix(".");
                                if (identifier.getResourceName().equals(type) || identifier.toString().equals(type)) {
                                    JsonObject resources = resType.getValue().getAsJsonObject();
                                    for (Map.Entry<String, JsonElement> resource : resources.entrySet()) {
                                        loadRes(mod, pathPrefix + path, callback, "", resource.getValue(), resource.getKey(), pack);
                                    }

                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        RockBottomAPI.logger().log(Level.SEVERE, "Couldn't read " + file + " from mod " + mod.getDisplayName() + " in content pack " + pack.getName(), e);
                        continue;
                    }
                } else if (pack.isDefault()) {
                    RockBottomAPI.logger().warning("Mod " + mod.getDisplayName() + " is missing " + file + " file at path " + pathPrefix + path);
                    continue;
                } else {
                    RockBottomAPI.logger().config("Content pack " + pack.getName() + " does not have " + file + " file at path " + pathPrefix + path + " for mod " + mod.getDisplayName());
                    continue;
                }

                RockBottomAPI.logger().info("Loaded everything in " + file + " file for mod " + mod.getDisplayName() + " at path " + pathPrefix + path + " in content pack " + pack.getName());
            }
        } else {
            RockBottomAPI.logger().info("Skipping mod " + mod.getDisplayName() + " that doesn't have a folder set for " + file);
        }
    }

    private static void loadRes(IMod mod, String path, LoaderCallback loader, String name, JsonElement element, String elementName, ContentPack pack) {
        try {
            if (!loader.dealWithSpecialCases(name, path, element, elementName, mod, pack)) {
                if ("*".equals(elementName)) {
                    name = name.substring(0, name.length() - 1);
                } else if (!"*.".equals(elementName)) {
                    name += elementName;
                }

                if (!elementName.endsWith(".")) {
                    ResourceName resourceName = new ResourceName(mod, name);

                    if (element.isJsonPrimitive() && "none".equals(element.getAsString())) {
                        loader.disable(resourceName);
                    } else {
                        loader.load(resourceName, path, element, elementName, mod, pack);
                    }
                } else if (element.isJsonObject()) {
                    JsonObject object = element.getAsJsonObject();
                    for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                        loadRes(mod, path, loader, name, entry.getValue(), entry.getKey(), pack);
                    }
                }
            }
        } catch (Exception e) {
            RockBottomAPI.logger().log(Level.SEVERE, "Couldn't load resource " + name + " for mod " + mod.getDisplayName(), e);
        }
    }

    public static InputStream getResourceAsStream(String s) {
        return Main.classLoader.getResourceAsStream(s);
    }

    public static URL getResource(String s) {
        return Main.classLoader.getResource(s);
    }

    public interface LoaderCallback {

        ResourceName getIdentifier();

        void load(ResourceName resourceName, String path, JsonElement element, String elementName, IMod loadingMod, ContentPack pack) throws Exception;

        boolean dealWithSpecialCases(String resourceName, String path, JsonElement element, String elementName, IMod loadingMod, ContentPack pack) throws Exception;

        void disable(ResourceName resourceName);
    }

    private static class ContentCallback implements LoaderCallback {

        private final IContentLoader loader;
        private final IGameInstance game;

        public ContentCallback(IContentLoader loader, IGameInstance game) {
            this.loader = loader;
            this.game = game;
        }

        @Override
        public ResourceName getIdentifier() {
            return this.loader.getContentIdentifier();
        }

        @Override
        public void load(ResourceName resourceName, String path, JsonElement element, String elementName, IMod loadingMod, ContentPack pack) throws Exception {
            this.loader.loadContent(this.game, resourceName, path, element, elementName, loadingMod, pack);
        }

        @Override
        public boolean dealWithSpecialCases(String resourceName, String path, JsonElement element, String elementName, IMod loadingMod, ContentPack pack) {
            return this.loader.dealWithSpecialCases(this.game, resourceName, path, element, elementName, loadingMod, pack);
        }

        @Override
        public void disable(ResourceName resourceName) {
            this.loader.disableContent(this.game, resourceName);
        }
    }
}
