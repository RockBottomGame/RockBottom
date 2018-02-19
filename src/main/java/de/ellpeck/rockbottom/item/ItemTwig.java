package de.ellpeck.rockbottom.item;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.item.ItemBasic;
import de.ellpeck.rockbottom.api.item.ItemInstance;

import java.util.List;

public class ItemTwig extends ItemBasic{

    public ItemTwig(){
        super(RockBottomAPI.createInternalRes("twig"));
    }

    @Override
    public void describeItem(IAssetManager manager, ItemInstance instance, List<String> desc, boolean isAdvanced){
        super.describeItem(manager, instance, desc, isAdvanced);
        desc.add(manager.localize(RockBottomAPI.createInternalRes("info.twig")));
    }
}
