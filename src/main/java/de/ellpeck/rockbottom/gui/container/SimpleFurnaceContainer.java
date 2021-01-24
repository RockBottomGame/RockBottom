package de.ellpeck.rockbottom.gui.container;

import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.gui.container.OutputSlotContainer;
import de.ellpeck.rockbottom.api.gui.container.RestrictedInputSlotContainer;
import de.ellpeck.rockbottom.api.tile.entity.IFilteredInventory;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.world.tile.entity.SimpleFurnaceTileEntity;

public class SimpleFurnaceContainer extends ItemContainer {

    public SimpleFurnaceContainer(AbstractPlayerEntity player, SimpleFurnaceTileEntity tile) {
        super(player);

        this.addPlayerInventory(player, 0, 60);

        IFilteredInventory inv = tile.getTileInventory();
        this.addSlot(new RestrictedInputSlotContainer(inv, 0, 34, 0));
        this.addSlot(new RestrictedInputSlotContainer(inv, 1, 59, 32));
        this.addSlot(new OutputSlotContainer(inv, 2, 85, 0));
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("simple_furnace");
    }

}
