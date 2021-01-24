package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.world.entity.player.InteractionManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public class AttackPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("attack");

    private UUID playerId;
    private double x;
    private double y;

    public AttackPacket(UUID playerId, double x, double y) {
        this.playerId = playerId;
        this.x = x;
        this.y = y;
    }

    public AttackPacket() {

    }

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.playerId = new UUID(buf.readLong(), buf.readLong());
        this.x = buf.readDouble();
        this.y = buf.readDouble();
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context) {
        if (game.getWorld() != null) {
            AbstractPlayerEntity player = game.getWorld().getPlayer(this.playerId);
            if (player != null) {
                InteractionManager.attackEntity(player, this.x, this.y);
            }
        }
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
