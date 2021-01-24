package de.ellpeck.rockbottom.item;

import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.item.BasicItem;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.List;

public class TwigItem extends BasicItem {

    public TwigItem() {
        super(ResourceName.intern("twig"));
    }

    @Override
    public void describeItem(IAssetManager manager, ItemInstance instance, List<String> desc, boolean isAdvanced, boolean isRealItem) {
        super.describeItem(manager, instance, desc, isAdvanced, isRealItem);
        desc.add(manager.localize(ResourceName.intern("info.twig")));
    }
}
