package de.ellpeck.rockbottom.construction.criteria;

import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.construction.compendium.ICriteria;
import de.ellpeck.rockbottom.api.construction.compendium.PlayerCompendiumRecipe;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CriteriaReachDepth implements ICriteria {
    private static final Map<Integer, List<PlayerCompendiumRecipe>> MAP = new HashMap<>();

    public static List<PlayerCompendiumRecipe> getRecipesFor(int depth) {
        return MAP.get(depth);
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("reach_depth");
    }

    @Override
    public boolean deserialize(PlayerCompendiumRecipe recipe, JsonObject params) {
        if (!params.has("depth")) return false;
        int depth = params.get("depth").getAsInt();
        MAP.putIfAbsent(depth, new ArrayList<>());
        MAP.get(depth).add(recipe);
        return true;
    }
}
