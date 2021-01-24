package de.ellpeck.rockbottom.gui.container;

import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.gui.container.OutputSlotContainer;
import de.ellpeck.rockbottom.api.gui.container.RestrictedInputSlotContainer;
import de.ellpeck.rockbottom.api.tile.entity.IFilteredInventory;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.world.tile.entity.CombinerTileEntity;

public class CombinerContainer extends ItemContainer {

    public CombinerContainer(AbstractPlayerEntity player, CombinerTileEntity tile) {
        super(player);

        this.addPlayerInventory(player, 0, 60);

        IFilteredInventory inv = tile.getTileInventory();
        this.addSlot(new RestrictedInputSlotContainer(inv, 0, 16, 0));
        this.addSlot(new RestrictedInputSlotContainer(inv, 1, 34, 0));
        this.addSlot(new RestrictedInputSlotContainer(inv, 2, 59, 32));
        this.addSlot(new OutputSlotContainer(inv, 3, 85, 0));
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("combiner");
    }

}
