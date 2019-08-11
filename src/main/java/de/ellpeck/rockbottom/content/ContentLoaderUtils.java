package de.ellpeck.rockbottom.content;

import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.compendium.ICriteria;
import de.ellpeck.rockbottom.api.construction.compendium.PlayerCompendiumRecipe;
import de.ellpeck.rockbottom.api.construction.resource.IUseInfo;
import de.ellpeck.rockbottom.api.construction.resource.ItemUseInfo;
import de.ellpeck.rockbottom.api.construction.resource.ResUseInfo;
import de.ellpeck.rockbottom.api.data.set.ModBasedDataSet;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ContentLoaderUtils {
    public static JsonObject getRecipeObject(String path) throws IOException {
        InputStreamReader reader = new InputStreamReader(ContentManager.getResourceAsStream(path), Charsets.UTF_8);
        JsonElement recipeElement = Util.JSON_PARSER.parse(reader);
        reader.close();
        return recipeElement.getAsJsonObject();
    }
    public static IUseInfo readUseInfo(JsonObject obj) {
        String name = obj.get("name").getAsString();
        int amount = obj.has("amount") ? obj.get("amount").getAsInt() : 1;
        if (Util.isResourceName(name)) {
            int meta = obj.has("meta") ? obj.get("meta").getAsInt() : 0;
            return new ItemUseInfo(Registries.ITEM_REGISTRY.get(new ResourceName(name)), amount, meta);
        } else return new ResUseInfo(name, amount);
    }

    public static List<IUseInfo> readUseInfos(JsonArray array) {
        List<IUseInfo> infos = new ArrayList<>();
        for (JsonElement input : array) {
            infos.add(ContentLoaderUtils.readUseInfo(input.getAsJsonObject()));
        }
        return infos;
    }

    public static ItemInstance readItemInstance(JsonObject obj) throws Exception {
        Item item = Registries.ITEM_REGISTRY.get(new ResourceName(obj.get("name").getAsString()));
        int amount = obj.has("amount") ? obj.get("amount").getAsInt() : 1;
        int meta = obj.has("meta") ? obj.get("meta").getAsInt() : 0;
        ItemInstance instance = new ItemInstance(item, amount, meta);
        if (obj.has("data")) {
            ModBasedDataSet set = instance.getOrCreateAdditionalData();
            RockBottomAPI.getApiHandler().readDataSet(obj.get("data").getAsJsonObject(), set);
        }
        return instance;
    }

    public static List<ItemInstance> readItemInstances(JsonArray array) throws Exception {
        List<ItemInstance> instances = new ArrayList<>();
        for (JsonElement output : array) {
            instances.add(ContentLoaderUtils.readItemInstance(output.getAsJsonObject()));
        }
        return instances;
    }

    public static void processCriteria(PlayerCompendiumRecipe recipe, JsonArray ca) {
        if (recipe.isKnowledge()) {
            for (JsonElement ce : ca) {
                JsonObject criteria = ce.getAsJsonObject();
                String cname = criteria.get("name").getAsString();
                ICriteria icriteria = Registries.CRITERIA_REGISTRY.get(new ResourceName(cname));
                if (icriteria == null) {
                    throw new IllegalArgumentException("Invalid criteria " + cname + " for recipe " + recipe.getName());
                }
                JsonObject params = criteria.getAsJsonObject("params");
                if (!icriteria.deserialize(recipe, params)) {
                    RockBottomAPI.logger().warning("Failed to deserialize criteria " + cname + " for recipe " + recipe.getName());
                }
            }
        } else {
            RockBottomAPI.logger().warning("Tried to register criteria for the recipe " + recipe.getName() + " which doesn't use knowledge!");
        }
    }
}
