package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.GuiContainer;
import de.ellpeck.rockbottom.api.gui.component.ComponentMenu;
import de.ellpeck.rockbottom.api.gui.component.GuiComponent;
import de.ellpeck.rockbottom.api.gui.component.MenuComponent;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

public class GuiItemList extends GuiContainer {

    public GuiItemList(AbstractEntityPlayer player) {
        super(player, 150, 163);
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("item_list");
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);

        BoundingBox box = new BoundingBox(0, 0, 150, 88).add(this.x, this.y);
        ComponentMenu menu = new ComponentMenu(this, 0, 0, 88, 8, 5, box);

        ItemContainer container = this.getContainer();
        for (int i = this.player.getInv().getSlotAmount() + 1; i < container.getSlotAmount(); i++) {
            GuiComponent comp = this.components.get(i);
            menu.add(new MenuComponent(comp.getWidth(), comp.getHeight()) {
                @Override
                public void init(Gui gui) {

                }
            }.add(0, 0, comp));
        }

        menu.organize();
        this.components.add(menu);
    }
}
