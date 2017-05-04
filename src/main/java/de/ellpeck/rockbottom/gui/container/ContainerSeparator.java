package de.ellpeck.rockbottom.gui.container;

import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import de.ellpeck.rockbottom.world.tile.entity.TileEntitySeparator;

public class ContainerSeparator extends ItemContainer{

    public ContainerSeparator(EntityPlayer player, TileEntitySeparator tile){
        super(player, player.inv, tile.inventory);
    }
}
