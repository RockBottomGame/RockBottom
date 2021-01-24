package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.gui.ContainerGui;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

public class ChestGui extends ContainerGui {

    public ChestGui(AbstractPlayerEntity player, IInventory inv) {
        super(player, 169, 115);

        int playerSlots = player.getInv().getSlotAmount();
        ShiftClickBehavior behavior = new ShiftClickBehavior(0, playerSlots - 1, playerSlots, playerSlots + inv.getSlotAmount() - 1);
        this.shiftClickBehaviors.add(behavior);
        this.shiftClickBehaviors.add(behavior.reversed());
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("chest");
    }
}
