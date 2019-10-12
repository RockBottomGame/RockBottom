package de.ellpeck.rockbottom.content.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.resource.IUseInfo;
import de.ellpeck.rockbottom.api.construction.smelting.CombinerRecipe;
import de.ellpeck.rockbottom.api.construction.smelting.SmeltingRecipe;
import de.ellpeck.rockbottom.api.content.IContentLoader;
import de.ellpeck.rockbottom.api.content.pack.ContentPack;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.HashSet;
import java.util.Set;

public class CombinerRecipeLoader implements IContentLoader<CombinerRecipe> {

    private final Set<ResourceName> disabled = new HashSet<>();

    @Override
    public ResourceName getContentIdentifier() {
        return CombinerRecipe.ID;
    }

    @Override
    public void loadContent(IGameInstance game, ResourceName resourceName, String path, JsonElement element, String elementName, IMod loadingMod, ContentPack pack) throws Exception {
        System.out.println("COMBINER RECIPE LOADER");
        if (!this.disabled.contains(resourceName)) {
            if (Registries.COMBINER_REGISTRY.get(resourceName) != null) {
                RockBottomAPI.logger().info("Combiner recipe with name " + resourceName + " already exists, not adding recipe for mod " + loadingMod.getDisplayName() + " with content pack " + pack.getName());
            } else {
                JsonObject object = getRecipeObject(game, path + element.getAsString());

                int time = object.get("time").getAsInt();
                ItemInstance output = readItemInstance(object.get("output").getAsJsonObject());
                IUseInfo input1 = readUseInfo(object.get("input1").getAsJsonObject());
                IUseInfo input2 = readUseInfo(object.get("input2").getAsJsonObject());

                new CombinerRecipe(resourceName, input1, input2, output, time).register();

                RockBottomAPI.logger().config("Loaded combiner recipe " + resourceName + " for mod " + loadingMod.getDisplayName() + " with time " + time + ", input 1 " + input1 + ", input 2 " + input2 + " and output " + output + " with content pack " + pack.getName());
            }
        } else {
            RockBottomAPI.logger().info("Combiner recipe " + resourceName + " will not be loaded for mod " + loadingMod.getDisplayName() + " with content pack " + pack.getName() + " because it was disabled by another content pack!");
        }
    }


    @Override
    public void disableContent(IGameInstance game, ResourceName resourceName) {
        this.disabled.add(resourceName);
    }
}
