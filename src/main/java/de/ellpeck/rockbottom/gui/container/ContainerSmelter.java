package de.ellpeck.rockbottom.gui.container;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.world.tile.entity.TileEntitySmelter;

public class ContainerSmelter extends ItemContainer{

    public ContainerSmelter(AbstractEntityPlayer player, TileEntitySmelter tile){
        super(player, player.getInv(), tile.inventory);

        this.addSlot(new RestrictedSlot(tile.inventory, TileEntitySmelter.INPUT, 50, 10, instance -> RockBottomAPI.getSmelterRecipe(instance) != null));
        this.addSlot(new RestrictedSlot(tile.inventory, TileEntitySmelter.COAL, 90, 30, instance -> RockBottomAPI.getFuelValue(instance) > 0));
        this.addSlot(new OutputSlot(tile.inventory, TileEntitySmelter.OUTPUT, 130, 10));

        this.addPlayerInventory(player, 20, 60);
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("smelter");
    }
}
