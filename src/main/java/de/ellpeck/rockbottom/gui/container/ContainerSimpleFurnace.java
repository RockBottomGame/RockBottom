package de.ellpeck.rockbottom.gui.container;

import de.ellpeck.rockbottom.api.construction.smelting.FuelInput;
import de.ellpeck.rockbottom.api.construction.smelting.SmeltingRecipe;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.world.tile.entity.TileEntitySimpleFurnace;

public class ContainerSimpleFurnace extends ItemContainer{

    public ContainerSimpleFurnace(AbstractEntityPlayer player, TileEntitySimpleFurnace tile){
        super(player);

        this.addPlayerInventory(player, 0, 60);

        IInventory inv = tile.getTileInventory();
        this.addSlot(new RestrictedSlot(inv, 0, 34, 0, inst -> SmeltingRecipe.forInput(inst) != null));
        this.addSlot(new RestrictedSlot(inv, 1, 59, 32, inst -> FuelInput.getFuelTime(inst) > 0));
        this.addSlot(new OutputSlot(inv, 2, 85, 0));
    }

    @Override
    public ResourceName getName(){
        return ResourceName.intern("simple_furnace");
    }

}
