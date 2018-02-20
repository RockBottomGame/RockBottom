package de.ellpeck.rockbottom.gui.container;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.world.tile.entity.TileEntityChest;

public class ContainerChest extends ItemContainer{

    private final TileEntityChest tile;

    public ContainerChest(AbstractEntityPlayer player, TileEntityChest tile){
        super(player);
        this.tile = tile;

        this.addPlayerInventory(player, 17, 45);

        IInventory inv = tile.getTileInventory();
        this.addSlotGrid(inv, 0, inv.getSlotAmount(), 0, 0, 10);
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("chest");
    }

    @Override
    public void onClosed(){
        if(!this.tile.world.isClient()){
            this.tile.setOpenCount(this.tile.getOpenCount()-1);
        }
    }

    @Override
    public void onOpened(){
        if(!this.tile.world.isClient()){
            this.tile.setOpenCount(this.tile.getOpenCount()+1);
        }
    }
}
