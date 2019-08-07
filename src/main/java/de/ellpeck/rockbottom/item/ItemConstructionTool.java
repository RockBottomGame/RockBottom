package de.ellpeck.rockbottom.item;

import de.ellpeck.rockbottom.api.item.ItemBasic;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

public class ItemConstructionTool extends ItemBasic {
    public ItemConstructionTool(ResourceName name) {
        super(name);
        this.maxAmount = 1;
    }
}
