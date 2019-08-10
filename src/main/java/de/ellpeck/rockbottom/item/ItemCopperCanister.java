package de.ellpeck.rockbottom.item;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.construction.compendium.ICompendiumRecipe;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.GuiMessageBox;
import de.ellpeck.rockbottom.api.item.ItemBasic;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentTranslation;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.entity.EntityItem;

import java.util.List;

public class ItemCopperCanister extends ItemBasic {

    public ItemCopperCanister() {
        super(ResourceName.intern("copper_canister"));
    }

    @Override
    public void describeItem(IAssetManager manager, ItemInstance instance, List<String> desc, boolean isAdvanced) {
        super.describeItem(manager, instance, desc, isAdvanced);
        desc.add(manager.localize(ResourceName.intern("info.copper_canister")));
    }

    @Override
    public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractEntityPlayer player, ItemInstance instance) {
		ICompendiumRecipe recipe = ICompendiumRecipe.forName(GameContent.TILE_SIMPLE_FURNACE.getName());
		if (!recipe.isKnown(player)) {
			if (!world.isClient()) {
                ItemInstance note = ItemRecipeNote.create(recipe);
                EntityItem.spawn(world, note, player.getX(), player.getY(), 0D, 0D);
            }

            player.openGui(new GuiMessageBox(null, 0.25F, 200, 18, new ChatComponentTranslation(ResourceName.intern("info.copper_canister.note"))));
        } else {
            player.openGui(new GuiMessageBox(null, 0.25F, 200, 18, new ChatComponentTranslation(ResourceName.intern("info.copper_canister.empty"))));
        }

        if (!world.isClient()) {
            player.getInv().remove(player.getSelectedSlot(), 1);
        }

        return true;
    }
}
