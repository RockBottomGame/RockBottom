package de.ellpeck.rockbottom.gui.container;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

public class ContainerItemList extends ItemContainer {

    public ContainerItemList(AbstractEntityPlayer player) {
        super(player);

        this.addPlayerInventory(player, 7, 93);
        this.addSlot(new TrashSlot(154, 72));

        for (Item item : RockBottomAPI.ITEM_REGISTRY.values()) {
            if (!item.useMetaAsDurability()) {
                for (int i = 0; i <= item.getHighestPossibleMeta(); i++) {
                    this.addSlot(new InfiniteSlot(new ItemInstance(item, item.getMaxAmount(), i), 0, 0));
                }
            } else {
                this.addSlot(new InfiniteSlot(new ItemInstance(item, item.getMaxAmount()), 0, 0));
            }
        }
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("item_list");
    }
}
