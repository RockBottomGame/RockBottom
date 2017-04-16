package de.ellpeck.game.gui.container;

import de.ellpeck.game.world.entity.player.EntityPlayer;
import de.ellpeck.game.world.tile.entity.TileEntityChest;

public class ContainerChest extends ItemContainer{

    public ContainerChest(EntityPlayer player, TileEntityChest tile){
        this.addSlotGrid(tile.inventory, 0, tile.inventory.getSlotAmount(), 0, 0, 10);
        this.addPlayerInventory(player, 20, 60);
    }
}
