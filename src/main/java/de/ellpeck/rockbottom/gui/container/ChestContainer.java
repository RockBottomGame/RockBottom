package de.ellpeck.rockbottom.gui.container;

import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.world.tile.entity.ChestTileEntity;

public class ChestContainer extends ItemContainer {

    private final ChestTileEntity tile;

    public ChestContainer(AbstractPlayerEntity player, ChestTileEntity tile) {
        super(player);
        this.tile = tile;

        this.addPlayerInventory(player, 17, 45);

        IInventory inv = tile.getTileInventory();
        this.addSlotGrid(inv, 0, inv.getSlotAmount(), 0, 0, 10);
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("chest");
    }

    @Override
    public void onClosed() {
        if (!this.tile.world.isClient()) {
            this.tile.setOpenCount(this.tile.getOpenCount() - 1);
        }
    }

    @Override
    public void onOpened() {
        if (!this.tile.world.isClient()) {
            this.tile.setOpenCount(this.tile.getOpenCount() + 1);
        }
    }
}
