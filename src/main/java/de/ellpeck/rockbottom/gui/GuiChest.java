package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.GuiContainer;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

public class GuiChest extends GuiContainer{

    public GuiChest(AbstractEntityPlayer player, IInventory inv){
        super(player, 169, 115);

        int playerSlots = player.getInv().getSlotAmount();
        ShiftClickBehavior behavior = new ShiftClickBehavior(0, playerSlots-1, playerSlots, playerSlots+inv.getSlotAmount()-1);
        this.shiftClickBehaviors.add(behavior);
        this.shiftClickBehaviors.add(behavior.reversed());
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("chest");
    }
}
