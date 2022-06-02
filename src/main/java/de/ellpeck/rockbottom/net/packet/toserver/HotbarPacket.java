package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.net.IPacketContext;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class HotbarPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("hotbar");

    private int slot;

    public HotbarPacket(int slot) {
        this.slot = slot;
    }

    public HotbarPacket() {}

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeInt(this.slot);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.slot = buf.readInt();
    }

    @Override
    public void handle(IGameInstance game, IPacketContext context) {
        AbstractPlayerEntity player = context.getSender();
        if (player != null) {
            player.setSelectedSlot(this.slot);
        }
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
