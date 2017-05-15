package de.ellpeck.rockbottom.gui.container;

import de.ellpeck.rockbottom.construction.ConstructionRegistry;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import de.ellpeck.rockbottom.world.tile.entity.TileEntitySeparator;

public class ContainerSeparator extends ItemContainer{

    public ContainerSeparator(EntityPlayer player, TileEntitySeparator tile){
        super(player, player.inv, tile.inventory);

        this.addSlot(new RestrictedSlot(tile.inventory, TileEntitySeparator.INPUT, 40, 10, instance -> ConstructionRegistry.getSeparatorRecipe(instance) != null));
        this.addSlot(new RestrictedSlot(tile.inventory, TileEntitySeparator.COAL, 80, 30, instance -> ConstructionRegistry.getFuelValue(instance) > 0));
        this.addSlot(new OutputSlot(tile.inventory, TileEntitySeparator.OUTPUT, 120, 10));
        this.addSlot(new OutputSlot(tile.inventory, TileEntitySeparator.BYPRODUCT, 140, 10));

        this.addPlayerInventory(player, 20, 60);
    }
}
