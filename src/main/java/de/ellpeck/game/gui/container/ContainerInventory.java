package de.ellpeck.game.gui.container;

import de.ellpeck.game.world.entity.player.EntityPlayer;

public class ContainerInventory extends ItemContainer{

    public ContainerInventory(EntityPlayer player){
        super(player);
        this.addPlayerInventory(player, 0, 0);
    }
}
