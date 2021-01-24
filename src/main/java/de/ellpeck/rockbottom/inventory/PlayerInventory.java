package de.ellpeck.rockbottom.inventory;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.compendium.PlayerCompendiumRecipe;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.inventory.Inventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.construction.RecipeCache;
import de.ellpeck.rockbottom.construction.criteria.PickupItemCriteria;
import de.ellpeck.rockbottom.world.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class PlayerInventory extends Inventory {

    public int selectedSlot;

    public PlayerInventory(PlayerEntity player) {
        super(32);

        if (!player.world.isClient()) {
            this.addChangeCallback((inv, slot) -> {
				if (RecipeCache.chest != null && !RecipeCache.chest.isKnown(player)) {
                    int fullness = 0;
                    for (ItemInstance instance : inv) {
                        if (instance != null) {
                            fullness++;

                            if (fullness >= inv.getSlotAmount() / 2) {
                                player.getKnowledge().teachRecipe(RecipeCache.chest);
                                break;
                            }
                        }
                    }
                }
            });

            this.addChangeCallback((inv, slot) -> {
                ItemInstance instance = inv.get(slot);
                if (instance == null) return;
                List<PlayerCompendiumRecipe> allRecipes = PickupItemCriteria.getRecipesFor(instance.getItem());
                if (allRecipes == null) allRecipes = new ArrayList<>();
                List<String> names = RockBottomAPI.getResourceRegistry().getNames(instance);
                for (String name : names) {
                    List<PlayerCompendiumRecipe> resRecipes = PickupItemCriteria.getRecipesFor(name);
                    if (resRecipes != null) allRecipes.addAll(resRecipes);
                }
                player.getKnowledge().teachRecipes(allRecipes);
            });
        }
    }

    @Override
    public void save(DataSet set) {
        super.save(set);

        set.addInt("selected_slot", this.selectedSlot);
    }

    @Override
    public void load(DataSet set) {
        super.load(set);

        this.selectedSlot = set.getInt("selected_slot");
    }
}
