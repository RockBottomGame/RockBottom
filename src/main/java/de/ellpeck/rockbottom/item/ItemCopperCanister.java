package de.ellpeck.rockbottom.item;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.ItemBasic;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentText;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.List;

public class ItemCopperCanister extends ItemBasic{

    public ItemCopperCanister(){
        super(RockBottomAPI.createInternalRes("copper_canister"));
    }

    @Override
    public void describeItem(IAssetManager manager, ItemInstance instance, List<String> desc, boolean isAdvanced){
        super.describeItem(manager, instance, desc, isAdvanced);
        desc.add(manager.localize(RockBottomAPI.createInternalRes("info.copper_canister")));
    }

    @Override
    public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractEntityPlayer player, ItemInstance instance){
        if(!world.isClient()){
            player.sendMessageTo(RockBottomAPI.getGame().getChatLog(), new ChatComponentText("<This would give you the recipe for some basic machines but that's not implemented yet>"));
            player.getInv().remove(player.getSelectedSlot(), 1);
        }
        return true;
    }
}
