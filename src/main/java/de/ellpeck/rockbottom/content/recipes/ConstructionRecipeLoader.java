package de.ellpeck.rockbottom.content.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.ConstructionTool;
import de.ellpeck.rockbottom.api.construction.compendium.ICompendiumRecipe;
import de.ellpeck.rockbottom.api.construction.compendium.ConstructionRecipe;
import de.ellpeck.rockbottom.api.construction.resource.IUseInfo;
import de.ellpeck.rockbottom.api.content.IContentLoader;
import de.ellpeck.rockbottom.api.content.pack.ContentPack;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConstructionRecipeLoader implements IContentLoader<ConstructionRecipe> {

    private final Set<ResourceName> disabled = new HashSet<>();

    @Override
    public ResourceName getContentIdentifier() {
        return ConstructionRecipe.ID;
    }

    @Override
    public void loadContent(IGameInstance game, ResourceName resourceName, String path, JsonElement element, String elementName, IMod loadingMod, ContentPack pack) throws Exception {
        if (!this.disabled.contains(resourceName)) {
            if (ICompendiumRecipe.forName(resourceName) != null) {
                RockBottomAPI.logger().info("Recipe with name " + resourceName + " already exists, not adding recipe for mod " + loadingMod.getDisplayName() + " with content pack " + pack.getName());
            } else {
                JsonObject object = getRecipeObject(game, path + element.getAsString());

                String type = object.get("type").getAsString();
                boolean knowledge = object.has("knowledge") && object.get("knowledge").getAsBoolean();
                float skill = object.has("skill") ? object.get("skill").getAsFloat() : 0;

                List<IUseInfo> inputList = readUseInfos(object.get("inputs").getAsJsonArray());
                List<ItemInstance> outputList = readItemInstances(object.get("outputs").getAsJsonArray());

                List<ConstructionTool> tools = new ArrayList<>();

                if (object.has("tools")) {
                    JsonArray toolsJson = object.get("tools").getAsJsonArray();
                    for (JsonElement toolRaw : toolsJson) {
                        JsonObject tool = toolRaw.getAsJsonObject();
                        Item item = Registries.ITEM_REGISTRY.get(new ResourceName(tool.get("name").getAsString()));
                        int usage = tool.has("usage") ? tool.get("usage").getAsInt() : 1;

                        if (item != null && usage > 0) {
                            tools.add(new ConstructionTool(item, usage));
                        } else {
                            RockBottomAPI.logger().warning("Invalid tool listed for recipe " + resourceName);
                        }
                    }
                }

                ConstructionRecipe recipe;
                if ("manual".equals(type) || "manual_only".equals(type)) {
                    recipe = new ConstructionRecipe(resourceName, null, inputList, outputList, "manual_only".equals(type), knowledge, skill).registerManual();
                } else if ("construction_table".equals(type)) {
                    recipe = new ConstructionRecipe(resourceName, tools, inputList, outputList, false, knowledge, skill).registerConstructionTable();
                } else {
                    throw new IllegalArgumentException("Invalid recipe type " + type + " for recipe " + resourceName);
                }

                if (object.has("criteria")) {
                    processCriteria(recipe, object.getAsJsonArray("criteria"));
                }

                RockBottomAPI.logger().config("Loaded recipe " + resourceName + " for mod " + loadingMod.getDisplayName() + " with type " + type + ", inputs " + inputList + " outputs " + outputList + " and skill " + skill + " with content pack " + pack.getName());
            }
        } else {
            RockBottomAPI.logger().info("Recipe " + resourceName + " will not be loaded for mod " + loadingMod.getDisplayName() + " with content pack " + pack.getName() + " because it was disabled by another content pack!");
        }
    }


    @Override
    public void disableContent(IGameInstance game, ResourceName resourceName) {
        this.disabled.add(resourceName);
    }
}
