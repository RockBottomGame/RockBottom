package de.ellpeck.rockbottom.construction.criteria;

import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.construction.compendium.ICompendiumRecipe;
import de.ellpeck.rockbottom.api.construction.compendium.ICriteria;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CriteriaBreakTile implements ICriteria {
    private static final Map<ResourceName, List<ICompendiumRecipe>> MAP = new HashMap<>();

    public static List<ICompendiumRecipe> getRecipesFor(Tile tile) {
        return MAP.get(tile.getName());
    }
    @Override
    public ResourceName getName() {
        return ResourceName.intern("break_tile");
    }

    @Override
    public boolean deserialize(ICompendiumRecipe recipe, JsonObject params) {
        if (!params.has("tile")) return false;
        ResourceName name = new ResourceName(params.get("tile").getAsString());
        if (Registries.TILE_REGISTRY.get(name) == null) return false;
        MAP.putIfAbsent(name, new ArrayList<>());
        MAP.get(name).add(recipe);
        return true;
    }
}
