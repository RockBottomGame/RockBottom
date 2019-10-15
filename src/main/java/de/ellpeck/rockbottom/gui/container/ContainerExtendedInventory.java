package de.ellpeck.rockbottom.gui.container;

import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.container.ContainerSlot;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.function.Consumer;

public class ContainerExtendedInventory extends ItemContainer {

    protected IInventory inventory;
    protected Consumer<IInventory> onClosed;

    public ContainerExtendedInventory(AbstractEntityPlayer player, IInventory inventory, int width, int height) {
        this(player, inventory, width, height, inv -> {}, ContainerSlot::new);
    }

    public ContainerExtendedInventory(AbstractEntityPlayer player, IInventory inventory, int width, int height, Consumer<IInventory> onClosed, ISlotCallback slotCallback) {
        super(player);
        this.inventory = inventory;
        this.onClosed = onClosed;
        int sizeX = Math.max(135, width * 17 - 1);
        this.addSlotGrid(this.inventory, 0, this.inventory.getSlotAmount(), (sizeX - width * 17 - 1) / 2, 0, width, slotCallback);
        int playerXStart = (sizeX - 135) / 2;
        this.addPlayerInventory(player, playerXStart, height * 17 + 8, slotCallback);
    }

    @Override
    public void onClosed() {
        this.onClosed.accept(this.inventory);
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("extended_inventory");
    }
}
