package de.ellpeck.rockbottom.item;

import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.item.ItemBasic;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.List;

public class ItemConstructionTool extends ItemBasic {
    private final int durability;

    public ItemConstructionTool(ResourceName name, int durability) {
        super(name);
        this.durability = durability;
        this.maxAmount = 1;
    }

    @Override
    public void describeItem(IAssetManager manager, ItemInstance instance, List<String> desc, boolean isAdvanced) {
        super.describeItem(manager, instance, desc, isAdvanced);

        int highest = this.getHighestPossibleMeta() + 1;
        desc.add(manager.localize(ResourceName.intern("info.durability"), highest - instance.getMeta(), highest));
    }

    @Override
    public boolean useMetaAsDurability() {
        return true;
    }

    @Override
    public int getHighestPossibleMeta() {
        return this.durability - 1;
    }
}
