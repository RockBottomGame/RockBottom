package de.ellpeck.rockbottom.gui.container;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

public class ContainerChest extends ItemContainer{

    public ContainerChest(AbstractEntityPlayer player, IInventory inventory){
        super(player, player.getInv(), inventory);

        this.addPlayerInventory(player, 20, 55);
        this.addSlotGrid(inventory, 0, inventory.getSlotAmount(), 0, 0, 10);
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("chest");
    }
}
