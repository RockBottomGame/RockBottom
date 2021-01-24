package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.gui.ContainerGui;
import de.ellpeck.rockbottom.api.gui.component.ProgressBarComponent;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.world.tile.entity.SimpleFurnaceTileEntity;

public class SimpleFurnaceGui extends ContainerGui {

    private final SimpleFurnaceTileEntity tile;

    public SimpleFurnaceGui(AbstractPlayerEntity player, SimpleFurnaceTileEntity tile) {
        super(player, 135, 130);
        this.tile = tile;

        int playerSlots = player.getInv().getSlotAmount();

        ShiftClickBehavior input = new ShiftClickBehavior(0, playerSlots - 1, playerSlots, playerSlots);
        this.shiftClickBehaviors.add(input);
        this.shiftClickBehaviors.add(input.reversed());

        ShiftClickBehavior output = new ShiftClickBehavior(0, playerSlots - 1, playerSlots + 2, playerSlots + 2);
        this.shiftClickBehaviors.add(output.reversed());

        ShiftClickBehavior fuel = new ShiftClickBehavior(0, playerSlots - 1, playerSlots + 1, playerSlots + 1);
        this.shiftClickBehaviors.add(fuel);
        this.shiftClickBehaviors.add(fuel.reversed());
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);

        this.components.add(new ProgressBarComponent(this, 52, 4, 31, 8, Colors.GREEN, false, this.tile::getSmeltPercentage));
        this.components.add(new ProgressBarComponent(this, 62, 16, 10, 15, Colors.RED, true, this.tile::getFuelPercentage));
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("simple_furnace");
    }
}
