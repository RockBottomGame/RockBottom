package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.GuiContainer;
import de.ellpeck.rockbottom.api.gui.component.ComponentProgressBar;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.world.tile.entity.TileEntityCombiner;
import de.ellpeck.rockbottom.world.tile.entity.TileEntitySimpleFurnace;

public class GuiCombiner extends GuiContainer {

    private final TileEntityCombiner tile;

    public GuiCombiner(AbstractEntityPlayer player, TileEntityCombiner tile) {
        super(player, 153, 130);
        this.tile = tile;

        int playerSlots = player.getInv().getSlotAmount();

        ShiftClickBehavior input = new ShiftClickBehavior(0, playerSlots - 1, playerSlots, playerSlots + 1);
        this.shiftClickBehaviors.add(input);
        this.shiftClickBehaviors.add(input.reversed());

        ShiftClickBehavior output = new ShiftClickBehavior(0, playerSlots - 1, playerSlots + 3, playerSlots + 3);
        this.shiftClickBehaviors.add(output.reversed());

        ShiftClickBehavior fuel = new ShiftClickBehavior(0, playerSlots - 1, playerSlots + 2, playerSlots + 2);
        this.shiftClickBehaviors.add(fuel);
        this.shiftClickBehaviors.add(fuel.reversed());
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);

        this.components.add(new ComponentProgressBar(this, 52, 4, 31, 8, Colors.GREEN, false, this.tile::getSmeltPercentage));
        this.components.add(new ComponentProgressBar(this, 62, 16, 10, 15, Colors.RED, true, this.tile::getFuelPercentage));
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("combiner");
    }
}
