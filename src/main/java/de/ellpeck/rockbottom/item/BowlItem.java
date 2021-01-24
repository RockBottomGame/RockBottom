package de.ellpeck.rockbottom.item;

import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.item.LiquidContainerItem;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

public class BowlItem extends LiquidContainerItem {

    public BowlItem() {
        super(ResourceName.intern("bowl"));
    }

    @Override
    public boolean allowsLiquid(ItemInstance instance, ResourceName name) {
        return name.getResourceName().contains("water");
    }

    @Override
    public int getCapacity(ItemInstance instance) {
        return 6;
    }
}
