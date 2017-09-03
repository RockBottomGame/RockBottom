package de.ellpeck.rockbottom.gui.container;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.world.tile.entity.TileEntityCombiner;

public class ContainerCombiner extends ItemContainer{

    public ContainerCombiner(AbstractEntityPlayer player, TileEntityCombiner tile){
        super(player, player.getInv(), tile.inventory);

        this.addSlot(new RestrictedSlot(tile.inventory, TileEntityCombiner.INPUT_ONE, 40, 10, instance -> RockBottomAPI.isCombinerInput(instance, tile.inventory.get(TileEntityCombiner.INPUT_TWO))));
        this.addSlot(new RestrictedSlot(tile.inventory, TileEntityCombiner.INPUT_TWO, 60, 10, instance -> RockBottomAPI.isCombinerInput(instance, tile.inventory.get(TileEntityCombiner.INPUT_ONE))));
        this.addSlot(new RestrictedSlot(tile.inventory, TileEntityCombiner.COAL, 100, 30, instance -> RockBottomAPI.getFuelValue(instance) > 0));
        this.addSlot(new OutputSlot(tile.inventory, TileEntityCombiner.OUTPUT, 140, 10));

        this.addPlayerInventory(player, 20, 60);
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("separator");
    }
}
