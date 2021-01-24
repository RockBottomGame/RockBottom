package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class ContainerDataPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("container_data");

    private ItemInstance[] data;

    public ContainerDataPacket(ItemContainer container) {
        this.data = new ItemInstance[container.getSlotAmount()];
        for (int i = 0; i < this.data.length; i++) {
            ItemInstance inst = container.getSlot(i).get();
            if (inst != null) {
                this.data[i] = inst;
            }
        }
    }

    public ContainerDataPacket() {
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeInt(this.data.length);
        for (ItemInstance inst : this.data) {
            DataSet set = new DataSet();

            if (inst != null) {
                inst.save(set);
            }

            NetUtil.writeSetToBuffer(set, buf);
        }
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        int amount = buf.readInt();
        this.data = new ItemInstance[amount];

        for (int i = 0; i < this.data.length; i++) {
            DataSet set = new DataSet();
            NetUtil.readSetFromBuffer(set, buf);

            if (!set.isEmpty()) {
                this.data[i] = ItemInstance.load(set);
            }
        }
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context) {
        if (game.getPlayer() != null) {
            ItemContainer container = game.getPlayer().getContainer();
            if (container != null && container.getSlotAmount() == this.data.length) {
                for (int i = 0; i < this.data.length; i++) {
                    container.getSlot(i).set(this.data[i]);
                }
            }
        }
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
