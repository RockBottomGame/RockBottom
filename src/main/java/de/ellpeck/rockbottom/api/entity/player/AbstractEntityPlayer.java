package de.ellpeck.rockbottom.api.entity.player;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.EntityLiving;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.inventory.IInvChangeCallback;
import de.ellpeck.rockbottom.api.inventory.Inventory;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.world.IWorld;

public abstract class AbstractEntityPlayer extends EntityLiving implements IInvChangeCallback{

    public AbstractEntityPlayer(IWorld world){
        super(world);
    }

    public abstract void openGuiContainer(Gui gui, ItemContainer container);

    public abstract void openContainer(ItemContainer container);

    public abstract void closeContainer();

    public abstract ItemContainer getContainer();

    public abstract void resetAndSpawn(IGameInstance game);

    public abstract void sendPacket(IPacket packet);

    public abstract void move(int type);

    public abstract int getCommandLevel();

    public abstract ItemContainer getInvContainer();

    public abstract Inventory getInv();

    public abstract int getSelectedSlot();

    public abstract void setSelectedSlot(int slot);

    public abstract String getChatColorFormat();
}
