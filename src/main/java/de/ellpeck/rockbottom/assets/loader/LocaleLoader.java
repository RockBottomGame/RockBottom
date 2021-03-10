package de.ellpeck.rockbottom.assets.loader;

import com.google.common.base.Charsets;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetLoader;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.Locale;
import de.ellpeck.rockbottom.api.content.pack.ContentPack;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.content.ContentManager;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LocaleLoader implements IAssetLoader<Locale> {

    private final Set<ResourceName> disabled = new HashSet<>();

    @Override
    public ResourceName getAssetIdentifier() {
        return Locale.ID;
    }

    @Override
    public void loadAsset(IAssetManager manager, ResourceName resourceName, String path, JsonElement element, String elementName, IMod loadingMod, ContentPack pack) throws Exception {
        if (!this.disabled.contains(resourceName)) {
            String resPath = path + element.getAsString();
            Locale locale = this.fromStream(ContentManager.getResourceAsStream(resPath), elementName);

            for (Locale asset : manager.<Locale>getAllOfType(Locale.ID).values()) {
                if (this.merge(asset, locale)) {
                    return;
                }
            }

            RockBottomAPI.logger().config("Loaded locale " + resourceName + " for mod " + loadingMod.getDisplayName());
            manager.addAsset(this, resourceName, locale);
        } else {
            RockBottomAPI.logger().info("Locale " + resourceName + " will not be loaded for mod " + loadingMod.getDisplayName() + " with content pack " + pack.getName() + " because it was disabled by another content pack!");
        }
    }

    @Override
    public void disableAsset(IAssetManager manager, ResourceName resourceName) {
        this.disabled.add(resourceName);
    }

    private Locale fromStream(InputStream stream, String name) throws Exception {
        JsonElement main = JsonParser.parseReader(new InputStreamReader(stream, Charsets.UTF_8));
        stream.close();

        Map<ResourceName, String> locale = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : main.getAsJsonObject().entrySet()) {
            this.recurseLoad(locale, name, entry.getKey(), "", entry.getValue());
        }

        return new Locale(name, locale);
    }

    private void recurseLoad(Map<ResourceName, String> locale, String localeName, String domain, String name, JsonElement element) {
        if (element.isJsonPrimitive()) {
            String key = domain + Constants.RESOURCE_SEPARATOR + name;
            String value = element.getAsJsonPrimitive().getAsString();

            locale.put(new ResourceName(key), value);
            RockBottomAPI.logger().config("Added localization " + key + " -> " + value + " to locale with name " + localeName);
        } else {
            for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                String key = entry.getKey();

                String newName;
                if (name.isEmpty()) {
                    newName = key;
                } else {
                    if ("*".equals(key)) {
                        newName = name.substring(0, name.length() - 1);
                    } else if ("*.".equals(key)) {
                        newName = name;
                    } else {
                        newName = name + key;
                    }
                }

                this.recurseLoad(locale, localeName, domain, newName, entry.getValue());
            }
        }
    }

    private boolean merge(Locale locale, Locale otherLocale) {
        String name = locale.getName();
        if (name.equals(otherLocale.getName())) {
            Map<ResourceName, String> other = otherLocale.getLocalization();
            locale.override(other);

            RockBottomAPI.logger().config("Merged locale " + name + " with " + other.size() + " bits of additional localization information");
            return true;
        } else {
            return false;
        }
    }
}
