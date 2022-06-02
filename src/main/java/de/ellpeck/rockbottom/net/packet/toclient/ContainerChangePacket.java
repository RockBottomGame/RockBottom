package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.gui.container.SlotContainer;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.net.IPacketContext;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import io.netty.buffer.ByteBuf;

public class ContainerChangePacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("container_change");

    private boolean isInv;
    private int index;
    private ItemInstance instance;

    public ContainerChangePacket(boolean isInv, int index, ItemInstance instance) {
        this.isInv = isInv;
        this.index = index;
        this.instance = instance;
    }

    public ContainerChangePacket() {
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeBoolean(this.isInv);
        buf.writeInt(this.index);

        DataSet set = new DataSet();
        if (this.instance != null) {
            this.instance.save(set);
        }
        NetUtil.writeSetToBuffer(set, buf);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.isInv = buf.readBoolean();
        this.index = buf.readInt();

        DataSet set = new DataSet();
        NetUtil.readSetFromBuffer(set, buf);
        if (!set.isEmpty()) {
            this.instance = ItemInstance.load(set);
        }
    }

    @Override
    public void handle(IGameInstance game, IPacketContext context) {
        if (game.getPlayer() != null) {
            ItemContainer container = this.isInv ? game.getPlayer().getInvContainer() : game.getPlayer().getContainer();
            if (container != null && container.getSlotAmount() > this.index) {
                SlotContainer slot = container.getSlot(this.index);
                slot.set(this.instance);
            }
        }
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
