package de.ellpeck.rockbottom.content;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.resource.IUseInfo;
import de.ellpeck.rockbottom.api.construction.smelting.SmeltingRecipe;
import de.ellpeck.rockbottom.api.content.IContentLoader;
import de.ellpeck.rockbottom.api.content.pack.ContentPack;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.HashSet;
import java.util.Set;

public class SmeltingRecipeLoader implements IContentLoader<SmeltingRecipe> {

    private final Set<ResourceName> disabled = new HashSet<>();

    @Override
    public ResourceName getContentIdentifier() {
        return SmeltingRecipe.ID;
    }

    @Override
    public void loadContent(IGameInstance game, ResourceName resourceName, String path, JsonElement element, String elementName, IMod loadingMod, ContentPack pack) throws Exception {
        if (!this.disabled.contains(resourceName)) {
            if (Registries.SMELTING_REGISTRY.get(resourceName) != null) {
                RockBottomAPI.logger().info("Smelting recipe with name " + resourceName + " already exists, not adding recipe for mod " + loadingMod.getDisplayName() + " with content pack " + pack.getName());
            } else {
                JsonObject object = ContentLoaderUtils.getRecipeObject(path + element.getAsString());

                int time = object.get("time").getAsInt();
                ItemInstance output = ContentLoaderUtils.readItemInstance(object.get("output").getAsJsonObject());
                IUseInfo input = ContentLoaderUtils.readUseInfo(object.get("input").getAsJsonObject());

                new SmeltingRecipe(resourceName, input, output, time).register();

                RockBottomAPI.logger().config("Loaded smelting recipe " + resourceName + " for mod " + loadingMod.getDisplayName() + " with time " + time + ", input " + input + " and output " + output + " with content pack " + pack.getName());
            }
        } else {
            RockBottomAPI.logger().info("Smelting recipe " + resourceName + " will not be loaded for mod " + loadingMod.getDisplayName() + " with content pack " + pack.getName() + " because it was disabled by another content pack!");
        }
    }


    @Override
    public void disableContent(IGameInstance game, ResourceName resourceName) {
        this.disabled.add(resourceName);
    }
}
