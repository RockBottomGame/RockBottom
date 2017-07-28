package de.ellpeck.rockbottom.gui.container;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

public class ContainerConstructionTable extends ItemContainer{

    public ContainerConstructionTable(AbstractEntityPlayer player){
        super(player, player.getInv());
        this.addPlayerInventory(player, 0, 57);
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("construction_table");
    }
}
