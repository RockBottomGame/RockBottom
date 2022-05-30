package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.gui.ContainerGui;
import de.ellpeck.rockbottom.api.gui.component.ImageButtonComponent;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

public class ExtendedInventoryGui extends ContainerGui {

    private IInventory inventory;

    public ExtendedInventoryGui(AbstractPlayerEntity player, IInventory inventory, int containerWidth, int containerHeight) {
        super(player, Math.max(135, containerWidth * 17 - 1), 70 + containerHeight * 17 + 8);
        this.inventory = inventory;

        int playerSlots = player.getInv().getSlotAmount();
        int invSlots = this.inventory.getSlotAmount();

        ShiftClickBehavior behavior = new ShiftClickBehavior(0, invSlots - 1, invSlots, invSlots + playerSlots);
        this.shiftClickBehaviors.add(behavior);
        this.shiftClickBehaviors.add(behavior.reversed());
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);

        this.components.add(new ImageButtonComponent(this, (135 - width) / 2 - 16, height - 70, 14, 14, () -> {
            player.openGuiContainer(new CompendiumGui(player), player.getInvContainer());
            return true;
        }, ResourceName.intern("gui.compendium.book_closed"), game.getAssetManager().localize(ResourceName.intern("button.open_compendium"))));
    }

    @Override
    public void onClosed(IGameInstance game) {
        super.onClosed(game);
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("extended_inventory");
    }
}
