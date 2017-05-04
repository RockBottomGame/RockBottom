package de.ellpeck.rockbottom.gui.container;

import de.ellpeck.rockbottom.construction.ConstructionRegistry;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import de.ellpeck.rockbottom.world.tile.entity.TileEntitySmelter;

public class ContainerSmelter extends ItemContainer{

    public ContainerSmelter(EntityPlayer player, TileEntitySmelter tile){
        super(player, player.inv, tile.inventory);

        this.addSlot(new RestrictedSlot(tile.inventory, TileEntitySmelter.INPUT, 50, 10, instance -> ConstructionRegistry.getSmelterRecipe(instance) != null));
        this.addSlot(new RestrictedSlot(tile.inventory, TileEntitySmelter.COAL, 90, 30, instance -> ConstructionRegistry.getFuelValue(instance) > 0));
        this.addSlot(new OutputSlot(tile.inventory, TileEntitySmelter.OUTPUT, 130, 10));

        this.addPlayerInventory(player, 20, 60);
    }
}
