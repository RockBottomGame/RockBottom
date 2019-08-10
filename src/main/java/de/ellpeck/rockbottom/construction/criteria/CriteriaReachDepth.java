package de.ellpeck.rockbottom.construction.criteria;

import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.construction.compendium.ICompendiumRecipe;
import de.ellpeck.rockbottom.api.construction.compendium.ICriteria;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CriteriaReachDepth implements ICriteria {
    private static final Map<Integer, List<ICompendiumRecipe>> MAP = new HashMap<>();

    public static List<ICompendiumRecipe> getRecipesFor(int depth) {
        return MAP.get(depth);
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("reach_depth");
    }

    @Override
    public boolean deserialize(ICompendiumRecipe recipe, JsonObject params) {
        if (!params.has("depth")) return false;
        int depth = params.get("depth").getAsInt();
        MAP.putIfAbsent(depth, new ArrayList<>());
        MAP.get(depth).add(recipe);
        return true;
    }
}
