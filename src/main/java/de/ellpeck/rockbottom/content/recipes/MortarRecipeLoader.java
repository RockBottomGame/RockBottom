package de.ellpeck.rockbottom.content.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.compendium.MortarRecipe;
import de.ellpeck.rockbottom.api.construction.resource.IUseInfo;
import de.ellpeck.rockbottom.api.content.IContentLoader;
import de.ellpeck.rockbottom.api.content.pack.ContentPack;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MortarRecipeLoader implements IContentLoader<MortarRecipe> {

    private final Set<ResourceName> disabled = new HashSet<>();

    @Override
    public ResourceName getContentIdentifier() {
        return MortarRecipe.ID;
    }

    @Override
    public void loadContent(IGameInstance game, ResourceName resourceName, String path, JsonElement element, String elementName, IMod loadingMod, ContentPack pack) throws Exception {
        if (!this.disabled.contains(resourceName)) {
            if (MortarRecipe.forName(resourceName) != null) {
                RockBottomAPI.logger().info("Mortar recipe with name " + resourceName + " already exists, not adding recipe for mod " + loadingMod.getDisplayName() + " with content pack " + pack.getName());
            } else {
				JsonObject object = getRecipeObject(game, path + element.getAsString());

				boolean isKnowledge = object.has("knowledge") && object.get("knowledge").getAsBoolean();
				int skillReward = object.has("skill") ? object.get("skill").getAsInt() : 0;
				int time = object.get("time").getAsInt();
                List<ItemInstance> output = readItemInstances(object.get("output").getAsJsonArray());
                List<IUseInfo> input = readUseInfos(object.get("input").getAsJsonArray());

                MortarRecipe recipe = new MortarRecipe(resourceName, input, output, time, isKnowledge, skillReward).register();

                if (object.has("criteria")) {
                    processCriteria(recipe, object.getAsJsonArray("criteria"));
                }

                RockBottomAPI.logger().config("Loaded mortar recipe " + resourceName + " for mod " + loadingMod.getDisplayName() + " with time " + time + ", input " + input + " and output " + output + " with content pack " + pack.getName());
            }
        } else {
            RockBottomAPI.logger().info("Mortar recipe " + resourceName + " will not be loaded for mod " + loadingMod.getDisplayName() + " with content pack " + pack.getName() + " because it was disabled by another content pack!");
        }
    }


    @Override
    public void disableContent(IGameInstance game, ResourceName resourceName) {
        this.disabled.add(resourceName);
    }
}
