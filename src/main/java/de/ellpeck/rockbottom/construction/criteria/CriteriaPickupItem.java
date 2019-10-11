package de.ellpeck.rockbottom.construction.criteria;

import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.construction.compendium.ICriteria;
import de.ellpeck.rockbottom.api.construction.compendium.PlayerCompendiumRecipe;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CriteriaPickupItem implements ICriteria {
    private static final Map<ResourceName, List<PlayerCompendiumRecipe>> ITEMS_MAP = new HashMap<>();
    private static final Map<String, List<PlayerCompendiumRecipe>> RES_MAP = new HashMap<>();

    public static List<PlayerCompendiumRecipe> getRecipesFor(Item item) {
        return ITEMS_MAP.get(item.getName());
    }

    public static List<PlayerCompendiumRecipe> getRecipesFor(String name) {
        return RES_MAP.get(name);
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("pickup_item");
    }

    @Override
    public boolean deserialize(PlayerCompendiumRecipe recipe, JsonObject params) {
        if (!params.has("item")) return false;
        String name = params.get("item").getAsString();
        if (!Util.isResourceName(name)) {
            RES_MAP.putIfAbsent(name, new ArrayList<>());
            RES_MAP.get(name).add(recipe);
            return true;
        }
        ResourceName resourceName = new ResourceName(name);
        if (Registries.ITEM_REGISTRY.get(resourceName) == null) return false;
        ITEMS_MAP.putIfAbsent(resourceName, new ArrayList<>());
        ITEMS_MAP.get(resourceName).add(recipe);
        return true;
    }
}
