package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.apiimpl.InternalHooks;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public class PacketSetOrPickHolding implements IPacket {

    private UUID playerId;
    private int slot;
    private boolean half;

    public PacketSetOrPickHolding() {
    }

    public PacketSetOrPickHolding(UUID playerId, int slot, boolean half) {
        this.playerId = playerId;
        this.slot = slot;
        this.half = half;
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());
        buf.writeInt(this.slot);
        buf.writeBoolean(this.half);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.playerId = new UUID(buf.readLong(), buf.readLong());
        this.slot = buf.readInt();
        this.half = buf.readBoolean();
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context) {
        IWorld world = game.getWorld();
        if (world != null) {
            AbstractEntityPlayer player = world.getPlayer(this.playerId);
            if (player != null) {
                ItemContainer container = player.getContainer();
                if (container != null) {
                    InternalHooks.setOrPickUpHolding(player, container, this.slot, this.half);
                }
            }
        }
    }
}
