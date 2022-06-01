package de.ellpeck.rockbottom.gui.container;

import de.ellpeck.rockbottom.api.gui.container.SlotContainer;
import de.ellpeck.rockbottom.api.inventory.AbstractInventory;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;

public class InfiniteSlotContainer extends SlotContainer {

    public InfiniteSlotContainer(ItemInstance instance, int x, int y) {
        super(makeInventory(instance), 0, x, y);
    }

    private static IInventory makeInventory(ItemInstance instance) {
        return new AbstractInventory() {
            @Override
            public void set(int id, ItemInstance instance) {

            }

            @Override
            public ItemInstance get(int id) {
                return instance.copy();
            }

            @Override
            public int getSlotAmount() {
                return 1;
            }

            @Override
            public void clear() {

            }
        };
    }
}
