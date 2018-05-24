package de.ellpeck.rockbottom.gui.container;

import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.gui.container.OutputSlot;
import de.ellpeck.rockbottom.api.gui.container.RestrictedInputSlot;
import de.ellpeck.rockbottom.api.tile.entity.IFilteredInventory;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.world.tile.entity.TileEntitySimpleFurnace;

public class ContainerSimpleFurnace extends ItemContainer{

    public ContainerSimpleFurnace(AbstractEntityPlayer player, TileEntitySimpleFurnace tile){
        super(player);

        this.addPlayerInventory(player, 0, 60);

        IFilteredInventory inv = tile.getTileInventory();
        this.addSlot(new RestrictedInputSlot(inv, 0, 34, 0));
        this.addSlot(new RestrictedInputSlot(inv, 1, 59, 32));
        this.addSlot(new OutputSlot(inv, 2, 85, 0));
    }

    @Override
    public ResourceName getName(){
        return ResourceName.intern("simple_furnace");
    }

}
