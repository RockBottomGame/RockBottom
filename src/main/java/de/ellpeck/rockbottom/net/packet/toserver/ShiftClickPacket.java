package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.apiimpl.InternalHooks;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public class ShiftClickPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("shift_click");

    private UUID playerId;
    private int slotFrom;
    private int slotInto;

    public ShiftClickPacket() {
    }

    public ShiftClickPacket(UUID playerId, int slotFrom, int slotInto) {
        this.playerId = playerId;
        this.slotFrom = slotFrom;
        this.slotInto = slotInto;
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());
        buf.writeInt(this.slotFrom);
        buf.writeInt(this.slotInto);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.playerId = new UUID(buf.readLong(), buf.readLong());
        this.slotFrom = buf.readInt();
        this.slotInto = buf.readInt();
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context) {
        IWorld world = game.getWorld();
        if (world != null) {
            AbstractPlayerEntity player = world.getPlayer(this.playerId);
            if (player != null) {
                ItemContainer container = player.getContainer();
                if (container != null) {
                    InternalHooks.shiftClick(player, container, this.slotFrom, this.slotInto);
                }
            }
        }
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
