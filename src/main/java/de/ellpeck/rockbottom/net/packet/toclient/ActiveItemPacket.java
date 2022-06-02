package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.net.IPacketContext;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class ActiveItemPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("active_item");

    private UUID playerId;
    private int slot;
    private ItemInstance newSlotItem;

    public ActiveItemPacket(UUID playerId, int slot, ItemInstance newSlotItem) {
        this.playerId = playerId;
        this.slot = slot;
        this.newSlotItem = newSlotItem;
    }

    public ActiveItemPacket() {
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());
        buf.writeInt(this.slot);

        DataSet set = new DataSet();
        if (this.newSlotItem != null) {
            this.newSlotItem.save(set);
        }
        NetUtil.writeSetToBuffer(set, buf);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.playerId = new UUID(buf.readLong(), buf.readLong());
        this.slot = buf.readInt();

        DataSet set = new DataSet();
        NetUtil.readSetFromBuffer(set, buf);
        if (!set.isEmpty()) {
            this.newSlotItem = ItemInstance.load(set);
        }
    }

    @Override
    public void handle(IGameInstance game, IPacketContext context) {
        IWorld world = game.getWorld();
        if (world != null) {
            AbstractPlayerEntity player = world.getPlayer(this.playerId);
            if (player != null) {
                player.setSelectedSlot(this.slot);
                player.getInv().set(this.slot, this.newSlotItem);
            }
        }
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
