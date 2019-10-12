package de.ellpeck.rockbottom.content.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.compendium.SmithingRecipe;
import de.ellpeck.rockbottom.api.construction.resource.IUseInfo;
import de.ellpeck.rockbottom.api.content.IContentLoader;
import de.ellpeck.rockbottom.api.content.pack.ContentPack;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SmithingRecipeLoader implements IContentLoader<SmithingRecipe> {

    private final Set<ResourceName> disabled = new HashSet<>();

    @Override
    public ResourceName getContentIdentifier() {
        return SmithingRecipe.ID;
    }

    @Override
    public void loadContent(IGameInstance game, ResourceName resourceName, String path, JsonElement element, String elementName, IMod loadingMod, ContentPack pack) throws Exception {
        if (!this.disabled.contains(resourceName)) {
            if (Registries.SMITHING_RECIPES.get(resourceName) != null) {
                RockBottomAPI.logger().info("Smithing recipe with name " + resourceName + " already exists, not adding recipe for mod " + loadingMod.getDisplayName() + " with content pack " + pack.getName());
            } else {
                JsonObject object = getRecipeObject(game, path + element.getAsString());


                boolean knowledge = object.has("knowledge") && object.get("knowledge").getAsBoolean();
                List<IUseInfo> inputList = readUseInfos(object.get("inputs").getAsJsonArray());
                List<ItemInstance> outputList = readItemInstances(object.get("outputs").getAsJsonArray());
                int usage = object.has("usage") ? object.get("usage").getAsInt() : 1;
                float skill = object.has("skill") ? object.get("skill").getAsFloat() : 0;

                SmithingRecipe recipe = new SmithingRecipe(resourceName, inputList, outputList, knowledge, skill, usage).register();

                if (object.has("criteria")) {
                    processCriteria(recipe, object.get("criteria").getAsJsonArray());
                }

                RockBottomAPI.logger().config("Loaded smithing recipe " + resourceName + " for mod " + loadingMod.getDisplayName() + ", recipeInputs " + inputList + " outputs " + outputList + " skill " + skill + " with content pack " + pack.getName());
            }
        } else {
            RockBottomAPI.logger().info("Smithing recipe " + resourceName + " will not be loaded for mod " + loadingMod.getDisplayName() + " with content pack " + pack.getName() + " because it was disabled by another content pack!");
        }
    }


    @Override
    public void disableContent(IGameInstance game, ResourceName resourceName) {
        this.disabled.add(resourceName);
    }
}
