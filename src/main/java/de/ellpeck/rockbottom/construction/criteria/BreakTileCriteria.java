package de.ellpeck.rockbottom.construction.criteria;

import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.construction.compendium.ICriteria;
import de.ellpeck.rockbottom.api.construction.compendium.PlayerCompendiumRecipe;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BreakTileCriteria implements ICriteria {
    private static final Map<ResourceName, List<PlayerCompendiumRecipe>> MAP = new HashMap<>();

    public static List<PlayerCompendiumRecipe> getRecipesFor(Tile tile) {
        return MAP.get(tile.getName());
    }
    @Override
    public ResourceName getName() {
        return ResourceName.intern("break_tile");
    }

    @Override
    public boolean deserialize(PlayerCompendiumRecipe recipe, JsonObject params) {
        if (!params.has("tile")) return false;
        ResourceName name = new ResourceName(params.get("tile").getAsString());
        if (Registries.TILE_REGISTRY.get(name) == null) return false;
        MAP.putIfAbsent(name, new ArrayList<>());
        MAP.get(name).add(recipe);
        return true;
    }
}
