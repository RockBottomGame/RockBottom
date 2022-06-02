package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.net.IPacketContext;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.apiimpl.InternalHooks;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class SetOrPickHoldingPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("set_or_pick_holding");

    private int slot;
    private boolean half;

    public SetOrPickHoldingPacket(int slot, boolean half) {
        this.slot = slot;
        this.half = half;
    }

    public SetOrPickHoldingPacket() {}

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeInt(this.slot);
        buf.writeBoolean(this.half);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.slot = buf.readInt();
        this.half = buf.readBoolean();
    }

    @Override
    public void handle(IGameInstance game, IPacketContext context) {
        AbstractPlayerEntity player = context.getSender();
        if (player != null) {
            ItemContainer container = player.getContainer();
            if (container != null) {
                InternalHooks.setOrPickUpHolding(player, container, this.slot, this.half);
            }
        }
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
