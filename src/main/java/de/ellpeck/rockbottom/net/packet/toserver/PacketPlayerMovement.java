package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.Direction;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public class PacketPlayerMovement implements IPacket {

    private UUID playerId;
    private double x;
    private double y;
    private double motionX;
    private double motionY;
    private Direction facing;
    private boolean isFlying;

    public PacketPlayerMovement(UUID playerId, double x, double y, double motionX, double motionY, Direction facing, boolean isFlying) {
        this.playerId = playerId;
        this.x = x;
        this.y = y;
        this.motionX = motionX;
        this.motionY = motionY;
        this.facing = facing;
        this.isFlying = isFlying;
    }

    public PacketPlayerMovement() {
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.motionX);
        buf.writeDouble(this.motionY);
        buf.writeInt(this.facing.ordinal());
        buf.writeBoolean(this.isFlying);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.playerId = new UUID(buf.readLong(), buf.readLong());
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.motionX = buf.readDouble();
        this.motionY = buf.readDouble();
        this.facing = Direction.DIRECTIONS[buf.readInt()];
        this.isFlying = buf.readBoolean();
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context) {
        if (game.getWorld() != null) {
            AbstractEntityPlayer player = game.getWorld().getPlayer(this.playerId);
            if (player != null) {
                player.motionX = this.motionX;
                player.motionY = this.motionY;
                player.facing = this.facing;
                player.setBoundsOrigin(this.x, this.y);
                player.isFlying = this.isFlying;
            }
        }
    }
}
