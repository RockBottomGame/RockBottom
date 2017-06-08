package de.ellpeck.rockbottom.game.gui.container;

import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.game.construction.ConstructionRegistry;
import de.ellpeck.rockbottom.game.world.tile.entity.TileEntitySmelter;

public class ContainerSmelter extends ItemContainer{

    public ContainerSmelter(AbstractEntityPlayer player, TileEntitySmelter tile){
        super(player, player.getInv(), tile.inventory);

        this.addSlot(new RestrictedSlot(tile.inventory, TileEntitySmelter.INPUT, 50, 10, instance -> ConstructionRegistry.getSmelterRecipe(instance) != null));
        this.addSlot(new RestrictedSlot(tile.inventory, TileEntitySmelter.COAL, 90, 30, instance -> ConstructionRegistry.getFuelValue(instance) > 0));
        this.addSlot(new OutputSlot(tile.inventory, TileEntitySmelter.OUTPUT, 130, 10));

        this.addPlayerInventory(player, 20, 60);
    }
}
