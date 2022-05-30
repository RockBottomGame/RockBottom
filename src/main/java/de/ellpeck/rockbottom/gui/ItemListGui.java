package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.ContainerGui;
import de.ellpeck.rockbottom.api.gui.component.ImageButtonComponent;
import de.ellpeck.rockbottom.api.gui.component.MenuComponent;
import de.ellpeck.rockbottom.api.gui.component.GuiComponent;
import de.ellpeck.rockbottom.api.gui.component.MenuItemComponent;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

public class ItemListGui extends ContainerGui {

    private final boolean openCompendium;
    public ItemListGui(AbstractPlayerEntity player, boolean openCompendium) {
        super(player, 144, 169);
        this.openCompendium = openCompendium;
    }

    public ItemListGui(AbstractPlayerEntity player) {
        this(player, false);
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("item_list");
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);

        BoundingBox box = new BoundingBox(0, 0, 150, 88).add(this.x, this.y);
        MenuComponent menu = new MenuComponent(this, -7, 0, 88, 8, 5, box);

        ItemContainer container = this.getContainer();
        for (int i = this.player.getInv().getSlotAmount() + 1; i < container.getSlotAmount(); i++) {
            GuiComponent comp = this.components.get(i);
            menu.add(new MenuItemComponent(comp.getWidth(), comp.getHeight()) {
                @Override
                public void init(Gui gui) {

                }
            }.add(0, 0, comp));
        }

        menu.organize();
        this.components.add(menu);

        if (this.player.getGameMode().isCreative()) {
            this.components.add(new ImageButtonComponent(this, 145, 153, 16, 16, () -> {
                if (this.openCompendium) {
                    this.player.openGuiContainer(new CompendiumGui(player), player.getInvContainer());
                } else {
                    this.player.openGuiContainer(new InventoryGui(player), player.getInvContainer());
                }
                return true;
            }, ResourceName.intern("gui.icons.survival_icon")));
        }
    }
}
