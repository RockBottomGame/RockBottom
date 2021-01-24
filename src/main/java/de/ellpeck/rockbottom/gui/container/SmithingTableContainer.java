package de.ellpeck.rockbottom.gui.container;

import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.gui.container.RestrictedInputSlotContainer;
import de.ellpeck.rockbottom.api.tile.entity.IFilteredInventory;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.world.tile.entity.SmithingTableTileEntity;

public class SmithingTableContainer extends ItemContainer {

    public SmithingTableContainer(AbstractPlayerEntity player, SmithingTableTileEntity tile) {
        super(player);

        this.addPlayerInventory(player, 0, 99);

        IFilteredInventory inv = tile.getTileInventory();
        this.addSlot(new RestrictedInputSlotContainer(inv, 0, 119, 2).disableSlotBackgroundRender());
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("construction_table");
    }

}
