package de.ellpeck.rockbottom.gui.container;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;

public class ContainerInventory extends ItemContainer{

    public static final IResourceName NAME = RockBottomAPI.createInternalRes("inventory");

    public ContainerInventory(EntityPlayer player){
        super(player);
        this.addPlayerInventory(player, 0, 0);
    }

    @Override
    public IResourceName getName(){
        return NAME;
    }
}
