package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.gui.ContainerGui;
import de.ellpeck.rockbottom.api.gui.component.ImageButtonComponent;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.gui.container.ItemListContainer;

public class InventoryGui extends ContainerGui {

    private boolean keepContainerOpen;

    public InventoryGui(AbstractPlayerEntity player) {
        super(player, 135, 70);

        ShiftClickBehavior behavior = new ShiftClickBehavior(0, 7, 8, player.getInv().getSlotAmount() - 1);
        this.shiftClickBehaviors.add(behavior);
        this.shiftClickBehaviors.add(behavior.reversed());
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);

        this.components.add(new ImageButtonComponent(this, -16, 0, 14, 14, () -> {
            this.keepContainerOpen = true;
            game.getGuiManager().openGui(new CompendiumGui(this.player));
            return true;
        }, ResourceName.intern("gui.compendium.book_closed"), game.getAssetManager().localize(ResourceName.intern("button.open_compendium"))));

        if (this.player.getGameMode().isCreative()) {
            this.components.add(new ImageButtonComponent(this, 140, 54, 16, 16, () -> {
                this.player.openGuiContainer(new ItemListGui(player), new ItemListContainer(player));
                return true;
            }, ResourceName.intern("gui.icons.creative_icon")));
        }
    }

    @Override
    public boolean shouldCloseContainer() {
        return !this.keepContainerOpen;
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("inventory");
    }
}
