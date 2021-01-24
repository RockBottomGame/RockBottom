package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public class HotbarPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("hotbar");

    private UUID playerId;
    private int slot;

    public HotbarPacket(UUID playerId, int slot) {
        this.playerId = playerId;
        this.slot = slot;
    }

    public HotbarPacket() {
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());
        buf.writeInt(this.slot);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.playerId = new UUID(buf.readLong(), buf.readLong());
        this.slot = buf.readInt();
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context) {
        if (game.getWorld() != null) {
            AbstractPlayerEntity player = game.getWorld().getPlayer(this.playerId);
            if (player != null) {
                player.setSelectedSlot(this.slot);
            }
        }
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
