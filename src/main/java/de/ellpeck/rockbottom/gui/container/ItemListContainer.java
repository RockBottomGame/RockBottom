package de.ellpeck.rockbottom.gui.container;

import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

public class ItemListContainer extends ItemContainer {

    public ItemListContainer(AbstractPlayerEntity player) {
        super(player);

        this.addPlayerInventory(player, 5, 99);
        this.addSlot(new TrashSlotContainer(148, 72));

        for (Item item : Registries.ITEM_REGISTRY.values()) {
            if (!item.useMetaAsDurability()) {
                for (int i = 0; i <= item.getHighestPossibleMeta(); i++) {
                    this.addSlot(new InfiniteSlotContainer(new ItemInstance(item, item.getMaxAmount(), i), 0, 0));
                }
            } else {
                this.addSlot(new InfiniteSlotContainer(new ItemInstance(item, item.getMaxAmount()), 0, 0));
            }
        }
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("item_list");
    }
}
