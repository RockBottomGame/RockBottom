package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.GuiContainer;
import de.ellpeck.rockbottom.api.gui.component.ComponentFancyButton;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

public class GuiInventory extends GuiContainer {

    private boolean keepContainerOpen;

    public GuiInventory(AbstractEntityPlayer player) {
        super(player, 135, 70);

        ShiftClickBehavior behavior = new ShiftClickBehavior(0, 7, 8, player.getInv().getSlotAmount() - 1);
        this.shiftClickBehaviors.add(behavior);
        this.shiftClickBehaviors.add(behavior.reversed());
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);

        this.components.add(new ComponentFancyButton(this, -16, 0, 14, 14, () -> {
            this.keepContainerOpen = true;
            game.getGuiManager().openGui(new GuiCompendium(this.player));
            return true;
        }, ResourceName.intern("gui.construction.book_closed"), game.getAssetManager().localize(ResourceName.intern("button.open_compendium"))));
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
