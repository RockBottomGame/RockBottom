package de.ellpeck.rockbottom.gui.container;

import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.gui.container.RestrictedInputSlotContainer;
import de.ellpeck.rockbottom.api.tile.entity.IFilteredInventory;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.world.tile.entity.ConstructionTableTileEntity;

public class ConstructionTableContainer extends ItemContainer {

    public ConstructionTableContainer(AbstractPlayerEntity player, ConstructionTableTileEntity tile) {
        super(player);

        this.addPlayerInventory(player, 0, 99);

        IFilteredInventory inv = tile.getTileInventory();
        this.addSlot(new RestrictedInputSlotContainer(inv, 0, 119, 2).disableSlotBackgroundRender());
        this.addSlot(new RestrictedInputSlotContainer(inv, 1, 119, 20).disableSlotBackgroundRender());
        this.addSlot(new RestrictedInputSlotContainer(inv, 2, 119, 38).disableSlotBackgroundRender());
        this.addSlot(new RestrictedInputSlotContainer(inv, 3, 119, 56).disableSlotBackgroundRender());
        this.addSlot(new RestrictedInputSlotContainer(inv, 4, 119, 74).disableSlotBackgroundRender());
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("construction_table");
    }

}
