package de.ellpeck.rockbottom.gui.container;

import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;

public class ContainerInventory extends ItemContainer{

    public ContainerInventory(EntityPlayer player){
        super(player, player.inv);
        this.addPlayerInventory(player, 0, 0);
    }

    @Override
    public int getUnboundId(){
        return 0;
    }
}
