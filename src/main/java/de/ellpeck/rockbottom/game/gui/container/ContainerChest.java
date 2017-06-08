package de.ellpeck.rockbottom.game.gui.container;

import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.game.world.tile.entity.TileEntityChest;

public class ContainerChest extends ItemContainer{

    private final TileEntityChest tile;

    public ContainerChest(AbstractEntityPlayer player, TileEntityChest tile){
        super(player, player.getInv(), tile.inventory);
        this.tile = tile;

        this.addSlotGrid(tile.inventory, 0, tile.inventory.getSlotAmount(), 0, 0, 10);
        this.addPlayerInventory(player, 20, 60);
    }

    @Override
    public void onOpened(){
        this.tile.openCount++;
    }

    @Override
    public void onClosed(){
        this.tile.openCount--;
    }
}
