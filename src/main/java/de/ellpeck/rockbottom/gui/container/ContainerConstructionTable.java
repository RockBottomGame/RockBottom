package de.ellpeck.rockbottom.gui.container;

import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.gui.container.RestrictedInputSlot;
import de.ellpeck.rockbottom.api.tile.entity.IFilteredInventory;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.world.tile.entity.TileEntityConstructionTable;

public class ContainerConstructionTable extends ItemContainer {

    public ContainerConstructionTable(AbstractEntityPlayer player, TileEntityConstructionTable tile) {
        super(player);

        this.addPlayerInventory(player, 0, 99);

        IFilteredInventory inv = tile.getTileInventory();
        this.addSlot(new RestrictedInputSlot(inv, 0, 109, 2));
        this.addSlot(new RestrictedInputSlot(inv, 1, 109, 20));
        this.addSlot(new RestrictedInputSlot(inv, 2, 109, 38));
        this.addSlot(new RestrictedInputSlot(inv, 3, 109, 56));
        this.addSlot(new RestrictedInputSlot(inv, 4, 109, 74));
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("construction_table");
    }

}
