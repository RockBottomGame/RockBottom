package de.ellpeck.rockbottom.content.recipes;

import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.compendium.ICompendiumRecipe;
import de.ellpeck.rockbottom.api.construction.compendium.ICriteria;
import de.ellpeck.rockbottom.api.construction.compendium.smithing.SmithingRecipe;
import de.ellpeck.rockbottom.api.construction.resource.IUseInfo;
import de.ellpeck.rockbottom.api.construction.resource.ItemUseInfo;
import de.ellpeck.rockbottom.api.construction.resource.ResUseInfo;
import de.ellpeck.rockbottom.api.construction.smelting.SmeltingRecipe;
import de.ellpeck.rockbottom.api.content.IContentLoader;
import de.ellpeck.rockbottom.api.content.pack.ContentPack;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.content.ContentManager;

import java.io.InputStreamReader;
import java.util.ArrayList;
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
                String resPath = path + element.getAsString();

                InputStreamReader reader = new InputStreamReader(ContentManager.getResourceAsStream(resPath), Charsets.UTF_8);
                JsonElement recipeElement = Util.JSON_PARSER.parse(reader);
                reader.close();

                JsonObject object = recipeElement.getAsJsonObject();

                String type = object.get("type").getAsString();

				List<IUseInfo> inputList = new ArrayList<>();
				List<ItemInstance> outputList = new ArrayList<>();

				JsonArray outputs = object.get("outputs").getAsJsonArray();
				for (JsonElement output : outputs) {
					JsonObject out = output.getAsJsonObject();

					Item item = Registries.ITEM_REGISTRY.get(new ResourceName(out.get("name").getAsString()));
					int amount = out.has("amount") ? out.get("amount").getAsInt() : 1;
					int meta = out.has("meta") ? out.get("meta").getAsInt() : 0;

					outputList.add(new ItemInstance(item, amount, meta));
				}

				JsonArray inputs = object.get("inputs").getAsJsonArray();
				for (JsonElement input : inputs) {
					JsonObject in = input.getAsJsonObject();

					String name = in.get("name").getAsString();
					int amount = in.has("amount") ? in.get("amount").getAsInt() : 1;

					if (Util.isResourceName(name)) {
						int meta = in.has("meta") ? in.get("meta").getAsInt() : 0;
						inputList.add(new ItemUseInfo(Registries.ITEM_REGISTRY.get(new ResourceName(name)), amount, meta));
					} else {
						inputList.add(new ResUseInfo(name, amount));
					}
				}

				float difficulty = object.has("difficulty") ? object.get("difficulty").getAsFloat() : 1;
				int hits = object.has("hits") ? object.get("hits").getAsInt() : 0;
				float skill = object.has("skill") ? object.get("skill").getAsFloat() : 0;
				int usage = object.get("usage").getAsInt();

				SmithingRecipe recipe;
                if ("smithing".equals(type)) {
					recipe = new SmithingRecipe(resourceName, inputList, outputList, false, skill, difficulty, hits, usage).register();
				} else if ("smithing_knowledge".equals(type)) {
					recipe = new SmithingRecipe(resourceName, inputList, outputList, true, skill, difficulty, hits, usage).register();
				} else {
					throw new IllegalArgumentException("Invalid recipe type " + type + " for recipe " + resourceName);
				}

				if (object.has("criteria")) {
					JsonArray ca = object.getAsJsonArray("criteria");
					for (JsonElement ce : ca) {
						JsonObject criteria = ce.getAsJsonObject();
						String cname = criteria.get("name").getAsString();
						ICriteria icriteria = Registries.CRITERIA_REGISTRY.get(new ResourceName(cname));
						if (icriteria == null) {
							throw new IllegalArgumentException("Invalid criteria " + cname + " for recipe " + resourceName);
						}
						JsonObject params = criteria.getAsJsonObject("params");
						if (!icriteria.deserialize(recipe, params)) {
							RockBottomAPI.logger().warning("Failed to deserialize criteria " + cname + " for recipe " + resourceName);
						}
					}
				}

				RockBottomAPI.logger().config("Loaded smithing recipe " + resourceName + " for mod " + loadingMod.getDisplayName() + " with type " + type + ", inputs " + inputList + " outputs " + outputList + " skill " + skill + " difficulty " + difficulty + " hits " + hits + " with content pack " + pack.getName());
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
